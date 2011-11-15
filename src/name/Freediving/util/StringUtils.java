package name.Freediving.util;

public class StringUtils {
	public static final String TAG = "StringUtils";

	/**
	 * elapsed time in hours/minutes/seconds
	 * 
	 * @return String
	 */
	public static String formatTime(int timeMilliseconds) {
		if (timeMilliseconds >= 0) {
			long elapsedTime = timeMilliseconds;
			String format = String.format("%%0%dd", 2);
			elapsedTime = elapsedTime / 1000;
			return String.format(format, (elapsedTime) / 60) + ":"
					+ String.format(format, elapsedTime % 60);
		} else {
			long elapsedTime = -timeMilliseconds / 1000;
			String format = String.format("%%0%dd", 2);
			return "-" + String.format(format, (elapsedTime) / 60) + ":"
					+ String.format(format, elapsedTime % 60);
		}
	}
}
