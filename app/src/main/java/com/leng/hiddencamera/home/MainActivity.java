package com.leng.hiddencamera.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.juntai.wisdom.basecomponent.utils.ActivityManagerTool;
import com.juntai.wisdom.basecomponent.utils.HawkProperty;
import com.juntai.wisdom.basecomponent.utils.ToastUtils;
import com.leng.hiddencamera.util.DCPubic;
import com.orhanobut.hawk.Hawk;
import com.regmode.RegLatestContact;
import com.regmode.Utils.RegOperateManager;

public class MainActivity extends Activity {

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        acquireWakeLock();
        ActivityManagerTool.getInstance().finishApp();
        if (!Hawk.contains(HawkProperty.REG_CODE)) {
            ToastUtils.toast(this, "您还没有注册，请先注册！");
            this.finish();
            return;
        }
        new RegOperateManager(this,
                new RegLatestContact.CancelCallBack() {
                    @Override
                    public void toFinishActivity() {
                        finish();
                    }

                    @Override
                    public void toDoNext(String input) {
                        if (DCPubic.sIsRecording) {
                            startCameraService(CameraRecordService.ACTION_RECORDING);
                        } else {
                            startCameraService(CameraRecordService.ACTION_START);
                        }
                    }
                });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //        releaseWakeLock();

    }


    /**
     * 启动service
     *
     * @param actionRecording
     */
    private void startCameraService(String actionRecording) {
        Intent startIntent = new Intent(
                actionRecording);
        startIntent.setClass(this, CameraRecordService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            startService(startIntent);
        } else {
            // Pre-O behavior.
            startService(startIntent);
        }
        finish();
    }

}

