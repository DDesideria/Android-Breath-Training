package name.Freediving.util;


public class Log {
	public static void d(String tag, String message) {
		if (Config.DEBUG)
			android.util.Log.d(tag, message);
	}
}
