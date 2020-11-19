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
    private boolean isPause = true;//������ͣ
    private boolean canRecycle = true;//�Ƿ�����ػ�
    private MediaPlayer mediaPlayer;
    //    private Handler handler;
    private boolean mIsBoundRemoteService;
    private ControlServiceReceiver mControlServiceReceiver;

    /**
     * ��Ļ״̬����
     */
    private class ScreenStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (intent.getAction().equals("_ACTION_SCREEN_OFF")) {
                //��Ļ�رպ� ������������
                isPause = false;
                play();
            } else if (intent.getAction().equals("_ACTION_SCREEN_ON")) {
                //��Ļ������  �ر���������
                isPause = true;
                pause();
            }
        }
    }

    /**
     * ���Ʒ���ļ���
     */
    private class ControlServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (intent.getAction().equals("ACTION_START_ALL_SERVICE")) {
                //���Կ���������
                canRecycle = true;
            } else if (intent.getAction().equals("ACTION_STOP_ALL_SERVICE")) {
                Log.d("8888888", "LocalService  ���յ��رշ���Ĺ㲥");
                //����˫�����ػ���
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
            //������������
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
                                    PmwsLog.writeLog("CameraRecordService  �Ѿ�ֹͣ������!!!!!!!!!!!");
                                    Log.d("8888888", "CameraRecordService  �Ѿ�ֹͣ������");
                                    Intent startIntent = new Intent(CameraRecordService.ACTION_RESTART);
                                    startIntent.setClass(LocalService.this, CameraRecordService.class);
                                    startService(startIntent);
                                }else {
                                    PmwsLog.writeLog("���ֲ������ ¼�Ʒ�����������!");

                                    Log.d("8888888", "CameraRecordService  ��������");
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
        //���ر���
        if (mOnepxReceiver == null) {
            mOnepxReceiver = new OnepxReceiver();
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        registerReceiver(mOnepxReceiver, intentFilter);
        //��Ļ����״̬���������ڵ����������ֲ���
        if (screenStateReceiver == null) {
            screenStateReceiver = new ScreenStateReceiver();
        }
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("_ACTION_SCREEN_OFF");
        intentFilter2.addAction("_ACTION_SCREEN_ON");
        registerReceiver(screenStateReceiver, intentFilter2);

        //�������Ʒ���ļ���  ��������͹رշ���
        if (mControlServiceReceiver == null) {
            mControlServiceReceiver = new ControlServiceReceiver();
        }
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("ACTION_STOP_ALL_SERVICE");
        intentFilter3.addAction("ACTION_START_ALL_SERVICE");
        registerReceiver(mControlServiceReceiver, intentFilter3);


        //����ǰ̨�����������ȼ�
        if (canRecycle) {
            startForeground(CameraRecordService.NOTIFICATION_FLAG, NotificationTool.getNotification(this));

        }
        //            //���ػ�����
        //            try {
        //                Intent intent3 = new Intent(this, RemoteService.class);
        //                mIsBoundRemoteService = this.bindService(intent3, connection, Context.BIND_ABOVE_CLIENT);
        //            } catch (Exception e) {
        //            }
        //���ط���֪ͨ
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
     * ��ʼ������������
     */
    private void play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            PmwsLog.writeLog("��ʼ������������!");
            Log.d("8888888", "��ʼ������������");
            mediaPlayer.start();
        }
    }

    /**
     * ��ͣ��������
     */
    private void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            Log.d("8888888", "��ʼ��ͣ��������");
            mediaPlayer.pause();
        }
    }

    /**
     * ��ͣ��������
     */
    private void release() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            Log.d("8888888", "�ͷ�ý����Դ");
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
    //            //����Զ�̷���Ͽ����Ӻ� ������ط��񻹻���  ���°�Զ�̷���
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
        PmwsLog.writeLog("���ط���down��!");
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
