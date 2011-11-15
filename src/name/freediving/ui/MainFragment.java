package name.freediving.ui;

import name.freediving.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class MainFragment extends Fragment implements OnClickListener,
		SensorEventListener {
	

	Handler handler = new Handler();
	Runnable getDepth = new Runnable() {

		public void run() {

			handler.postDelayed(getDepth, 100);

		}
	};
	@Override
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	};

	@Override
	public View onCreateView(android.view.LayoutInflater inflater,
			android.view.ViewGroup container,
			android.os.Bundle savedInstanceState) {
		return inflater
				.inflate(R.layout.main_fragment_layout, container, false);

	};

	@Override
	public void onAttach(android.app.Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onPause() {
		mSensorManager.unregisterListener(this);
		mLayout.removeCallbacks(getDepth);
		super.onPause();
		handler.removeCallbacks(getDepth);
		defaultZ = null;
	}

	@Override
	public void onResume() {
		mSensorManager.registerListener((SensorEventListener) this,
				mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_GAME);
		depth = 0;
		handler.post(getDepth);
		super.onResume();
	}

	private DepthLinearLayout mLayout;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	Button btnHelpAbout;
	private Display display;

	@Override
	public void onActivityCreated(android.os.Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		this.getView().findViewById(R.id.editProgram).setOnClickListener(this);
		this.getView().findViewById(R.id.startProgram).setOnClickListener(this);
		btnHelpAbout = (Button) this.getView().findViewById(R.id.HelpAbout);
		btnHelpAbout.setOnClickListener(this);
		this.getView().findViewById(R.id.prefs).setOnClickListener(this);
		mLayout = (DepthLinearLayout) getView().findViewById(
				R.id.main_linear_layout);
		mLayout.getBackground().setDither(true);
		Activity activity = getActivity();
		mSensorManager = (SensorManager) activity
				.getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		display = ((WindowManager) activity
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	};

	public void onClick(View view) {
		FreediveActivity f = (FreediveActivity) getActivity();
		switch (view.getId()) {
		case R.id.editProgram:
			Log.v(this.getClass().getName(), "onClick: Starting service.");
			// TODO ������� Layout activity
			f.attachFragment("name.freediving.ui.ProgramChooserFragment");
			break;
		case R.id.startProgram:
			DialogFragment df = new ChoseProgramFragment();
			df.show(getActivity().getSupportFragmentManager(), "dialog");
			break;
		case R.id.prefs:
			Intent intent = new Intent(getActivity(), PrefsActivity.class);
			startActivity(intent);
		}

	}

	private static final float MAX_DEPTH = 1000, MIN_DEPTH = 0, MAX_SPEED = 1,
			MIN_SPEED = -1;
	private int speed, depth;
	//TODO DeepDive Effect
	double sensor;
	float[] baseR = { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, currR = { 0, 0, 0 },
			magnetic;
	Integer defaultZ = null, defaultY = null;

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			if (magnetic != null) {
				SensorManager.getRotationMatrix(baseR, null, event.values,
						magnetic);
			}
			if (baseR != null)
				SensorManager.getOrientation(baseR, currR);
		} else {
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				magnetic = event.values;
			}
		}
		int orientation = display.getOrientation();
		if (currR != null)
			switch (orientation) {
			case (1):
				mLayout.setDepth((int) (MAX_DEPTH * (1.7f + currR[2])));
				break;
			default:
				mLayout.setDepth((int) (MAX_DEPTH * (currR[1] + 1.4f)));
				break;
			}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	//Nothing
	}
}
