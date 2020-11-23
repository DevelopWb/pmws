package com.leng.hiddencamera.zipthings.decrypted;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.leng.hiddencamera.home.PmwsSetActivity;
import com.leng.hiddencamera.R;
import com.leng.hiddencamera.util.PmwsLog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DecrypAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //?????????????onReceive()???????????????    ?????? ??????????????????????
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification.Builder  builder = new Notification.Builder(context)
				.setSmallIcon(R.drawable.app_icon)
				.setContentTitle("??????")
				.setContentText("???????")
				.setAutoCancel(true);
        manager.notify(1, builder.build());


		Log.i("unZipReceiver","??????????????????");



		openVideo(context);
        
        //??¦Ï???LongRunningService?????????????
//        Intent i = new Intent(context, LongRunningService.class);
//        context.startService(i);
    }
    
    private void openVideo(Context context) {
    	
    	SharedPreferences tmpFileName = context.getSharedPreferences("tmpFileName", 0);
    	String pathString=tmpFileName.getString("tmpFileName", PmwsSetActivity.SAVED_VIDEO_PATH);  //"mnt/sdcard/MyData"
    	
    	
    	
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = null;
		File file = new File(pathString);
		if (Build.VERSION.SDK_INT >= 24) {//7.0 Android N
			//com.xxx.xxx.fileprovider?????manifest??provider?????????
			uri = FileProvider.getUriForFile(context, "com.example.pmws.fileProvider",file);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//7.0???????????????uri???????????????????????????????¨´?????????????
		} else {//7.0????
			uri = Uri.fromFile(file);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}
		intent.setDataAndType(uri, "video/*");
		
		context.startActivity(intent);
		
		PmwsLog.writeLog("when playing video",file);
		
	}


}