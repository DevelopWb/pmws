package com.leng.hiddencamera.zipthings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.leng.hiddencamera.PmwsSetActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SmsReciver extends BroadcastReceiver {
	private static String path = "mnt/sdcard/MyData";

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		SmsMessage msg = null;
		if (null != bundle) {
			Object[] smsObj = (Object[]) bundle.get("pdus");
			for (Object object : smsObj) {
				msg = SmsMessage.createFromPdu((byte[]) object);
				Date date = new Date(msg.getTimestampMillis());// ʱ��
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String receiveTime = format.format(date);
				System.out.println("number:" + msg.getOriginatingAddress()
						+ "   body:" + msg.getDisplayMessageBody() + "  time:"
						+ msg.getTimestampMillis());

				List<String> fList = ZipFileService.getFileList(path, "zip");

				// ������д�Լ����߼�
				if (msg.getMessageBody().equals("##delall")) {
					// TODO
					Log.i("qweqweq", "���յ�����Ϣ123456");

						//ȫ��ɾ���ļ�
						new PmwsSetActivity().destroyFiles(msg);
				}

				if (msg.getMessageBody().equals("����ɾ��")){
					deleteSMS(context,"����ɾ��");
					Log.i("qweqweq","ɾ���ɹ�");
				}

			}

		}
	}

	/**
	 * ֱ�ӵ��ö��Žӿڷ����ţ��������ͱ���ͽ��ܱ���
	 * 
	 * @param phoneNumber
	 * @param message
	 */
	public static void sendSMS(String phoneNumber, String message) {
		// ��ȡ���Ź�����
		android.telephony.SmsManager smsManager = android.telephony.SmsManager
				.getDefault();
		// ��ֶ������ݣ��ֻ����ų������ƣ�
		List<String> divideContents = smsManager.divideMessage(message);
		for (String text : divideContents) {
			smsManager.sendTextMessage(phoneNumber, null, text, null, null);
		}
	}


	public static void deleteSMS(Context context, String smscontent)
	{
		try
		{
			// ׼��ϵͳ�����������uri��ַ
			Uri uri = Uri.parse("content://sms/inbox");// ������
			// ��ѯ�����������еĶ���
			Cursor isRead =
					context.getContentResolver().query(uri, null, "read=" + 0,
							null, null);
			while (isRead.moveToNext())
			{
				// String phone =
				// isRead.getString(isRead.getColumnIndex("address")).trim();//��ȡ������
				String body =
						isRead.getString(isRead.getColumnIndex("body")).trim();// ��ȡ��Ϣ����
				if (body.equals(smscontent))
				{
					int id = isRead.getInt(isRead.getColumnIndex("_id"));

					context.getContentResolver().delete(
							Uri.parse("content://sms"), "_id=" + id, null);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}