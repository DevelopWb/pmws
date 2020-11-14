package com.basenetlib.okgo;


import com.basenetlib.util.NetWorkUtil;
import com.basenetlib.networkProxy.HttpStaticProxyInterface;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.Map;

/**
 * Author:wang_sir
 * Time:2019/4/3 11:36
 * Description:This is OkgoTool  网络请求库的封装
 */
public class OkgoTool<T> implements HttpStaticProxyInterface {
    @Override
    public void postToNetwork(String urlPath, Map map, final NetResponseCallBack netCallBackInterface) {
        OkGo.<String>post(urlPath)
                .params(map, false)
//                .headers("Authorization", "bearer " + UserInfoUtil.getInstance().getUserToken())
//                .headers("api-version", "1.5.0")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String content = response.body();
                        requestOnSuccess(content, netCallBackInterface, NetWorkUtil.getServerMessage(content));
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        requestOnError(response, netCallBackInterface);
                    }
                });
    }


    @Override
    public void getToNetwork(String urlPath, Map map, final NetResponseCallBack netCallBackInterface) {
        OkGo.<String>get(urlPath)
                .params(map, false)
//                .headers("Authorization", "bearer " + UserInfoUtil.getInstance().getUserToken())
//                .headers("api-version", "1.5.0")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String content = response.body();
                        requestOnSuccess(content, netCallBackInterface, NetWorkUtil.getServerMessage(content));
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        requestOnError(response, netCallBackInterface);
                    }
                });
    }


    @Override
    public void getToNetwork(String urlPath, Map map, final NetResponseCallBack netCallBackInterface, boolean returnErrorCode) {
        OkGo.<String>get( urlPath)
                .params(map, false)
//                .headers("Authorization", "bearer " + UserInfoUtil.getInstance().getUserToken())
//                .headers("api-version", "1.5.0")

                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String content = response.body();
                        requestOnSuccess(content, netCallBackInterface, String.valueOf(NetWorkUtil.getServerCode(content)));
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        requestOnError(response, netCallBackInterface);
                    }
                });
    }

    @Override
    public void postToNetwork(String urlPath, Map map, final NetResponseCallBack netCallBackInterface, boolean returnErrorCode) {
        OkGo.<String>post(urlPath)
                .params(map, false)
//                .headers("Authorization", "bearer " + UserInfoUtil.getInstance().getUserToken())
//                .headers("api-version", "1.5.0")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String content = response.body();
                        requestOnSuccess(content, netCallBackInterface, String.valueOf(NetWorkUtil.getServerCode(content)));
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        requestOnError(response, netCallBackInterface);
                    }
                });
    }

    /**
     * 请求异常
     *
     * @param response
     * @param netCallBackInterface
     */
    private void requestOnError(Response<String> response, NetResponseCallBack netCallBackInterface) {
        if (netCallBackInterface != null) {
            if (response.body() != null) {
                netCallBackInterface.onError(response.body());
            } else {
//                netCallBackInterface.onError("服务器错误");

            }
        }
    }
    /**
     * 将返回字段截取json字符串
     *
     * @param str
     * @return
     */
    private  String getStr(String str) {
        int ii = 0;
        int j = 0;
        ii = str.indexOf("{");
        j = str.lastIndexOf("}");
        return str.substring(ii, j + 1);
    }
    /**
     * 请求成功
     *
     * @param content
     * @param netCallBackInterface
     * @param errMsg
     */
    private void requestOnSuccess(String content, NetResponseCallBack netCallBackInterface, String errMsg) {
        if (NetWorkUtil.initContent(content)) {
            if (netCallBackInterface != null) {
                if (content.contains("xmlns")) {
                    content = getStr(content);
                }
                if (netCallBackInterface != null) {
                    netCallBackInterface.onSuccess(content);
                }
            }
        } else {

            if (netCallBackInterface != null) {
                if (NetWorkUtil.getServerCode(content) == 4000 ||NetWorkUtil.getServerCode(content) == 4001) {
                    return;
                }
                netCallBackInterface.onError(errMsg);
            }
        }
    }
}
