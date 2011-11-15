package name.freediving.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.freediving.entry.TrainingPrimitive;
import name.freediving.entry.TrainingProg;
import name.freediving.util.Log;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class TrainingFactory {
	private SqlManager sqlManager;

	public TrainingFactory(Context ctx) {
		sqlManager = new SqlManager(ctx);
	}

	private static final String[] ADDPRIMP_ROJECTION = new String[] { "NAME",
			"PROGID", "PRIMTYPE", "A", "B", "C", "D", "E", "F", "G", "H",
			"COUNTS", "RESTTIME", "ORDERING"

	};
	private static final String TAG = TrainingFactory.class.getSimpleName();

	public void saveProg(TrainingProg prog) {
		if (prog.getID() == -1)
			addProg(prog);
		else
			updateProg(prog);
	}

	private void addProg(TrainingProg prog) {
		SQLiteDatabase db = sqlManager.getWritableDatabase();
		db.beginTransaction();
		long id = sqlManager.addProg(db, prog.getName());
		List<TrainingPrimitive> primitives = prog.getPrimitives();
		if (primitives != null) {
			for (TrainingPrimitive prim : primitives) {
				prim.setProgID(id);
				String[] primData = new String[] { prim.getName(),
						String.valueOf(prim.getProgID()),
						String.valueOf(prim.getType()),
						String.valueOf(prim.getA()),
						String.valueOf(prim.getB()),
						String.valueOf(prim.getC()),
						String.valueOf(prim.getD()),
						String.valueOf(prim.getE()),
						String.valueOf(prim.getF()),
						String.valueOf(prim.getG()),
						String.valueOf(prim.getH()),
						String.valueOf(prim.getCount()),
						String.valueOf(prim.getRest()),
						String.valueOf(primitives.indexOf(prim)) };
				sqlManager.addPrimitive(db, primData, ADDPRIMP_ROJECTION);
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}

	private void updateProg(TrainingProg prog) {
		SQLiteDatabase db = sqlManager.getWritableDatabase();
		db.beginTransaction();
		if (sqlManager.updateProgName(db, prog.getName(), prog.getID())) {
			List<TrainingPrimitive> primitives = prog.getPrimitives();
			if (primitives != null) {
				for (TrainingPrimitive prim : primitives) {
					String[] primData = new String[] { prim.getName(),
							String.valueOf(prim.getProgID()),
							String.valueOf(prim.getType()),
							String.valueOf(prim.getA()),
							String.valueOf(prim.getB()),
							String.valueOf(prim.getC()),
							String.valueOf(prim.getD()),
							String.valueOf(prim.getE()),
							String.valueOf(prim.getF()),
							String.valueOf(prim.getG()),
							String.valueOf(prim.getH()),
							String.valueOf(prim.getCount()),
							String.valueOf(prim.getRest()),
							String.valueOf(primitives.indexOf(prim)) };
					if (prim.getId() == -1
							|| !sqlManager.updatePrimitive(db, primData,
									ADDPRIMP_ROJECTION, prim.getId()))
						sqlManager.addPrimitive(db, primData,
								ADDPRIMP_ROJECTION);
				}
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}

	public Map<String, Long> getListTrainingProgs() {
		SQLiteDatabase db = sqlManager.getReadableDatabase();
		Map<String, Long> progs = new HashMap<String, Long>();
		Cursor cursor = sqlManager.getListProgs(db);
		while (cursor.moveToNext()) {
			progs.put(cursor.getString(1), cursor.getLong(0));
		}
		Log.d(TAG, "getListTrainingProgs:" + progs.toString());
		cursor.close();
		db.close();
		return progs;
	}

	public TrainingProg getTrainingProg(long id)  {
		SQLiteDatabase db = sqlManager.getReadableDatabase();
		TrainingProg prog = new TrainingProg();
		String progname = sqlManager.getProgNameById(db, id);
		if (progname == null) return null;
		prog.setName(progname);
		prog.setPrimitives(getPrimitives(db,id));
		db.close();
		return prog;
	}
	private  List<TrainingPrimitive>  getPrimitives(SQLiteDatabase db, long id){
		List<TrainingPrimitive> prims = new ArrayList<TrainingPrimitive>();
		Cursor cursor = sqlManager.getPrimsByProgID(db, id);
		while (cursor.moveToNext()) {
			TrainingPrimitive prim = new TrainingPrimitive();
			prim.setProgID(id);
			prim.setId(cursor.getLong(0));
			prim.setType(cursor.getInt(3));
			prim.setA(cursor.getInt(4));
			prim.setB(cursor.getInt(5));
			prim.setC(cursor.getInt(6));
			prim.setD(cursor.getInt(7));
			prim.setE(cursor.getInt(8));
			prim.setF(cursor.getInt(9));
			prim.setG(cursor.getInt(10));
			prim.setH(cursor.getInt(11));
			prim.setCount(cursor.getInt(12));
			prim.setRest(cursor.getInt(13));
			prim.setOrdering(cursor.getInt(14));
			prim.setName(cursor.getString(1));
			prims.add(prim);
		}
		cursor.close();
		return prims;
	}
	public List<TrainingPrimitive> getPrimitives(long id) {
		SQLiteDatabase db = sqlManager.getReadableDatabase();
		List<TrainingPrimitive> prims = getPrimitives(db, id);
		db.close();
		return prims;
	}

}
