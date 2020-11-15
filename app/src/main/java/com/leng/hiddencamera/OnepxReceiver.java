package com.leng.hiddencamera;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;


/**
 * һ���ع㲥������
 */
public final class OnepxReceiver extends BroadcastReceiver {
    android.os.Handler mHander;
    boolean screenOn = true;

    public OnepxReceiver() {
        mHander = new android.os.Handler(Looper.getMainLooper());
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {    //��Ļ�رյ�ʱ����ܵ��㲥
            screenOn = false;
            mHander.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!screenOn){
                        Log.d("8888888","��Ļ�ر�  ����1px");
                        Intent intent2 = new Intent(context, OnePixelActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);
                        try {
                            pendingIntent.send();
                            /*} catch (PendingIntent.CanceledException e) {*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            },1000);
            //֪ͨ��Ļ�ѹرգ���ʼ������������
            context.sendBroadcast(new Intent("_ACTION_SCREEN_OFF"));
            Log.d("8888888","��Ļ�ر�  �㲥��Ļ�رյ�֪ͨ��������������");
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {   //��Ļ�򿪵�ʱ���͹㲥  ����һ����
            screenOn = true;
            //֪ͨ��Ļ�ѵ�����ֹͣ������������
            context.sendBroadcast(new Intent("_ACTION_SCREEN_ON"));
            Log.d("8888888","��Ļ����  �㲥��Ļ������֪ͨ���ر���������");

        }
    }
}
