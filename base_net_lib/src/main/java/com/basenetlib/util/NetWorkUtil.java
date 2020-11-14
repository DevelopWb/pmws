package com.basenetlib.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.basenetlib.https.MyTrustManager;
import com.basenetlib.networkProxy.HttpProxy;
import com.basenetlib.okgo.OkGoInterceptor;
import com.basenetlib.okgo.OkgoTool;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import okhttp3.OkHttpClient;


/**
 * Author:wang_sir
 * Time:2018/8/20 10:42
 * Description:This is NetWorkUtil
 */
public class NetWorkUtil {
    public static Context context;

    public static void  initContext(Application context) {
        NetWorkUtil.context = context.getApplicationContext();
        setOkGoConfig(context);
    }
    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ssfFactory;
    }
    /**
     * 配置okgo
     */
    private static void setOkGoConfig(Application context) {
        //OKGO
        /**
         * 配置OkHttpClient
         */
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(3000, TimeUnit.SECONDS)
//                .sslSocketFactory(createSSLSocketFactory())
                .writeTimeout(3000, TimeUnit.SECONDS)
                .addInterceptor(new OkGoInterceptor("TokenInterceptor"))//添加获取token的拦截器
                .build();
        OkGo.getInstance().init(context)                       //必须调用初始化
                .setOkHttpClient(client)               //建议设置OkHttpClient，不设置将使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE);   //全局统一缓存时间，默认永不过期，可以不传
//                .setRetryCount(3);                           //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
//                .addCommonHeaders(headers)                      //全局公共头
//                .addCommonParams(params);

        HttpProxy.getInstance().setNetProxyType(new OkgoTool());
    }
    //判断当前网络是否可用
    public static boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (null != info && info.isConnected()) {
            if (info.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }
        return false;
    }
    /**
     * 获取activity
     * @return
     */
    public static Activity getActivity() {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }
        if (context instanceof Activity) {
            return (Activity) context;
        }else {
            return null;
        }
    }

    /**
     * code
     *
     * @param content
     * @return
     */
    public static int getServerCode(String content) {
        int code = -1;
        if (isStringValueOk(content)) {
            try {
                JSONObject object = new JSONObject(content);
                code = object.getInt("code");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return code;
    }
    /**
     * 判断str是否为空或者是空字符串
     *
     * @param str
     * @return
     */
    public static boolean isStringValueOk(String str) {
        if (str != null && !TextUtils.isEmpty(str)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取服务端返回的message信息
     *
     * @param content
     * @return
     */
    public static String getServerMessage(String content) {
        String message = "";
        if (isStringValueOk(content)) {
            try {
                JSONObject object = new JSONObject(content);
                message = object.getString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return message;
    }

    /**
     * 检测服务端返回数据的状态
     *
     * @param content
     * @return
     */
    public static boolean initContent(String content) {
        boolean status = false;
        if (isStringValueOk(content)) {

            try {
                JSONObject object = new JSONObject(content);
                int code = object.getInt("code");
                if (code == 1000) {
                    status = true;
                } else {
                    status = false;
                }
            } catch (JSONException e) {
                status = true;
                e.printStackTrace();
            }

        }
        return status;
    }
}
