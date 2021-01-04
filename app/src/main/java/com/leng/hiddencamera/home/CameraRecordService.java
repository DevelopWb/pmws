package com.leng.hiddencamera.home;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.juntai.wisdom.basecomponent.utils.HawkProperty;
import com.juntai.wisdom.basecomponent.utils.NotificationTool;
import com.juntai.wisdom.basecomponent.utils.ToastUtils;
import com.leng.hiddencamera.MyApp;
import com.leng.hiddencamera.R;
import com.leng.hiddencamera.util.DCPubic;
import com.leng.hiddencamera.util.PmwsLog;
import com.orhanobut.hawk.Hawk;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * @aouther tobato
 * @description 描述  后台录像的服务
 * @date 2020/11/15 17:00
 */
public class CameraRecordService extends Service implements TextureView.SurfaceTextureListener {
    public static final int NOTIFICATION_FLAG = 1;
    public static final String ACTION_START = "action_start";
    public static final String ACTION_STOP = "action_stop";
    public static final String ACTION_RECORDING = "action_recording";
    public static final String KEYCODE_VOLUME_DOWN = "KEYCODE_VOLUME_DOWN";//音量下键 按下了
    private static final int MSG_START_RECORDING = 1;
    private static final int MSG_STOP_RECORDING = 2;
    private static final int MSG_SHOW_PREVIEW = 3;
    private static final int MSG_RESTART_RECORDING = 5;
    private static final int MSG_SEND_MESSAGE = 10;
    private static final int MSG_OPEN_CAMERA = 6;

    private WindowManager mWindowManager = null;
    private WindowManager.LayoutParams mWindowLayoutParams = null;
    private View mRootView;
    private NotificationManager mNotificationManager;
    private int mMaxDuration = -1;
    public MediaStream mMediaStream;
    // 预览屏幕的大小
    private final int mPreviewWidth = 400;
    private final int mPreviewHeight = 500;
    private String TAG = "CameraRecordService";
    private boolean mPreviewEnabled;
    private PowerManager.WakeLock wakeLock = null;
    private TextureView mTextureView;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case MSG_START_RECORDING:
                    startRecording();
                    break;
                case MSG_STOP_RECORDING:
                    stopRecording();
                    break;
                case MSG_OPEN_CAMERA:
                    addSurfaceView();
                    showPreview(false);
                    mHandler.sendMessageDelayed(
                            mHandler.obtainMessage(MSG_START_RECORDING), 2000);
                    break;
                case MSG_RESTART_RECORDING:
                    PmwsLog.d("Max duration reached, restart the recording");
                    mHandler.sendMessageDelayed(
                            mHandler.obtainMessage(MSG_STOP_RECORDING), 500);
                    mHandler.sendMessageDelayed(
                            mHandler.obtainMessage(MSG_OPEN_CAMERA), 1000);
                    break;
                case MSG_SHOW_PREVIEW:
                    showPreview(true);
                    break;
                case MSG_SEND_MESSAGE:
                    //                    if (time < 300) {
                    //                        Toast.makeText(CameraRecordService.this, "存储空间不足", Toast.LENGTH_SHORT)
                    //                        .show();
                    //
                    //                        stopRecording();
                    //                        // MediaRecorder,
                    //                        // release it first
                    //                        releaseCamera(); // release the camera immediately on
                    //                        // pause event
                    //                        mHandler.removeMessages(MSG_RESTART_RECORDING);
                    //                        mHandler.removeMessages(MSG_START_RECORDING);
                    //                        removeSurfaceView();
                    //                        mNotificationManager.cancel(NOTIFICATION_FLAG);
                    //                        DCPubic.sIsRecording = false;
                    //                        stopSelf();
                    //                    }

