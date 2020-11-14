package com.leng.hiddencamera.zipthings.decrypted;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.leng.hiddencamera.R;
import com.leng.hiddencamera.zipthings.AddFilesWithAESEncryption;
import com.leng.hiddencamera.zipthings.AlertActivity;
import com.leng.hiddencamera.zipthings.encrypte.EncryptedService;

import java.io.File;
import java.io.IOException;

public class DecryptedFileService extends IntentService {
	public static String path = "mnt/sdcard/MyData";
	private static String password = "fls94#@AB";

	/** Notification��ID */
	int notifyId = 102;
	/** Notification�Ľ�������ֵ */
	int progress = 0;
	NotificationCompat.Builder mBuilder;
	public NotificationManager mNotificationManager;
	Handler handler;
	private String TAG="DecryptedFileService";

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 *
	 * @param name Used to name the worker thread, important only for debugging.
	 */
	public DecryptedFileService(String name) {
		super(name);
	}
	public DecryptedFileService() {
		super("");
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		SharedPreferences targetPath = getSharedPreferences(
				"targetPath", 0);
		final String target = targetPath.getString("target", "");
		Log.i(TAG, "��Ҫ�����ļ�--"+target);

		//����֪ͨ�Ĵ���

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				getApplicationContext());

		// ����֪ͨ�Ļ�����Ϣ��icon�����⡢����
		builder.setSmallIcon(R.drawable.app_icon);
		builder.setContentTitle("��Ļ��ʿ");
		builder.setContentText("���ڽӽ��ܣ����Ժ󣬽�������Զ�����");

		// ����֪ͨ�������
		Notification notification = builder.build();
		notification.flags |= Notification.FLAG_NO_CLEAR;

		// ����֪ͨ id ��Ҫ��Ӧ����Ψһ
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, notification);


		//������7.25�� �µ� �����ļ� �޸��ļ��ķ���
		//�����ļ���
		String newFileName=target.replace(".m9xs", ".mp4");


		try {
			AddFilesWithAESEncryption.repairFile(target, newFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "���ܵ�ʱ��׽���쳣");

			//����Toast�ķ���
//					handler=new Handler(Looper.getMainLooper());
//					handler.post(new Runnable(){
//						public void run(){
//							Toast.makeText(getApplicationContext(),"�洢�ռ䲻�㲻�ܽ��ܣ��������"+EncryptedService.FormetFileSize(EncryptedService.getFileSize(target)+800*1024*1024)+"�ռ�֮���ٽ��ܲ鿴",Toast.LENGTH_LONG).show();
//						}
//					});

			AlertActivity.MESSAGE="�洢�ռ䲻�㲻�ܽ��ܣ��������"+ EncryptedService.FormetFileSize(EncryptedService.getFileSize(target)+800*1024*1024)+"�ռ�֮���ٽ��ܲ鿴";

			//��dialog�ķ�ʽչʾһ��activity
			Intent it =new Intent(getApplicationContext(),AlertActivity.class);
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(it);


			e.printStackTrace();
			stopSelf();
			return;
		}


		// ִ�н�ѹ�� ���ܵĴ���
//				AddFilesWithAESEncryption.unZipFilesWithPassword(target,
//						SettingsActivity.SAVED_VIDEO_PATH, password); // path

		// �����Ƿ���֪ͨ�Ĵ�����
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

		long triggerAtTime = SystemClock.elapsedRealtime();// ���ʱ�������ѹ�����֮���ټ�1��ͷ��͹㲥��Ȼ�����֪ͨ�ͺ�
		// �˴����ÿ���AlarmReceiver���Service
		Intent i2 = new Intent(getApplicationContext(),
				DecrypAlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(
				getApplicationContext(), 0, i2, 0);
//				sendBroadcast(i2);

		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				triggerAtTime, pi);

//		Log.e(TAG, "����ִ�����");
//		playMp4File(newFileName);//���β���
		Intent intentDialogDismiss = new Intent("DialogDismiss");
		sendBroadcast(intentDialogDismiss);
	}

	private void playMp4File(String newFileName) {
		File f = new File(newFileName);
		Intent intent_  = new Intent();
		intent_.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent_.setAction(Intent.ACTION_VIEW);
		intent_.setDataAndType(Uri.fromFile(f), getMIMEType(f));
		startActivity(intent_);
	}
	/**
	 * @param f
	 * @return ���ص����ļ���׺����ԭ����׺��ҪСЩ����д����û�ã����Ը���û�Ƚϵ����Ǻ���
	 */
	private String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();

		// ������ѹ�õ���Ƶ�ļ�����Ȼ�󱣴浽SharedPreferences
		SharedPreferences tmpFileName = getSharedPreferences("tmpFileName", 0);

		SharedPreferences.Editor editor = tmpFileName.edit();
		String newNameString = f.getAbsolutePath().replace("m9xs", "mp4");

		editor.putString("tmpFileName", newNameString);

		editor.commit();

		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else if (end.equals("m9xs")) {

			type = "m9xs";

		} else {
			type = "*";
		}

		type += "/*";
		return type;
	}
	@Override
	public void onDestroy() {
		Log.i(TAG,"DecryptedFileService is Desotrying");
		super.onDestroy();

		// ��Service������ر�AlarmManager
//		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//		Intent i = new Intent(this, AlarmReceiver.class);
//		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
//		manager.cancel(pi);

	}
}