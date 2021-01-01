package com.leng.hiddencamera;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.basenetlib.util.NetWorkUtil;
import com.juntai.wisdom.basecomponent.app.BaseApplication;
import com.leng.hiddencamera.util.CrashHandler;
import com.orhanobut.hawk.Hawk;

public class MyApp extends BaseApplication {
	public static final String CHANNEL_NAME = "通知";
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
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = getString(R.string.camera);

			int importance = NotificationManager.IMPORTANCE_HIGH;
			NotificationChannel channel = new NotificationChannel(CHANNEL_NAME, name, importance);
			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
		super.onCreate();
	}

}
