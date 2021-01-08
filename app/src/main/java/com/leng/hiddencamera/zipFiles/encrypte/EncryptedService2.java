package com.leng.hiddencamera.zipFiles.encrypte;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.leng.hiddencamera.R;
import com.leng.hiddencamera.util.DCPubic;
import com.leng.hiddencamera.zipFiles.AddFilesWithAESEncryption;
import com.leng.hiddencamera.zipFiles.AlertActivity;

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

    private String TAG = "ZipFileService";
    ExecutorService service = Executors.newSingleThreadExecutor();
    public static String POINT_SUFFIX_NAME = ".m9xs";//后缀名
    public static String SUFFIX_NAME = "m9xs";//后缀名

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
        List<String> fList = getFileList(DCPubic.getRecordPath(), "mp4");  //path

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
                AddFilesWithAESEncryption.damageFile(newFileName + EncryptedService2.POINT_SUFFIX_NAME, fList.get(i));
                Log.i("QWEQWE", "加密的时候捕捉到异常1");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.i("QWEQWE", "加密的时候捕捉到异常2");

                //要是空间不足的时候还在录制
                if (DCPubic.sIsRecording) {
                    Intent intent = new Intent("com.leng.hiddencamera.CameraService.RECEIVER");
                    sendBroadcast(intent);
                }

                AlertActivity.MESSAGE = "存储空间不足不能加密，请清理出" + FormetFileSize(getFileSize(temFileName) + 800 * 1024 * 1024) + "空间之后手动加密";
                //以dialog的方式展示一个activity
                Intent it = new Intent(getApplicationContext(), AlertActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);


                //加密失败之后想删除没有成功的文件，不知道为什么没有成功，以后再试
                File failedfile = new File(newFileName + EncryptedService2.POINT_SUFFIX_NAME);
                Log.i(TAG, "如果加密完成之后的文件名" + newFileName + EncryptedService2.POINT_SUFFIX_NAME);
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