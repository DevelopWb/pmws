package com.leng.hiddencamera.zipFiles;

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

import com.leng.hiddencamera.R;
import com.leng.hiddencamera.util.DCPubic;
import com.leng.hiddencamera.zipFiles.decrypted.DecryptedFileService;
import com.leng.hiddencamera.zipFiles.encrypte.EncryptedService2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class MyVediosActivity extends ListActivity {

    String TAG = "MyFileManager";
    /**
     * 文件（夹）名字
     */
    private List<String> items = null;
    /**
     * 文件（夹）路径
     */
    private List<String> paths = null;
    /**
     * 根目录
     **/
    private String rootPath = "/";

    /**
     * 显示当前目录
     **/
    private TextView mPath;

    /**
     * Notification的ID
     */
    int notifyId = 102;
    /**
     * Notification的进度条数值
     */
    int progress = 0;

    NotificationCompat.Builder mBuilder;
    public NotificationManager mNotificationManager;
    private TextView textView;
    private Dialog mDialog;
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fileselect);
        mDialog = DCPubic.getProgressDialog(this, "正在解密，请稍后...");
        mPath = (TextView) findViewById(R.id.mPath);
        textView = (TextView) findViewById(R.id.cancel_tv_);
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
        DCPubic.RECORD_DIALOG = 1;
        finish();

    }

    private void registerDialogDismissReceiver() {
        IntentFilter intentFilter = new IntentFilter("DialogDismiss");
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    /**
     * 获取指定目录下的所有文件(夹)
     *
     * @param filePath
     */
    private void getFileDir(String filePath) {
        mPath.setText(filePath);
        items = new ArrayList<String>();
        paths = new ArrayList<String>();
        File f = new File(filePath);
        File[] files = f.listFiles();

        // 用来显示 “返回根目录”+"上级目录"
        if (!filePath.equals(rootPath)) {
            items.add("rootPath");
            paths.add(rootPath);

            items.add("parentPath");
            paths.add(f.getParent());
        }

        // 先排序
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
            Log.i("hnyer", filePath + "无子文件");
        }

        setListAdapter(new MyAdapter(this, items, paths));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        File file = new File(paths.get(position));
        //       解密并播放
        if (file.isDirectory()) {
            getFileDir(paths.get(position));
        } else {
            openFile(file);
        }
    }

    /**
     * 检测文件是否已被解密
     * @param file
     */
    private boolean checkFileIsDecryped(File file) {
        List<String> fileNamesMp4 = new ArrayList<String>();
        for (String path : paths) {
            File f = new File(path);
            String name = f.getName();
            if (name.endsWith("mp4")) {
                String nameMp4 =   name.substring(0,name.lastIndexOf("."));
                fileNamesMp4.add(nameMp4);
            }

        }
        if (fileNamesMp4.contains(file.getName().substring(0,file.getName().lastIndexOf(".")))) {
            Toast.makeText(this, "此视频文件已经解密过了", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }


    @Override
    protected void onDestroy() {
        Log.i("MyFileManager", "MyFileManager onDestroy");
        //		Pingmws_SetActivity.RECORD_DIALOG=0;
        if (mDialog!=null) {
            mDialog.dismiss();
            mDialog=null;
        }
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    private void openFile(File f) {


        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        String type = getMIMEType(f);
        if (type.equals(EncryptedService2.SUFFIX_NAME+"/*")) {
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
            writeLog("decrypt fiel and play；", f);
            return;

        } else {
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= 24) {//7.0 Android N
                //com.xxx.xxx.fileprovider?????manifest??provider?????????
                uri = FileProvider.getUriForFile(this, "com.welkin.ybzd.fileProvider",f);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//7.0???????????????uri???????????????????????????????ù?????????????
            } else {//7.0????
                uri = Uri.fromFile(f);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            intent.setDataAndType(uri, "video/*");
            startActivity(intent);
        }

    }

    // 这个方法的存在就是为了区分是不是特定的文件，比如zip,如果是的话再进行操作的，要是不是的话就不操作，鉴于现在只是对于特定的文件进行操作，
    // 那我还要不要继续研究这个类型這個東西，還是去研究廣播

    /**
     * @param f
     * @return 返回的是文件后缀名，原来后缀名要小些，大写根本没用，所以根本没比较到，呵呵了
     */
    private String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();

        // 将来解压好的视频文件名，然后保存到SharedPreferences
        SharedPreferences tmpFileName = getSharedPreferences("tmpFileName", 0);

        SharedPreferences.Editor editor = tmpFileName.edit();
        String newNameString = f.getAbsolutePath().replace(EncryptedService2.SUFFIX_NAME, "mp4");

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
        } else if (end.equals(EncryptedService2.SUFFIX_NAME)) {

            type = EncryptedService2.SUFFIX_NAME;

        } else {
            type = "*";
        }

        type += "/*";
        return type;
    }

    /**
     * @param first
     * @param f     写LOG到SD卡
     */
    private void writeLog(String first, File f) {
        //写日志到SD卡
        File dir = new File(Environment.getExternalStorageDirectory(), "PMWSLog");
        if (!dir.exists()) {
            dir.mkdir();
        }

        try {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss ");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            String str = formatter.format(curDate);

            FileWriter writer = new FileWriter(dir + "/log.txt", true);
            writer.write(first + str + ";" + f.getName() + "\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getFileDir(DCPubic.getRecordPath()); // curPath
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