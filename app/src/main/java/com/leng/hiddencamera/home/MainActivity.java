package com.leng.hiddencamera.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;

import com.juntai.wisdom.basecomponent.utils.HawkProperty;
import com.juntai.wisdom.basecomponent.utils.ToastUtils;
import com.orhanobut.hawk.Hawk;
import com.regmode.RegLatestContact;
import com.regmode.Utils.RegOperateManager;

public class MainActivity extends Activity {

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        acquireWakeLock();
        String strreg = Hawk.get(HawkProperty.REG_CODE);
        if ("".equals(strreg) || TextUtils.isEmpty(strreg)) {
            ToastUtils.toast(this, "����û��ע�ᣬ����ע�ᣡ");
            this.finish();
            return;
        } else {
            RegOperateManager regOperateManager = new RegOperateManager(this);
            regOperateManager.setCallBack(new RegLatestContact.CancelCallBack() {
                @Override
                public void toFinishActivity() {
                    finish();
                }

                @Override
                public void toDoNext(String input) {
                    //                    //�������ط���
                    //                    Intent localIntent = new Intent(MainActivity.this, LocalService.class);
                    //                    //�����ػ�����
                    //                    startService(localIntent);
                    if (PmwsSetActivity.sIsRecording) {
                        startCameraService(CameraRecordService.ACTION_RECORDING);
                    } else {
                        startCameraService(CameraRecordService.ACTION_START);
                    }
                }
            });

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseWakeLock();

    }

    private PowerManager.WakeLock wakeLock;

    /**
     * ��ֹCUP����
     */
    private void acquireWakeLock() {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
            wakeLock.acquire();
        }
    }


    /**
     * �ͷ�CPU������
     */
    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    /**
     * ����service
     *
     * @param actionRecording
     */
    private void startCameraService(String actionRecording) {
        Intent startIntent = new Intent(
                actionRecording);
        startIntent.setClass(getBaseContext(), CameraRecordService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            startService(startIntent);
        } else {
            // Pre-O behavior.
            startService(startIntent);
        }
        finish();
    }

}

