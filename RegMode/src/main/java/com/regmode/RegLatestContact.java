package com.regmode;

import com.basenetlib.RequestStatus;

/**
 * Author:wang_sir
 * Time:2019/12/24 20:05
 * Description:This is RegLatestContact
 */
public interface RegLatestContact {

    String  SET_CODE = "set_code";
    String  SET_IMEI= "set_imei";
    String GET_REG_INFO = "get_reg_info";
    String GET_VERSION = "get_version";
    String GET_KEY = "get_key";
    String GET_APP_VERSION_INFO= "GET_APP_VERSION_INFO";//获取App 版本信息
    String  REGIST = "regist";
    String CHECK_REG = "check_reg";//第一次注册码验证
    String CHECK_REG_EVERYTIME = "check_reg_everytime";//每次进入软件的时候校验
    String  UPLOAD_V_INFO = "upload_v_info";

    interface  IRegLatestPresent{
        void  setRegisCodeNumber(String regisCode, int size, RequestStatus requestStatus);
        void getRegInfo(String regisCode, RequestStatus requestStatus);
        void uploadVersionInfo(String regisCode,String versionMsg,RequestStatus requestStatus);

        /**
         * 从服务器获取版本信息和软件使用的key
         * @param requestStatus
         */
        void getAppVersionInfoAndKeyFromService(String tag,RequestStatus requestStatus);
//        void  regist(String regisCode,String phoneMessage,RequestStatus requestStatus);
        //注册码验证
        void checkReg(String regisCode,  String softwareId, String tag, RequestStatus requestStatus);

        //配置IMEI
        void setImei(String regisCode, String imei,String tag, RequestStatus requestStatus);
    }
    interface CancelCallBack {
        void toFinishActivity();

        void toDoNext(String input);
    }
}
