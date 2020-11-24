package com.leng.hiddencamera;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.basenetlib.util.NetWorkUtil;
import com.fanjun.keeplive.KeepLive;
import com.fanjun.keeplive.config.ForegroundNotification;
import com.fanjun.keeplive.config.ForegroundNotificationClickListener;
import com.fanjun.keeplive.config.KeepLiveService;
import com.fanjun.keeplive.utils.ServiceUtils;
import com.juntai.wisdom.basecomponent.app.BaseApplication;
import com.juntai.wisdom.basecomponent.utils.NotificationTool;
import com.leng.hiddencamera.home.CameraRecordService;
import com.leng.hiddencamera.home.PmwsSetActivity;
import com.leng.hiddencamera.util.CrashHandler;
import com.leng.hiddencamera.util.PmwsLog;
import com.orhanobut.hawk.Hawk;

public class MyApp extends BaseApplication {

	@Override
	public void appBackground(boolean isBackground, Activity activity) {

	}

	@Override
	public String getTinkerId() {
		return null;
	}

	@Override
	public void onCreate() {
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		Hawk.init(this).build();
		NetWorkUtil.initContext(this);
//		NotificationTool.createNotificationChannel(this,NotificationManager.IMPORTANCE_HIGH);
		aaa(this);
		super.onCreate();
	}
	public static void aaa(Application context) {
		//����ǰ̨�����Ĭ����ʽ�������⡢������ͼ��
		final ForegroundNotification foregroundNotification =
				new ForegroundNotification(context.getString(R.string.pmws_app_name)
						, "��������",
						R.drawable.app_icon,
						//����ǰ̨�����֪ͨ����¼�
						new ForegroundNotificationClickListener() {

							@Override
							public void foregroundNotificationClick(Context context, Intent intent) {
								Log.d("8888888", "Myapp  ���͹رշ���Ĺ㲥");
								PmwsLog.writeLog("Myapp  ���͹رշ���Ĺ㲥");
								context.sendBroadcast(new Intent("ACTION_STOP_ALL_SERVICE"));
							}
						});
		//�����������
		KeepLive.startWork(context, KeepLive.RunMode.ROGUE, foregroundNotification, new KeepLiveService() {
					//����Ҫ����ķ�����socket���ӡ���ʱ����ȣ����鲻�������ڲ���ķ�ʽ������д
					@Override
					public void onWorking() {
						PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
						boolean  isOn = pm.isScreenOn();
						if (!ServiceUtils.isServiceRunning(context,"com.leng.hiddencamera.home.CameraRecordService")&&!isOn) {
							PmwsLog.writeLog("Myapp  onWorking----service down");
							Intent startIntent = new Intent(CameraRecordService.ACTION_START);
							startIntent.setClass(context, CameraRecordService.class);
							context.startService(startIntent);
						}else {
							PmwsLog.writeLog("Myapp  onWorking----service running"+PmwsSetActivity.sIsRecording+
									"��Ļ״̬"+isOn);
							if (!PmwsSetActivity.sIsRecording&&!isOn) {
								Intent startIntent = new Intent(CameraRecordService.ACTION_START);
								startIntent.setClass(context, CameraRecordService.class);
								context.startService(startIntent);
							}
						}


					}

					@Override
					public void onServiceStop() {

					}


				}

		);
	}

}
