package name.Freediving.ui;

import name.Freediving.R;
import name.Freediving.entry.TrainingPrimitive;
import name.Freediving.ui.EditProgFragment.EditProgFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Fragment for editing our primitive
 * 
 * @author DD
 * 
 */
public class EditPrimitiveFragment extends Fragment implements
		OnItemSelectedListener, android.view.View.OnClickListener {

	private TrainingPrimitive mPrimitive;
	private Spinner mSpinner;
	private Button mButtonDone;
	private View view;
	private boolean editing = false;
	private EditProgFragment target;
	private int primPosition;

	/**
	 * Getting primitive from bundle or creating new
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		view = getView();
		// Извлекаем primitive
		Bundle arguments = getArguments();
		target = (EditProgFragment) getTargetFragment();
		if (arguments != null) {
			primPosition = arguments.getInt("primitive");
			if (primPosition == -1) {
				editing = false;
				mPrimitive = new TrainingPrimitive();
			} else {

				editing = true;
				mPrimitive = (TrainingPrimitive) target.mTrainingProg.getPrimitives()
						.get(primPosition);
			}
		}

		mSpinner = (Spinner) view.findViewById(R.id.primitive_type_select);
		mSpinner.setOnItemSelectedListener(this);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.edit_primitive, container, false);
	}

	@Override
	public void onStart() {
		mButtonDone = (Button) view.findViewById(R.id.buttonDone);
		mButtonDone.setOnClickListener(this);
		((EditText) view.findViewById(R.id.editA)).setText(String
				.valueOf(mPrimitive.getA() / 1000));
		((EditText) view.findViewById(R.id.editB)).setText(String
				.valueOf(mPrimitive.getB() / 1000));
		((EditText) view.findViewById(R.id.editC)).setText(String
				.valueOf(mPrimitive.getC() / 1000));
		((EditText) view.findViewById(R.id.editD)).setText(String
				.valueOf(mPrimitive.getD() / 1000));
		((EditText) view.findViewById(R.id.editE)).setText(String
				.valueOf(mPrimitive.getE() / 1000));
		((EditText) view.findViewById(R.id.editF)).setText(String
				.valueOf(mPrimitive.getF() / 1000));
		((EditText) view.findViewById(R.id.EditG)).setText(String
				.valueOf(mPrimitive.getG() / 1000));
		((EditText) view.findViewById(R.id.EditH)).setText(String
				.valueOf(mPrimitive.getH() / 1000));
		((EditText) view.findViewById(R.id.editRest)).setText(String
				.valueOf(mPrimitive.getRest() / 1000));
		((EditText) view.findViewById(R.id.EditCounts)).setText(String
				.valueOf(mPrimitive.getCount()));
		((EditText) view.findViewById(R.id.editName)).setText(mPrimitive.getName());
		mSpinner.setSelection(mPrimitive.getType());
		super.onStart();
	}

	/**
	 * Getting primitive and giving it to target fragment
	 */
	public void onClick(View v) {
		if (editing) {
			target.mTrainingProg.getPrimitives().remove(primPosition);
			target.mTrainingProg.getPrimitives().add(primPosition, getPrimitive());
			getActivity().getSupportFragmentManager().popBackStack();
		} else {
			target.mTrainingProg.getPrimitives().add(getPrimitive());
			getActivity().getSupportFragmentManager().popBackStack();
		}

	}

	/**
	 * Hardcoded primitive type values from res/values/primitive_types.xml:<br>
	 * 0- hold<br>
	 * 1- square<br>
	 * 2- rest<br>
	 * 3- custom <br>
	 */
	public void onItemSelected(AdapterView<?> arg0, View v, int position,
			long id) {
		mPrimitive.setType(position);
		switch (position) {
		case 0:
			view.findViewById(R.id.Br_in_row).setVisibility(View.GONE);
			view.findViewById(R.id.br_out_row).setVisibility(View.GONE);
			view.findViewById(R.id.delay_out_row).setVisibility(View.GONE);
			view.findViewById(R.id.hold_in_row).setVisibility(View.VISIBLE);
			view.findViewById(R.id.Rest_row).setVisibility(View.VISIBLE);
			break;
		case 1:
			view.findViewById(R.id.Br_in_row).setVisibility(View.VISIBLE);
			view.findViewById(R.id.br_out_row).setVisibility(View.GONE);
			view.findViewById(R.id.delay_out_row).setVisibility(View.GONE);
			view.findViewById(R.id.hold_in_row).setVisibility(View.GONE);
			view.findViewById(R.id.Rest_row).setVisibility(View.GONE);
			break;
		case 2:
			view.findViewById(R.id.Br_in_row).setVisibility(View.GONE);
			view.findViewById(R.id.br_out_row).setVisibility(View.GONE);
			view.findViewById(R.id.delay_out_row).setVisibility(View.GONE);
			view.findViewById(R.id.hold_in_row).setVisibility(View.GONE);
			view.findViewById(R.id.Rest_row).setVisibility(View.VISIBLE);
			break;
		case 3:
			view.findViewById(R.id.Br_in_row).setVisibility(View.VISIBLE);
			view.findViewById(R.id.br_out_row).setVisibility(View.VISIBLE);
			view.findViewById(R.id.delay_out_row).setVisibility(View.VISIBLE);
			view.findViewById(R.id.hold_in_row).setVisibility(View.VISIBLE);
			view.findViewById(R.id.Rest_row).setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	public void onNothingSelected(AdapterView<?> arg0) {
	}

	/**
	 * Simply collecting primitive data from ui's elements Hardcoded primitive
	 * type values from res/values/primitive_types.xml:<br>
	 * 0- hold<br>
	 * 1- square<br>
	 * 2- rest<br>
	 * 3- custom <br>
	 * 
	 * @return primitive, configured by user
	 */
	private TrainingPrimitive getPrimitive() {
		switch (mSpinner.getSelectedItemPosition()) {
		case (0): {
			mPrimitive.setB(0);
			mPrimitive.setA(0);
			mPrimitive.setE(0);
			mPrimitive.setF(0);
			mPrimitive.setG(0);
			mPrimitive.setH(0);
			mPrimitive.setC(Integer.valueOf(((EditText) view
					.findViewById(R.id.editC)).getText().toString()) * 1000);
			mPrimitive.setD(Integer.valueOf(((EditText) view
					.findViewById(R.id.editD)).getText().toString()) * 1000);
			mPrimitive.setRest(Integer.valueOf(((EditText) view
					.findViewById(R.id.editRest)).getText().toString()) * 1000);
			mPrimitive.setCount(Integer.valueOf(((EditText) view
					.findViewById(R.id.EditCounts)).getText().toString()));
			mPrimitive.setName(((EditText) view.findViewById(R.id.editName))
					.getText().toString());
			break;
		}
		case (1): {

			mPrimitive.setA(Integer.valueOf(((EditText) view
					.findViewById(R.id.editA)).getText().toString()) * 1000);
			mPrimitive.setB(Integer.valueOf(((EditText) view
					.findViewById(R.id.editB)).getText().toString()) * 1000);
			mPrimitive.setC(mPrimitive.getA());
			mPrimitive.setD(mPrimitive.getB());
			mPrimitive.setE(mPrimitive.getA());
			mPrimitive.setF(mPrimitive.getB());
			mPrimitive.setG(mPrimitive.getA());
			mPrimitive.setH(mPrimitive.getB());
			mPrimitive.setRest(0);
			mPrimitive.setCount(Integer.valueOf(((EditText) view
					.findViewById(R.id.EditCounts)).getText().toString()));
			mPrimitive.setName(((EditText) view.findViewById(R.id.editName))
					.getText().toString());
			break;
		}
		case (2): {
			mPrimitive.setB(0);
			mPrimitive.setA(0);
			mPrimitive.setC(0);
			mPrimitive.setD(0);
			mPrimitive.setE(0);
			mPrimitive.setF(0);
			mPrimitive.setG(0);
			mPrimitive.setH(0);
			mPrimitive.setRest(Integer.valueOf(((EditText) view
					.findViewById(R.id.editRest)).getText().toString()) * 1000);
			mPrimitive.setCount(Integer.valueOf(((EditText) view
					.findViewById(R.id.EditCounts)).getText().toString()));
			mPrimitive.setName(((EditText) view.findViewById(R.id.editName))
					.getText().toString());
			break;
		}
		case 3: {

			mPrimitive.setA(Integer.valueOf(((EditText) view
					.findViewById(R.id.editA)).getText().toString()) * 1000);
			mPrimitive.setB(Integer.valueOf(((EditText) view
					.findViewById(R.id.editB)).getText().toString()) * 1000);
			mPrimitive.setC(Integer.valueOf(((EditText) view
					.findViewById(R.id.editC)).getText().toString()) * 1000);
			mPrimitive.setD(Integer.valueOf(((EditText) view
					.findViewById(R.id.editD)).getText().toString()) * 1000);
			mPrimitive.setE(Integer.valueOf(((EditText) view
					.findViewById(R.id.editE)).getText().toString()) * 1000);
			mPrimitive.setF(Integer.valueOf(((EditText) view
					.findViewById(R.id.editF)).getText().toString()) * 1000);
			mPrimitive.setG(Integer.valueOf(((EditText) view
					.findViewById(R.id.EditG)).getText().toString()) * 1000);
			mPrimitive.setH(Integer.valueOf(((EditText) view
					.findViewById(R.id.EditH)).getText().toString()) * 1000);
			mPrimitive.setRest(Integer.valueOf(((EditText) view
					.findViewById(R.id.editRest)).getText().toString()) * 1000);
			mPrimitive.setCount(Integer.valueOf(((EditText) view
					.findViewById(R.id.EditCounts)).getText().toString()));
			mPrimitive.setName(((EditText) view.findViewById(R.id.editName))
					.getText().toString());
			break;
		}
		}
		return mPrimitive;
	}

}
