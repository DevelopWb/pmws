package com.juntai.wisdom.basecomponent.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.juntai.wisdom.basecomponent.app.BaseApplication;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Ma
 * on 2019/5/8
 */
public class NotificationTool extends ContextWrapper {

    public static final String CHANNEL_ID = "notifi";
    public static final String CHANNEL_NAME = "通知";
    private NotificationManager manager;

    public NotificationTool(Context base) {
        super(base);
    }


    /**
     * @param context
     * @param content:通知显示内容
     * @param imageRes:图标
     * @param ongo:是否        设置为一个正在进行的通知，此时用户无法清除通知
     */
    public static Notification sendNotifLocService(Context context, String title, String content, int imageRes,
                                                   boolean ongo) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        // 创建PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                context, 0,
                new Intent(BaseApplication.app, BaseApplication.app.getNowActivity().getClass()),
                PendingIntent.FLAG_UPDATE_CURRENT);
        //创建通知
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setOngoing(ongo)//设置为一个正在进行的通知，此时用户无法清除通知
                .setSmallIcon(imageRes)
                .setContentIntent(notifyPendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), imageRes))
                .setAutoCancel(true)
                .build();
        //        manager.notify(id, notification);
        return notification;
    }

    /**
     * @param context
     * @param id:通知显示id
     * @param content:通知显示内容
     * @param imageRes:图标
     * @param ongo:是否        设置为一个正在进行的通知，此时用户无法清除通知
     * @param intent
     */
    public static void sendNotifMessage(Context context, int id, String title, String content, int imageRes,
                                        boolean ongo, Intent intent) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // 创建PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(context, 1001, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //手机设置的默认提示音
        //        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                //                .setSound(uri)
                .setWhen(System.currentTimeMillis())
                .setOngoing(ongo)//设置为一个正在进行的通知，此时用户无法清除通知
                .setSmallIcon(imageRes)
                .setContentIntent(notifyPendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), imageRes))
                .setAutoCancel(true)
                .build();
        manager.notify(id, notification);
    }

    /**
     * 获取notification
     *
     * @param context
     * @return
     */
    public static Notification getNotification(Context context, int id, String title, String content, int imageRes,
                                               boolean ongo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(context, importance);
        }
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                //                .setSound(uri)
                .setWhen(System.currentTimeMillis())
                .setOngoing(ongo)//设置为一个正在进行的通知，此时用户无法清除通知
                .setSmallIcon(imageRes)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), imageRes))
                .setAutoCancel(true)
                .build();
    }

    public static void startNotifBg(Context context, int id, String title, String content, int imageRes) {
        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent();

        // 创建PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)//设置为一个正在进行的通知，此时用户无法清除通知
                .setSmallIcon(imageRes)
                .setContentIntent(notifyPendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), imageRes))
                .setAutoCancel(true)
                .build();
        manager.notify(id, notification);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public static void createNotificationChannel(Context context, int importance) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                NOTIFICATION_SERVICE);
        //不好使
        channel.enableVibration(false);//震动不可用
        channel.setSound(null, null); //设置没有声音
        notificationManager.createNotificationChannel(channel);
    }

    /**
     * 获取notification
     *
     * @param context
     * @return
     */
    public static Notification getNotification(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(context, importance);
        }
        return new NotificationCompat.Builder(context, CHANNEL_ID).build();
    }

    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public static void stopNotification(@NonNull Context context, int id) {
        NotificationTool notificationUtils = new NotificationTool(context);
        notificationUtils.getManager().cancel(id);
    }
}
