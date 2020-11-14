package com.leng.hiddencamera.onePx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.lang.ref.WeakReference;

/**
 * @Author: tobato
 * @Description: ��������  1px������
 * @CreateDate: 2020/10/19 21:50
 * @UpdateUser: ������
 * @UpdateDate: 2020/10/19 21:50
 */
public class OnePixelManager {
    private static final String TAG = "[OnePixelManager]";
    private WeakReference<Activity> mActivity;
    private OnePixelReceiver onePixelReceiver;


    public  static  OnePixelManager getInstance(){
        return OnePixelManagerHolder.onePixelManager;
    }
    private static class  OnePixelManagerHolder {

        public static  OnePixelManager  onePixelManager = new OnePixelManager();

    }

    /**
     * һ���ع㲥������ע�᷽�����÷����г�ʼ��OnePixelReceiver��������˹�������
     * ��ĻϢ����������Ȼ��ע��ù㲥������
     * @param context
     */
    public void registerOnePixelReceiver(Context context){
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        onePixelReceiver = new OnePixelReceiver();
        context.registerReceiver(onePixelReceiver,filter);
    }

    /**
     * �Թ㲥�����߽��н�ע��
     * @param context
     */
    public void unregisterOnePixelReceiver(Context context){
        if (null != onePixelReceiver){
            context.unregisterReceiver(onePixelReceiver);
        }
    }

    /**
     * ����һ����Activity
     * @param context
     */
    public void startOnePixelActivity(Context context){
        Intent intent = new Intent();
        intent.setClass(context,OnePixelActivity.class);
        context.startActivity(intent);
    }

    /**
     * �ر�һ����Activity
     */
    public void finishOnePixelActivity(){
        if(null!=mActivity){
            Activity activity = mActivity.get();
            if(null!=activity){
                activity.finish();
            }
            mActivity = null;
        }
    }

    /**
     * ʹ�������û�ȡһ���ص�������
     * @param activity
     */
    public void setKeepAliveReference(OnePixelActivity activity){
        mActivity = new WeakReference<Activity>(activity);
    }

}
