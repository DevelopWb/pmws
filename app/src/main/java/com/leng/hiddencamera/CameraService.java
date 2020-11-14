package com.leng.hiddencamera;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.StatFs;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.volumekey.CallBackInBG;
import com.example.volumekey.VolumeService;
import com.juntai.wisdom.basecomponent.utils.NotificationTool;
import com.leng.hiddencamera.onePx.OnePixelManager;
import com.leng.hiddencamera.util.SdCard;
import com.leng.hiddencamera.view.CameraPreview;
import com.leng.hiddencamera.zipthings.ZipFileService2;
import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Intent.ACTION_DELETE;

public class CameraService extends Service {
    private static final int NOTIFI_ID_SERVICE_STARTED = 100;
    public static final String EXTRA_ACTION = "extra_action";
    public static final String ACTION_START = "action_start";
    public static final String ACTION_STOP = "action_stop";
    public static final String ACTION_TAKE_RECORD = "action_take_record";
    public static final String ACTION_SWITCH_STATE = "action_switch_state";
    public static final String STOP_RECORDING = "stop_recording";
    public static final String ACTION_RECORDING = "action_recording";
    private Intent intent;
    private static final int MSG_START_RECORDING = 1;
    private static final int MSG_STOP_RECORDING = 2;
    private static final int MSG_SHOW_PREVIEW = 3;
    private static final int MSG_HIDE_PREVIEW = 4;
    private static final int MSG_RESTART_RECORDING = 5;
    private static final int MSG_SEND_MESSAGE = 10;

    private static final long LOW_STORAGE_SIZE = 2000288000;

    private WindowManager mWindowManager = null;
    private WindowManager.LayoutParams mWindowLayoutParams = null;
    private View mRootView;
    private NotificationManager mNotificationManager;
    private Camera mCamera;
    private int mCameraId;
    private MediaRecorder mMediaRecorder;
    private CameraPreview mPreview;
    private Handler mHandler;
    private Button mCaptureButton;
    private Button mQuitButton;
    private boolean mIsRecording = false;
    private int mMaxDuration = -1;
    private boolean mSDReady = false;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private SensorEventListener mSensorListener;
    private float mSensorValueX;
    private float mSensorValueY;
    private float mSensorValueZ;
    private long mShakeTS;
    private int mShakeValue;
    private String mFileDir;
    private long available_;
    private int time, time1, time2;
    private Timer timer;
    private TimerTask task;

    // Note3,Note4屏幕预览大小
    // private final int mPreviewWidth = 600;
    // private final int mPreviewHeight = 800;

    // HTC小屏幕大小
    private final int mPreviewWidth = 400;
    private final int mPreviewHeight = 500;
    private long availableInternalMemorySize;
    private SharedPreferences sp;

    private String TAG = "CameraService";

    private StopReCordingReceiver stopReCordingReceiver;
    private ValumeChangeCarme valumeTest;

    private String CAMERAID_BACK = "后置";
    private String CAMERAID_FRONT = "前置";
    private String CAMERAID_SPECIAL = "特殊前置";

    private int VolumeEmbellish = 1;
    private int initStatus = 1;
    private boolean mPreviewEnabled;
    private OnePixelManager manager;

