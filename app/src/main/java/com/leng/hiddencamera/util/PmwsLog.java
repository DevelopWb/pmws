package com.leng.hiddencamera.util;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PmwsLog {
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
	public static void writeLog(String content) {
		//写志到SD卡
		File dir = new File(Environment.getExternalStorageDirectory(), "PMWSLog");
		if (!dir.exists()) {
			dir.mkdir();
		}

		try {

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd  HH:mm:ss ");
			Date curDate = new Date(System.currentTimeMillis());//获取当前时间
			String str = formatter.format(curDate);

			FileWriter writer = new FileWriter(dir + "/log.txt", true);
			writer.write(content + str + ";" + "\r\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();

		}
	}
	public static void writeLog(String content, File f) {
		//写志到SD卡
		File dir = new File(Environment.getExternalStorageDirectory(), "PMWSLog");
		if (!dir.exists()) {
			dir.mkdir();
		}

		try {

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd  HH:mm:ss ");
			Date curDate = new Date(System.currentTimeMillis());//获取当前时间
			String str = formatter.format(curDate);

			FileWriter writer = new FileWriter(dir + "/log.txt", true);
			writer.write(content + str + ";" + f.getName() + "\r\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();

		}
	}



}
