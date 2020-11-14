package com.leng.hiddencamera;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.basenetlib.util.NetWorkUtil;
import com.juntai.wisdom.basecomponent.app.BaseApplication;
import com.juntai.wisdom.basecomponent.utils.NotificationTool;
import com.leng.hiddencamera.util.CrashHandler;
import com.orhanobut.hawk.Hawk;

public class MyApplication extends BaseApplication {

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
		super.onCreate();
	}
}
