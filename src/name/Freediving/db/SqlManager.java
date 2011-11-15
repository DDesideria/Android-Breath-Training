package name.Freediving.db;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.StringTokenizer;

import name.Freediving.util.Log;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

class SqlManager extends SQLiteOpenHelper {
	private static final String TAG = "SQLiteOpenHelper";
	private static String DB_NAME = "trainingrogs.sqlite";
	private static String TABLE_PROGS = "TRAININGPROGRAM";
	private static String TABLE_PRIMITIVES = "PRIMITIVES";
	private static String[] PROGS_PROJECTION = new String[] { "ID", "NAME" };
	private static String[] PRIMINTIVES_PROJECTION = new String[] { "ID",
			"NAME", "PROGID", "PRIMTYPE", "A", "B", "C", "D", "E", "F", "G",
			"H", "COUNTS", "RESTTIME", "ORDERING" };
	private static int DB_VERSION = 1;
	private final Context myContext;

	SqlManager(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.myContext = context;
	}

	Cursor getListProgs(SQLiteDatabase db) {

		Cursor query = db.query(TABLE_PROGS, PROGS_PROJECTION, null, null,
				null, null, null);
		Log.d(TAG, "getListProgs:" + query.getCount());
		return query;
	}

	String getProgNameById(SQLiteDatabase db, long id) {
		Cursor c = db.query(TABLE_PROGS, PROGS_PROJECTION, "ID=?",
				new String[] { String.valueOf(id) }, null, null, null);
		String string = null;
		while (c.moveToNext()) {
			string = c.getString(1);
		}
		c.close();
		return string;
	}

	long addProg(SQLiteDatabase db, String progname) {
		ContentValues values = new ContentValues();
		values.put(PROGS_PROJECTION[1], progname);
		return db.insert(TABLE_PROGS, null, values);
	}

	boolean updateProgName(SQLiteDatabase db, String progname, int id) {
		ContentValues values = new ContentValues();
		values.put(PROGS_PROJECTION[1], progname);
		return db.update(TABLE_PROGS, values, "ID=?",
				new String[] { String.valueOf(id) }) == 1;
	}

	boolean deleteProg(SQLiteDatabase db, int id) {
		return db.delete(TABLE_PROGS, "ID=?",
				new String[] { String.valueOf(id) }) > 0;
	}

	Cursor getPrimsByProgID(SQLiteDatabase db, long id) {
		return db
				.query(TABLE_PRIMITIVES, PRIMINTIVES_PROJECTION, "PROGID=?",
						new String[] { String.valueOf(id) }, null, null,
						"ORDERING ASC");
	}

	long addPrimitive(SQLiteDatabase db, String[] primData, String[] projection) {
		ContentValues values = new ContentValues();
		for (int i = 0; i < primData.length; i++) {
			values.put(projection[i], primData[i]);
		}
		return db.insert(TABLE_PRIMITIVES, null, values);
	}

	boolean updatePrimitive(SQLiteDatabase db, String[] primData,
			String[] projection, long id) {
		ContentValues values = new ContentValues();
		for (int i = 0; i < primData.length; i++) {
			values.put(projection[i], primData[i]);
		}
		return db.update(TABLE_PRIMITIVES, values, "ID=?",
				new String[] { String.valueOf(id) }) == 1;
	}

	boolean deletePrimitive(SQLiteDatabase db, int id) {
		return db.delete(TABLE_PRIMITIVES, "ID=?",
				new String[] { String.valueOf(id) }) > 0;
	}

	boolean deletePrimitives(SQLiteDatabase db, int progId) {
		return db.delete(TABLE_PRIMITIVES, "PROGID=?",
				new String[] { String.valueOf(progId) }) > 0;
	}

	SqlManager(Context context, String name, CursorFactory factory, int version) {
		super(context, DB_NAME, null, DB_VERSION);
		this.myContext = context;
	}

	// The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/name.freedive/databases/";

	private SQLiteDatabase myDataBase;

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
		} else {

			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();

			copyDataBase();

		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {

			// database does't exist yet.

		}

		if (checkDB != null) {

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void openDataBase() throws SQLException {

		// Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);

	}

	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();
		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = null;
		try {
			Log.d(TAG, "onCreate:" + "exec SQL");
			InputStream stream = myContext.getAssets().open("Domain.sql");
			BufferedReader br = new BufferedReader(
					new InputStreamReader(stream));
			char[] buf = new char[3000];
			int read = br.read(buf);
			String sqls = new String(buf, 0, --read);
			StringTokenizer st = new StringTokenizer(sqls, ";", false);
			while (st.hasMoreTokens()) {
				db.execSQL(st.nextToken() + ";");
				Log.d(TAG, "onCreate:" + sql);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
