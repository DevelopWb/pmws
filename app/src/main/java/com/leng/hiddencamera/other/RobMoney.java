package com.leng.hiddencamera.other;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by Administrator on 2017/4/28.
 */

public class RobMoney extends AccessibilityService {
    private static final String TAG = "dxj";
   boolean  isX=false;
    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.i(TAG, "onKeyEvent");
        int key = event.getKeyCode();

            if (key == KeyEvent.KEYCODE_VOLUME_DOWN) {
                if (!isX) {
                    Intent intent = new Intent("asasqwe");
                    intent.putExtra("ABC", "KEYCODE_VOLUME_DOWN");
                    sendBroadcast(intent);
                    isX=true;
                    xxx();}
                return true;
            } else if (key == KeyEvent.KEYCODE_VOLUME_UP) {

//                Intent intent2 = new Intent("asasqwe");
//                intent2.putExtra("ABC", "KEYCODE_VOLUME_UP");
//                sendBroadcast(intent2);
//                return true;
            }
            return super.onKeyEvent( null);}

    private  void xxx(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                isX=false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    public void onInterrupt() {
          Log.i("QWEQWE","onInterrupt");
    }

    @Override
    public void onCreate() {
        Log.i("QWEQWE", "RobMoney::onCreate");
        // Toast.makeText(this, "RobMoney::onCreate", Toast.LENGTH_SHORT).show();

        //?????????????
//        CameraRecordService.ValumeTest valumeTest = new  CameraRecordService.ValumeTest();
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("asasqwe");
//        registerReceiver(valumeTest, intentFilter);

    }

    /**
     * ?????????仯
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // TODO Auto-generated method stub
//        Log.i("QWEQWE", "onAccessibilityEvent");

    }

    
}