package com.leng.hiddencamera;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.juntai.wisdom.basecomponent.utils.NotificationTool;
import com.leng.hiddencamera.home.CameraRecordService;
import com.leng.hiddencamera.util.PmwsLog;
import com.leng.hiddencamera.util.ServiceUtils;


public final class LocalService extends Service {
    private OnepxReceiver mOnepxReceiver;
    private ScreenStateReceiver screenStateReceiver;
    private boolean isPause = true;//控制暂停
    private boolean canRecycle = true;//是否可以守护
    private MediaPlayer mediaPlayer;
    //    private Handler handler;
    private boolean mIsBoundRemoteService;
    private ControlServiceReceiver mControlServiceReceiver;

    /**
     * 屏幕状态监听
     */
    private class ScreenStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (intent.getAction().equals("_ACTION_SCREEN_OFF")) {
                //屏幕关闭后 开启无声音乐
                isPause = false;
                play();
            } else if (intent.getAction().equals("_ACTION_SCREEN_ON")) {
                //屏幕开启后  关闭无声音乐
                isPause = true;
                pause();
            }
        }
    }

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
                Log.d("8888888", "LocalService  接收到关闭服务的广播");
                //不用双进程守护了
                canRecycle = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    stopForeground(0);
                }
                stopForeground(true);
                NotificationTool.stopNotification(getApplicationContext(), CameraRecordService.NOTIFICATION_FLAG);
                //                if (connection != null){
                //                    try {
                //                        if (mIsBoundRemoteService){
                //                            unbindService(connection);
                //                        }
                //                    }catch (Exception e){}
                //                }
                release();
                stopSelf();

            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        isPause = pm.isScreenOn();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!canRecycle) {
            release();
            stopSelf();
        } else {
            Log.d("8888888", "LocalService  onStartCommand");
            //播放无声音乐
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, R.raw.novioce);
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(0f, 0f);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            if (!isPause) {
                                if (!ServiceUtils.isServiceRunning(getApplicationContext(), "com.leng.hiddencamera" +
                                        ".home.CameraRecordService")) {
                                    PmwsLog.writeLog("CameraRecordService  已经停止运行了!!!!!!!!!!!");
                                    Log.d("8888888", "CameraRecordService  已经停止运行了");
                                    Intent startIntent = new Intent(CameraRecordService.ACTION_RESTART);
                                    startIntent.setClass(LocalService.this, CameraRecordService.class);
                                    startService(startIntent);
                                }else {
                                    PmwsLog.writeLog("音乐播放完毕 录制服务正常运行!");

                                    Log.d("8888888", "CameraRecordService  正在运行");
                                }
                                play();
                            }
                        }
                    });
                    mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            return false;
                        }
                    });
                }
            }
        }
        //像素保活
        if (mOnepxReceiver == null) {
            mOnepxReceiver = new OnepxReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        registerReceiver(mOnepxReceiver, intentFilter);
        //屏幕点亮状态监听，用于单独控制音乐播放
        if (screenStateReceiver == null) {
            screenStateReceiver = new ScreenStateReceiver();
        }
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("_ACTION_SCREEN_OFF");
        intentFilter2.addAction("_ACTION_SCREEN_ON");
        registerReceiver(screenStateReceiver, intentFilter2);

        //主动控制服务的监听  开启服务和关闭服务
        if (mControlServiceReceiver == null) {
            mControlServiceReceiver = new ControlServiceReceiver();
        }
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("ACTION_STOP_ALL_SERVICE");
        intentFilter3.addAction("ACTION_START_ALL_SERVICE");
        registerReceiver(mControlServiceReceiver, intentFilter3);


        //启用前台服务，提升优先级
        if (canRecycle) {
            startForeground(CameraRecordService.NOTIFICATION_FLAG, NotificationTool.getNotification(this));

        }
        //            //绑定守护进程
        //            try {
        //                Intent intent3 = new Intent(this, RemoteService.class);
        //                mIsBoundRemoteService = this.bindService(intent3, connection, Context.BIND_ABOVE_CLIENT);
        //            } catch (Exception e) {
        //            }
        //隐藏服务通知
        //        try {
        //            if(Build.VERSION.SDK_INT < 25){
        //                startService(new Intent(this, HideForegroundService.class));
        //            }
        //        } catch (Exception e) {
        //        }
        //            if (KeepLive.keepLiveService != null) {
        //                KeepLive.keepLiveService.onWorking();
        //            }
        return START_STICKY;
    }

    /**
     * 开始播放无声音乐
     */
    private void play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            PmwsLog.writeLog("开始播放无声音乐!");
            Log.d("8888888", "开始播放无声音乐");
            mediaPlayer.start();
        }
    }

    /**
     * 暂停无声音乐
     */
    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            Log.d("8888888", "开始暂停无声音乐");
            mediaPlayer.pause();
        }
    }

    /**
     * 暂停无声音乐
     */
    private void release() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d("8888888", "释放媒体资源");
        }
    }


    //
    //    private ServiceConnection connection = new ServiceConnection() {
    //
    //        @Override
    //        public void onServiceDisconnected(ComponentName name) {
    //            if (!canRecycle) {
    //                return;
    //            }
    //            //当和远程服务断开连接后 如果本地服务还活着  重新绑定远程服务
    //            if (ServiceUtils.isServiceRunning(getApplicationContext(), "com.fanjun.keeplive.service
    //            .LocalService")){
    //                Intent remoteService = new Intent(LocalService.this,
    //                        RemoteService.class);
    //                LocalService.this.startService(remoteService);
    //                Intent intent = new Intent(LocalService.this, RemoteService.class);
    //                mIsBoundRemoteService = LocalService.this.bindService(intent, connection,
    //                        Context.BIND_ABOVE_CLIENT);
    //            }
    //            PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
    //            boolean isScreenOn = pm.isScreenOn();
    //            if (isScreenOn) {
    //                sendBroadcast(new Intent("_ACTION_SCREEN_ON"));
    //            } else {
    //                sendBroadcast(new Intent("_ACTION_SCREEN_OFF"));
    //            }
    //        }
    //
    //        @Override
    //        public void onServiceConnected(ComponentName name, IBinder service) {
    //            try {
    //                if (mBilder != null && KeepLive.foregroundNotification != null) {
    //                    GuardAidl guardAidl = GuardAidl.Stub.asInterface(service);
    //                    guardAidl.wakeUp(KeepLive.foregroundNotification.getTitle(), KeepLive
    //                    .foregroundNotification.getDescription(), KeepLive.foregroundNotification.getIconRes());
    //                }
    //            } catch (RemoteException e) {
    //                e.printStackTrace();
    //            }
    //        }
    //    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        PmwsLog.writeLog("本地服务down了!");
        Log.d("8888888", "LocalService  onDestroy");
        //        if (connection != null){
        //            try {
        //                if (mIsBoundRemoteService){
        //                    unbindService(connection);
        //                }
        //            }catch (Exception e){}
        //        }
        try {
            unregisterReceiver(mOnepxReceiver);
            unregisterReceiver(screenStateReceiver);
            unregisterReceiver(mControlServiceReceiver);
        } catch (Exception e) {
        }
        //        if (KeepLive.keepLiveService != null) {
        //            KeepLive.keepLiveService.onStop();
        //        }
    }
}
