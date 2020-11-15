package com.leng.hiddencamera;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public final class OnePixelActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("8888888","1px activity  ����");
        //�趨һ���ص�activity
        Window window = getWindow();
        window.setGravity(Gravity.START | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkScreenOn("onResume");
    }
    private void checkScreenOn(String methodName) {
        try{
            PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = pm.isScreenOn();
            if (isScreenOn) {
                finish();
            }
        }catch (Exception e){}
    }
}
