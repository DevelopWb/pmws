package com.juntai.wisdom.basecomponent.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.juntai.wisdom.basecomponent.utils.BaseAppUtils;
import com.juntai.wisdom.basecomponent.utils.LogUtil;
import com.juntai.wisdom.basecomponent.utils.MD5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Calendar;

/**
 * 文件
 *
 * @aouther Ma
 * @date 2019/3/21
 */
public class FileUtils {


    /**
     * 获取app文件地址
     *
     * @return
     */
    public static String getAppPath() {
        File destDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + BaseAppUtils.getAppName());
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return destDir.getAbsolutePath() + "/";
    }


    /**
     * 创建文件夹
     *
     * @param fileName
     */
    public static void creatFile(String fileName) {

        File file = new File(fileName);

        if (!file.exists()) {
            file.mkdirs();
        }
    }


    /**
     * 将字符串写入到文本文件中
     *
     * @param strcontent 要保存的字符串
     * @param fileName   文件的名称  a.txt
     */
    public static void writeToTxtFile(String strcontent, String fileName) {


        String writedContent = getFileContent(fileName);
        if (!TextUtils.isEmpty(writedContent)) {
            return;
        }

        // 每次写入时，都换行写
        String strContent = MD5.md5(strcontent);
        try {
          //生成文件夹之后，再生成文件，不然会出错
            File file =   makeFilePath(getSavedLocalPropertyFilePath(), fileName);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    /**
     * 生成文件
     *
     * @param filePath
     * @param fileName
     * @return
     */
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    //生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    /**
     * 获取保存本地配置文件的路径
     *
     * @return
     */
    private static String getSavedLocalPropertyFilePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() +File.separator+PubUtil.APP_NAME +"/.配置文件/";
    }

    //读取指定目录下的所有TXT文件的文件内容
    public static String getFileContent(String fileName) {

        File file = makeFilePath(getSavedLocalPropertyFilePath(), fileName);
        String content = "";
        if (!file.isDirectory()) { //检查此路径名的文件是否是一个目录(文件夹)
            if (file.getName().endsWith("txt")) {//文件格式为""文件
                try {
                    InputStream instream = new FileInputStream(file);
                    if (instream != null) {
                        InputStreamReader inputreader = new InputStreamReader(instream, "UTF-8");
                        BufferedReader buffreader = new BufferedReader(inputreader);
                        String line = "";
                        //分行读取
                        while ((line = buffreader.readLine()) != null) {
                            content += line;
                            //                            content += line + "\n";
                        }
                        instream.close();//关闭输入流
                    }
                } catch (FileNotFoundException e) {
                    Log.d("TestFile", "The File doesn't not exist.");
                } catch (IOException e) {
                    Log.d("TestFile", e.getMessage());
                }
            }
        }
        return content;
    }

}
