package name.freediving.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import name.freediving.R;
import name.freediving.db.TrainingFactory;
import name.freediving.ui.editprog.EditProgFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class ProgramChooserFragment extends Fragment implements OnTouchListener {
	private AlertDialog longPressDialog;
	private GestureDetector detector;
	private ListView myListView;
	private ArrayAdapter<String> mAdapter;
	private Context mContext;
	private File directory;
	private int mCurrPosition;
	private Map<String, Long> progs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		longPressDialog = new AlertDialog.Builder(getActivity()).setItems(
				new String[] { "Default", "Delete" },
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 1:
							ProgramChooserFragment.this
									.removeProg(mCurrPosition);
							break;
						case 0:
							// TODO Add defaultHandler
							break;
						}

					}
				}).create();
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		mContext = activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.program_chooser_fragment, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		myListView = (ListView) getView().findViewById(R.id.myListView);
		progs = new TrainingFactory(getActivity()).getListTrainingProgs();
		ArrayList<String> names = new ArrayList<String>(progs.keySet());
		mAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, names);
		myListView.setAdapter(mAdapter);
		myListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				editProgram(mAdapter.getItem(arg2));
			}
		});
		myListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				mCurrPosition = arg2;
				longPressDialog.show();
				return false;
			}
		});

		View btnAdd = getView().findViewById(R.id.buttonAdd);
		btnAdd.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				showNewProgDialog();
			}
		});
		super.onActivityCreated(arg0);
	}

	private void showNewProgDialog() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
		final EditText input = new EditText(mContext);
		alert.setView(input);
		alert.setTitle(getString(R.string.specify_prog_name));
		alert.setPositiveButton(getString(R.string.create),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String value = input.getText().toString().trim();
						editProgram(value);
					}
				});
		alert.setNegativeButton(getString(android.R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
		alert.show();
	}

	public boolean onTouch(View v, MotionEvent event) {
		detector.onTouchEvent(event);
		return false;
	}

	protected void editProgram(String name) {
		Fragment f = new EditProgFragment();
		Bundle bundle = new Bundle();
		bundle.putString("progName", name);
		bundle.putLong("progId", progs.containsKey(name) ? progs.get(name) : -1);
		f.setArguments(bundle);

		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.mainFrame, f);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	void removeProg(int mTargetPosition) {
		String item = mAdapter.getItem(mTargetPosition);
		File d = new File(directory, item);
		d.delete();
	}
}
