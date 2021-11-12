package com.leng.hiddencamera.mine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.juntai.wisdom.basecomponent.utils.HawkProperty;
import com.leng.hiddencamera.other.MyService;
import com.orhanobut.hawk.Hawk;



/**
 * @Author: tobato
 * @Description: 作用描述
 * @CreateDate: 2020/3/27 20:11
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/3/27 20:11
 */

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
         boolean autoRun =    Hawk.get(HawkProperty.AUTO_RUN,true);
            if (autoRun) {
                startMyService(context);

            }

        }
    }

    /**
     * 启动service
     *
     */
    private void startMyService(Context context) {
        Intent startIntent = new Intent(MyService.ACTION_START);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startIntent.setClass(context, MyService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(startIntent);
        } else {
            // Pre-O behavior.
            context.startService(startIntent);
        }
    }
}
