//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.serenegiant.usb;

import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.easydarwin.USBMonitor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UVCCamera {
    private static final boolean DEBUG = false;
    private static final String TAG = UVCCamera.class.getSimpleName();
    private static final String DEFAULT_USBFS = "/dev/bus/usb";
    public static final int DEFAULT_PREVIEW_WIDTH = 640;
    public static final int DEFAULT_PREVIEW_HEIGHT = 480;
    public static final int DEFAULT_PREVIEW_MODE = 0;
    public static final int DEFAULT_PREVIEW_MIN_FPS = 1;
    public static final int DEFAULT_PREVIEW_MAX_FPS = 30;
    public static final float DEFAULT_BANDWIDTH = 1.0F;
    public static final int FRAME_FORMAT_YUYV = 0;
    public static final int FRAME_FORMAT_MJPEG = 1;
    public static final int PIXEL_FORMAT_RAW = 0;
    public static final int PIXEL_FORMAT_YUV = 1;
    public static final int PIXEL_FORMAT_RGB565 = 2;
    public static final int PIXEL_FORMAT_RGBX = 3;
    public static final int PIXEL_FORMAT_YUV420SP = 4;
    public static final int PIXEL_FORMAT_NV21 = 5;
    public static final int CTRL_SCANNING = 1;
    public static final int CTRL_AE = 2;
    public static final int CTRL_AE_PRIORITY = 4;
    public static final int CTRL_AE_ABS = 8;
    public static final int CTRL_AR_REL = 16;
    public static final int CTRL_FOCUS_ABS = 32;
    public static final int CTRL_FOCUS_REL = 64;
    public static final int CTRL_IRIS_ABS = 128;
    public static final int CTRL_IRIS_REL = 256;
    public static final int CTRL_ZOOM_ABS = 512;
    public static final int CTRL_ZOOM_REL = 1024;
    public static final int CTRL_PANTILT_ABS = 2048;
    public static final int CTRL_PANTILT_REL = 4096;
    public static final int CTRL_ROLL_ABS = 8192;
    public static final int CTRL_ROLL_REL = 16384;
    public static final int CTRL_FOCUS_AUTO = 131072;
    public static final int CTRL_PRIVACY = 262144;
    public static final int CTRL_FOCUS_SIMPLE = 524288;
    public static final int CTRL_WINDOW = 1048576;
    public static final int PU_BRIGHTNESS = -2147483647;
    public static final int PU_CONTRAST = -2147483646;
    public static final int PU_HUE = -2147483644;
    public static final int PU_SATURATION = -2147483640;
    public static final int PU_SHARPNESS = -2147483632;
    public static final int PU_GAMMA = -2147483616;
    public static final int PU_WB_TEMP = -2147483584;
    public static final int PU_WB_COMPO = -2147483520;
    public static final int PU_BACKLIGHT = -2147483392;
    public static final int PU_GAIN = -2147483136;
    public static final int PU_POWER_LF = -2147482624;
    public static final int PU_HUE_AUTO = -2147481600;
    public static final int PU_WB_TEMP_AUTO = -2147479552;
    public static final int PU_WB_COMPO_AUTO = -2147475456;
    public static final int PU_DIGITAL_MULT = -2147467264;
    public static final int PU_DIGITAL_LIMIT = -2147450880;
    public static final int PU_AVIDEO_STD = -2147418112;
    public static final int PU_AVIDEO_LOCK = -2147352576;
    public static final int PU_CONTRAST_AUTO = -2147221504;
    public static final int STATUS_CLASS_CONTROL = 16;
    public static final int STATUS_CLASS_CONTROL_CAMERA = 17;
    public static final int STATUS_CLASS_CONTROL_PROCESSING = 18;
    public static final int STATUS_ATTRIBUTE_VALUE_CHANGE = 0;
    public static final int STATUS_ATTRIBUTE_INFO_CHANGE = 1;
    public static final int STATUS_ATTRIBUTE_FAILURE_CHANGE = 2;
    public static final int STATUS_ATTRIBUTE_UNKNOWN = 255;
    private static boolean isLoaded;
    private USBMonitor.UsbControlBlock mCtrlBlock;
    protected long mControlSupports;
    protected long mProcSupports;
    protected int mCurrentFrameFormat = 1;
    protected int mCurrentWidth = 640;
    protected int mCurrentHeight = 480;
    protected float mCurrentBandwidthFactor = 1.0F;
    protected String mSupportedSize = null;
    protected List<Size> mCurrentSizeList;
    protected long mNativePtr = this.nativeCreate();
    protected int mScanningModeMin;
    protected int mScanningModeMax;
    protected int mScanningModeDef;
    protected int mExposureModeMin;
    protected int mExposureModeMax;
    protected int mExposureModeDef;
    protected int mExposurePriorityMin;
    protected int mExposurePriorityMax;
    protected int mExposurePriorityDef;
    protected int mExposureMin;
    protected int mExposureMax;
    protected int mExposureDef;
    protected int mAutoFocusMin;
    protected int mAutoFocusMax;
    protected int mAutoFocusDef;
    protected int mFocusMin;
    protected int mFocusMax;
    protected int mFocusDef;
    protected int mFocusRelMin;
    protected int mFocusRelMax;
    protected int mFocusRelDef;
    protected int mFocusSimpleMin;
    protected int mFocusSimpleMax;
    protected int mFocusSimpleDef;
    protected int mIrisMin;
    protected int mIrisMax;
    protected int mIrisDef;
    protected int mIrisRelMin;
    protected int mIrisRelMax;
    protected int mIrisRelDef;
    protected int mPanMin;
    protected int mPanMax;
    protected int mPanDef;
    protected int mTiltMin;
    protected int mTiltMax;
    protected int mTiltDef;
    protected int mRollMin;
    protected int mRollMax;
    protected int mRollDef;
    protected int mPanRelMin;
    protected int mPanRelMax;
    protected int mPanRelDef;
    protected int mTiltRelMin;
    protected int mTiltRelMax;
    protected int mTiltRelDef;
    protected int mRollRelMin;
    protected int mRollRelMax;
    protected int mRollRelDef;
    protected int mPrivacyMin;
    protected int mPrivacyMax;
    protected int mPrivacyDef;
    protected int mAutoWhiteBlanceMin;
    protected int mAutoWhiteBlanceMax;
    protected int mAutoWhiteBlanceDef;
    protected int mAutoWhiteBlanceCompoMin;
    protected int mAutoWhiteBlanceCompoMax;
    protected int mAutoWhiteBlanceCompoDef;
    protected int mWhiteBlanceMin;
    protected int mWhiteBlanceMax;
    protected int mWhiteBlanceDef;
    protected int mWhiteBlanceCompoMin;
    protected int mWhiteBlanceCompoMax;
    protected int mWhiteBlanceCompoDef;
    protected int mWhiteBlanceRelMin;
    protected int mWhiteBlanceRelMax;
    protected int mWhiteBlanceRelDef;
    protected int mBacklightCompMin;
    protected int mBacklightCompMax;
    protected int mBacklightCompDef;
    protected int mBrightnessMin;
    protected int mBrightnessMax;
    protected int mBrightnessDef;
    protected int mContrastMin;
    protected int mContrastMax;
    protected int mContrastDef;
    protected int mSharpnessMin;
    protected int mSharpnessMax;
    protected int mSharpnessDef;
    protected int mGainMin;
    protected int mGainMax;
    protected int mGainDef;
    protected int mGammaMin;
    protected int mGammaMax;
    protected int mGammaDef;
    protected int mSaturationMin;
    protected int mSaturationMax;
    protected int mSaturationDef;
    protected int mHueMin;
    protected int mHueMax;
    protected int mHueDef;
    protected int mZoomMin;
    protected int mZoomMax;
    protected int mZoomDef;
    protected int mZoomRelMin;
    protected int mZoomRelMax;
    protected int mZoomRelDef;
    protected int mPowerlineFrequencyMin;
    protected int mPowerlineFrequencyMax;
    protected int mPowerlineFrequencyDef;
    protected int mMultiplierMin;
    protected int mMultiplierMax;
    protected int mMultiplierDef;
    protected int mMultiplierLimitMin;
    protected int mMultiplierLimitMax;
    protected int mMultiplierLimitDef;
    protected int mAnalogVideoStandardMin;
    protected int mAnalogVideoStandardMax;
    protected int mAnalogVideoStandardDef;
    protected int mAnalogVideoLockStateMin;
    protected int mAnalogVideoLockStateMax;
    protected int mAnalogVideoLockStateDef;
    private static final String[] SUPPORTS_CTRL;
    private static final String[] SUPPORTS_PROC;

    public UVCCamera() {
    }

    public synchronized void open(USBMonitor.UsbControlBlock ctrlBlock) {
        int result;
        try {
            this.mCtrlBlock = ctrlBlock.clone();
            result = this.nativeConnect(this.mNativePtr, this.mCtrlBlock.getVenderId(), this.mCtrlBlock.getProductId(), this.mCtrlBlock.getFileDescriptor(), this.mCtrlBlock.getBusNum(), this.mCtrlBlock.getDevNum(), this.getUSBFSName(this.mCtrlBlock));
        } catch (Exception var4) {
            Log.w(TAG, var4);
            result = -1;
        }

        if (result != 0) {
            throw new UnsupportedOperationException("open failed:result=" + result);
        } else {
            if (this.mNativePtr != 0L && TextUtils.isEmpty(this.mSupportedSize)) {
                this.mSupportedSize = nativeGetSupportedSize(this.mNativePtr);
            }

            nativeSetPreviewSize(this.mNativePtr, 640, 480, 1, 30, 0, 1.0F);
        }
    }

    public void setStatusCallback(IStatusCallback callback) {
        if (this.mNativePtr != 0L) {
            nativeSetStatusCallback(this.mNativePtr, callback);
        }

    }

    public void setButtonCallback(IButtonCallback callback) {
        if (this.mNativePtr != 0L) {
            nativeSetButtonCallback(this.mNativePtr, callback);
        }

    }

    public synchronized void close() {
        this.stopPreview();
        if (this.mNativePtr != 0L) {
            nativeRelease(this.mNativePtr);
        }

        if (this.mCtrlBlock != null) {
            this.mCtrlBlock.close();
            this.mCtrlBlock = null;
        }

        this.mControlSupports = this.mProcSupports = 0L;
        this.mCurrentFrameFormat = -1;
        this.mCurrentBandwidthFactor = 0.0F;
        this.mSupportedSize = null;
        this.mCurrentSizeList = null;
    }

    public UsbDevice getDevice() {
        return this.mCtrlBlock != null ? this.mCtrlBlock.getDevice() : null;
    }

    public String getDeviceName() {
        return this.mCtrlBlock != null ? this.mCtrlBlock.getDeviceName() : null;
    }

    public USBMonitor.UsbControlBlock getUsbControlBlock() {
        return this.mCtrlBlock;
    }

    public synchronized String getSupportedSize() {
        return !TextUtils.isEmpty(this.mSupportedSize) ? this.mSupportedSize : (this.mSupportedSize = nativeGetSupportedSize(this.mNativePtr));
    }

    public Size getPreviewSize() {
        Size result = null;
        List<Size> list = this.getSupportedSizeList();
        Iterator var3 = list.iterator();

        while(var3.hasNext()) {
            Size sz = (Size)var3.next();
            if (sz.width == this.mCurrentWidth || sz.height == this.mCurrentHeight) {
                result = sz;
                break;
            }
        }

        return result;
    }

    public void setPreviewSize(int width, int height) {
        this.setPreviewSize(width, height, 1, 30, this.mCurrentFrameFormat, this.mCurrentBandwidthFactor);
    }

    public void setPreviewSize(int width, int height, int frameFormat) {
        this.setPreviewSize(width, height, 1, 30, frameFormat, this.mCurrentBandwidthFactor);
    }

    public void setPreviewSize(int width, int height, int frameFormat, float bandwidth) {
        this.setPreviewSize(width, height, 1, 30, frameFormat, bandwidth);
    }

    public void setPreviewSize(int width, int height, int min_fps, int max_fps, int frameFormat, float bandwidthFactor) {
        if (width != 0 && height != 0) {
            if (this.mNativePtr != 0L) {
                int result = nativeSetPreviewSize(this.mNativePtr, width, height, min_fps, max_fps, frameFormat, bandwidthFactor);
                if (result != 0) {
                    throw new IllegalArgumentException("Failed to set preview size");
                }

                this.mCurrentFrameFormat = frameFormat;
                this.mCurrentWidth = width;
                this.mCurrentHeight = height;
                this.mCurrentBandwidthFactor = bandwidthFactor;
            }

        } else {
            throw new IllegalArgumentException("invalid preview size");
        }
    }

    public List<Size> getSupportedSizeList() {
        int type = this.mCurrentFrameFormat > 0 ? 6 : 4;
        return getSupportedSize(type, this.mSupportedSize);
    }

    public static List<Size> getSupportedSize(int type, String supportedSize) {
        List<Size> result = new ArrayList();
        if (!TextUtils.isEmpty(supportedSize)) {
            try {
                JSONObject json = new JSONObject(supportedSize);
                JSONArray formats = json.getJSONArray("formats");
                int format_nums = formats.length();

                for(int i = 0; i < format_nums; ++i) {
                    JSONObject format = formats.getJSONObject(i);
                    int format_type = format.getInt("type");
                    if (format_type == type || type == -1) {
                        addSize(format, format_type, 0, result);
                    }
                }
            } catch (JSONException var9) {
            }
        }

        return result;
    }

    private static final void addSize(JSONObject format, int formatType, int frameType, List<Size> size_list) throws JSONException {
        JSONArray size = format.getJSONArray("size");
        int size_nums = size.length();

        for(int j = 0; j < size_nums; ++j) {
            String[] sz = size.getString(j).split("x");

            try {
                size_list.add(new Size(formatType, frameType, j, Integer.parseInt(sz[0]), Integer.parseInt(sz[1])));
            } catch (Exception var9) {
                break;
            }
        }

    }

    public synchronized void setPreviewDisplay(SurfaceHolder holder) {
        nativeSetPreviewDisplay(this.mNativePtr, holder.getSurface());
    }

    public synchronized void setPreviewTexture(SurfaceTexture texture) {
        Surface surface = new Surface(texture);
        nativeSetPreviewDisplay(this.mNativePtr, surface);
    }

    public synchronized void setPreviewDisplay(Surface surface) {
        nativeSetPreviewDisplay(this.mNativePtr, surface);
    }

    public void setFrameCallback(IFrameCallback callback, int pixelFormat) {
        if (this.mNativePtr != 0L) {
            nativeSetFrameCallback(this.mNativePtr, callback, pixelFormat);
        }

    }

    public synchronized void startPreview() {
        if (this.mCtrlBlock != null) {
            int r = nativeStartPreview(this.mNativePtr);
            if (r != 0) {
                throw new IllegalStateException(String.format("start preview return:%d", r));
            }
        } else {
            throw new IllegalStateException("ctrl block is null!");
        }
    }

    public synchronized void stopPreview() {
        this.setFrameCallback((IFrameCallback)null, 0);
        if (this.mCtrlBlock != null) {
            nativeStopPreview(this.mNativePtr);
        }

    }

    public synchronized void destroy() {
        this.close();
        if (this.mNativePtr != 0L) {
            this.nativeDestroy(this.mNativePtr);
            this.mNativePtr = 0L;
        }

    }

    public boolean checkSupportFlag(long flag) {
        this.updateCameraParams();
        if ((flag & -2147483648L) == -2147483648L) {
            return (this.mProcSupports & flag) == (flag & 2147483647L);
        } else {
            return (this.mControlSupports & flag) == flag;
        }
    }

    public synchronized void setAutoFocus(boolean autoFocus) {
        if (this.mNativePtr != 0L) {
            nativeSetAutoFocus(this.mNativePtr, autoFocus);
        }

    }

    public synchronized boolean getAutoFocus() {
        boolean result = true;
        if (this.mNativePtr != 0L) {
            result = nativeGetAutoFocus(this.mNativePtr) > 0;
        }

        return result;
    }

    public synchronized void setFocus(int focus) {
        if (this.mNativePtr != 0L) {
            float range = (float)Math.abs(this.mFocusMax - this.mFocusMin);
            if (range > 0.0F) {
                nativeSetFocus(this.mNativePtr, (int)((float)focus / 100.0F * range) + this.mFocusMin);
            }
        }

    }

    public synchronized int getFocus(int focus_abs) {
        int result = 0;
        if (this.mNativePtr != 0L) {
            this.nativeUpdateFocusLimit(this.mNativePtr);
            float range = (float)Math.abs(this.mFocusMax - this.mFocusMin);
            if (range > 0.0F) {
                result = (int)((float)(focus_abs - this.mFocusMin) * 100.0F / range);
            }
        }

        return result;
    }

    public synchronized int getFocus() {
        return this.getFocus(nativeGetFocus(this.mNativePtr));
    }

    public synchronized void resetFocus() {
        if (this.mNativePtr != 0L) {
            nativeSetFocus(this.mNativePtr, this.mFocusDef);
        }

    }

    public synchronized void setAutoWhiteBlance(boolean autoWhiteBlance) {
        if (this.mNativePtr != 0L) {
            nativeSetAutoWhiteBlance(this.mNativePtr, autoWhiteBlance);
        }

    }

    public synchronized boolean getAutoWhiteBlance() {
        boolean result = true;
        if (this.mNativePtr != 0L) {
            result = nativeGetAutoWhiteBlance(this.mNativePtr) > 0;
        }

        return result;
    }

    public synchronized void setWhiteBlance(int whiteBlance) {
        if (this.mNativePtr != 0L) {
            float range = (float)Math.abs(this.mWhiteBlanceMax - this.mWhiteBlanceMin);
            if (range > 0.0F) {
                nativeSetWhiteBlance(this.mNativePtr, (int)((float)whiteBlance / 100.0F * range) + this.mWhiteBlanceMin);
            }
        }

    }

    public synchronized int getWhiteBlance(int whiteBlance_abs) {
        int result = 0;
        if (this.mNativePtr != 0L) {
            this.nativeUpdateWhiteBlanceLimit(this.mNativePtr);
            float range = (float)Math.abs(this.mWhiteBlanceMax - this.mWhiteBlanceMin);
            if (range > 0.0F) {
                result = (int)((float)(whiteBlance_abs - this.mWhiteBlanceMin) * 100.0F / range);
            }
        }

        return result;
    }

    public synchronized int getWhiteBlance() {
        return this.getFocus(nativeGetWhiteBlance(this.mNativePtr));
    }

    public synchronized void resetWhiteBlance() {
        if (this.mNativePtr != 0L) {
            nativeSetWhiteBlance(this.mNativePtr, this.mWhiteBlanceDef);
        }

    }

    public synchronized void setBrightness(int brightness) {
        if (this.mNativePtr != 0L) {
            float range = (float)Math.abs(this.mBrightnessMax - this.mBrightnessMin);
            if (range > 0.0F) {
                nativeSetBrightness(this.mNativePtr, (int)((float)brightness / 100.0F * range) + this.mBrightnessMin);
            }
        }

    }

    public synchronized int getBrightness(int brightness_abs) {
        int result = 0;
        if (this.mNativePtr != 0L) {
            this.nativeUpdateBrightnessLimit(this.mNativePtr);
            float range = (float)Math.abs(this.mBrightnessMax - this.mBrightnessMin);
            if (range > 0.0F) {
                result = (int)((float)(brightness_abs - this.mBrightnessMin) * 100.0F / range);
            }
        }

        return result;
    }

    public synchronized int getBrightness() {
        return this.getBrightness(nativeGetBrightness(this.mNativePtr));
    }

    public synchronized void resetBrightness() {
        if (this.mNativePtr != 0L) {
            nativeSetBrightness(this.mNativePtr, this.mBrightnessDef);
        }

    }

    public synchronized void setContrast(int contrast) {
        if (this.mNativePtr != 0L) {
            this.nativeUpdateContrastLimit(this.mNativePtr);
            float range = (float)Math.abs(this.mContrastMax - this.mContrastMin);
            if (range > 0.0F) {
                nativeSetContrast(this.mNativePtr, (int)((float)contrast / 100.0F * range) + this.mContrastMin);
            }
        }

    }

    public synchronized int getContrast(int contrast_abs) {
        int result = 0;
        if (this.mNativePtr != 0L) {
            float range = (float)Math.abs(this.mContrastMax - this.mContrastMin);
            if (range > 0.0F) {
                result = (int)((float)(contrast_abs - this.mContrastMin) * 100.0F / range);
            }
        }

        return result;
    }

    public synchronized int getContrast() {
        return this.getContrast(nativeGetContrast(this.mNativePtr));
    }

    public synchronized void resetContrast() {
        if (this.mNativePtr != 0L) {
            nativeSetContrast(this.mNativePtr, this.mContrastDef);
        }

    }

    public synchronized void setSharpness(int sharpness) {
        if (this.mNativePtr != 0L) {
            float range = (float)Math.abs(this.mSharpnessMax - this.mSharpnessMin);
            if (range > 0.0F) {
                nativeSetSharpness(this.mNativePtr, (int)((float)sharpness / 100.0F * range) + this.mSharpnessMin);
            }
        }

    }

    public synchronized int getSharpness(int sharpness_abs) {
        int result = 0;
        if (this.mNativePtr != 0L) {
            this.nativeUpdateSharpnessLimit(this.mNativePtr);
            float range = (float)Math.abs(this.mSharpnessMax - this.mSharpnessMin);
            if (range > 0.0F) {
                result = (int)((float)(sharpness_abs - this.mSharpnessMin) * 100.0F / range);
            }
        }

        return result;
    }

    public synchronized int getSharpness() {
        return this.getSharpness(nativeGetSharpness(this.mNativePtr));
    }

    public synchronized void resetSharpness() {
        if (this.mNativePtr != 0L) {
            nativeSetSharpness(this.mNativePtr, this.mSharpnessDef);
        }

    }

    public synchronized void setGain(int gain) {
        if (this.mNativePtr != 0L) {
            float range = (float)Math.abs(this.mGainMax - this.mGainMin);
            if (range > 0.0F) {
                nativeSetGain(this.mNativePtr, (int)((float)gain / 100.0F * range) + this.mGainMin);
            }
        }

    }

    public synchronized int getGain(int gain_abs) {
        int result = 0;
        if (this.mNativePtr != 0L) {
            this.nativeUpdateGainLimit(this.mNativePtr);
            float range = (float)Math.abs(this.mGainMax - this.mGainMin);
            if (range > 0.0F) {
                result = (int)((float)(gain_abs - this.mGainMin) * 100.0F / range);
            }
        }

        return result;
    }

    public synchronized int getGain() {
        return this.getGain(nativeGetGain(this.mNativePtr));
    }

    public synchronized void resetGain() {
        if (this.mNativePtr != 0L) {
            nativeSetGain(this.mNativePtr, this.mGainDef);
        }

    }

    public synchronized void setGamma(int gamma) {
        if (this.mNativePtr != 0L) {
            float range = (float)Math.abs(this.mGammaMax - this.mGammaMin);
            if (range > 0.0F) {
                nativeSetGamma(this.mNativePtr, (int)((float)gamma / 100.0F * range) + this.mGammaMin);
            }
        }

    }

    public synchronized int getGamma(int gamma_abs) {
        int result = 0;
        if (this.mNativePtr != 0L) {
            this.nativeUpdateGammaLimit(this.mNativePtr);
            float range = (float)Math.abs(this.mGammaMax - this.mGammaMin);
            if (range > 0.0F) {
                result = (int)((float)(gamma_abs - this.mGammaMin) * 100.0F / range);
            }
        }

        return result;
    }

    public synchronized int getGamma() {
        return this.getGamma(nativeGetGamma(this.mNativePtr));
    }

    public synchronized void resetGamma() {
        if (this.mNativePtr != 0L) {
            nativeSetGamma(this.mNativePtr, this.mGammaDef);
        }

    }

    public synchronized void setSaturation(int saturation) {
        if (this.mNativePtr != 0L) {
            float range = (float)Math.abs(this.mSaturationMax - this.mSaturationMin);
            if (range > 0.0F) {
                nativeSetSaturation(this.mNativePtr, (int)((float)saturation / 100.0F * range) + this.mSaturationMin);
            }
        }

    }

    public synchronized int getSaturation(int saturation_abs) {
        int result = 0;
        if (this.mNativePtr != 0L) {
            this.nativeUpdateSaturationLimit(this.mNativePtr);
            float range = (float)Math.abs(this.mSaturationMax - this.mSaturationMin);
            if (range > 0.0F) {
                result = (int)((float)(saturation_abs - this.mSaturationMin) * 100.0F / range);
            }
        }

        return result;
    }

    public synchronized int getSaturation() {
        return this.getSaturation(nativeGetSaturation(this.mNativePtr));
    }

    public synchronized void resetSaturation() {
        if (this.mNativePtr != 0L) {
            nativeSetSaturation(this.mNativePtr, this.mSaturationDef);
        }

    }

    public synchronized void setHue(int hue) {
        if (this.mNativePtr != 0L) {
            float range = (float)Math.abs(this.mHueMax - this.mHueMin);
            if (range > 0.0F) {
                nativeSetHue(this.mNativePtr, (int)((float)hue / 100.0F * range) + this.mHueMin);
            }
        }

    }

    public synchronized int getHue(int hue_abs) {
        int result = 0;
        if (this.mNativePtr != 0L) {
            this.nativeUpdateHueLimit(this.mNativePtr);
            float range = (float)Math.abs(this.mHueMax - this.mHueMin);
            if (range > 0.0F) {
                result = (int)((float)(hue_abs - this.mHueMin) * 100.0F / range);
            }
        }

        return result;
    }

    public synchronized int getHue() {
        return this.getHue(nativeGetHue(this.mNativePtr));
    }

    public synchronized void resetHue() {
        if (this.mNativePtr != 0L) {
            nativeSetHue(this.mNativePtr, this.mSaturationDef);
        }

    }

    public void setPowerlineFrequency(int frequency) {
        if (this.mNativePtr != 0L) {
            nativeSetPowerlineFrequency(this.mNativePtr, frequency);
        }

    }

    public int getPowerlineFrequency() {
        return nativeGetPowerlineFrequency(this.mNativePtr);
    }

    public synchronized void setZoom(int zoom) {
        if (this.mNativePtr != 0L) {
            float range = (float)Math.abs(this.mZoomMax - this.mZoomMin);
            if (range > 0.0F) {
                int z = (int)((float)zoom / 100.0F * range) + this.mZoomMin;
                nativeSetZoom(this.mNativePtr, z);
            }
        }

    }

    public synchronized int getZoom(int zoom_abs) {
        int result = 0;
        if (this.mNativePtr != 0L) {
            this.nativeUpdateZoomLimit(this.mNativePtr);
            float range = (float)Math.abs(this.mZoomMax - this.mZoomMin);
            if (range > 0.0F) {
                result = (int)((float)(zoom_abs - this.mZoomMin) * 100.0F / range);
            }
        }

        return result;
    }

    public synchronized int getZoom() {
        return this.getZoom(nativeGetZoom(this.mNativePtr));
    }

    public synchronized void resetZoom() {
        if (this.mNativePtr != 0L) {
            nativeSetZoom(this.mNativePtr, this.mZoomDef);
        }

    }

    public synchronized void updateCameraParams() {
        if (this.mNativePtr != 0L) {
            if (this.mControlSupports == 0L || this.mProcSupports == 0L) {
                if (this.mControlSupports == 0L) {
                    this.mControlSupports = nativeGetCtrlSupports(this.mNativePtr);
                }

                if (this.mProcSupports == 0L) {
                    this.mProcSupports = nativeGetProcSupports(this.mNativePtr);
                }

                if (this.mControlSupports != 0L && this.mProcSupports != 0L) {
                    this.nativeUpdateBrightnessLimit(this.mNativePtr);
                    this.nativeUpdateContrastLimit(this.mNativePtr);
                    this.nativeUpdateSharpnessLimit(this.mNativePtr);
                    this.nativeUpdateGainLimit(this.mNativePtr);
                    this.nativeUpdateGammaLimit(this.mNativePtr);
                    this.nativeUpdateSaturationLimit(this.mNativePtr);
                    this.nativeUpdateHueLimit(this.mNativePtr);
                    this.nativeUpdateZoomLimit(this.mNativePtr);
                    this.nativeUpdateWhiteBlanceLimit(this.mNativePtr);
                    this.nativeUpdateFocusLimit(this.mNativePtr);
                }
            }
        } else {
            this.mControlSupports = this.mProcSupports = 0L;
        }

    }

    private static final void dumpControls(long controlSupports) {
        Log.i(TAG, String.format("controlSupports=%x", controlSupports));

        for(int i = 0; i < SUPPORTS_CTRL.length; ++i) {
            Log.i(TAG, SUPPORTS_CTRL[i] + ((controlSupports & (long)(1 << i)) != 0L ? "=enabled" : "=disabled"));
        }

    }

    private static final void dumpProc(long procSupports) {
        Log.i(TAG, String.format("procSupports=%x", procSupports));

        for(int i = 0; i < SUPPORTS_PROC.length; ++i) {
            Log.i(TAG, SUPPORTS_PROC[i] + ((procSupports & (long)(1 << i)) != 0L ? "=enabled" : "=disabled"));
        }

    }

    private final String getUSBFSName(USBMonitor.UsbControlBlock ctrlBlock) {
        String result = null;
        String name = ctrlBlock.getDeviceName();
        String[] v = !TextUtils.isEmpty(name) ? name.split("/") : null;
        if (v != null && v.length > 2) {
            StringBuilder sb = new StringBuilder(v[0]);

            for(int i = 1; i < v.length - 2; ++i) {
                sb.append("/").append(v[i]);
            }

            result = sb.toString();
        }

        if (TextUtils.isEmpty(result)) {
            Log.w(TAG, "failed to get USBFS path, try to use default path:" + name);
            result = "/dev/bus/usb";
        }

        return result;
    }

    private final native long nativeCreate();

    private final native void nativeDestroy(long var1);

    private final native int nativeConnect(long var1, int var3, int var4, int var5, int var6, int var7, String var8);

    private static final native int nativeRelease(long var0);

    private static final native int nativeSetStatusCallback(long var0, IStatusCallback var2);

    private static final native int nativeSetButtonCallback(long var0, IButtonCallback var2);

    private static final native int nativeSetPreviewSize(long var0, int var2, int var3, int var4, int var5, int var6, float var7);

    private static final native String nativeGetSupportedSize(long var0);

    private static final native int nativeStartPreview(long var0);

    private static final native int nativeStopPreview(long var0);

    private static final native int nativeSetPreviewDisplay(long var0, Surface var2);

    private static final native int nativeSetFrameCallback(long var0, IFrameCallback var2, int var3);

    public void startCapture(Surface surface) {
        if (this.mCtrlBlock != null && surface != null) {
            nativeSetCaptureDisplay(this.mNativePtr, surface);
        } else {
            throw new NullPointerException("startCapture");
        }
    }

    public void stopCapture() {
        if (this.mCtrlBlock != null) {
            nativeSetCaptureDisplay(this.mNativePtr, (Surface)null);
        }

    }

    private static final native int nativeSetCaptureDisplay(long var0, Surface var2);

    private static final native long nativeGetCtrlSupports(long var0);

    private static final native long nativeGetProcSupports(long var0);

    private final native int nativeUpdateScanningModeLimit(long var1);

    private static final native int nativeSetScanningMode(long var0, int var2);

    private static final native int nativeGetScanningMode(long var0);

    private final native int nativeUpdateExposureModeLimit(long var1);

    private static final native int nativeSetExposureMode(long var0, int var2);

    private static final native int nativeGetExposureMode(long var0);

    private final native int nativeUpdateExposurePriorityLimit(long var1);

    private static final native int nativeSetExposurePriority(long var0, int var2);

    private static final native int nativeGetExposurePriority(long var0);

    private final native int nativeUpdateExposureLimit(long var1);

    private static final native int nativeSetExposure(long var0, int var2);

    private static final native int nativeGetExposure(long var0);

    private final native int nativeUpdateExposureRelLimit(long var1);

    private static final native int nativeSetExposureRel(long var0, int var2);

    private static final native int nativeGetExposureRel(long var0);

    private final native int nativeUpdateAutoFocusLimit(long var1);

    private static final native int nativeSetAutoFocus(long var0, boolean var2);

    private static final native int nativeGetAutoFocus(long var0);

    private final native int nativeUpdateFocusLimit(long var1);

    private static final native int nativeSetFocus(long var0, int var2);

    private static final native int nativeGetFocus(long var0);

    private final native int nativeUpdateFocusRelLimit(long var1);

    private static final native int nativeSetFocusRel(long var0, int var2);

    private static final native int nativeGetFocusRel(long var0);

    private final native int nativeUpdateIrisLimit(long var1);

    private static final native int nativeSetIris(long var0, int var2);

    private static final native int nativeGetIris(long var0);

    private final native int nativeUpdateIrisRelLimit(long var1);

    private static final native int nativeSetIrisRel(long var0, int var2);

    private static final native int nativeGetIrisRel(long var0);

    private final native int nativeUpdatePanLimit(long var1);

    private static final native int nativeSetPan(long var0, int var2);

    private static final native int nativeGetPan(long var0);

    private final native int nativeUpdatePanRelLimit(long var1);

    private static final native int nativeSetPanRel(long var0, int var2);

    private static final native int nativeGetPanRel(long var0);

    private final native int nativeUpdateTiltLimit(long var1);

    private static final native int nativeSetTilt(long var0, int var2);

    private static final native int nativeGetTilt(long var0);

    private final native int nativeUpdateTiltRelLimit(long var1);

    private static final native int nativeSetTiltRel(long var0, int var2);

    private static final native int nativeGetTiltRel(long var0);

    private final native int nativeUpdateRollLimit(long var1);

    private static final native int nativeSetRoll(long var0, int var2);

    private static final native int nativeGetRoll(long var0);

    private final native int nativeUpdateRollRelLimit(long var1);

    private static final native int nativeSetRollRel(long var0, int var2);

    private static final native int nativeGetRollRel(long var0);

    private final native int nativeUpdateAutoWhiteBlanceLimit(long var1);

    private static final native int nativeSetAutoWhiteBlance(long var0, boolean var2);

    private static final native int nativeGetAutoWhiteBlance(long var0);

    private final native int nativeUpdateAutoWhiteBlanceCompoLimit(long var1);

    private static final native int nativeSetAutoWhiteBlanceCompo(long var0, boolean var2);

    private static final native int nativeGetAutoWhiteBlanceCompo(long var0);

    private final native int nativeUpdateWhiteBlanceLimit(long var1);

    private static final native int nativeSetWhiteBlance(long var0, int var2);

    private static final native int nativeGetWhiteBlance(long var0);

    private final native int nativeUpdateWhiteBlanceCompoLimit(long var1);

    private static final native int nativeSetWhiteBlanceCompo(long var0, int var2);

    private static final native int nativeGetWhiteBlanceCompo(long var0);

    private final native int nativeUpdateBacklightCompLimit(long var1);

    private static final native int nativeSetBacklightComp(long var0, int var2);

    private static final native int nativeGetBacklightComp(long var0);

    private final native int nativeUpdateBrightnessLimit(long var1);

    private static final native int nativeSetBrightness(long var0, int var2);

    private static final native int nativeGetBrightness(long var0);

    private final native int nativeUpdateContrastLimit(long var1);

    private static final native int nativeSetContrast(long var0, int var2);

    private static final native int nativeGetContrast(long var0);

    private final native int nativeUpdateAutoContrastLimit(long var1);

    private static final native int nativeSetAutoContrast(long var0, boolean var2);

    private static final native int nativeGetAutoContrast(long var0);

    private final native int nativeUpdateSharpnessLimit(long var1);

    private static final native int nativeSetSharpness(long var0, int var2);

    private static final native int nativeGetSharpness(long var0);

    private final native int nativeUpdateGainLimit(long var1);

    private static final native int nativeSetGain(long var0, int var2);

    private static final native int nativeGetGain(long var0);

    private final native int nativeUpdateGammaLimit(long var1);

    private static final native int nativeSetGamma(long var0, int var2);

    private static final native int nativeGetGamma(long var0);

    private final native int nativeUpdateSaturationLimit(long var1);

    private static final native int nativeSetSaturation(long var0, int var2);

    private static final native int nativeGetSaturation(long var0);

    private final native int nativeUpdateHueLimit(long var1);

    private static final native int nativeSetHue(long var0, int var2);

    private static final native int nativeGetHue(long var0);

    private final native int nativeUpdateAutoHueLimit(long var1);

    private static final native int nativeSetAutoHue(long var0, boolean var2);

    private static final native int nativeGetAutoHue(long var0);

    private final native int nativeUpdatePowerlineFrequencyLimit(long var1);

    private static final native int nativeSetPowerlineFrequency(long var0, int var2);

    private static final native int nativeGetPowerlineFrequency(long var0);

    private final native int nativeUpdateZoomLimit(long var1);

    private static final native int nativeSetZoom(long var0, int var2);

    private static final native int nativeGetZoom(long var0);

    private final native int nativeUpdateZoomRelLimit(long var1);

    private static final native int nativeSetZoomRel(long var0, int var2);

    private static final native int nativeGetZoomRel(long var0);

    private final native int nativeUpdateDigitalMultiplierLimit(long var1);

    private static final native int nativeSetDigitalMultiplier(long var0, int var2);

    private static final native int nativeGetDigitalMultiplier(long var0);

    private final native int nativeUpdateDigitalMultiplierLimitLimit(long var1);

    private static final native int nativeSetDigitalMultiplierLimit(long var0, int var2);

    private static final native int nativeGetDigitalMultiplierLimit(long var0);

    private final native int nativeUpdateAnalogVideoStandardLimit(long var1);

    private static final native int nativeSetAnalogVideoStandard(long var0, int var2);

    private static final native int nativeGetAnalogVideoStandard(long var0);

    private final native int nativeUpdateAnalogVideoLockStateLimit(long var1);

    private static final native int nativeSetAnalogVideoLoackState(long var0, int var2);

    private static final native int nativeGetAnalogVideoLoackState(long var0);

    private final native int nativeUpdatePrivacyLimit(long var1);

    private static final native int nativeSetPrivacy(long var0, boolean var2);

    private static final native int nativeGetPrivacy(long var0);

    static {
        if (!isLoaded) {
            System.loadLibrary("jpeg-turbo1500");
            System.loadLibrary("usb100");
            System.loadLibrary("uvc");
            System.loadLibrary("UVCCamera");
            isLoaded = true;
        }

        SUPPORTS_CTRL = new String[]{"D0:  Scanning Mode", "D1:  Auto-Exposure Mode", "D2:  Auto-Exposure Priority", "D3:  Exposure Time (Absolute)", "D4:  Exposure Time (Relative)", "D5:  Focus (Absolute)", "D6:  Focus (Relative)", "D7:  Iris (Absolute)", "D8:  Iris (Relative)", "D9:  Zoom (Absolute)", "D10: Zoom (Relative)", "D11: PanTilt (Absolute)", "D12: PanTilt (Relative)", "D13: Roll (Absolute)", "D14: Roll (Relative)", "D15: Reserved", "D16: Reserved", "D17: Focus, Auto", "D18: Privacy", "D19: Focus, Simple", "D20: Window", "D21: Region of Interest", "D22: Reserved, set to zero", "D23: Reserved, set to zero"};
        SUPPORTS_PROC = new String[]{"D0: Brightness", "D1: Contrast", "D2: Hue", "D3: Saturation", "D4: Sharpness", "D5: Gamma", "D6: White Balance Temperature", "D7: White Balance Component", "D8: Backlight Compensation", "D9: Gain", "D10: Power Line Frequency", "D11: Hue, Auto", "D12: White Balance Temperature, Auto", "D13: White Balance Component, Auto", "D14: Digital Multiplier", "D15: Digital Multiplier Limit", "D16: Analog Video Standard", "D17: Analog Video Lock Status", "D18: Contrast, Auto", "D19: Reserved. Set to zero", "D20: Reserved. Set to zero", "D21: Reserved. Set to zero", "D22: Reserved. Set to zero", "D23: Reserved. Set to zero"};
    }
}
