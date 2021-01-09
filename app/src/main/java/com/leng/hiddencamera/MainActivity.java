package com.leng.hiddencamera;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.juntai.wisdom.basecomponent.utils.ActivityManagerTool;
import com.juntai.wisdom.basecomponent.utils.HawkProperty;
import com.juntai.wisdom.basecomponent.utils.ToastUtils;
import com.leng.hiddencamera.home.CameraRecordService;
import com.leng.hiddencamera.home.UVCCameraService;
import com.leng.hiddencamera.util.DCPubic;
import com.orhanobut.hawk.Hawk;
import com.regmode.RegLatestContact;
import com.regmode.Utils.RegOperateManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends Activity {
    private ServiceConnection connUVC;
    private UVCCameraService mUvcService;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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
            startForegroundService(startIntent);
        } else {
            // Pre-O behavior.
            startService(startIntent);
        }
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}

