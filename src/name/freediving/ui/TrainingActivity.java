package name.freediving.ui;

import name.freediving.R;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

public class TrainingActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.training_activity);
	}
}
