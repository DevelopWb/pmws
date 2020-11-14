package com.leng.hiddencamera;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class SettingsUtil {
	public static final String PREF_KEY_PASSWORD = "pref_key_password";
	public static final String PREF_KEY_PREVIEW = "pref_key_preview";
	public static final String PREF_KEY_CAMERAID = "pref_key_cameraid";
	public static final String PREF_KEY_MAX_DURATION = "pref_key_max_duration";
	public static final String PREF_KEY_SHAKE_VALUE = "pref_key_shake_value";
	public static final String PREF_KEY_FILE_PATH = "pref_file_path";
	public static final String PREF_KEY_SETPASSWORD="pref_key_setpassword";
	public static final String PREF_KEY_ZIP="pref_key_zip";  
	public static final String PREF_KEY_UNZIP="pref_key_unzip"; 
	public static final String PREF_KEY_CLEAR_HISTORY="pref_key_clear_history";
	public static final String PREF_KEY_CLEAR_APP="pref_key_clear_app";
	public static final String IMEI = "357507050011298";  

	public static final String DIR_SDCRAD1 = "/mnt/sdcard";
	public static final String DIR_SDCRAD2 = "/storage/extSdCard";
	public static final String DIR_DATA = "/MyData";

	public static void write(Context ctx, String key, String value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		sharedPreferences.edit().putString(key, value).commit();
	}

	public static void write(Context ctx, String key, int value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		sharedPreferences.edit().putInt(key, value).commit();
	}

	public static void write(Context ctx, String key, long value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		sharedPreferences.edit().putLong(key, value).commit();
	}

	public static void write(Context ctx, String key, boolean value) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		sharedPreferences.edit().putBoolean(key, value).commit();
	}

	public static boolean read(Context ctx, String key, boolean defValue) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		return sharedPreferences.getBoolean(key, defValue);
	}

	public static int read(Context ctx, String key, int defValue) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		return sharedPreferences.getInt(key, defValue);
	}

	public static String read(Context ctx, String key, String defValue) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		return sharedPreferences.getString(key, defValue);
	}

	public static long read(Context ctx, String key, long defValue) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		return sharedPreferences.getLong(key, defValue);
	}

	public static void clear(Context ctx) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		sharedPreferences.edit().clear().commit();
	}

	public static void clear(Context ctx, String key) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		sharedPreferences.edit().remove(key).commit();
	}

	public static SharedPreferences getSharedPreferences(Context ctx,
			String PREF_NAME_SETTINGS) {
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		return sharedPreferences;
	}

	public static boolean isMounted(Context context, String path) {

		if (TextUtils.isEmpty(path))
			return false;
		File file = new File(path, SettingsUtil.DIR_DATA);
		if (file.exists() || file.mkdir() || file.isDirectory())
			return true;
		return false;
	}

	public static boolean checkIMEI(Context cxt) {
		try {
			String imei = SettingsUtil.IMEI;

			String imeiStr = null;
			TelephonyManager tm = (TelephonyManager) cxt
					.getSystemService(Context.TELEPHONY_SERVICE);
			imeiStr = tm.getDeviceId();

			PmwsLog.d("Manifest_IMEI: " + imei + ", Phone_IMEI:" + imeiStr);
			if (!TextUtils.isEmpty(imei) && imei.equals(imeiStr))
				return true;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