    //    private    MyConn mConn;
    @Override
    public void onCreate() {
        super.onCreate();
//        manager = new OnePixelManager();
//        manager.registerOnePixelReceiver(this);//注册广播接收者
//        动态注册接受来自辅助服务的广播
        valumeTest = new ValumeChangeCarme();
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("asasqwe");
        registerReceiver(valumeTest, intentFilter2);
        sp = getSharedPreferences("PMWS_SET", MODE_PRIVATE);
        // 文件存储路径选择
        String mFilepath = sp.getString(SettingsUtil.PREF_KEY_FILE_PATH, "手机");
        if (mFilepath.equals("手机")) {
            mFileDir = SettingsUtil.DIR_SDCRAD1 + SettingsUtil.DIR_DATA;
            available_ = SdCard.getAvailableInternalMemorySize(CameraService.this);
        } else if (mFilepath.equals("内存卡")) {
            if (!SettingsUtil.isMounted(this, SettingsUtil.DIR_SDCRAD2)) {
                mFileDir = SettingsUtil.DIR_SDCRAD1 + SettingsUtil.DIR_DATA;
                available_ = SdCard.getAvailableInternalMemorySize(CameraService.this);
            } else {
                mFileDir = SettingsUtil.DIR_SDCRAD2 + SettingsUtil.DIR_DATA;
                available_ = SdCard.SdcardAvailable(CameraService.this, mFileDir);
            }
        }
        time = (int) (available_ / 2.03986711);
        if (time < 300) {
            Toast.makeText(getBaseContext(), "存储空间不足请及时处理", Toast.LENGTH_SHORT).show();
            stopSelf();
            return;
        }

        if (available_ < 500) {
            Toast.makeText(getBaseContext(), "存储空间不足请及时处理", Toast.LENGTH_SHORT).show();

            Pingmws_SetActivity.sIsRecording = false;
            //二次设置为
            stopSelf();
            return;
        } else {
            mSDReady = true;
        }
//        mWindowManager = ((WindowManager) getApplicationContext()
//                .getSystemService("window"));
        mWindowManager = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        loadSettings();

        // Create an instance of Camera
        mCamera = getCameraInstance();

        mHandler = new Handler() {

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
                    case MSG_RESTART_RECORDING:
                        L.d("Max duration reached, restart the recording");
                        mHandler.sendMessageDelayed(
                                mHandler.obtainMessage(MSG_STOP_RECORDING), 1000);
                        mHandler.sendMessageDelayed(
                                mHandler.obtainMessage(MSG_START_RECORDING), 2000);
                        break;
                    case MSG_SHOW_PREVIEW:
                        showPreview(true);
                        break;
                    case MSG_SEND_MESSAGE:
                        if (time < 300) {
                            Toast.makeText(CameraService.this, "存储空间不足", Toast.LENGTH_SHORT).show();

                            stopRecording();

                            releaseMediaRecorder(); // if you are using
                            // MediaRecorder,
                            // release it first
                            releaseCamera(); // release the camera immediately on
                            // pause event
                            mHandler.removeMessages(MSG_RESTART_RECORDING);
                            mHandler.removeMessages(MSG_START_RECORDING);

                            mWindowManager.removeView(mRootView);
                            mNotificationManager.cancel(NOTIFI_ID_SERVICE_STARTED);

                            Pingmws_SetActivity.sIsRecording = false;
                            stopSelf();
                        }

                        break;
                }

            }
        };
        /*
         * TimerTask task = new TimerTask() { public void run() { Message msg =
         * new Message(); msg.what = 10; mHandler.sendMessage(msg); } };
         */

        initView();
        //动态注册广播接收器
        stopReCordingReceiver = new StopReCordingReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.leng.hiddencamera.CameraService.RECEIVER");
        registerReceiver(stopReCordingReceiver, intentFilter);
        Log.i(TAG, "onCreate");
        startForeground(NOTIFI_ID_SERVICE_STARTED, NotificationTool.getNotification(this));
    }

    private void setTimerTask() {

        task = new TimerTask() {

            @Override
            public void run() {
                if (mIsRecording) {
                    time--;
                    Message msg = new Message();
                    msg.what = 10;
                    mHandler.sendMessage(msg);
                }

            }
        };
        timer.schedule(task, 1000, 1000);/* 表示1000毫秒之後，每隔1000毫秒執行一次 */
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY ;
        }
