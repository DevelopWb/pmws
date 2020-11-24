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
		//定义前台服务的默认样式。即标题、描述和图标
		final ForegroundNotification foregroundNotification =
				new ForegroundNotification(context.getString(R.string.pmws_app_name)
						, "正在运行",
						R.drawable.app_icon,
						//定义前台服务的通知点击事件
						new ForegroundNotificationClickListener() {

							@Override
							public void foregroundNotificationClick(Context context, Intent intent) {
								Log.d("8888888", "Myapp  发送关闭服务的广播");
								PmwsLog.writeLog("Myapp  发送关闭服务的广播");
								context.sendBroadcast(new Intent("ACTION_STOP_ALL_SERVICE"));
							}
						});
		//启动保活服务
		KeepLive.startWork(context, KeepLive.RunMode.ROGUE, foregroundNotification, new KeepLiveService() {
					//你需要保活的服务，如socket连接、定时任务等，建议不用匿名内部类的方式在这里写
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
									"屏幕状态"+isOn);
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
