package com.leng.hiddencamera.zipthings;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.leng.hiddencamera.R;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //?????????????onReceive()???????????????
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder  builder = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle("??????")
                .setContentText("???????")
                .setAutoCancel(true);
        manager.notify(1, builder.build());

        //??ο???LongRunningService?????????????
//        Intent i = new Intent(context, LongRunningService.class);
//        context.startService(i);
    }

}