//		checkInfo();

        L.d("OnStartCommand receive intent: " + intent.toString());

        if (!mSDReady)
            return super.onStartCommand(intent, flags, startId);
        Pingmws_SetActivity.sIsRecording = true;
        Log.i(TAG, "设置完 Pingmws_SetActivity.sIsRecording的状态=" + Pingmws_SetActivity.sIsRecording);
        String action = intent.getAction();
        if (ACTION_START.equals(action)) {
            if (mIsRecording) {
                // 如果正在录制，这个action就是要停止录制
                L.d("The service has been started before, stop the recording");
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
                    L.d("The service not started and preview enabled start the preview");
                    showPreview(true);
                    // mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_START_RECORDING),
                    // 1000);
                } else {
                    // 如果没有被点击，不显示预览，开始录制
                    L.d("The service not started but preview disabled start the recording");
                    showPreview(false);
                    mHandler.sendMessageDelayed(
                            mHandler.obtainMessage(MSG_START_RECORDING), 1000);
                }
            }
        } else if (ACTION_STOP.equals(action) || ACTION_DELETE.equals(action)) {
            mHandler.removeMessages(MSG_RESTART_RECORDING);
            mHandler.removeMessages(MSG_START_RECORDING);

            stopRecording();

            releaseMediaRecorder(); // if you are using MediaRecorder,
            // release it first
            releaseCamera(); // release the camera immediately on pause event

            mWindowManager.removeView(mRootView);
            mNotificationManager.cancel(NOTIFI_ID_SERVICE_STARTED);

            Pingmws_SetActivity.sIsRecording = false;


            stopSelf();
        } else if (ACTION_RECORDING.equals(action)) {
            // 注册完成后，点击屏幕，显示preView
            if (mIsRecording) {
                mHandler.sendMessageDelayed(
                        mHandler.obtainMessage(MSG_SHOW_PREVIEW), 1000);
                // showPreview(true);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }




    private void loadSettings() {

        // 是否展示预览
        mPreviewEnabled = Hawk.get(SettingsUtil.PREF_KEY_PREVIEW,false);
        String cameraIdStr = sp.getString(SettingsUtil.PREF_KEY_CAMERAID, "");

        if (Constantx.isSpecial && cameraIdStr != null) {
            if (cameraIdStr.equals("前置")) {
                mCameraId = 1;

            } else {
                mCameraId = 0;
            }
        } else {
            if (cameraIdStr.equals("前置")) {
                Toast.makeText(this, "前置摄像", Toast.LENGTH_SHORT).show();
                mCameraId = 1;
//                Toast.makeText(this, "特殊前置摄像", Toast.LENGTH_SHORT).show();
            } else if (cameraIdStr.equals("后置")) {
                Toast.makeText(this, "后置摄像", Toast.LENGTH_SHORT).show();
                mCameraId = 0;
            } else {
                Toast.makeText(this, "特殊摄像", Toast.LENGTH_SHORT).show();
                mCameraId = 2;
            }
        }

//        // 录像时间选择  录像时间
        String maxDuration = "";
        String vedio_time = sp.getString(SettingsUtil.PREF_KEY_MAX_DURATION, "");
        if (vedio_time.equals("5分钟") || vedio_time.equals("")) {
            maxDuration = "5";
        } else if (vedio_time.equals("10分钟")) {
            maxDuration = "10";
        } else if (vedio_time.equals("30分钟")) {
            maxDuration = "30";
        }
        mMaxDuration = Integer.valueOf(maxDuration) * 60 * 1000;// * 60 *
        // 1000;表示1fen

    }


    private void initView() {
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

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) mRootView
                .findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        // 预览界面的点击事件
        mPreview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                L.d("Preview clicked, hide the preview first");
                Toast.makeText(CameraService.this, "开启录制中请勿操作", Toast.LENGTH_SHORT).show();
                showPreview(false);
                if (mIsRecording)
                    return;
                // stopSelf();


                L.d("Preview clicked, recording not started, start recording");
                // 预览界面点击后，隐藏，然后开始录制
                mPreview.postDelayed(new Runnable() {
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
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mHandler.removeMessages(MSG_RESTART_RECORDING);
                    mHandler.removeMessages(MSG_START_RECORDING);

                    if (mIsRecording) {
                        stopRecording();
                    }

                    releaseMediaRecorder(); // if you are using MediaRecorder,
                    // release it first
                    releaseCamera(); // release the camera immediately on pause
                    // event
                    mWindowManager.removeView(mRootView);
                    mNotificationManager.cancel(NOTIFI_ID_SERVICE_STARTED);

                    stopSelf();

                }
                return false;
            }
        });
        mWindowManager.addView(mRootView, mWindowLayoutParams);
    }


    private void startRecording() {

//        acquireWakeLock();

        L.d("Start recording...");
        if (timer == null) {
            timer = new Timer();
            setTimerTask();
        }
        // initialize video camera
        if (prepareVideoRecorder()) {

            // Camera is available and unlocked, MediaRecorder is
            // prepared,
            // now you can start recording
            mMediaRecorder.start();

            // inform the user that recording has started
            // mCaptureButton.setText("Stop");
            // setCaptureButtonText("Stop");
            mIsRecording = true;

            showNotification();
            Toast.makeText(this, "开启成功", Toast.LENGTH_SHORT).show();

            if (mMaxDuration > 0) {
                mHandler.sendMessageDelayed(
                        mHandler.obtainMessage(MSG_RESTART_RECORDING),
                        60*1000);
            }

        } else {
            // prepare didn't work, release the camera
            releaseMediaRecorder();

            // inform user
        }

        Pingmws_SetActivity.sIsRecording = true;

    }

    // 停止录像
    private void stopRecording() {

        if (timer != null) {

            timer.cancel();

            timer = null;
        }

        mNotificationManager.cancel(NOTIFI_ID_SERVICE_STARTED);
        // stop recording and release camera
        mMediaRecorder.stop(); // stop the recording
        releaseMediaRecorder(); // release the MediaRecorder object


        //停止录像的时候就执行加密的操作
        Intent intent = new Intent(getApplicationContext(), ZipFileService2.class);
        startService(intent);
        Log.i("QWEQWE", "ONE AGAIN");
        mIsRecording = false;
//        stopSelf();  //不要停止服务了

//        mNotificationManager.cancel(NOTIFI_ID_SERVICE_STARTED);
        Pingmws_SetActivity.sIsRecording = false;

    }

    /*
     * public static string getSdcardAvailable(){ StatFs fs = new
     * StatFs(Environment.getDataDirectory().getPath()); return
     * Formatter.formatFileSize(this,fs.getAvailableBlocks()); }
     */
    private boolean prepareVideoRecorder() {
        L.d("Prepare recording...");
        int rate = 10;
        mCamera = getCameraInstance();
        List<Integer> rates = mCamera.getParameters().getSupportedPreviewFrameRates();
        if (rates != null) {
            rate = rates.get(rates.size() - 1);
        }
        mMediaRecorder = new MediaRecorder();
        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
// 设置录制的视频编码h263 h264
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);// 音频格式
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //设置编码比特率,不设置会使视频图像模糊
        mMediaRecorder.setVideoEncodingBitRate(5 * 1080 * 1920);  //清晰
