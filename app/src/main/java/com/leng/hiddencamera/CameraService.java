package com.leng.hiddencamera;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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

import com.juntai.wisdom.basecomponent.utils.NotificationTool;
import com.leng.hiddencamera.util.SdCard;
import com.leng.hiddencamera.util.SettingsUtil;
import com.leng.hiddencamera.view.CameraPreview;
import com.leng.hiddencamera.zipthings.encrypte.EncryptedService2;
import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
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

    // Note3,Note4��ĻԤ����С
    // private final int mPreviewWidth = 600;
    // private final int mPreviewHeight = 800;

    // HTCС��Ļ��С
    private final int mPreviewWidth = 400;
    private final int mPreviewHeight = 500;
    private long availableInternalMemorySize;
    private SharedPreferences sp;

    private String TAG = "CameraService";

    private StopReCordingReceiver stopReCordingReceiver;
    private ValumeChangeCarme valumeTest;

    private String CAMERAID_BACK = "����";
    private String CAMERAID_FRONT = "ǰ��";
    private String CAMERAID_SPECIAL = "����ǰ��";

    private int VolumeEmbellish = 1;
    private int initStatus = 1;
    private boolean mPreviewEnabled;

    //    private    MyConn mConn;
    @Override
    public void onCreate() {
        super.onCreate();
//        manager = new OnePixelManager();
//        manager.registerOnePixelReceiver(this);//ע��㲥������
//        ��̬ע��������Ը�������Ĺ㲥
        valumeTest = new ValumeChangeCarme();
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("asasqwe");
        registerReceiver(valumeTest, intentFilter2);
        sp = getSharedPreferences("PMWS_SET", MODE_PRIVATE);
        // �ļ��洢·��ѡ��
        String mFilepath = sp.getString(SettingsUtil.PREF_KEY_FILE_PATH, "�ֻ�");
        if (mFilepath.equals("�ֻ�")) {
            mFileDir = SettingsUtil.DIR_SDCRAD1 + SettingsUtil.DIR_DATA;
            available_ = SdCard.getAvailableInternalMemorySize(CameraService.this);
        } else if (mFilepath.equals("�ڴ濨")) {
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
            Toast.makeText(getBaseContext(), "�洢�ռ䲻���뼰ʱ����", Toast.LENGTH_SHORT).show();
            stopSelf();
            return;
        }

        if (available_ < 500) {
            Toast.makeText(getBaseContext(), "�洢�ռ䲻���뼰ʱ����", Toast.LENGTH_SHORT).show();

            PmwsSetActivity.sIsRecording = false;
            //��������Ϊ
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
                        PmwsLog.d("Max duration reached, restart the recording");
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
                            Toast.makeText(CameraService.this, "�洢�ռ䲻��", Toast.LENGTH_SHORT).show();

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

                            PmwsSetActivity.sIsRecording = false;
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
        //��̬ע��㲥������
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
        timer.schedule(task, 1000, 1000);/* ��ʾ1000����֮�ᣬÿ��1000�������һ�� */
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY ;
        }
//		checkInfo();

        PmwsLog.d("OnStartCommand receive intent: " + intent.toString());

        if (!mSDReady)
            return super.onStartCommand(intent, flags, startId);
        PmwsSetActivity.sIsRecording = true;
        Log.i(TAG, "������ PmwsSetActivity.sIsRecording��״̬=" + PmwsSetActivity.sIsRecording);
        String action = intent.getAction();
        if (ACTION_START.equals(action)) {
            if (mIsRecording) {
                // �������¼�ƣ����action����Ҫֹͣ¼��
                PmwsLog.d("The service has been started before, stop the recording");
                mHandler.removeMessages(MSG_RESTART_RECORDING);
                mHandler.removeMessages(MSG_START_RECORDING);

                stopRecording();
                // ���¼�ƹ����У����������ʾԤ��
                if (mPreviewEnabled) {
                    mHandler.sendMessageDelayed(
                            mHandler.obtainMessage(MSG_SHOW_PREVIEW), 1000);
                }
            } else {
                // ���û��¼�ƣ����򱻵������ʾԤ��
                if (mPreviewEnabled) {
                    PmwsLog.d("The service not started and preview enabled start the preview");
                    showPreview(true);
                    // mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_START_RECORDING),
                    // 1000);
                } else {
                    // ���û�б����������ʾԤ������ʼ¼��
                    PmwsLog.d("The service not started but preview disabled start the recording");
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

            PmwsSetActivity.sIsRecording = false;


            stopSelf();
        } else if (ACTION_RECORDING.equals(action)) {
            // ע����ɺ󣬵����Ļ����ʾpreView
            if (mIsRecording) {
                mHandler.sendMessageDelayed(
                        mHandler.obtainMessage(MSG_SHOW_PREVIEW), 1000);
                // showPreview(true);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }




    private void loadSettings() {

        // �Ƿ�չʾԤ��
        mPreviewEnabled = Hawk.get(SettingsUtil.PREF_KEY_PREVIEW,false);
        String cameraIdStr = sp.getString(SettingsUtil.PREF_KEY_CAMERAID, "");

        if (cameraIdStr != null) {
            if (cameraIdStr.equals("ǰ��")) {
                mCameraId = 1;

            } else {
                mCameraId = 0;
            }
        } else {
            if (cameraIdStr.equals("ǰ��")) {
                Toast.makeText(this, "ǰ������", Toast.LENGTH_SHORT).show();
                mCameraId = 1;
//                Toast.makeText(this, "����ǰ������", Toast.LENGTH_SHORT).show();
            } else if (cameraIdStr.equals("����")) {
                Toast.makeText(this, "��������", Toast.LENGTH_SHORT).show();
                mCameraId = 0;
            } else {
                Toast.makeText(this, "��������", Toast.LENGTH_SHORT).show();
                mCameraId = 2;
            }
        }

//        // ¼��ʱ��ѡ��  ¼��ʱ��
        String maxDuration = "";
        String vedio_time = sp.getString(SettingsUtil.PREF_KEY_MAX_DURATION, "");
        if (vedio_time.equals("5����") || vedio_time.equals("")) {
            maxDuration = "5";
        } else if (vedio_time.equals("10����")) {
            maxDuration = "10";
        } else if (vedio_time.equals("30����")) {
            maxDuration = "30";
        }
        mMaxDuration = Integer.valueOf(maxDuration) * 60 * 1000;// * 60 *
        // 1000;��ʾ1fen

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
        // Ԥ������ĵ���¼�
        mPreview.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PmwsLog.d("Preview clicked, hide the preview first");
                Toast.makeText(CameraService.this, "����¼�����������", Toast.LENGTH_SHORT).show();
                showPreview(false);
                if (mIsRecording)
                    return;
                // stopSelf();


                PmwsLog.d("Preview clicked, recording not started, start recording");
                // Ԥ�������������أ�Ȼ��ʼ¼��
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

        PmwsLog.d("Start recording...");
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
            Toast.makeText(this, "�����ɹ�", Toast.LENGTH_SHORT).show();

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

        PmwsSetActivity.sIsRecording = true;

    }

    // ֹͣ¼��
    private void stopRecording() {

        if (timer != null) {

            timer.cancel();

            timer = null;
        }

        mNotificationManager.cancel(NOTIFI_ID_SERVICE_STARTED);
        // stop recording and release camera
        mMediaRecorder.stop(); // stop the recording
        releaseMediaRecorder(); // release the MediaRecorder object


        //ֹͣ¼���ʱ���ִ�м��ܵĲ���
        Intent intent = new Intent(getApplicationContext(), EncryptedService2.class);
        startService(intent);
        Log.i("QWEQWE", "ONE AGAIN");
        mIsRecording = false;
//        stopSelf();  //��Ҫֹͣ������

//        mNotificationManager.cancel(NOTIFI_ID_SERVICE_STARTED);
        PmwsSetActivity.sIsRecording = false;

    }

    /*
     * public static string getSdcardAvailable(){ StatFs fs = new
     * StatFs(Environment.getDataDirectory().getPath()); return
     * Formatter.formatFileSize(this,fs.getAvailableBlocks()); }
     */
    private boolean prepareVideoRecorder() {
        PmwsLog.d("Prepare recording...");
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

// ����¼����ɺ���Ƶ�ķ�װ��ʽTHREE_GPPΪ3gp.MPEG_4Ϊmp4
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
// ����¼�Ƶ���Ƶ����h263 h264
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);// ��Ƶ��ʽ
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //���ñ��������,�����û�ʹ��Ƶͼ��ģ��
        mMediaRecorder.setVideoEncodingBitRate(5 * 1080 * 1920);  //����
//        mediarecorder.setVideoEncodingBitRate(900*1024); //��Ϊ���������ļ���СΪ3.26M(30��)
        // ������Ƶ¼�Ƶķֱ��ʡ�����������ñ���͸�ʽ�ĺ��棬���򱨴�
        mMediaRecorder.setVideoSize(1280, 720);
        // ����¼�Ƶ���Ƶ֡�ʡ�����������ñ���͸�ʽ�ĺ��棬���򱨴�
//        ֡�ʲ�������㶨�壬���ϵͳ��֧�־ͻᱨ��Ӧ����ͨ��camera��ȡ֧�ֵ�֡�ʣ�Ȼ��������
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
            PmwsLog.d("IllegalStateException preparing MediaRecorder: "
                    + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            PmwsLog.d("IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }


        return true;
    }



    private void showPreview(boolean showFlag) {
        PmwsLog.d("Switch priview status: " + showFlag);

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
                .setContentTitle("��Ļ��ʿ").setContentText("��Ļ��ʿ, ���ֹͣ").build();
//        n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT; //��֪ͨ����Ϊ��������

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
        PmwsLog.d("Change preview orientation, cameraId: " + cameraId
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
        PmwsLog.d("Change recorder orientation, cameraId: " + cameraId
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
                PmwsLog.d("failed to create directory");
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
                PmwsLog.d("SD�� ������Ϣ�ǣ�" + e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * �ⲿ�洢�Ƿ����
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
//        manager.unregisterOnePixelReceiver(this);//Activity�˳�ʱ��ע��
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
     * ��̬�㲥������
     */
    public class StopReCordingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "���յ��˹㲥");
            stopRecording();
            releaseMediaRecorder();
            releaseCamera();
            stopSelf();
        }

    }

    /**
     * ����+-���л�surfaceԤ������
     */

    public class ValumeChangeCarme extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String qubie = intent.getStringExtra("ABC");
            if (qubie != null && qubie.equals("KEYCODE_VOLUME_DOWN")) {
                TestsBroadStop();//���������ֹͣ��Ƶ¼��
                //����1������û�п�ʼ¼��
                if (VolumeEmbellish == 1) {
                    VolumeCarmeChange();
                    Log.i("qweqwe", "KEYCODE_VOLUME_DOWN=1");
                }
                //������������������������
                Intent i = new Intent(getBaseContext(), MyServiceStart.class);
                startService(i);
            }
            if (qubie != null && qubie.equals("KEYCODE_VOLUME_UP")) {
                Log.i("qweqwe", "KEYCODE_VOLUME_UP=1");
                TestsBroadStop();//���������ֹͣ����¼��
                //����1������û�п�ʼ¼��
                if (VolumeEmbellish == 1) {
                    VolumeCarmeChange();
                    Log.i("qweqwe", "KEYCODE_VOLUME_DOWN=1");
                }
                //������������������������
                Intent i = new Intent(getBaseContext(), MyServiceStart.class);
                startService(i);
            }
        }
    }

    /**
     * ����������ͷ�л�ѡ��
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
     * �����¼��ֹͣ�����û��¼�����л�����ͷ
     *
     * @VolumeEmbellish �ж�����ͷ��ѡ��
     * @VolumeCarmeChange �ı�ϵͳ���õ�����ͷ
     */
    private void TestsBroadStop() {
        if (mIsRecording == false) {
            Log.i(TAG, "���³�ʼ��");
            VolumeCarmeChange();
            VolumeEmbellish = 2;
            Log.i("VolumeEmbellish", "VolumeEmbellish=2");
            releaseMediaRecorder();
            releaseCamera();
            mWindowManager.removeViewImmediate(mRootView);
//            mWindowManager.removeView(mRootView);
            stopSelf();
        } else {
            // Toast.makeText(this, "¼���Ѿ�ֹͣ", Toast.LENGTH_SHORT).show();
            // Toast.makeText(this, "¼���Ѿ�ֹͣ", Toast.LENGTH_SHORT).show();
            mHandler.removeMessages(MSG_RESTART_RECORDING);
            mHandler.removeMessages(MSG_START_RECORDING);

            stopRecording();

            releaseMediaRecorder(); // if you are using MediaRecorder,
            // release it first
            releaseCamera(); // release the camera immediately on pause event

            mWindowManager.removeViewImmediate(mRootView);
//            mWindowManager.removeView(mRootView);
            mNotificationManager.cancel(NOTIFI_ID_SERVICE_STARTED);

            PmwsSetActivity.sIsRecording = false;
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