package name.Freediving.ui;

import name.Freediving.R;
import name.Freediving.services.TrainingService;
import name.Freediving.services.TrainingService.ImBinder;
import name.Freediving.services.TrainingServiceState;
import name.Freediving.services.TrainingServiceState.TimeFormat;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

public class TrainingFragment extends Fragment {

	public void togglePause() {
		switch (mService.getState().getState()) {
		case STOPPED:
			mService.startTraining();
			break;
		case PAUSED:
			mService.resumeTraining();
			break;
		case RUNNING:
			mService.pauseTraining();
			break;
		}
	}

	private Button startButton;
	private TextView stateView, elapsedView, elapsedPhaseView;

	boolean bound = true;
	private TrainingService mService;
	private ImBinder mBinder;

	private Context mContext;

	private ServiceConnection serviceConnection = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {
			bound = false;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			mBinder = (ImBinder) service;
			mService = mBinder.getService();
			mService.setUIfragment(TrainingFragment.this);
			bound = true;
			Log.v(this.getClass().getName(), "onServiceConnected(..)");
		}
	};
	private long progId;
	private static final String NEWLAUNCH = "newLaunch";
	protected static final String TAG = "TrainingFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		mContext = getActivity().getApplicationContext();
		Intent intent = getActivity().getIntent();
		progId = intent.getLongExtra("progId", -1);
		boolean newLaunch = intent.getBooleanExtra(NEWLAUNCH, false);
		if (newLaunch) {

			// Create service in new thread
			newTraining();
		} else
			mContext.bindService(new Intent(mContext, TrainingService.class),
					serviceConnection, Context.BIND_AUTO_CREATE);
		bound = true;
		intent.putExtra(NEWLAUNCH, false);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.training_fragment, container, false);
	}

	Button btnSkip;
	ToggleButton awaitingBtn;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// get ui controls refs
		initViews();
		super.onActivityCreated(savedInstanceState);
	}

	private void initViews() {
		startButton = (Button) getView().findViewById(R.id.PauseBtn);
		stateView = (TextView) getView().findViewById(R.id.CurrentTextView);
		startButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (bound && mService != null) {
					togglePause();
				} else
					newTraining();
			}
		});
		elapsedView = (TextView) getView().findViewById(R.id.TotalTextView);
		elapsedPhaseView = (TextView) getView().findViewById(R.id.progres_text);
		elapsedPhaseView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (bound && mService != null)
					toggleElapsedView(mService.getState());
			}
		});
		awaitingBtn = (ToggleButton) getView()
				.findViewById(R.id.BtnAwaitToggle);
		awaitingBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (bound && mService != null)
					mService.toggleAwaiting();
			}
		});
		btnSkip = (Button) getView().findViewById(R.id.skipBtn);
		btnSkip.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (bound && mService != null)
					mService.skipPhase();

			}
		});
	}

	@Override
	public void onResume() {
		// bind to service
		if (!bound) {
			mContext.bindService(new Intent(mContext, TrainingService.class),
					serviceConnection, Context.BIND_AUTO_CREATE);
			Log.v(this.getClass().getName(), "Trying onBind(..)");
		}

		super.onResume();
	}

	@Override
	public void onPause() {
		// unbind from service
		// if(mService.state==State.STOPPED) mService.stopSelf();
		if (bound) {
			mContext.unbindService(serviceConnection);
			Log.v(this.getClass().getName(), "Trying onUnBind(..)");
			bound = false;
		}
		super.onPause();
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if (!bound)
			menu.findItem(R.id.stoptraining).setEnabled(false);
		else
			menu.findItem(R.id.stoptraining).setEnabled(true);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_training_fragment, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		mService.stopTraining();
		bound = false;
		return super.onOptionsItemSelected(item);
	}

	/*
	 * This called only when starting new
	 */
	private void newTraining() {
		Context ctx = getActivity().getApplicationContext();
		Intent intent = new Intent(ctx, TrainingService.class);
		intent.putExtra("progId", progId);
		ctx.startService(intent);
		ctx.bindService(new Intent(ctx, TrainingService.class),
				serviceConnection, Context.BIND_AUTO_CREATE);
		Log.v(this.getClass().getName(),
				"Attached: onBind(..) è StartService()");
	}

	public void toggleElapsedView(TrainingServiceState state) {
		if (state == null)
			return;
		switch (state.getTimeFormat()) {
		case ELAPSED:
			state.setTimeFormat(TimeFormat.ELAPSEDREMAINING);
			break;
		case ELAPSEDREMAINING:
			state.setTimeFormat(TimeFormat.REMAINING);
			break;
		case REMAINING:
			state.setTimeFormat(TimeFormat.ELAPSED);
			break;
		}
		if (bound && mService != null)
			updateFromService(mService.getState());
	}

	public void updateFromService(final TrainingServiceState st) {

		Log.v(this.getClass().getName(), "updateFromService" + st.toString());
		getActivity().runOnUiThread(new Runnable() {

			public void run() {
				Log.d(TAG, "run:" + st.toString());
				switch (st.getState()) {
				case RUNNING:
					startButton.setText(R.string.Pause);

					String name = st.getCurrPrimitive().getName();
					String text = st.getPhase().toText(mContext);
					int currentCount = st.getCurrentCount();
					stateView.setText(name + "\n" + text
							+ getText(R.string.cycle)
							+ String.valueOf(currentCount));
					elapsedPhaseView.setText(st.elapsedTimeToText());
					elapsedView.setText(TrainingService.totalToText(st));
					awaitingBtn.setChecked(st.isAutoSwitch());
					break;
				case STOPPED:
					startButton.setText(R.string.start);
					stateView.setText(st.getWhyStopped());
					break;
				case PAUSED:
					startButton.setText(R.string.resume);
					String nme = st.getCurrPrimitive().getName();
					String txt = st.getPhase().toText(mContext);
					int currentCnt = st.getCurrentCount();
					stateView.setText(nme + "\n" + txt
							+ getText(R.string.cycle)
							+ String.valueOf(currentCnt));
					elapsedPhaseView.setText(st.elapsedTimeToText());
					elapsedView.setText(TrainingService.totalToText(st));
					awaitingBtn.setChecked(st.isAutoSwitch());
					break;
				}

			}
		});

	}

}
