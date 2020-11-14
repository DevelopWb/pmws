package com.leng.hiddencamera.zipthings;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.leng.hiddencamera.PmwsSetActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ZipFileService extends IntentService {

    public static String path = "mnt/sdcard/MyData";
    private static String password = "fls94#@AB";
    Handler handler;
    private String TAG = "ZipFileService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public ZipFileService() {
        super("");
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        List<String> fList = getFileList(PmwsSetActivity.SAVED_VIDEO_PATH, "mp4");  //path


        if (fList.size() == 0) {
            Log.i(TAG, "��Ƶ�ļ��ĸ���Ϊ0����ִ�м��ܲ���");
            return;
        }
        for (int i = 0; i < fList.size(); i++) {
            Log.i(TAG, "���ܵ�ʱ���ļ���=" + fList.get(i));
            final String temFileName = fList.get(i);
            String newFileName = temFileName.replace(".mp4", "");
            Log.i(TAG, "���ܿ�ʼ��ʱ��" + GetTime());

            //7.25 ���ĵ��µļ��ܷ���
            try {
                AddFilesWithAESEncryption.damageFile(newFileName + ".m9xs", fList.get(i));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.i(TAG, "���ܵ�ʱ��׽���쳣");

                //Ҫ�ǿռ䲻���ʱ����¼��
                if (PmwsSetActivity.sIsRecording) {
                    Intent intent_ = new Intent("com.leng.hiddencamera.CameraService.RECEIVER");
                    sendBroadcast(intent_);
                }


                AlertActivity.MESSAGE = "�洢�ռ䲻�㲻�ܼ��ܣ��������" + FormetFileSize(getFileSize(temFileName) + 800 * 1024 * 1024) + "�ռ�֮���ֶ�����";
                //��dialog�ķ�ʽչʾһ��activity
                Intent it = new Intent(getApplicationContext(), AlertActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(it);


                //����ʧ��֮����ɾ��û�гɹ����ļ�����֪��Ϊʲôû�гɹ����Ժ�����
                File failedfile = new File(newFileName + ".m9xs");
                Log.i(TAG, "����������֮����ļ���" + newFileName + ".m9xs");
                if (failedfile.exists()) {
                    failedfile.delete();
                }

                Log.i(TAG, "failedfile.exists=" + String.valueOf(failedfile.exists()));
                e.printStackTrace();
                return;
            }


//
            Log.i(TAG, "���ܽ�����ʱ��" + GetTime());
//
            // ��������֮��ɾ��Դ�ļ�
            File file = new File(fList.get(i));
            file.delete();


        }
        //�ر�dialog
        Intent intentCloseDialog = new Intent("CloseDialog");
        sendBroadcast(intentCloseDialog);

        //�µķ���֪ͨ�Ĵ���


        //ע���Ժ���ʾ�������Notification����ע����������ʾ
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(
//                        getApplicationContext());
//
//                // ����֪ͨ�Ļ�����Ϣ��icon�����⡢����
//                builder.setSmallIcon(R.drawable.app_icon);
//                builder.setContentTitle("��Ļ��ʿ");
//                builder.setContentText("�������");
//
//
//                Notification notification = builder.build();
//                // ����֪ͨ id ��Ҫ��Ӧ����Ψһ
//                NotificationManager notificationManager = (NotificationManager) getSystemService
//                        (Context.NOTIFICATION_SERVICE);
//                notificationManager.notify(1, notification);


        Log.i(TAG, "����ִ�����");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    public static List<String> getFileList(String strPath, String endsWith) {
        List<String> filelist = new ArrayList<String>();
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // ���ļ�Ŀ¼���ļ�ȫ����������
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) { // �ж����ļ������ļ���
                    getFileList(files[i].getAbsolutePath(), endsWith); // ��ȡ�ļ�����·��
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
                "yyyy��MM��dd��    HH:mm:ss     ");
        Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
        String str = formatter.format(curDate);
        return str;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "ZipFileService is Desotrying");
        super.onDestroy();

//        // ��Service������ر�AlarmManager
//        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        Intent i = new Intent(this, AlarmReceiver.class);
//        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
//        manager.cancel(pi);

    }


    /**
     * ��ȡָ���ļ���С
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

            Log.i("ZipFileService", "�ļ�������!");
        }
        return size;
    }


    /**
     * ת���ļ���С
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