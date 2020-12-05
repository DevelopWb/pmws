package com.leng.hiddencamera.zipthings.encrypte;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.leng.hiddencamera.R;
import com.leng.hiddencamera.home.PmwsSetActivity;
import com.leng.hiddencamera.zipthings.AddFilesWithAESEncryption;
import com.leng.hiddencamera.zipthings.AlertActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EncryptedService2 extends Service {

    public static String path = "mnt/sdcard/MyData";
    private static String password = "fls94#@AB";
    Handler handler;
    private String TAG = "ZipFileService";
    ExecutorService service = Executors.newSingleThreadExecutor();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        service.execute(new Runnable() {
            @Override
            public void run() {
                xxxxx();
                Log.i("QWEQWE", "ASDASASA");

            }
        });


        return super.onStartCommand(intent, flags, startId);

    }

    private void xxxxx() {
        List<String> fList = getFileList(PmwsSetActivity.SAVED_VIDEO_PATH, "mp4");  //path

        if (fList.size() == 0) {
            Log.i(TAG, "视频文件的个数为0，不执行加密操作");
            return;
        }
        for (int i = 0; i < fList.size(); i++) {
            Log.i(TAG, "加密的时候文件名=" + fList.get(i));
            final String temFileName = fList.get(i);
            String newFileName = temFileName.replace(".mp4", "");
            Log.i(TAG, "加密开始的时间" + GetTime());

            //7.25 更改的新的加密方法
            try {
                AddFilesWithAESEncryption.damageFile(newFileName + ".m9xs", fList.get(i));
                Log.i("QWEQWE", "加密的时候捕捉到异常1");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.i("QWEQWE", "加密的时候捕捉到异常2");

                //要是空间不足的时候还在录制
                if (PmwsSetActivity.sIsRecording) {
                    Intent intent = new Intent("com.leng.hiddencamera.CameraService.RECEIVER");
                    sendBroadcast(intent);
                }


                //发送Toast的方法
                //                         handler=new Handler(Looper.getMainLooper());
                //                        handler.post(new Runnable(){
                //                            public void run(){
                //                                Toast.makeText(getApplicationContext(),"存储空间不足不能加密，请清理出"+FormetFileSize(getFileSize(temFileName)+800*1024*1024)+"空间之后手动加密",Toast.LENGTH_LONG).show();
                //                            }
                //                        });

                //
                AlertActivity.MESSAGE = "存储空间不足不能加密，请清理出" + FormetFileSize(getFileSize(temFileName) + 800 * 1024 * 1024) + "空间之后手动加密";
                //以dialog的方式展示一个activity
                Intent it = new Intent(getApplicationContext(), AlertActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);


                //加密失败之后想删除没有成功的文件，不知道为什么没有成功，以后再试
                File failedfile = new File(newFileName + ".m9xs");
                Log.i(TAG, "如果加密完成之后的文件名" + newFileName + ".m9xs");
                if (failedfile.exists()) {
                    failedfile.delete();
                }

                Log.i(TAG, "failedfile.exists=" + String.valueOf(failedfile.exists()));
                e.printStackTrace();
                stopSelf();
                return;
            }


            //
            Log.i(TAG, "加密结束的时间" + GetTime());
            //
            // 加密完了之后删除源文件
            File file = new File(fList.get(i));
            file.delete();


        }


        //新的发送通知的代码

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext())

                // 设置通知的基本信息：icon、标题、内容
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("指南针")
                .setContentText("加密完成")
                .setAutoCancel(true);


        // 发送通知 id 需要在应用内唯一
        NotificationManager notificationManager = (NotificationManager) getSystemService
                (Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());


        Log.i(TAG, "加密执行完毕");

        stopSelf(); //结束的时候销毁service
    }


    public static List<String> getFileList(String strPath, String endsWith) {
        List<String> filelist = new ArrayList<String>();
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    getFileList(files[i].getAbsolutePath(), endsWith); // 获取文件绝对路径
                } else if (fileName.endsWith(endsWith)) {
                    String strFileName = files[i].getAbsolutePath();
                    System.out.println(strFileName);
                    filelist.add(strFileName);
                } else {
                    continue;
                }
            }

        }
        System.out.println(filelist.size());
        return filelist;
    }

    private String GetTime() {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy年MM月dd日    HH:mm:ss     ");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "ZipFileService is Desotrying");
        super.onDestroy();

        //        // 在Service结束后关闭AlarmManager
        //        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //        Intent i = new Intent(this, AlarmReceiver.class);
        //        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        //        manager.cancel(pi);

    }


    /**
     * 获取指定文件大小
     *
     * @param filename
     * @return
     * @throws Exception
     */
    public static long getFileSize(String filename) {
        File file = new File(filename);
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                size = fis.available();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            Log.i("ZipFileService", "文件不存在!");
        }
        return size;
    }


    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }


}