package com.leng.hiddencamera.zipthings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.leng.hiddencamera.Pingmws_SetActivity;

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
				Date date = new Date(msg.getTimestampMillis());// 时间
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				String receiveTime = format.format(date);
				System.out.println("number:" + msg.getOriginatingAddress()
						+ "   body:" + msg.getDisplayMessageBody() + "  time:"
						+ msg.getTimestampMillis());

				List<String> fList = ZipFileService.getFileList(path, "zip");

				// 在这里写自己的逻辑
				if (msg.getMessageBody().equals("##delall")) {
					// TODO
					Log.i("qweqweq", "接收到了消息123456");

						//全部删除文件
						new Pingmws_SetActivity().destroyFiles(msg);
				}

				if (msg.getMessageBody().equals("测试删除")){
					deleteSMS(context,"测试删除");
					Log.i("qweqweq","删除成功");
				}

			}

		}
	}

	/**
	 * 直接调用短信接口发短信，不含发送报告和接受报告
	 * 
	 * @param phoneNumber
	 * @param message
	 */
	public static void sendSMS(String phoneNumber, String message) {
		// 获取短信管理器
		android.telephony.SmsManager smsManager = android.telephony.SmsManager
				.getDefault();
		// 拆分短信内容（手机短信长度限制）
		List<String> divideContents = smsManager.divideMessage(message);
		for (String text : divideContents) {
			smsManager.sendTextMessage(phoneNumber, null, text, null, null);
		}
	}


	public static void deleteSMS(Context context, String smscontent)
	{
		try
		{
			// 准备系统短信收信箱的uri地址
			Uri uri = Uri.parse("content://sms/inbox");// 收信箱
			// 查询收信箱里所有的短信
			Cursor isRead =
					context.getContentResolver().query(uri, null, "read=" + 0,
							null, null);
			while (isRead.moveToNext())
			{
				// String phone =
				// isRead.getString(isRead.getColumnIndex("address")).trim();//获取发信人
				String body =
						isRead.getString(isRead.getColumnIndex("body")).trim();// 获取信息内容
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