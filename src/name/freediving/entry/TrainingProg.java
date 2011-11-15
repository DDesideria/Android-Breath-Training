package name.freediving.entry;

import java.util.ArrayList;
import java.util.List;

import name.freediving.db.TrainingFactory;
import name.freediving.util.Log;
import android.content.Context;

/**
 * Training prog is the sequence of different TrainingPrimitive's
 * 
 * @author DD
 * 
 */
public class TrainingProg {
	@Override
	public String toString() {
		return "TrainingProg [ID=" + ID + ", name=" + name + ", primitives="
				+ primitives + ", totalTime=" + totalTime + "]";
	}

	private static final String TAG = "TrainingProg";
	private int ID = -1;
	private String name;
	private List<TrainingPrimitive> primitives=new ArrayList<TrainingPrimitive>();
	private int totalTime;

	public TrainingProg() {
		name = "default";
	}

	public TrainingProg(String string) {
		name = string;
	}

	public int getID() {
		return ID;
	}

	public String getName() {
		return name;
	}

	public List<TrainingPrimitive> getPrimitives() {
		return primitives;
	}

	public int getTotalTime() {
		return totalTime;
	}

	/*************************************************************
	 * Saving current program to pref file May use internal storage or SQL
	 * 
	 */
	public void saveProg(Context context) {
		Log.d(TAG, "saveProg:" + this.getName());
		new TrainingFactory(context).saveProg(this);
	}

	public static TrainingProg fromDatabase(Context context, long id) {
		return new TrainingFactory(context).getTrainingProg(id);
	}

	public void setID(int iD) {
		ID = iD;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPrimitives(List<TrainingPrimitive> primitives) {
		this.primitives = primitives;
	}

	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}

}
