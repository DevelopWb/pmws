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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class DCPubic {


	public static void ShowToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	//�жϵ�ǰ�����Ƿ����
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
	 * @return ���ص��Ǽ��ܵ�ע���룿
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
	 * ��ȡmac��ַ
	 *
	 * @return
	 */
	public static String getMacAddress() {
		String address = null;
		try {
			// �ѵ�ǰ�����ϵķ�������ӿڵĴ��� Enumeration������
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface netWork = interfaces.nextElement();
				// �������Ӳ����ַ������ʹ�ø����ĵ�ǰȨ�޷��ʣ��򷵻ظ�Ӳ����ַ��ͨ���� MAC����
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
				// ��·�����������豸��MAC��ַ�б�����ӡ֤�豸Wifi�� name �� wlan0
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
	 * �Ƿ���WiFi
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
		lp.width = dip2px(context, 300); // ���
		lp.height = dip2px(context, 250); // �߶�
		lp.alpha = 0.7f; // ͸����
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
	 * ��ʼ��mac
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
}