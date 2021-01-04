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
        startUvcService();

    }

    /**
     * 开启uvc服务
     */
    private void startUvcService() {
        startService(new Intent(this, UVCCameraService.class));
        if (connUVC == null) {
            connUVC = new ServiceConnection() {


                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    mUvcService = ((UVCCameraService.LocalBinder) iBinder).getService();
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {

                }
            };
        }
        bindService(new Intent(this, UVCCameraService.class), connUVC, 0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receivedStringMsg(String msg) {
        switch (msg) {
            case "onAttach":
                //                Toast.makeText(getApplicationContext(),"Attached",Toast.LENGTH_SHORT).show();
                break;
            case "onConnect":
                //                Toast.makeText(getApplicationContext(),"connect",Toast.LENGTH_SHORT).show();
                break;
            case "onDisconnect":
                break;
            default:
                break;
        }
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
        if (connUVC != null) {
            unbindService(connUVC);
            connUVC = null;
        }
    }
}

