package name.Freediving.ui;

import java.util.ArrayList;
import java.util.Map;

import name.Freediving.R;
import name.Freediving.db.TrainingFactory;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;

/**
 * Shows the dialog when starting new Training
 */
public class ChoseProgramFragment extends DialogFragment implements
		DialogInterface.OnClickListener {
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		progs = new TrainingFactory(getActivity()).getListTrainingProgs();
		ArrayList<String> names = new ArrayList<String>(progs.keySet());
		mAdapter = new ArrayAdapter<String>(getActivity(),
				R.layout.my_simple_list_item, names);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
				.setAdapter(mAdapter, this)
				.setNegativeButton(android.R.string.cancel, null)
				.setTitle(R.string.choose_prog);
		return builder.create();
	}

	private ArrayAdapter<String> mAdapter;
	private Map<String, Long> progs;

	public void onClick(DialogInterface dialog, int which) {
		String name = mAdapter.getItem(which);
		long id= progs.get(name);
		Intent intent = new Intent(getActivity(), TrainingActivity.class);
		intent.putExtra("newLaunch", true);
		intent.putExtra("progId", id);
		startActivity(intent);

	}
}
