package com.leng.hiddencamera.util;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.leng.hiddencamera.R;
import com.leng.hiddencamera.home.CameraRecordService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

public class DCPubic {


	public static void ShowToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	//判断当前网络是否可用
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
				Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return false;
		}
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (null != info && info.isConnected()) {
			if (info.getState() == NetworkInfo.State.CONNECTED) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param context
	 * @param filename
	 * @param key
	 * @param defaut
	 * @return 返回的是加密的注册码？
	 */
	public static String getDataFromSp(Context context, String filename,
									   String key, String defaut) {
		SharedPreferences reg = context.getSharedPreferences(filename, 0);
		String regCode = reg.getString(key, defaut);
		return regCode;
	}

	public static String getIMEI(Context context) {
		String IMEI;
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		IMEI = telephonyManager.getDeviceId();

		return IMEI;
	}

	/**
	 * 获取mac地址
	 *
	 * @return
	 */
	public static String getMacAddress() {
		String address = null;
		try {
			// 把当前机器上的访问网络接口的存入 Enumeration集合中
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface netWork = interfaces.nextElement();
				// 如果存在硬件地址并可以使用给定的当前权限访问，则返回该硬件地址（通常是 MAC）。
				byte[] by = netWork.getHardwareAddress();
				if (by == null || by.length == 0) {
					continue;
				}
				StringBuilder builder = new StringBuilder();
				for (byte b : by) {
					builder.append(String.format("%02X:", b));
				}
				if (builder.length() > 0) {
					builder.deleteCharAt(builder.length() - 1);
				}
				String mac = builder.toString();
				// 从路由器上在线设备的MAC地址列表，可以印证设备Wifi的 name 是 wlan0
				if (netWork.getName().equals("wlan0")) {
					address = mac;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return address;
	}

	/**
	 * 是否开启WiFi
	 * @param context
	 * @return
	 */
	public static boolean isWifiEnable(Context context) {
		WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			return false;
		}
		return true;
	}

	public static Dialog getProgressDialog(Context context, String str) {
		Dialog dialog = new Dialog(context, R.style.DialogStyle);
		View v = LayoutInflater.from(context).inflate(R.layout.dialog, null);
		dialog.setCancelable(false);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		lp.width = dip2px(context, 300); // 宽度
		lp.height = dip2px(context, 250); // 高度
		lp.alpha = 0.7f; // 透明度
		window.setAttributes(lp);
		window.setContentView(v);
		TextView progress_content_tv = (TextView) v.findViewById(R.id.progress_content_tv);
		progress_content_tv.setText(str);
		return dialog;
	}

	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 初始化mac
	 *
	 * @return
	 */
	public static String initMac(String mac) {
		if (!TextUtils.isEmpty(mac)) {
			if (mac.contains(":")) {
				mac = mac.replaceAll(":", "");
			}
		}

		return mac;
	}

	public static boolean isContentUriExists(Context context, Uri uri) {
		if (null == context) {
			return false;
		}
		ContentResolver cr = context.getContentResolver();
		try {
			AssetFileDescriptor afd = cr.openAssetFileDescriptor(uri, "r");
			if (null == afd) {
				return false;
			} else {
				try {
					afd.close();
				} catch (IOException e) {
				}
			}
		} catch (FileNotFoundException e) {
			return false;
		}

		return true;

	}


	/**
	 * Create a File for saving an image or video
	 */
	public static File getOutputMediaFile(String fileDir ,int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(fileDir);
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				PmwsLog.d("failed to create directory");
				return null;
			}
		}

		// Create a media file name

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		if (type == CameraRecordService.MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else if (type == CameraRecordService.MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;

	}


}