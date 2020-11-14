package com.leng.hiddencamera;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MediaRecordService extends Service {
	private static final int MSG_START_RECORD = 1;
	private static final int MSG_START_RECORD_DISMISS = 2;
	private static final int MSG_TAKE_RECORD = 3;
	private static final int MSG_STOP_RECORD = 4;
	private static final int MSG_STOP_SERVICE = 5;

	public static final String EXTRA_ACTION = "extra_action";
	public static final String ACTION_START = "action_start";
	public static final String ACTION_STOP = "action_stop";
	public static final String ACTION_TAKE_RECORD = "action_take_record";
	public static final String ACTION_SWITCH_STATE = "action_switch_state";
	public static final String ACTION_RECORDING = "action_recording";
	private static final int NOTIFI_ID_SERVICE_STARTED = 100;

	private final int mMaxDurationInMs = 10 * 1000;
	private final int mPreviewWidth = 600;
	private final int mPreviewHeight = 800;
	private final int mPicWidth = 720;
	private final int mPicHeight = 1280;

	private SurfaceView mSurPreview;
	private Camera mCamera;
	private MediaRecorder mRecorder;
	private MediaRecorder.OnInfoListener mMediaRecordListener;
	private PictureCallback mPictureCallback;
	private GestureDetector mGestureDetector;
	private Handler mHandler;
	private WindowManager mWindowManager = null;
	private WindowManager.LayoutParams mWindowLayoutParams = null;
	private View mRootView;

	private NotificationManager mNotificationManager;

	private File mFileDir;
	public static int mCameraId = 0;//Ĭ��0
	private boolean mPreviewEnabled = true;

	private boolean mRecordStarted = false;
	private boolean mPreviewStarted = false;
	private File mRecordFile = null;

	@Override
	public void onCreate() {
		PmwsLog.d("Service onCreate");
		super.onCreate();

		initData();
		initListener();
		initPreview();
		initCamera();
	}

	private void initData() {
		File dir = new File(Environment.getExternalStorageDirectory(), "MyData");
		if (!dir.exists()) {
			dir.mkdir();
		}
		mFileDir = dir;

		mPictureCallback = new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				// TODO Auto-generated method stub
				File file = new File(mFileDir, new Date().toLocaleString()
						+ ".jpg");
				FileOutputStream fo = null;
				try {
					fo = new FileOutputStream(file);
					fo.write(data);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (fo != null) {
						try {
							fo.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		};

		HandlerThread mHandlerThread = new HandlerThread("RecordHandler");
		mHandlerThread.start();

		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				int what = msg.what;
				PmwsLog.d("Hanlder reveive message: " + what);
				switch (what) {
				case MSG_START_RECORD:
					startRecording();
					break;
				case MSG_START_RECORD_DISMISS:
					startRecording();
					stopPreview();
					break;
				case MSG_TAKE_RECORD:
					stopRecording();
					startRecording();
					break;
				case MSG_STOP_RECORD:
					stopRecording();
					break;
				case MSG_STOP_SERVICE:
					stopService();
					break;
				}
			}

		};

		mGestureDetector = new GestureDetector(
				new GestureDetector.SimpleOnGestureListener() {

					@Override
					public boolean onDoubleTap(MotionEvent e) {
						PmwsLog.d("Surface double clicked, start to record");
						mHandler.removeMessages(MSG_START_RECORD);
						Message msg = mHandler.obtainMessage(MSG_START_RECORD);
						mHandler.sendMessage(msg);
						return false;
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {
						PmwsLog.d("Surface scrolled, stop the recording");
						mHandler.removeMessages(MSG_STOP_RECORD);
						Message msg = mHandler.obtainMessage(MSG_STOP_RECORD);
						mHandler.sendMessage(msg);
						return false;
					}

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						PmwsLog.d("Surface clicked, start to dismiss and record back ground");
						mHandler.removeMessages(MSG_START_RECORD_DISMISS);
						Message msg = mHandler
								.obtainMessage(MSG_START_RECORD_DISMISS);
						mHandler.sendMessage(msg);
						return false;
					}

					@Override
					public void onLongPress(MotionEvent e) {
						PmwsLog.d("Surface long pressed, take a record");
						mHandler.removeMessages(MSG_TAKE_RECORD);
						Message msg = mHandler.obtainMessage(MSG_TAKE_RECORD);
						mHandler.sendMessage(msg);
					}

				});
	}

	private void initListener() {
		mMediaRecordListener = new MediaRecorder.OnInfoListener() {

			@Override
			public void onInfo(MediaRecorder mr, int what, int extra) {
				if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
					PmwsLog.d("MediaRecorder max duration reached, start recording into anther file");
					mHandler.removeMessages(MSG_TAKE_RECORD);
					Message msg = mHandler.obtainMessage(MSG_TAKE_RECORD);
					mHandler.sendMessageDelayed(msg, 500);
				}

			}

		};
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		PmwsLog.d("Service onStartCommand");
		if (intent != null) {
			PmwsLog.d("MideaRecordService receive intent: " + intent.toString());

			String action = intent.getAction();
			if (MediaRecordService.ACTION_START.equals(action)) {
				if (mRecordStarted) {
					PmwsLog.d("The service has been started before, do nothing this time");
					// startPreview();
				} else {
					if (mPreviewEnabled) {
						PmwsLog.d("The service not started and preview enabled start the preview");
						startPreview();
					} else {
						PmwsLog.d("The service not started but preview disabled start the recording");
						startRecording();
					}
				}
			} else if (MediaRecordService.ACTION_SWITCH_STATE.equals(action)) {
				if (mPreviewEnabled) {
					startPreview();
				} else {
					stopPreview();
					startRecording();
				}
			} else if (MediaRecordService.ACTION_STOP.equals(action)) {
				stopRecording();
				stopPreview();
				stopService();
			}

		}

		return super.onStartCommand(intent, flags, startId);
	}

	private void initCamera() {
		mCamera = Camera.open(mCameraId);
		try {
			Camera.Parameters parameters = mCamera.getParameters();
			parameters.setPreviewFpsRange(30000, 30000); // ÿ����ʾ20~30֡
			parameters.setPictureFormat(ImageFormat.JPEG); // ����ͼƬ��ʽ
			parameters.setPreviewSize(mPicHeight, mPicWidth); // ����Ԥ����Ƭ�Ĵ�С
			parameters.setPictureSize(mPicHeight, mPicWidth); // ������Ƭ�Ĵ�С
			parameters.setRecordingHint(true);

			mCamera.setDisplayOrientation(getCameraDisplayOrientation(this,
					mCameraId)); // ����camera�Ƕ�

			mCamera.setPreviewDisplay(mSurPreview.getHolder());
			mCamera.setParameters(parameters);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// initMediaRecorder();
	}

	private int getCameraDisplayOrientation(Context activity, int cameraId) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int orientation = 0;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
			orientation = 90;
		} else if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			orientation = 270;
		}
		return orientation;
	}

	private void initPreview() {
		mWindowManager = ((WindowManager) getApplicationContext()
				.getSystemService("window"));
		mWindowLayoutParams = new WindowManager.LayoutParams();
		mWindowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		mWindowLayoutParams.flags = mWindowLayoutParams.flags
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		mWindowLayoutParams.gravity = Gravity.CENTER;
		mWindowLayoutParams.width = mPreviewWidth;
		mWindowLayoutParams.height = mPreviewHeight;
		mWindowLayoutParams.format = PixelFormat.RGBA_8888;

		PmwsLog.d("Start preview, init the preview and show the preview on screen");

		mRootView = LayoutInflater.from(this).inflate(R.layout.activity_main,
				null);
		mRootView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mGestureDetector.onTouchEvent(event);
				return false;
			}
		});

		mSurPreview = (SurfaceView) mRootView.findViewById(R.id.camera_preview);

		mSurPreview.getHolder().addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub

			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				try {
					if (mCamera != null) {
						mCamera.setPreviewDisplay(holder);
					}
					/*
					 * if (mRecordStarted) {
					 * mRecorder.setPreviewDisplay(mSurPreview
					 * .getHolder().getSurface()); }
					 */
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				try {
					if (mCamera != null) {
						mCamera.setPreviewDisplay(holder);
					}
					/*
					 * if (mRecordStarted) {
					 * mRecorder.setPreviewDisplay(mSurPreview
					 * .getHolder().getSurface()); }
					 */
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		// ���ø�SurfaceView�Լ���ά������
		mSurPreview.getHolder()
				.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mWindowManager.addView(mRootView, mWindowLayoutParams);
	}

	private void startPreview() {
		if (mPreviewStarted) {
			PmwsLog.d("Start preview, preview allready started, do nothing");
			return;
		} else {
			PmwsLog.d("Start preview, show the preview on screen");
			mCamera.startPreview();
			updatePriview(true);
			mPreviewStarted = true;
			return;
		}
	}

	private void stopPreview() {
		if (!mPreviewStarted) {
			PmwsLog.d("Stop preview, preview not started, do nothing");
			return;
		} else {
			PmwsLog.d("Stop preview, remove the preview from screen");
		}

		mCamera.stopPreview();
		updatePriview(false);
		mPreviewStarted = false;

		// mWindowManager.removeView(mRootView);
	}

	private void updatePriview(boolean showFlag) {
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

	private File getRecordFile() {
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(
				new Date()).toString();
		String fileSuffix = ".mp4";
		File recordFile = new File(mFileDir, fileName + fileSuffix);
		if (recordFile.exists()) {
			recordFile.delete();
		} else {
			try {
				recordFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return recordFile;
	}

	public void startRecording() {
		File file = getRecordFile();
		PmwsLog.d("Start to record the camera into " + file.getPath());
		try {
			mRecorder = new MediaRecorder();
			mCamera.unlock();

			mRecorder.setCamera(mCamera);
			mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			// mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			mRecorder.setProfile(CamcorderProfile.get(mCameraId,
					CamcorderProfile.QUALITY_720P));
			mRecorder.setMaxDuration(mMaxDurationInMs);
			/*
			 * mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
			 * mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			 * mRecorder.setVideoSize(mPicWidth, mPicHeight);
			 * mRecorder.setVideoFrameRate(15);
			 */
			mRecorder.setOrientationHint(getCameraDisplayOrientation(this,
					mCameraId));
			mRecorder.setOnInfoListener(mMediaRecordListener);

			mRecorder.setOutputFile(file.getPath());

			mRecorder.prepare();
			mRecorder.start();
			mRecordStarted = true;

			showNotification();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showNotification() {
		mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = new NotificationCompat.Builder(getBaseContext())
				.setSmallIcon(R.drawable.ic_notification_start)
				.setContentTitle("��Ļ��ʿ").setContentText("��Ļ��ʿ, ���ֹͣ").build();
		n.flags |= Notification.FLAG_NO_CLEAR;
		n.defaults = Notification.DEFAULT_LIGHTS;
		Intent intent = new Intent(MediaRecordService.ACTION_STOP);
		intent.setClass(getBaseContext(), MediaRecordService.class);

		PendingIntent pi = PendingIntent.getService(getBaseContext(), 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		n.contentIntent = pi;

		mNotificationManager.notify(NOTIFI_ID_SERVICE_STARTED, n);
	}

	public void stopRecording() {
		PmwsLog.d("Stop recording...");

		mRecorder.setOnErrorListener(null);
		mRecorder.setOnInfoListener(null);

		mRecorder.stop();
		mRecorder.reset();
		mRecorder.release();
		mRecorder = null;

		mCamera.lock();
		mRecordStarted = false;

		PmwsLog.d("Media record stoped, cancel the notification");

		mNotificationManager.cancel(NOTIFI_ID_SERVICE_STARTED);
	}

	private void stopService() {
		PmwsLog.d("Release the hardware and stop the service");
		mCamera.stopPreview();
		mCamera.release();

		mWindowManager.removeView(mRootView);
		mRootView = null;

		stopSelf();
	}

	@Override
	public void onDestroy() {
		PmwsLog.d("Service onDestroy");

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
