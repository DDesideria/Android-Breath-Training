package name.Freediving.services;

import name.Freediving.R;
import name.Freediving.entry.TrainingProg;
import name.Freediving.services.TrainingServiceState.Phase;
import name.Freediving.services.TrainingServiceState.State;
import name.Freediving.services.TrainingServiceState.TimeFormat;
import name.Freediving.ui.TrainingActivity;
import name.Freediving.ui.TrainingFragment;
import name.Freediving.util.Log;
import name.Freediving.util.StringUtils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

/**
 * The "trener". Holds all training state and refreshes ui? sends notification
 * etc..
 * 
 * @author DD
 * 
 */
public class TrainingService extends Service implements SensorEventListener {
	public class ImBinder extends Binder {
		public TrainingService getService() {
			return TrainingService.this;
		}
	};

	private final static int NOTIFICATION_ID = 1;
	protected static final String TAG = "TrainingService";

	public static CharSequence totalToText(TrainingServiceState state) {

		return "  " + state.getProgram().getName() + "\nTotal time: "
				+ StringUtils.formatTime(state.getTimeElapsed());
	}

	private TrainingFragment activity;
	private boolean awaitAccelerometer;
	private boolean bound;;
	private String BREATH_TRAINING;
	private PendingIntent contentIntent;
	private Handler handler = new Handler();
	private Sensor mAccelerometer;
	private ImBinder mBinder = new ImBinder();
	private NotificationManager mNotificationManager;

	private PowerManager mPowerManager;
	private SensorManager mSensorManager;
	private Vibrator mVibrator;

	private WakeLock mWakeLock;
	private int sensorZ;
	private TrainingServiceState state = new TrainingServiceState(false,
			TimeFormat.ELAPSED, Phase.BREATHINGIN, 300, 600,
			"No program selected");

	/*
	 * This is our "mainloop"
	 */
	Runnable tik = new Runnable() {

		public void run() {
			Log.d(TAG, "tik!");
			if (state.getState() == State.RUNNING) {
				handler.postDelayed(tik, TrainingServiceState.TICK_TIME);
				state.tik();
				while (!state.hasMoreTime() && isAutoswitching()) {
					if (!state.switchPhase()) {
						stopTraining();
						state.setWhyStopped("Finished in "
								+ StringUtils.formatTime(state.getTimeElapsed()));
					}

				}
				updateUI();
				periodicEvent();
			}
		}

	};

	private PendingIntent createContentIntent() {
		return PendingIntent.getActivity(TrainingService.this, 0,
				createIntent(), 0);
	}

	private Intent createIntent() {
		return new Intent(this, TrainingActivity.class).putExtra("newLaunch",
				false);
	}

	private int getIcResId(int i) {
		switch ((int) state.getPhaseTimeElapsed() / 60000) {
		case 0:
			return R.drawable.ic_stat_zero;
		case 1:
			return R.drawable.ic_stat_one;
		case 2:
			return R.drawable.ic_stat_two;
		case 3:
			return R.drawable.ic_stat_three;
		case 4:
			return R.drawable.ic_stat_four;
		case 5:
			return R.drawable.ic_stat_five;
		case 6:
			return R.drawable.ic_stat_six;
		case 7:
			return R.drawable.ic_stat_seven;
		case 8:
			return R.drawable.ic_stat_eihgt;
		case 9:
			return R.drawable.ic_stat_nine;
		default:
			return R.drawable.ic_stat_nine_more;
		}
	}

	public TrainingServiceState getState() {
		return state;
	}