//        mediarecorder.setVideoEncodingBitRate(900*1024); //较为清晰，且文件大小为3.26M(30秒)
        // 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
        mMediaRecorder.setVideoSize(1280, 720);
        // 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
//        帧率不可以随便定义，如果系统不支持就会报错。应该先通过camera获取支持的帧率，然后再设置
        mMediaRecorder.setVideoFrameRate(rate);

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO)
                .toString());

        // Step 5: Set the preview output
        // mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

        mMediaRecorder.setOrientationHint(getRecorderPlayOrientation(this,
                mCameraId));

        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            L.d("IllegalStateException preparing MediaRecorder: "
                    + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            L.d("IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }


        return true;
    }



    private void showPreview(boolean showFlag) {
        L.d("Switch priview status: " + showFlag);

        if (showFlag && mWindowLayoutParams.width <= 1) {
            mWindowLayoutParams.width = mPreviewWidth;
            mWindowLayoutParams.height = mPreviewHeight;
            mWindowManager.updateViewLayout(mRootView, mWindowLayoutParams);
        } else if (!showFlag && mWindowLayoutParams.width != 1) {
            mWindowLayoutParams.width = 1;
            mWindowLayoutParams.height = 1;
            mWindowManager.updateViewLayout(mRootView, mWindowLayoutParams);
        }


    }

    private void showNotification() {
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification n = new NotificationCompat.Builder(this, NotificationTool.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification_start)
                .setOngoing(true)
                .setContentTitle("屏幕卫士").setContentText("屏幕卫士, 点击停止").build();
//        n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT; //把通知设置为正在运行

        Intent intent = new Intent(ACTION_STOP);
        intent.setClass(getBaseContext(), this.getClass());

        PendingIntent pi = PendingIntent.getService(getBaseContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        n.contentIntent = pi;

        PendingIntent pi_delete = PendingIntent.getService(getBaseContext(), 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        n.deleteIntent = pi_delete;

        mNotificationManager.notify(NOTIFI_ID_SERVICE_STARTED, n);
//        startForeground(NOTIFI_ID_SERVICE_STARTED, n);

//        unbindService(mConn);
    }



    /*
     * @Override protected void onPause() { super.onPause();
     * releaseMediaRecorder(); // if you are using MediaRecorder, release it //
     * first releaseCamera(); // release the camera immediately on pause event }
     */
    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset(); // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }

    public Camera getCameraInstance() {
        if (mCamera != null) {
            return mCamera;
        }

        Camera c = null;
        try {
            int cameraId = mCameraId;
            c = Camera.open(cameraId); // attempt to get a Camera instance
            c.setDisplayOrientation(getCameraDisplayOrientation(mCameraId));
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    private int getCameraDisplayOrientation(int cameraId) {
        int orientation = 0;
        if (cameraId == 0) {
            orientation = 90;
        } else if (cameraId == 1) {
            orientation = 90;
        } else if (cameraId == 2) {
            orientation = 90;
        }
        L.d("Change preview orientation, cameraId: " + cameraId
                + ", orientation: " + orientation);

        return orientation;

    }

    private int getRecorderPlayOrientation(Context activity, int cameraId) {
        int orientation = 0;
        if (cameraId == 0) {
            orientation = 90;
        } else if (cameraId == 1) {
            orientation = 270;
        } else if (cameraId == 2) {
            orientation = 90;
        }
        L.d("Change recorder orientation, cameraId: " + cameraId
                + ", orientation: " + orientation);

        return orientation;

    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;


    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(mFileDir);
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                L.d("failed to create directory");
                return null;
            }
        }

        // Create a media file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;

    }

    private long[] getSdCardTotalSize() {
        return calcSize(getSdCardPath());
    }

    private static String getSdCardPath() {
        File file = Environment.getExternalStorageDirectory();
        if (file == null || !file.exists()) {
            return null;
        }
        return file.getPath();
    }

    private long[] calcSize(String path) {
        if (externalMemoryAvailable()) {
            try {
                if (path == null) {
                    return null;
                }
                StatFs stat = new StatFs(path);
                long blockSize = stat.getBlockSize();
                long totalBlocks = stat.getBlockCount();
                long availableBlocks = stat.getAvailableBlocks();
                long totalSize = totalBlocks * blockSize;
                long availableSize = availableBlocks * blockSize;
                long[] size = new long[]{totalSize, availableSize};
                return size;
            } catch (Exception e) {
                e.printStackTrace();
                L.d("SD卡 错误信息是：" + e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 外部存储是否可用
     *
     * @return
     */
    private boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
//        releaseWakeLock();

        Log.i("CameraService", "onDestroy");
//        manager.unregisterOnePixelReceiver(this);//Activity退出时解注册
        if (mSensorListener != null)
            mSensorManager.unregisterListener(mSensorListener);

        if (timer != null) {

            timer.cancel();

            timer = null;
        }

        unregisterReceiver(stopReCordingReceiver);
        //
        unregisterReceiver(valumeTest);
        if (mIsRecording) {
            stopRecording();
        }
        stopForeground(true);
        super.onDestroy();

    }


    /**
     * 动态广播接收器
     */
    public class StopReCordingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "接收到了广播");
            stopRecording();
            releaseMediaRecorder();
            releaseCamera();
            stopSelf();
        }

    }

    /**
     * 音量+-键切换surface预览窗口
     */

    public class ValumeChangeCarme extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String qubie = intent.getStringExtra("ABC");
            if (qubie != null && qubie.equals("KEYCODE_VOLUME_DOWN")) {
                TestsBroadStop();//点击音量键停止视频录制
                //等于1是则是没有开始录制
                if (VolumeEmbellish == 1) {
                    VolumeCarmeChange();
                    Log.i("qweqwe", "KEYCODE_VOLUME_DOWN=1");
                }
                //启动另外服务开启，点击音量键
                Intent i = new Intent(getBaseContext(), MyServiceStart.class);
                startService(i);
            }
            if (qubie != null && qubie.equals("KEYCODE_VOLUME_UP")) {
                Log.i("qweqwe", "KEYCODE_VOLUME_UP=1");
                TestsBroadStop();//点击音量键停止正在录制
                //等于1是则是没有开始录制
                if (VolumeEmbellish == 1) {
                    VolumeCarmeChange();
                    Log.i("qweqwe", "KEYCODE_VOLUME_DOWN=1");
                }
                //启动另外服务开启，点击音量键
                Intent i = new Intent(getBaseContext(), MyServiceStart.class);
                startService(i);
            }
        }
    }

    /**
     * 音量键摄像头切换选择
     */
    private void VolumeCarmeChange() {

        if (mCameraId == 0) {
            SaveToSp(SettingsUtil.PREF_KEY_CAMERAID, CAMERAID_FRONT);
        }
        if (mCameraId == 1) {
            SaveToSp(SettingsUtil.PREF_KEY_CAMERAID, CAMERAID_SPECIAL);
        }
        if (mCameraId == 2) {
            SaveToSp(SettingsUtil.PREF_KEY_CAMERAID, CAMERAID_BACK);
        }
    }


    /**
     * 如果在录制停止，如果没有录制则切换摄像头
     *
     * @VolumeEmbellish 判断摄像头的选择
     * @VolumeCarmeChange 改变系统配置的摄像头
     */
    private void TestsBroadStop() {
        if (mIsRecording == false) {
            Log.i(TAG, "重新初始化");
            VolumeCarmeChange();
            VolumeEmbellish = 2;
            Log.i("VolumeEmbellish", "VolumeEmbellish=2");
            releaseMediaRecorder();
            releaseCamera();
            mWindowManager.removeViewImmediate(mRootView);
//            mWindowManager.removeView(mRootView);
            stopSelf();
        } else {
            // Toast.makeText(this, "录制已经停止", Toast.LENGTH_SHORT).show();
            // Toast.makeText(this, "录制已经停止", Toast.LENGTH_SHORT).show();
            mHandler.removeMessages(MSG_RESTART_RECORDING);
            mHandler.removeMessages(MSG_START_RECORDING);

            stopRecording();

            releaseMediaRecorder(); // if you are using MediaRecorder,
            // release it first
            releaseCamera(); // release the camera immediately on pause event

            mWindowManager.removeViewImmediate(mRootView);
//            mWindowManager.removeView(mRootView);
            mNotificationManager.cancel(NOTIFI_ID_SERVICE_STARTED);

            Pingmws_SetActivity.sIsRecording = false;
            stopSelf();
            VolumeEmbellish = 1;
        }
    }


    private void SaveToSp(String key, String value) {
        SharedPreferences.Editor et = sp.edit();
        et.putString(key, value);
        et.commit();
    }

}