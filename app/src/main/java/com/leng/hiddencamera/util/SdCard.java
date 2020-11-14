package com.leng.hiddencamera.util;

import android.content.Context;
import android.os.StatFs;

import java.io.File;

public class SdCard {
	private static File mFile;
	private static String path1;
	// 获取手机存储可用空间
	public static long getAvailableInternalMemorySize(Context context) {

		 path1 = SettingsUtil.DIR_SDCRAD1 + SettingsUtil.DIR_DATA;
		 if(!fileIsExists()){
			mFile.mkdirs(); 
		 }
		StatFs stat = new StatFs(path1);
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize / 1048576;
	}
	public static boolean fileIsExists() {

		mFile = new File(path1);
		if (!mFile.exists()) {
			
			return false;

		}
		return true;
	}

	/*
	 * // 获取手机存储空间 public static long getTotalInternalMemorySize() { File path =
	 * Environment.getDataDirectory(); File path = new
	 * File(Environment.getExternalStorageDirectory() .getAbsolutePath());
	 * StatFs stat = new StatFs(path.getPath()); long blockSize =
	 * stat.getBlockSize(); long totalBlocks = stat.getBlockCount(); return
	 * totalBlocks * blockSize; }
	 */
	/*
	 * public static Long SdcardTotal(Context context) {
	 * 
	 * String path = SettingsUtil.read(context, SettingsUtil.PREF_KEY_FILE_PATH,
	 * SettingsUtil.DIR_SDCRAD1 + SettingsUtil.DIR_DATA); StatFs stat = new
	 * StatFs(path); long blockSize = stat.getBlockSize(); long totalBlocks =
	 * stat.getBlockCount(); long sdTotal = blockSize * totalBlocks ;
	 * 
	 * return sdTotal; }
	 */
	public static Long SdcardAvailable(Context context,String path) {

		StatFs stat = new StatFs(path);
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();

		return availableBlocks * blockSize / 1048576;
	}

	/*
	 * public static String getSdcardAvailable(Context context) { StatFs fs =
	 * new StatFs(Environment.getExternalStorageDirectory().getPath()); String
	 * free =Formatter.formatFileSize(context,
	 * fs.getFreeBlocks()*fs.getBlockSize());
	 * 
	 * return free;
	 * 
	 * }
	 */
}
