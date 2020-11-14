package com.softconfig.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Created by ${王sir} on 2017/6/23.
 * application
 */

public class ProperUtil {

    private static String FilePath = "/sdcard/.Properties/";
    private static String FileName = ".investigation_terminal.properties";
    /**
     * 得到properties配置文件中的所有配置属性
     *
     * @return Properties对象
     */
    public static String getConfigProperties(String key) {
        File mfile = new File(FilePath);
        if (!mfile.exists()) {
            mfile.mkdirs();
        }
        File mfileName = new File(FilePath+FileName);
        if (!mfileName.exists()) {
            return "";
        }
        Properties props = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(FilePath+FileName);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        try {
            props.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props.getProperty(key);
    }


    /**
     * 给属性文件添加属性
     *
     * @param value
     * @author qiulinhe
     * @createTime 2016年6月7日 下午1:46:53
     */
    public static void writeDateToLocalFile( String key, String value) {
        File mfile = new File(FilePath);
        if (!mfile.exists()) {
            mfile.mkdirs();
        }
        Properties p = new Properties();
        try {
            InputStream in = new FileInputStream(FilePath+FileName);
            p.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        p.put(key, value);
        OutputStream fos;
        try {
            fos = new FileOutputStream(FilePath+FileName);
            p.store(fos, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
