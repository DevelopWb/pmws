package com.basenetlib.networkProxy;

import android.util.ArrayMap;

import com.basenetlib.util.NetWorkUtil;
import com.basenetlib.okgo.NetResponseCallBack;

import java.util.Map;


/**
 * Author:wang_sir
 * Time:2019/4/3 16:50
 * Description:This is HttpProxy
 * 网络请求代理类，用于网络请求参数的封装和网络请求的接入
 * 使用到了build设计模式  静态代理模式
 */
public class HttpProxy<H extends HttpProxy> {
    private static Map requestMap = new ArrayMap();

    private HttpStaticProxyInterface mHttpStaticProxy = null;


    public static HttpProxy getInstance() {
        requestMap.clear();
        return HttpStaticProxyHolder.httpStaticProxy;
    }

    /**
     * 配置网络代理  在Application中配置
     * @param httpProxy
     */
    public void setNetProxyType(HttpStaticProxyInterface httpProxy) {
        this.mHttpStaticProxy = httpProxy;
    }

    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @return
     */
    public H params(String key, String value) {
        requestMap.put(key, value);
        return (H) this;
    }

    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @return
     */
    public H params(String key, int value) {
        if (-1 == value) {
            return (H) this;
        }
        String valueStr = String.valueOf(value);
        requestMap.put(key, valueStr);
        return (H) this;
    }

    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @return
     */
    public H params(String key, double value) {
        if (-1 == value) {
            return (H) this;
        }
        String valueStr = String.valueOf(value);
        requestMap.put(key, valueStr);
        return (H) this;
    }


    public H params(String key, Long value) {

        if (value != null) {
            String valueStr = String.valueOf(value);
            requestMap.put(key, valueStr);
        } else {
            requestMap.put(key, null);
        }

        return (H) this;
    }


    /**
     * 添加参数
     *
     * @param map
     * @return
     */
    public H params(Map<String, String> map) {
        requestMap.putAll(map);
        return (H) this;
    }


    /**
     * 请求网络
     * @param urlPath
     * @param netCallBackInterface
     */
    public void postToNetwork(String urlPath, NetResponseCallBack netCallBackInterface) {
        if (NetWorkUtil.isNetworkAvailable()) {
            if (mHttpStaticProxy != null) {
                mHttpStaticProxy.postToNetwork(urlPath, requestMap, netCallBackInterface);
            }
        }else{
            //通知网络变化
//            EventManager.sendStringMsg(EventBusProperty.NET_WORK_UNCONNECT);
        }

    }

    public void getToNetwork(String urlPath, NetResponseCallBack netCallBackInterface) {

        if (NetWorkUtil.isNetworkAvailable()) {
            if (mHttpStaticProxy != null) {
                mHttpStaticProxy.getToNetwork(urlPath, requestMap, netCallBackInterface);
            }
        }else{
            //通知网络变化
//            EventManager.sendStringMsg(EventBusProperty.NET_WORK_UNCONNECT);
        }
    }

    public void postToNetwork(String urlPath, boolean returnErrorCode, NetResponseCallBack netCallBackInterface) {
        if (NetWorkUtil.isNetworkAvailable()) {
            if (mHttpStaticProxy != null) {
                mHttpStaticProxy.postToNetwork(urlPath, requestMap, netCallBackInterface, returnErrorCode);
            }
        }else{
            //通知网络变化
//            EventManager.sendStringMsg(EventBusProperty.NET_WORK_UNCONNECT);
        }
    }

    public void getToNetwork(String urlPath, boolean returnErrorCode, NetResponseCallBack netCallBackInterface) {
        if (NetWorkUtil.isNetworkAvailable()) {
            if (mHttpStaticProxy != null) {
                mHttpStaticProxy.getToNetwork(urlPath, requestMap, netCallBackInterface, returnErrorCode);
            }
        }else{
            //通知网络变化
//            EventManager.sendStringMsg(EventBusProperty.NET_WORK_UNCONNECT);
        }

    }

    private static class HttpStaticProxyHolder {
        private static HttpProxy httpStaticProxy = new HttpProxy();
    }


}
