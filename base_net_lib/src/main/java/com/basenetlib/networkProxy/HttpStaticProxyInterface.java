package com.basenetlib.networkProxy;


import com.basenetlib.okgo.NetResponseCallBack;

import java.util.Map;

/**
 * Author:wang_sir
 * Time:2019/4/3 17:00
 * Description:This is HttpStaticProxyInterface
 * 网络请求的公共方法
 * OkgoTool需要实现的接口，所有的第三方网络请求框架需要实现此接口
 */
public interface HttpStaticProxyInterface<T> {

    void postToNetwork(String urlPath, Map<String, String> map, NetResponseCallBack netCallBackInterface);

    void getToNetwork(String urlPath, Map<String, String> map, NetResponseCallBack netCallBackInterface);

    void getToNetwork(String urlPath, Map<String, String> map, NetResponseCallBack netCallBackInterface, boolean returnErrorCode);

    void postToNetwork(String urlPath, Map<String, String> map, NetResponseCallBack netCallBackInterface, boolean returnErrorCode);

}
