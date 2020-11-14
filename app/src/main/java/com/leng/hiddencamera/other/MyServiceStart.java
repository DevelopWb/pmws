package com.leng.hiddencamera.other;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.leng.hiddencamera.home.CameraRecordService;

public class MyServiceStart extends IntentService {


    //------------------����ʵ��-----------------------------

    public MyServiceStart() {
        super("myIntentService");
        // ע�⹹�캯������Ϊ�գ�����ַ�������worker thread������
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        Intent startIntent = new Intent(CameraRecordService.ACTION_START);
        startIntent.setClass(getBaseContext(), CameraRecordService.class);
        startService(startIntent);



        //����Intent�Ĳ�ͬ���в�ͬ��������
//        String  taskName = intent.getExtras().getString("taskName");
//        switch (taskName) {
//            case "task1":
//                Log.i("myIntentService", "do task1");
//                break;
//            case "task2":
//                Log.i("myIntentService", "do task2");
//                break;
//            default:
//                break;
//        }
        Log.i("myIntentService", "do task2");
    }

    //--------------------���ڴ�ӡ��������--------------------
    @Override
    public void onCreate() {
        Log.i("myIntentService", "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("myIntentService", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("myIntentService", "onDestroy");
        super.onDestroy();
    }
}
