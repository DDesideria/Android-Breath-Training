package name.freediving.ui;

import name.freediving.R;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

public class FreediveActivity extends FragmentActivity {
	boolean isHoneycomb;
	boolean isPortret;

	FragmentTransaction transaction;
	
	private FragmentManager manager;

	public void attachFragment(String s) {
		Fragment f;
		try {
			f = (Fragment) Class.forName(s).newInstance();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.replace(R.id.mainFrame, f);
			transaction.addToBackStack(null);
			transaction.commit();
		} catch (Exception e) {
		}

	};
	 @Override
	  public void onAttachedToWindow() {
	    super.onAttachedToWindow();
	    Window window = getWindow();
	    // Eliminates color banding
	    window.setFormat(PixelFormat.RGBA_8888);
	  }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO ������������ � ��������������� ����������, ���������� ����� ���
		// ������������, ������������ �� �������� ��
		isHoneycomb = (android.os.Build.VERSION.SDK_INT > 11);
		if (!isHoneycomb) {
			manager = getSupportFragmentManager();
		}
		// else manager=getFragmentManager();
		isPortret = true;
		if (isPortret) {
			setContentView(R.layout.main);
		}
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(R.id.mainFrame, new MainFragment());
		transaction.commit();

	}

}
