package com.leng.hiddencamera.zipFiles.decrypted;

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
import com.leng.hiddencamera.zipFiles.AddFilesWithAESEncryption;
import com.leng.hiddencamera.zipFiles.AlertActivity;
import com.leng.hiddencamera.zipFiles.encrypte.EncryptedService;

import java.io.File;
import java.io.IOException;

public class DecryptedFileService extends IntentService {
	public static String path = "mnt/sdcard/MyData";
	private static String password = "fls94#@AB";

	/** Notification的ID */
	int notifyId = 102;
	/** Notification的进度条数值 */
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
		Log.i(TAG, "将要解密文件--"+target);

		//发送通知的代码

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				getApplicationContext());

		// 设置通知的基本信息：icon、标题、内容
		builder.setSmallIcon(R.mipmap.app_icon);
		builder.setContentTitle("指南针");
		builder.setContentText("正在解密，请稍后，解密完会自动播放");

		// 设置通知不被清除
		Notification notification = builder.build();
		notification.flags |= Notification.FLAG_NO_CLEAR;

		// 发送通知 id 需要在应用内唯一
		NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(1, notification);


		//下面是7.25日 新的 解密文件 修复文件的方法
		//更改文件名
		String newFileName=target.replace(".m9xs", ".mp4");


		try {
			AddFilesWithAESEncryption.repairFile(target, newFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "解密的时候捕捉到异常");

			//发送Toast的方法
//					handler=new Handler(Looper.getMainLooper());
//					handler.post(new Runnable(){
//						public void run(){
//							Toast.makeText(getApplicationContext(),"存储空间不足不能解密，请清理出"+EncryptedService.FormetFileSize(EncryptedService.getFileSize(target)+800*1024*1024)+"空间之后再解密查看",Toast.LENGTH_LONG).show();
//						}
//					});

			AlertActivity.MESSAGE="存储空间不足不能解密，请清理出"+ EncryptedService.FormetFileSize(EncryptedService.getFileSize(target)+800*1024*1024)+"空间之后再解密查看";

			//以dialog的方式展示一个activity
			Intent it =new Intent(getApplicationContext(),AlertActivity.class);
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(it);


			e.printStackTrace();
			stopSelf();
			return;
		}


		// 执行界压缩 解密的代码
//				AddFilesWithAESEncryption.unZipFilesWithPassword(target,
//						SettingsActivity.SAVED_VIDEO_PATH, password); // path

		// 下面是发送通知的代码了
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

		long triggerAtTime = SystemClock.elapsedRealtime();// 这个时间可以是压缩完成之后再加1秒就发送广播，然后给个通知就好
		// 此处设置开启AlarmReceiver这个Service
		Intent i2 = new Intent(getApplicationContext(),
				DecrypAlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(
				getApplicationContext(), 0, i2, 0);
//				sendBroadcast(i2);

		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				triggerAtTime, pi);

//		Log.e(TAG, "解密执行完毕");
//		playMp4File(newFileName);//二次播放

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
	 * @return 返回的是文件后缀名，原来后缀名要小些，大写根本没用，所以根本没比较到，呵呵了
	 */
	private String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();

		// 将来解压好的视频文件名，然后保存到SharedPreferences
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

		// 在Service结束后关闭AlarmManager
//		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//		Intent i = new Intent(this, AlarmReceiver.class);
//		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
//		manager.cancel(pi);

	}
}