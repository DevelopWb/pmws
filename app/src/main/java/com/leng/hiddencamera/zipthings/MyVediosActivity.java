package com.leng.hiddencamera.zipthings;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.leng.hiddencamera.home.PmwsSetActivity;
import com.leng.hiddencamera.R;
import com.leng.hiddencamera.util.DCPubic;
import com.leng.hiddencamera.util.PmwsLog;
import com.leng.hiddencamera.zipthings.decrypted.DecryptedFileService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * @aouther tobato
 * @description ����  ¼�Ƶ��ļ�
 * @date 2020/10/6 22:36
 */

public class MyVediosActivity extends ListActivity {

    String TAG = "MyVediosActivity";
    /**
     * �ļ����У�����
     */
    private List<String> items = null;
    /**
     * �ļ����У�·��
     */
    private List<String> paths = null;
    /**
     * ��Ŀ¼
     **/
    private String rootPath = "/";

    /**
     * ��ʾ��ǰĿ¼
     **/
    private TextView mPath;

    /**
     * Notification��ID
     */
    int notifyId = 102;
    /**
     * Notification�Ľ�������ֵ
     */
    int progress = 0;

    NotificationCompat.Builder mBuilder;
    public NotificationManager mNotificationManager;
    private TextView textView;
    private Dialog mDialog;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.fileselect);
        mDialog = DCPubic.getProgressDialog(this, "���ڽ��ܣ����Ժ�...");
        mPath = (TextView) findViewById(R.id.mPath);
        textView = (TextView) findViewById(R.id.cancel_tv_);
        //		findViewById(R.id.buttonConfirm).setOnClickListener(this);
        //		findViewById(R.id.buttonCancle).setOnClickListener(this);
        sp = getSharedPreferences("videoPath", MODE_PRIVATE);
        String path = sp.getString("videoPath", "/mnt/sdcard/MyData");
        getFileDir(path); // curPath
        registerDialogDismissReceiver();

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        PmwsSetActivity.RECORD_DIALOG = 1;
        finish();

    }

    private void registerDialogDismissReceiver() {
        IntentFilter intentFilter = new IntentFilter("DialogDismiss");
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    //	public void finish_MyFile(View v) {
    //		finish();
    //	}

    /**
     * ��ȡָ��Ŀ¼�µ������ļ�(��)
     *
     * @param filePath
     */
    private void getFileDir(String filePath) {
        mPath.setText(filePath);
        items = new ArrayList<String>();
        paths = new ArrayList<String>();
        File f = new File(filePath);
        File[] files = f.listFiles();

        // ������ʾ �����ظ�Ŀ¼��+"�ϼ�Ŀ¼"
        if (!filePath.equals(rootPath)) {
            items.add("rootPath");
            paths.add(rootPath);

            items.add("parentPath");
            paths.add(f.getParent());
        }

        // ������
        List<File> resultList = null;
        if (files != null) {
            Log.i("hnyer", files.length + " " + filePath);
            resultList = new ArrayList<File>();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (!file.getName().startsWith(".")) {
                    resultList.add(file);
                }
            }

            //
            Collections.sort(resultList, new Comparator<File>() {
                @Override
                public int compare(File bean1, File bean2) {
                    return bean1.getName().toLowerCase()
                            .compareTo(bean2.getName().toLowerCase());

                }
            });

            for (int i = 0; i < resultList.size(); i++) {
                File file = resultList.get(i);
                items.add(file.getName());
                paths.add(file.getPath());
            }
        } else {
            Log.i("hnyer", filePath + "�����ļ�");
        }

        setListAdapter(new MyAdapter(this, items, paths));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        File file = new File(paths.get(position));
        //       ���ܲ�����
        if (file.isDirectory()) {
            getFileDir(paths.get(position));
        } else {
            openFile(file);
        }
    }

    /**
     * ����ļ��Ƿ��ѱ�����
     *
     * @param file
     */
    private boolean checkFileIsDecryped(File file) {
        List<String> fileNamesMp4 = new ArrayList<String>();
        for (String path : paths) {
            File f = new File(path);
            String name = f.getName();
            if (name.endsWith("mp4")) {
                String nameMp4 = name.substring(0, name.lastIndexOf("."));
                fileNamesMp4.add(nameMp4);
            }

        }
        if (fileNamesMp4.contains(file.getName().substring(0, file.getName().lastIndexOf(".")))) {
            Toast.makeText(this, "����Ƶ�ļ��Ѿ����ܹ���", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        Log.i("MyVediosActivity", "MyVediosActivity onDestroy");
        //		PmwsSetActivity.RECORD_DIALOG=0;
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    private void openFile(File f) {


        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        String type = getMIMEType(f);
        if (type.equals("m9xs/*")) {
            if (checkFileIsDecryped(f)) {
                return;
            }
            SharedPreferences targetPath = getSharedPreferences("targetPath", 0);

            SharedPreferences.Editor editor = targetPath.edit();

            editor.putString("target", f.getAbsolutePath());

            editor.commit();

            intent = new Intent(getApplicationContext(), DecryptedFileService.class);

            startService(intent);
            mDialog.show();
            PmwsLog.writeLog("decrypt fiel and play��", f);
            return;

        } else {
            //mp4��ʽ����
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= 24) {//7.0 Android N
                //com.xxx.xxx.fileproviderΪ����manifest��provider��������ͬ
                uri = FileProvider.getUriForFile(getApplicationContext(), "com.example.pmws.fileProvider", f);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//7.0�Ժ�ϵͳҪ��������ʱuri
                // ��ȡȨ�ޣ���װ����Ժ�ϵͳ���Զ��ջ�Ȩ�ޣ��ù���û���û�����
            } else {//7.0����
                uri = Uri.fromFile(f);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            intent.setDataAndType(uri, type);
            startActivity(intent);
        }

    }

    // ��������Ĵ��ھ���Ϊ�������ǲ����ض����ļ�������zip,����ǵĻ��ٽ��в����ģ�Ҫ�ǲ��ǵĻ��Ͳ���������������ֻ�Ƕ����ض����ļ����в�����
    // ���һ�Ҫ��Ҫ�����о���������@���|����߀��ȥ�о��V��

    /**
     * @param f
     * @return ���ص����ļ���׺����ԭ����׺��ҪСЩ����д����û�ã����Ը���û�Ƚϵ����Ǻ���
     */
    private String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();

        // ������ѹ�õ���Ƶ�ļ�����Ȼ�󱣴浽SharedPreferences
        SharedPreferences tmpFileName = getSharedPreferences("tmpFileName", 0);

        SharedPreferences.Editor editor = tmpFileName.edit();
        String newNameString = f.getAbsolutePath().replace("m9xs", "mp4");

        editor.putString("tmpFileName", newNameString);

        editor.commit();

        String end = fName
                .substring(fName.lastIndexOf(".") + 1, fName.length())
                .toLowerCase();

        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
                || end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            type = "audio";
        } else if (end.equals("3gp") || end.equals("mp4")) {
            type = "video";
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
                || end.equals("jpeg") || end.equals("bmp")) {
            type = "image";
        } else if (end.equals("m9xs")) {

            type = "m9xs";

        } else {
            type = "*";
        }

        type += "/*";
        return type;
    }


    @Override
    protected void onResume() {
        super.onResume();
        sp = getSharedPreferences("videoPath", MODE_PRIVATE);
        String path = sp.getString("videoPath", "/mnt/sdcard/MyData");
        getFileDir(path); // curPath
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && !TextUtils.isEmpty(action)) {
                if (action.equals("DialogDismiss")) {
                    mDialog.dismiss();
                }
            }
        }
    };
}