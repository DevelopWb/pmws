package com.leng.hiddencamera;

public class L {
	private static final boolean ENABLED = true;
	private static final String TAG = "leng";

	public static void d(String msg) {
		if (ENABLED) {
			android.util.Log.d(TAG, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (ENABLED) {
			android.util.Log.d(tag, msg);
		}
	}
}