	private void inintSystemServices() {

		mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				getClass().getName());
	}

	private TrainingServiceState initState(Intent intent) {
		Log.d(TAG, "initState:");
		TrainingServiceState state = new TrainingServiceState(false,
				TimeFormat.ELAPSED, Phase.BREATHINGIN, 300, 600,
				"No program selected");
		state.setState(State.STOPPED);
		state.setCurrentCount(0);
		long progId = intent.getLongExtra("progId", -1);
		TrainingProg prog = TrainingProg.fromDatabase(this, progId);
		if (prog == null || !state.initProgram(prog))
			notifyError("No prog");

		return state;
	}

	/*
	 * Let's check if we got to change phase
	 */
	boolean isAutoswitching() {
		return state.isAutoSwitch() || awaitAccelerometer && sensorZ >= 0;
	}

	private void notifyError(String string) {
		throw new RuntimeException(string);
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind(..)");
		bound = true;
		// If bound then we don't need notification- we've got fragments ui
		mNotificationManager.cancel(NOTIFICATION_ID);
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		inintSystemServices();
		BREATH_TRAINING = getString(R.string.breath_training);
		contentIntent = createContentIntent();
		Log.d(TAG, "onCreate(..)");
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy(..)");
		// To be confident that we will stoopped
		handler.removeCallbacksAndMessages(tik);
	}

	@Override
	public void onRebind(Intent intent) {
		Log.d(TAG, "onReBind(..)");
		bound = true;
		mNotificationManager.cancel(NOTIFICATION_ID);
		updateUI();
		super.onRebind(intent);
	}

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
			return;
		sensorZ = (int) event.values[2];

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent == null)
			notifyError("intent is null");
		state = initState(intent);
		readPrefs(state);
		Log.d(TAG, "onStart(..)");
		return START_STICKY;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		bound = false;
		activity = null;
		Log.d(TAG, "onUnBind(..)");
		// TODO ����� �� ���������
		super.onUnbind(intent);
		return true;
	}

	public void pauseTraining() {
		// TODO ������� �������������, ������� ���������,
		handler.removeCallbacksAndMessages(tik);
		state.setState(State.PAUSED);
		if (awaitAccelerometer)
			mSensorManager.unregisterListener(this);
		mNotificationManager.cancel(NOTIFICATION_ID);
		updateUI();
	}

	/*
	 * This notifies according to settings when it's time to
	 */
	private void periodicEvent() {
		if (state.getPhaseTimeElapsed() == 0) {
			if (state.isVibroPhaseOn())
				mVibrator.vibrate(state.getVibroPhaseDuration());
			if (state.isSoundPhaseOn())
				RingtoneManager.getRingtone(getApplicationContext(),
						state.getSoundPhase()).play();
			return;
		}
		if (state.getPhase() == Phase.HOLDINGIN
				&& state.getPhaseTimeElapsed() / 1000 % 60 == 0) {
			if (state.isVibroPeriodicOn())
				mVibrator.vibrate(state.getVibroPeriodicalDuration());
			if (state.isSoundPeriodicOn())
				RingtoneManager.getRingtone(getApplicationContext(),
						state.getSoundPhase()).play();
		}
	}

	/*
	 * Reading prefs and setting variables
	 */
	private TrainingServiceState readPrefs(TrainingServiceState state) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		if (prefs.getBoolean(getString(R.string.sound_on), false)) {
			state.setSoundPhaseOn(prefs.getBoolean(
					getString(R.string.sound_phase_change), false));
			state.setSoundPeriodicOn(prefs.getBoolean(
					getString(R.string.sound_periodic), false));
			state.setSoundPhase(Uri.parse(prefs.getString(
					getString(R.string.ringtone), "")));
		} else {
			state.setSoundPhaseOn(false);
			state.setSoundPeriodicOn(false);
		}
		if (prefs.getBoolean(getString(R.string.vibro_periodical), false)) {
			state.setVibroOn(prefs.getBoolean(getString(R.string.vibro_on),
					false));
			state.setVibroPhaseOn(prefs.getBoolean(
					getString(R.string.vibro_phase_change), false));
		} else {
			state.setVibroPeriodicOn(false);
			state.setVibroPhaseOn(false);
		}
		awaitAccelerometer = prefs.getBoolean(
				getString(R.string.accelerometer_on), false);
		return state;
	}

	public void resumeTraining() {
		state.setState(State.RUNNING);
		handler.post(tik);
	}

	public void setUIfragment(TrainingFragment fragment) {
		activity = fragment;
		updateUI();
	}

	public void skipPhase() {
		state.switchPhase();
		updateUI();
	}

	/*
	 * start's new training
	 */
	public void startTraining() {
		state.setState(State.RUNNING);
		if (awaitAccelerometer)
			mSensorManager.registerListener(this, mAccelerometer,
					SensorManager.SENSOR_DELAY_NORMAL);
		handler.postDelayed(tik, TrainingServiceState.TICK_TIME);
	}

	public void stopTraining() {
		Log.d(TAG, "stopTraining:" + state.getWhyStopped());
		handler.removeCallbacksAndMessages(tik);
		state.setState(State.STOPPED);
		if (awaitAccelerometer)
			mSensorManager.unregisterListener(this);
		mNotificationManager.cancel(NOTIFICATION_ID);
		if (!bound) {
			Intent notificationIntent = createIntent();
			startActivity(notificationIntent);
		}

	}

	/**
	 * Toggles autophase switching
	 */
	public void toggleAwaiting() {
		state.setAutoSwitch(!state.isAutoSwitch());
	}

	private void updateNotification() {
		Notification mNotification = new Notification(
				getIcResId(state.getPhaseTimeElapsed()), BREATH_TRAINING,
				System.currentTimeMillis());

		mNotification.flags |= Notification.FLAG_NO_CLEAR;
		mNotification.contentIntent = contentIntent;
		RemoteViews notifView = new RemoteViews(getPackageName(),
				R.layout.notification_layout);
		notifView.setImageViewResource(R.id.imageView1, R.drawable.ic_launcher);

		notifView.setTextViewText(R.id.textView1, totalToText(state));

		notifView.setTextViewText(R.id.textView2, state.elapsedTimeToText());

		if (state.getPhase() == Phase.BREATHINGOUT)
			notifView
					.setProgressBar(
							R.id.progressBar1,
							state.getTimeOfPhase(),
							state.getTimeOfPhase()
									- state.getPhaseTimeElapsed(), false);
		else
			notifView.setProgressBar(R.id.progressBar1, state.getTimeOfPhase(),
					state.getPhaseTimeElapsed(), false);
		mNotification.contentView = notifView;
		mNotificationManager.notify(NOTIFICATION_ID, mNotification);
	}

	/*
	 * Updating fragment if awailable or creating notification
	 */
	private void updateUI() {
		if (bound && activity != null) {
			activity.updateFromService(state);
		} else {
			updateNotification();
		}

	}

}