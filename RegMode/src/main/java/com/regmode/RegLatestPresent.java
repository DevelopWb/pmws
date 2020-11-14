package com.regmode;

import com.basenetlib.RequestStatus;
import com.basenetlib.networkProxy.HttpProxy;
import com.basenetlib.okgo.NetResponseCallBack;
import com.basenetlib.util.GsonManager;
import com.regmode.Utils.RegOperateManager;
import com.regmode.bean.AppInfoBean;
import com.regmode.bean.RegCodeBean;

/**
 * Author:wang_sir
 * Time:2019/12/24 20:10
 * Description:This is RegLatestPresent
 */
public class RegLatestPresent implements RegLatestContact.IRegLatestPresent {


    @Override
    public void setRegisCodeNumber(String regisCode, int size, final RequestStatus requestStatus) {
        HttpProxy.getInstance()
                .params("softType", "mb")
                .params("regisCode", regisCode)
                .params("number", size)
                .postToNetwork(AppHttpUrl.SET_REG_NUM,
                        new NetResponseCallBack() {
                    @Override
                    public void onSuccess(String content) {
                        requestStatus.onSuccess(content, RegLatestContact.SET_CODE);
                    }

                    @Override
                    public void onError(String str) {

                    }
                });
    }

    @Override
    public void getRegInfo(String regisCode, final RequestStatus requestStatus) {
        HttpProxy.getInstance()
                .params("softwareType", "mb")
                .params("regisCode", regisCode)
                .params("softwareId", RegOperateManager.APP_MARK)
                .postToNetwork(AppHttpUrl.GET_REG_INFO, new NetResponseCallBack() {
                    @Override
                    public void onSuccess(String content) {
                        RegCodeBean regCodeBean = GsonManager.getInstance().parseJsonToBean(content,RegCodeBean.class);
                        requestStatus.onSuccess(regCodeBean,RegLatestContact.GET_REG_INFO );
                    }

                    @Override
                    public void onError(String str) {

                    }
                });
    }

    @Override
    public void uploadVersionInfo(String regisCode, String versionMsg,final  RequestStatus requestStatus) {
        HttpProxy.getInstance()
                .params("regisCode", regisCode)
                .params("versionMsg", versionMsg)
                .postToNetwork(AppHttpUrl.SET_VERSION_INFO, new NetResponseCallBack() {
                    @Override
                    public void onSuccess(String content) {
                        requestStatus.onSuccess(content, RegLatestContact.UPLOAD_V_INFO);
                    }

                    @Override
                    public void onError(String str) {

                    }
                });
    }


    @Override
    public void getAppVersionInfoAndKeyFromService(final  String tag,final RequestStatus requestStatus) {
        HttpProxy.getInstance()
                .params("softwareType", "mb")
                .params("softwareId", RegOperateManager.APP_MARK)
                .postToNetwork(AppHttpUrl.GET_REG_DETAIL , new NetResponseCallBack() {
                    @Override
                    public void onSuccess(String content) {
                        AppInfoBean appInfoBean = GsonManager.getInstance().parseJsonToBean(content,AppInfoBean.class);
                        requestStatus.onSuccess(appInfoBean, tag);
                    }

                    @Override
                    public void onError(String str) {

                    }
                });
    }
//    @Override
//    public void regist(String regisCode, String phoneMessage, final RequestStatus requestStatus) {
//        HttpProxy.getInstance()
//                .params("softType", "mb")
//                .params("regisCode", regisCode)
//                .params("softIdentification", AppHttpUrl.APP_MARK)
//                .params("model","")
//                .postToNetwork(AppHttpUrl.BASE_URL + "/WebService/RegisCode.asmx/GetRegisCodeInfo", new NetResponseCallBack() {
//                    @Override
//                    public void onSuccess(String content) {
//                        requestStatus.onSuccess(content, RegLatestContact.REGIST);
//                    }
//
//                    @Override
//                    public void onError(String str) {
//                        requestStatus.onError(str);
//
//                    }
//                });
//    }

    @Override
    public void checkReg(String regisCode, String softwareId, final String tag, final RequestStatus requestStatus) {
        HttpProxy.getInstance()
                .params("softwareType", "mb")
                .params("regisCode", regisCode)
                .params("softwareId",softwareId)
                .postToNetwork(AppHttpUrl.CHECK_REGIST, new NetResponseCallBack() {
                    @Override
                    public void onSuccess(String content) {
                        RegCodeBean regCodeBean = GsonManager.getInstance().parseJsonToBean(content,RegCodeBean.class);
                        requestStatus.onSuccess(regCodeBean,tag );
                    }

                    @Override
                    public void onError(String str) {
                        requestStatus.onError(str);

                    }
                });
    }

    @Override
    public void setImei(String regisCode,  String imei,final String tag, final  RequestStatus requestStatus) {
        HttpProxy.getInstance()
                .params("softType", "mb")
                .params("regisCode", regisCode)
                .params("imei",imei)
                .postToNetwork(AppHttpUrl.SET_IMEI, new NetResponseCallBack() {
                    @Override
                    public void onSuccess(String content) {
                        requestStatus.onSuccess(content,tag );
                    }

                    @Override
                    public void onError(String str) {
                        requestStatus.onError(str);

                    }
                });
    }

}
