package name.freediving.ui.editprog;

import java.util.ArrayList;
import java.util.List;

import name.freediving.entry.TrainingPrimitive;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DnDAdapter extends BaseAdapter implements RemoveListener,
		DropListener, EditListener {

	static class ViewHolder {
		TextView text;
	}

	private int[] mIds;
	private int[] mLayouts;
	private LayoutInflater mInflater;
	private List<TrainingPrimitive> mContent=new ArrayList<TrainingPrimitive>();

	public DnDAdapter(Context context, int[] itemLayouts, int[] itemIDs,
			ArrayList<TrainingPrimitive> content) {
		init(context, itemLayouts, itemIDs, content);
	}

	public int getCount() {
		return mContent.size();
	}

	/**
	 * Since the data comes from an array, just returning the index is
	 * sufficient to get at the data. If we were using a more complex data
	 * structure, we would return whatever object represents one row in the
	 * list.
	 * 
	 * @see android.widget.ListAdapter#getItem(int)
	 */
	public TrainingPrimitive getItem(int position) {
		return mContent.get(position);
	}

	/**
	 * Use the array index as a unique id.
	 * 
	 * @see android.widget.ListAdapter#getItemId(int)
	 */
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Make a view to hold each row.
	 * 
	 * @see android.widget.ListAdapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		// A ViewHolder keeps references to children views to avoid unneccessary
		// calls
		// to findViewById() on each row.
		ViewHolder holder;

		// When convertView is not null, we can reuse it directly, there is no
		// need
		// to reinflate it. We only inflate a new View when the convertView
		// supplied
		// by ListView is null.
		if (convertView == null) {
			convertView = mInflater.inflate(mLayouts[0], null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(mIds[0]);

			convertView.setTag(holder);
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		// Bind the data efficiently with the holder.
		holder.text.setText(mContent.get(position).getName());

		return convertView;
	}

	public void onDrop(int from, int to) {
		TrainingPrimitive temp = mContent.get(from);
		mContent.remove(from);
		mContent.add(to, temp);
	}

	public void onRemove(int which) {
		if (which < 0 || which > mContent.size())
			return;
		mContent.remove(which);
	}

	public void onEdit(Integer which) {
		if (which < 0 || which > mContent.size())
			return;
	}

	private void init(Context context, int[] layouts, int[] ids,
			ArrayList<TrainingPrimitive> content) {
		// Cache the LayoutInflate to avoid asking for a new one each time.
		mInflater = LayoutInflater.from(context);
		mIds = ids;
		mLayouts = layouts;
		mContent = content;
	}
}