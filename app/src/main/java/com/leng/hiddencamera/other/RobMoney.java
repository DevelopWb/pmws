package com.leng.hiddencamera.other;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import com.leng.hiddencamera.home.CameraRecordService;

import org.greenrobot.eventbus.EventBus;

/**
 * @aouther tobato
 * @description 描述 辅助服务相关服务
 * @date 2021/1/4 15:06
 */

public class RobMoney extends AccessibilityService {
    private static final String TAG = "qweqwe";

    @Override
    protected boolean onKeyEvent(KeyEvent event) {

        int key = event.getKeyCode();
        final int action = event.getAction();
        final boolean isDown = action == KeyEvent.ACTION_DOWN;
        if (key == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (isDown) {
                Log.i(TAG, "onKeyEvent-down"+isDown);
                EventBus.getDefault().post(CameraRecordService.KEYCODE_VOLUME_DOWN);
            }
            return true;
        }
        return super.onKeyEvent(null);
    }


    @Override
    public void onInterrupt() {
        Log.i("QWEQWE", "onInterrupt");
    }

    @Override
    public void onCreate() {
        Log.i("QWEQWE", "RobMoney::onCreate");

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
//        Log.i("QWEQWE", "RobMoney::onAccessibilityEvent");
    }


}