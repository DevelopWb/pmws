package com.leng.hiddencamera.home;

import android.app.Service;
import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.leng.hiddencamera.R;
import com.leng.hiddencamera.util.DCPubic;
import com.serenegiant.usb.DeviceFilter;
import com.serenegiant.usb.IButtonCallback;
import com.serenegiant.usb.IStatusCallback;
import com.serenegiant.usb.UVCCamera;

import org.easydarwin.USBMonitor;
import org.greenrobot.eventbus.EventBus;

import java.nio.ByteBuffer;

public class UVCCameraService extends Service {

    public static boolean uvcConnected = false;
    public static boolean uvcAttached = false;



    public static class UVCCameraLivaData extends LiveData<UVCCamera> {
        @Override
        protected void postValue(UVCCamera value) {
            super.postValue(value);
        }
    }

    public static final UVCCameraLivaData liveData = new UVCCameraLivaData();

    public static class MyUVCCamera extends UVCCamera {
        boolean prev = false;

        @Override
        public synchronized void startPreview() {
            if (prev)
                return;

            super.startPreview();
            prev = true;
        }

        @Override
        public synchronized void stopPreview() {
            if (!prev)
                return;

            super.stopPreview();
            prev = false;
        }

        @Override
        public synchronized void destroy() {
            prev = false;
            super.destroy();
        }
    }

    private static final String TAG = UVCCameraService.class.getSimpleName();

    private USBMonitor mUSBMonitor;
    private UVCCamera mUVCCamera;

    private SparseArray<UVCCamera> cameras = new SparseArray<>();


    public UVCCamera getCamera() {
        return mUVCCamera;
    }

    private void releaseCamera() {
        if (mUVCCamera != null) {
            try {
                mUVCCamera.close();
                mUVCCamera.destroy();
                mUVCCamera = null;
            } catch (final Exception e) {
                //
            }
        }
    }

    public void reRequestOtg() {
        mUSBMonitor.requestPermission(DCPubic.usbDevice);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onCreate() {
        super.onCreate();

        mUSBMonitor = new USBMonitor(this, new USBMonitor.OnDeviceConnectListener() {
            @Override
            public void onAttach(final UsbDevice device) {
                Log.v(TAG, "onAttach:" + device);
                DCPubic.usbDevice = device;
                uvcAttached = true;
                mUSBMonitor.requestPermission(device);
                EventBus.getDefault().post("onAttach");
            }

            @Override
            public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
                releaseCamera();
                uvcConnected = true;

                Log.v(TAG, "onConnect:");

                try {
                    final UVCCamera camera = new MyUVCCamera();
                    camera.open(ctrlBlock);
                    camera.setStatusCallback(new IStatusCallback() {
                        @Override
                        public void onStatus(final int statusClass, final int event, final int selector, final int statusAttribute, final ByteBuffer data) {
                            Log.i(TAG, "onStatus(statusClass=" + statusClass
                                    + "; " +
                                    "event=" + event + "; " +
                                    "selector=" + selector + "; " +
                                    "statusAttribute=" + statusAttribute + "; " +
                                    "data=...)");
                        }
                    });

                    camera.setButtonCallback(new IButtonCallback() {
                        @Override
                        public void onButton(final int button, final int state) {
                            Log.i(TAG, "onButton(button=" + button + "; " + "state=" + state + ")");
                        }
                    });

//					camera.setPreviewTexture(camera.getSurfaceTexture());
                    mUVCCamera = camera;
                    liveData.postValue(camera);
//                    Toast.makeText(UVCCameraService.this, "UVCCamera connected!", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().post("onConnect");
                    if (device != null){
                        cameras.append(device.getDeviceId(), camera);
                    }

                } catch (Exception ex) {
                    uvcConnected = false;
                    ex.printStackTrace();
                }
            }

            @Override
            public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
                Log.v(TAG, "onDisconnect:");
                uvcConnected = false;
//                Toast.makeText(MainActivity.this, R.string.usb_camera_disconnected, Toast.LENGTH_SHORT).show();

//                releaseCamera();

                if (device != null) {
                    //设备还存在 没有拔下来
                    UVCCamera camera = cameras.get(device.getDeviceId());

                    if (mUVCCamera == camera) {
                        mUVCCamera = null;
                        liveData.postValue(null);
                    }

                    cameras.remove(device.getDeviceId());
                } else {
                    Toast.makeText(UVCCameraService.this, "UVCCamera disconnected!", Toast.LENGTH_SHORT).show();
                    mUVCCamera = null;
                    liveData.postValue(null);
                }
                EventBus.getDefault().post("onDisconnect");

//                mUSBMonitor = new USBMonitor(OutterCameraService.this, this);
//                mUSBMonitor.setDeviceFilter(DeviceFilter.getDeviceFilters(OutterCameraService.this, R.xml.device_filter));
//                mUSBMonitor.register();
            }

            @Override
            public void onCancel(UsbDevice usbDevice) {
                releaseCamera();
            }

            @Override
            public void onDettach(final UsbDevice device) {
                Log.v(TAG, "onDettach:");
                releaseCamera();
//                AppContext.getInstance().bus.post(new UVCCameraDisconnect());
            }
        });

        mUSBMonitor.setDeviceFilter(DeviceFilter.getDeviceFilters(this, R.xml.device_filter));
        mUSBMonitor.register();
    }

    @Override
    public void onDestroy() {
        releaseCamera();
        EventBus.getDefault().unregister(this);
        if (mUSBMonitor != null) {
            mUSBMonitor.destroy();
            mUSBMonitor.unregister();
        }

        super.onDestroy();
    }
    public class LocalBinder extends Binder {
        public UVCCameraService getService() {
            return UVCCameraService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }
}
