package com.leng.hiddencamera.other;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.leng.hiddencamera.home.CameraRecordService;

public class MyServiceStart extends IntentService {


    //------------------必须实现-----------------------------

    public MyServiceStart() {
        super("myIntentService");
        // 注意构造函数参数为空，这个字符串就是worker thread的名字
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        Intent startIntent = new Intent(CameraRecordService.ACTION_START);
        startIntent.setClass(getBaseContext(), CameraRecordService.class);
        startService(startIntent);



        //根据Intent的不同进行不同的事务处理
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

    //--------------------用于打印生命周期--------------------
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