                    break;
            }

        }
    };


    private void removeSurfaceView() {
        mWindowManager.removeView(mRootView);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, CameraRecordService.class.getName());
        wakeLock.acquire();
        AudioManager localAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        localAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0,
                4);
        PmwsLog.writeLog("cameraservice  onCreate--------");
        loadSettings();
        addSurfaceView();
        Log.i(TAG, "onCreate");
    }


    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(CameraRecordService.NOTIFICATION_FLAG, NotificationTool.getNotification(this));
        //        backGroundNotificate();
        Log.i(TAG, "设置完 DCPubic.sIsRecording的状态=" + DCPubic.sIsRecording);
        String action = intent.getAction();
        PmwsLog.writeLog("cameraservice  onStartCommand--------");
        if (ACTION_START.equals(action)) {
            actionStartLogic();
        } else if (ACTION_STOP.equals(action)) {
            releaseRes();
            stopSelf();
        } else if (ACTION_RECORDING.equals(action)) {
            // 注册完成后，点击屏幕，显示preView
            mHandler.sendMessageDelayed(
                    mHandler.obtainMessage(MSG_SHOW_PREVIEW), 1000);

        }
        return START_NOT_STICKY;
    }

    /**
     * 服务开始的逻辑
     */
    private void actionStartLogic() {
        if (DCPubic.sIsRecording) {
            // 如果正在录制，这个action就是要停止录制
            PmwsLog.d("The service has been started before, stop the recording");
            mHandler.removeMessages(MSG_RESTART_RECORDING);
            mHandler.removeMessages(MSG_START_RECORDING);
            stopRecording();
            // 如果录制过程中，点击程序，显示预览
            if (mPreviewEnabled) {
                mHandler.sendMessageDelayed(
                        mHandler.obtainMessage(MSG_SHOW_PREVIEW), 1000);
            }
        } else {
            // 如果没有录制，程序被点击，显示预览
            if (mPreviewEnabled) {
                PmwsLog.d("The service not started and preview enabled start the preview");
                showPreview(true);
            } else {
                // 如果没有被点击，不显示预览，开始录制
                PmwsLog.d("The service not started but preview disabled start the recording");
                showPreview(false);
                mHandler.sendMessageDelayed(
                        mHandler.obtainMessage(MSG_START_RECORDING), 1000);
            }
        }
    }

    private void backGroundNotificate() {
        Notification notification = new NotificationCompat.Builder(this, MyApp.CHANNEL_NAME)
                .setContentTitle(getString(R.string.app_name))
                .build();

        startForeground(NOTIFICATION_FLAG, notification);
    }

    /**
     * 释放资源
     */
    private void releaseRes() {
        if (mHandler != null) {
            mHandler.removeMessages(MSG_RESTART_RECORDING);
            mHandler.removeMessages(MSG_START_RECORDING);
        }

        // release it first
        if (mWindowManager != null && mRootView != null) {
            removeSurfaceView();
        }
        if (mNotificationManager != null) {
            mNotificationManager.cancel(NOTIFICATION_FLAG);
        }
        stopRecording();
        DCPubic.sIsRecording = false;
    }

    /**
     * 加载配置数据
     */
    private void loadSettings() {

        // 是否展示预览
        mPreviewEnabled = 0 == Hawk.get(HawkProperty.FLOAT_IS_SHOW_INDEX, 0) ? true : false;
        //        // 录像时间选择  录像时间
        int intervalIndex = Hawk.get(HawkProperty.RECORD_INTERVAL_TIME_INDEX, 0);
        switch (intervalIndex) {
            case 0:
                //5分钟
                mMaxDuration = 5 * 60 * 1000;
                break;
            case 1:
                //10分钟
                mMaxDuration = 10 * 60 * 1000;
                break;
            case 2:
                //30分钟
                mMaxDuration = 30 * 60 * 1000;
                break;
            default:
                break;
        }
    }

    /**
     * 动态添加surfaceview
     */
    private void addSurfaceView() {
        if (mWindowManager == null) {
            mWindowManager = ((WindowManager) getApplicationContext()
                    .getSystemService(Context.WINDOW_SERVICE));
        }
        mWindowLayoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0
            mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        mWindowLayoutParams.flags = mWindowLayoutParams.flags
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowLayoutParams.gravity = Gravity.CENTER;
        mWindowLayoutParams.width = mPreviewWidth;
        mWindowLayoutParams.height = mPreviewHeight;
        mWindowLayoutParams.format = PixelFormat.RGBA_8888;

        mRootView = LayoutInflater.from(this).inflate(R.layout.activity_camera,
                null);
        mTextureView = (TextureView) mRootView
                .findViewById(R.id.sv_surfaceview);
        mTextureView.setSurfaceTextureListener(this);
        // 预览界面的点击事件
        mTextureView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PmwsLog.d("Preview clicked, hide the preview first");
                showPreview(false);
                if (DCPubic.sIsRecording) {
                    //录制过程中预览关闭
                    return;
                }
                // stopSelf();
                PmwsLog.d("Preview clicked, recording not started, start recording");
                // 预览界面点击后，隐藏，然后开始录制
                mTextureView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startRecording();
                    }
                }, 1000);

            }
        });

        mRootView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mHandler.removeMessages(MSG_RESTART_RECORDING);
                    mHandler.removeMessages(MSG_START_RECORDING);

                    if (DCPubic.sIsRecording) {
                        stopRecording();
                    }
                    // event
                    removeSurfaceView();
                    mNotificationManager.cancel(NOTIFICATION_FLAG);

                    stopSelf();

                }
                return false;
            }
        });
        mWindowManager.addView(mRootView, mWindowLayoutParams);
    }
    /*
     * 初始化MediaStream
     * */


    private void goonWithAvailableTexture(SurfaceTexture surface) {
        //        Configuration mConfiguration = getResources().getConfiguration(); //获取设置的配置信息
        //        int ori = mConfiguration.orientation; //获取屏幕方向
        //        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
        //            //横屏
        //            IS_VERTICAL_SCREEN = false;
        //        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
        //            //竖屏
        //            IS_VERTICAL_SCREEN = true;
        //        }


        final File easyPusher = new File(DCPubic.getRecordPath());
        easyPusher.mkdir();
        if (mMediaStream == null) {
            mMediaStream = new MediaStream(getApplicationContext(), surface);
            mMediaStream.setRecordPath(easyPusher.getPath());
            mMediaStream.createCamera(0);
            mMediaStream.setDisplayRotationDegree(getDisplayRotationDegree());
            mMediaStream.startPreview();
            //            mService.setMediaStream(ms);
            //            if (ms.getDisplayRotationDegree() != getDisplayRotationDegree()) {
            //                int orientation = getRequestedOrientation();
            //
            //                if (orientation == SCREEN_ORIENTATION_UNSPECIFIED || orientation == ActivityInfo
            //                .SCREEN_ORIENTATION_PORTRAIT) {
            //                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //                } else {
            //                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            //                }
            //            }
        }
        //        MediaStream ms = mService.getMediaStream();
        //
        //        if (ms != null) { // switch from background to front
        //            ms.stopPreview();
        //            mService.inActivePreview();
        //            ms.setSurfaceTexture(surface);
        //            ms.startPreview();
        //            mMediaStream = ms;
        //
        //            if (isStreaming()) {
        //                String url = Config.getServerURL();
        //                //                txtStreamAddress.setText(url);
        //
        //                //                sendMessage(getPushStatusMsg());
        //
        //                mVedioPushBottomTagIv.setImageResource(R.drawable.start_push_pressed);
        //            }
        //
        //            //            if (ms.getDisplayRotationDegree() != getDisplayRotationDegree()) {
        //            //                int orientation = getRequestedOrientation();
        //            //
        //            //                if (orientation == SCREEN_ORIENTATION_UNSPECIFIED || orientation ==
        //            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
        //            //                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //            //                } else {
        //            //                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //            //                }
        //            //            }
        //        } else {
        //
        //            boolean enableVideo = SPUtil.getEnableVideo(this);
        //
        //            ms = new MediaStream(getApplicationContext(), surface, enableVideo);
        //            ms.setRecordPath(easyPusher.getPath());
        //            mMediaStream = ms;
        //            startCamera();
        //            mService.setMediaStream(ms);
        //            if (ms.getDisplayRotationDegree() != getDisplayRotationDegree()) {
        //                int orientation = getRequestedOrientation();
        //
        //                if (orientation == SCREEN_ORIENTATION_UNSPECIFIED || orientation == ActivityInfo
        //                .SCREEN_ORIENTATION_PORTRAIT) {
        //                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //                } else {
        //                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //                }
        //            }
        //        }
    }

    // 屏幕的角度
    private int getDisplayRotationDegree() {
        int rotation = mWindowManager.getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break; // Natural orientation
            case Surface.ROTATION_90:
                degrees = 90;
                break; // Landscape left
            case Surface.ROTATION_180:
                degrees = 180;
                break;// Upside down
            case Surface.ROTATION_270:
                degrees = 270;
                break;// Landscape right
        }

        return degrees;
    }

    /**
     * 开始录像
     */
    private void startRecording() {
        if (mMediaStream != null) {
            mMediaStream.startRecord();
        }
        showNotification();
        PmwsLog.writeLog("startRecording...");
        Toast.makeText(this, "开启成功", Toast.LENGTH_SHORT).show();

        if (mMaxDuration > 0) {
            mHandler.sendMessageDelayed(
                    mHandler.obtainMessage(MSG_RESTART_RECORDING),
                    mMaxDuration);
        }
        DCPubic.sIsRecording = true;

    }

    /**
     * 停止录像
     */
    private void stopRecording() {
        if (mMediaStream != null) {
            mMediaStream.stopRecord();
        }
        PmwsLog.writeLog("stopRecording...");
        if (mNotificationManager != null) {
            mNotificationManager.cancel(NOTIFICATION_FLAG);
        }
        //        //停止录像的时候就执行加密的操作
        //        Intent intent = new Intent(getApplicationContext(), EncryptedService2.class);
        //        startService(intent);
        //        Log.i("QWEQWE", "ONE AGAIN");
        DCPubic.sIsRecording = false;

    }


    /**
     * 是否展示预览
     *
     * @param showFlag
     */
    private void showPreview(boolean showFlag) {
        PmwsLog.d("Switch priview status: " + showFlag);

        if (showFlag) {
            mWindowLayoutParams.width = mPreviewWidth;
            mWindowLayoutParams.height = mPreviewHeight;
            mWindowManager.updateViewLayout(mRootView, mWindowLayoutParams);
        } else if (!showFlag) {
            mWindowLayoutParams.width = 1;
            mWindowLayoutParams.height = 1;
            mWindowManager.updateViewLayout(mRootView, mWindowLayoutParams);
        }


    }

    /**
     * 展示notification
     */
    private void showNotification() {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        Notification notification = new NotificationCompat.Builder(this, NotificationTool.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_start)
                .setOngoing(true)
                .setContentTitle("指南针").setContentText("指南针, 点击停止").build();

        Intent intent = new Intent(ACTION_STOP);
        intent.setClass(getBaseContext(), this.getClass());

        PendingIntent pi = PendingIntent.getService(getBaseContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentIntent = pi;
        mNotificationManager.notify(NOTIFICATION_FLAG, notification);
    }


    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        PmwsLog.writeLog("CameraRecordService  down了!   onDestroy");
        Log.i("CameraRecordService", "onDestroy");
        stopForeground(true);
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
        super.onDestroy();

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        goonWithAvailableTexture(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receivedStringMsg(String msg) {
        switch (msg) {
            case UVCCameraService.UVC_ONATTACH:
                //                Toast.makeText(getApplicationContext(),"Attached",Toast.LENGTH_SHORT).show();
                break;
            case UVCCameraService.UVC_ONCONNECT:
                //                Toast.makeText(getApplicationContext(),"connect",Toast.LENGTH_SHORT).show();
                mMediaStream.switchCamera(MediaStream.CAMERA_FACING_BACK_UVC);
                break;
            case UVCCameraService.UVC_ONDISSCONNECT:
                if (DCPubic.sIsRecording) {
                    mMediaStream.stopRecord();
                }
                break;
            case KEYCODE_VOLUME_DOWN:
                //音量- 键

                break;
            default:
                break;
        }
    }
}