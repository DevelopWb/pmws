package com.basenetlib.okgo;

/**
 * Author:wang_sir
 * Time:2019/4/3 11:42
 * Description:This is NetResponseCallBack
 * 网络请求返回的回调
 */
public interface NetResponseCallBack {


    void  onSuccess(String content);
    void  onError(String str);
}
