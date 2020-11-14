package com.juntai.wisdom.basecomponent.utils;


import android.content.Context;

import com.juntai.wisdom.basecomponent.app.BaseApplication;

import es.dmoral.toasty.Toasty;

/**
 * @aouther Ma
 * @date 2019/3/17
 */
public class ToastUtils {

    /**
     * 普通不含图标，其他含图标
     * @param msg
     */
    public static void toast(Context context,String msg){
        Toasty.normal(context.getApplicationContext(),msg).show();
    }
    public static void toast(Context context,int msg){
        Toasty.normal(context,msg).show();
    }

    /**
     * 信息-灰色
     * @param context
     * @param msg
     */
    public static void info(Context context,String msg){
        Toasty.info(context.getApplicationContext(),msg).show();
    }
    public static void info(Context context,int msg){
        Toasty.info(context,msg).show();
    }
    /**
     * 成功-绿色背景
     * @param msg
     */
    public static void success(Context context,String msg){
        Toasty.success(context.getApplicationContext(),msg).show();
    }
    public static void success(Context context,int msg){
        Toasty.success(context.getApplicationContext(),msg).show();
    }

    /**
     * 错误-
     * @param msg
     */
    public static void error(Context context,String msg){
        Toasty.error(context.getApplicationContext(),msg).show();
    }

    public static void error(Context context,int msg){
        Toasty.error(context,msg).show();
    }

    /**
     * 警告-黄色背景
     * @param msg
     */
    public static void warning(Context context,String msg){
        Toasty.warning(context.getApplicationContext(),msg).show();
    }
    public static void warning(Context context,int msg){
        Toasty.warning(context,msg).show();
    }
}
