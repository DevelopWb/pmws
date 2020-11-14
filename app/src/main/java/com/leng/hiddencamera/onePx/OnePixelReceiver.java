package com.leng.hiddencamera.onePx;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @Author: tobato
 * @Description: ��������
 * @CreateDate: 2020/10/19 21:51
 * @UpdateUser: ������
 * @UpdateDate: 2020/10/19 21:51
 */
public class OnePixelReceiver extends BroadcastReceiver {
    private static final String TAG = "[OnePixelReceiver]";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        OnePixelManager manager = OnePixelManager.getInstance();
        if (Intent.ACTION_SCREEN_ON.equals(action)){//�����������ر�1����Activity
            manager.finishOnePixelActivity();
        }else if(Intent.ACTION_SCREEN_OFF.equals(action)){//���Ϣ��������1����Activity
            manager.startOnePixelActivity(context);
        }
    }
}