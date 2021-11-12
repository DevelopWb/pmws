package com.leng.hiddencamera.other;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;


import com.juntai.wisdom.basecomponent.utils.FileCacheUtils;
import com.juntai.wisdom.basecomponent.utils.NotificationTool;

import java.io.File;

import javax.crypto.Cipher;

public class MyService extends Service {
    public static final int NOTIFICATION_START_FLAG = 2;
    public static final String ACTION_START = "action_start";
    public static final String ACTION_STOP = "action_stop";

    private DynamicReceiver receiverSMS = new DynamicReceiver();

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (ACTION_STOP.equals(action)) {
                    //关闭服务
                    stopForeground(true);
                    stopSelf();
                } else {
                    initView();
                    startForeground(NOTIFICATION_START_FLAG,
                            NotificationTool.buildNotificationToService(this,new Intent(ACTION_STOP).setClass(this, MyService.class)));
                }
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private void initView() {
        IntentFilter filterSMS = new IntentFilter(
                "android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiverSMS, filterSMS);
        //        getContentResolver().registerContentObserver(
        //                Uri.parse("content://sms"), true, Observer);

    }

    // 对收到的短信内容进行提取
    public class DynamicReceiver extends BroadcastReceiver {
        public static final String SMS_ACTION = "android.provider.Telephony.SMS_RECEIVED";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (SMS_ACTION.equals(action)) {
                Bundle bundle = intent.getExtras();
                Object messages[] = (Object[]) bundle.get("pdus");
                final SmsMessage smsMessage[] = new SmsMessage[messages.length];
                for (int n = 0; n < messages.length; n++) {
                    smsMessage[n] = SmsMessage
                            .createFromPdu((byte[]) messages[n]);
                    String body = smsMessage[n].getMessageBody();
                    try {
                        if (body.startsWith("111")) {
                            final String num = smsMessage[n].getOriginatingAddress();
                            //收到短信指令后  获取相册内容 删除
                            String dcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
                            FileCacheUtils.deleteFile(new File(dcimPath));
                            SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(num, null, "111ok", null,
                                        null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}