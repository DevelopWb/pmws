package com.leng.hiddencamera;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;

import com.juntai.wisdom.basecomponent.utils.NotificationTool;
import com.leng.hiddencamera.aidl.service.TestAidl;
import com.leng.hiddencamera.home.CameraRecordService;
import com.leng.hiddencamera.util.PmwsLog;
import com.leng.hiddencamera.util.ServiceUtils;

/**
 * 守护进程
 */
@SuppressWarnings(value={"unchecked", "deprecation"})
public final class RemoteService extends Service {
    private MyBilder mBilder;
    private boolean mIsBoundLocalService ;
    private boolean canRecycle = true;//是否可以守护
    private ControlServiceReceiver mControlServiceReceiver;

    /**
     * 控制服务的监听
     */
    private class ControlServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (intent.getAction().equals("ACTION_START_ALL_SERVICE")) {
                //可以开启服务了
                canRecycle = true;
            } else if (intent.getAction().equals("ACTION_STOP_ALL_SERVICE")) {
                //不用双进程守护了
                Log.d("8888888","RemoteService  接收到关闭服务的广播");

                canRecycle = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    stopForeground(0);
                }
                stopForeground(true);
                NotificationUtils.stopNotification(getApplicationContext(),13691);
                if (connection != null){
                    try {
                        if (mIsBoundLocalService){
                            unbindService(connection);

                        }
                    }catch (Exception e){}
                }
                stopSelf();
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mBilder == null){
            mBilder = new MyBilder();
        }
        //主动控制服务的监听  开启服务和关闭服务
        if (mControlServiceReceiver == null) {
            mControlServiceReceiver = new ControlServiceReceiver();
        }
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("ACTION_STOP_ALL_SERVICE");
        intentFilter3.addAction("ACTION_START_ALL_SERVICE");
        registerReceiver(mControlServiceReceiver, intentFilter3);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBilder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("8888888","RemoteService  onStartCommand");

        PmwsLog.writeLog("RemoteService  onStartCommand" );

        try {
            mIsBoundLocalService = this.bindService(new Intent(RemoteService.this, CameraRecordService.class),
                    connection, Context.BIND_ABOVE_CLIENT);
        }catch (Exception e){
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("8888888","RemoteService  onDestroy");

        unregisterReceiver(mControlServiceReceiver);
        if (connection != null){
            try {
                if (mIsBoundLocalService){
                    unbindService(connection);

                }
            }catch (Exception e){}
        }
    }
    private final class MyBilder extends TestAidl.Stub {

        @Override
        public void wakeUp(String title, String discription, int iconRes) throws RemoteException {
            PmwsLog.writeLog("收到本地服务的通信");
            if(Build.VERSION.SDK_INT < 25){
                if (canRecycle) {
                    PmwsLog.writeLog("远程服务 收到本地服务的通信 开启前台服务startForeground");
//                    Intent intent2 = new Intent(null);
//                    intent2.setAction(NotificationClickReceiver.CLICK_NOTIFICATION);
                    startForeground(CameraRecordService.NOTIFICATION_FLAG, NotificationTool.getNotification(RemoteService.this));
//                    Notification notification = NotificationUtils.createNotification(RemoteService.this, title,
//                            discription, iconRes, null);
//                    RemoteService.this.startForeground(CameraRecordService.NOTIFICATION_FLAG, notification);
                }

            }
        }

    }

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (!canRecycle) {
                return;
            }
            PowerManager pm = (PowerManager) RemoteService.this.getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = pm.isScreenOn();
            if (isScreenOn){
                sendBroadcast(new Intent("_ACTION_SCREEN_ON"));
            }else{
                sendBroadcast(new Intent("_ACTION_SCREEN_OFF"));
            }
            PmwsLog.writeLog("和本地服务断开连接  准备开启本地服务  屏幕状态"+isScreenOn);
            if (ServiceUtils.isRunningTaskExist(getApplicationContext(), getPackageName() + ":remote")){
                if (!isScreenOn) {
                    PmwsLog.writeLog("和本地服务连接   屏幕状态"+isScreenOn);
                    Intent localService = new Intent(RemoteService.this,
                            CameraRecordService.class);
                    localService.setAction(CameraRecordService.ACTION_RESTART);
                    RemoteService.this.startService(localService);
                    mIsBoundLocalService = RemoteService.this.bindService(new Intent(RemoteService.this,
                            CameraRecordService.class), connection, Context.BIND_ABOVE_CLIENT);
                }

            }

        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }
    };

}
