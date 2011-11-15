package name.Freediving.ui.EditProgFragment;

import java.util.ArrayList;

import name.Freediving.R;
import name.Freediving.entry.TrainingPrimitive;
import name.Freediving.entry.TrainingProg;
import name.Freediving.ui.EditPrimitiveFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class EditProgFragment extends ListFragment implements EditListener {
	@Override
	public void onPause() {
		mTrainingProg.saveProg(getActivity());
		super.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mTrainingProg = extractProg();
		super.onCreate(savedInstanceState);
	}

	private TrainingProg extractProg() {
		long progId = getArguments().getLong(("progId"));
		TrainingProg trainingProg = null;
		if (progId != -1)
			trainingProg = TrainingProg.fromDatabase(getActivity(), progId);
		if (trainingProg == null)
			trainingProg = new TrainingProg(getArguments()
					.getString("progName"));
		return trainingProg;
	}

	public TrainingProg mTrainingProg;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.dragndroplistview, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		ListView listView = getListView();

		setListAdapter(new DnDAdapter(getActivity().getApplication(),
				new int[] { R.layout.dragitem }, new int[] { R.id.TextView01 },
				(ArrayList<TrainingPrimitive>) mTrainingProg.getPrimitives()));
		if (listView instanceof DnDListView) {
			((DnDListView) listView).setDropListener(mDropListener);
			((DnDListView) listView).setRemoveListener(mRemoveListener);
			((DnDListView) listView).setDragListener(mDragListener);
			((DnDListView) listView).setEditListener(this);
		}
		View btnAdd = getView().findViewById(R.id.buttonAddPrim);
		btnAdd.setOnClickListener(addBtnOnclickListener);
		super.onActivityCreated(savedInstanceState);
	}

	public void onEdit(Integer which) {
		mTrainingProg.saveProg(getActivity());
		Bundle b = new Bundle();
		if (which != null) {
			DnDAdapter adapter = (DnDAdapter) getListAdapter();
			if (adapter instanceof DnDAdapter) {
				b.putInt("primitive", which);
			}
		} else
			b.putInt("primitive", -1);
		Fragment fragment = (Fragment) new EditPrimitiveFragment();
		fragment.setArguments(b);
		fragment.setTargetFragment(this, 0);
		getActivity().getSupportFragmentManager().beginTransaction()
				.addToBackStack("prog").replace(R.id.mainFrame, fragment)
				.commit();

	}

	private RemoveListener mRemoveListener = new RemoveListener() {
		public void onRemove(int which) {
			ListAdapter adapter = getListAdapter();
			if (adapter instanceof DnDAdapter) {
				((DnDAdapter) adapter).onRemove(which);
				getListView().invalidateViews();
			}
		}
	};
	private DragListener mDragListener = new DragListener() {

		int backgroundColor = 0xe0103010;
		int defaultBackgroundColor;

		public void onDrag(int x, int y, ListView listView) {
		}

		public void onStartDrag(View itemView) {
			itemView.setVisibility(View.INVISIBLE);
			defaultBackgroundColor = itemView.getDrawingCacheBackgroundColor();
			itemView.setBackgroundColor(backgroundColor);
			ImageView iv = (ImageView) itemView.findViewById(R.id.ImageView01);
			if (iv != null)
				iv.setVisibility(View.INVISIBLE);
		}

		public void onStopDrag(View itemView) {
			itemView.setVisibility(View.VISIBLE);
			itemView.setBackgroundColor(defaultBackgroundColor);
			ImageView iv = (ImageView) itemView.findViewById(R.id.ImageView01);
			if (iv != null)
				iv.setVisibility(View.VISIBLE);
		}

	};
	private OnClickListener addBtnOnclickListener = new OnClickListener() {

		public void onClick(View v) {
			onEdit(null);
		}
	};
	private DropListener mDropListener = new DropListener() {
		public void onDrop(int from, int to) {
			ListAdapter adapter = getListAdapter();
			if (adapter instanceof DnDAdapter) {
				((DnDAdapter) adapter).onDrop(from, to);
				getListView().invalidateViews();
			}
		}
	};
}
