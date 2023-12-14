package com.leng.hiddencamera;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.res.AssetManager;
import android.os.Build;

import com.basenetlib.util.NetWorkUtil;
import com.juntai.wisdom.basecomponent.app.BaseApplication;
import com.leng.hiddencamera.util.CrashHandler;
import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
		File youyuan = getFileStreamPath("SIMYOU.ttf");
		if (!youyuan.exists()) {
			AssetManager am = getAssets();

			try {
				InputStream is = am.open("zk/SIMYOU.ttf");
				FileOutputStream os = openFileOutput("SIMYOU.ttf", MODE_PRIVATE);
				byte[] buffer = new byte[1024];
				int len;

				while ((len = is.read(buffer)) != -1) {
					os.write(buffer, 0, len);
				}

				os.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		super.onCreate();
	}

}
