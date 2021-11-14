package com.regmode;

/**
 * @Author: tobato
 * @Description: 作用描述
 * @CreateDate: 2020/6/11 15:16
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/6/11 15:16
 */
public class AppHttpUrl {
    public static String BASE_URL = "https://zc.cha365.cn";//注册码中心系统
    public static String SoftWare = BASE_URL + "/WebService/SoftWare.asmx";
    public static String RegisCode = BASE_URL + "/WebService/RegisCode.asmx";
    public static String InterfaceRegisCode = BASE_URL + "/WebService/InterfaceRegisCode.asmx";

    public static String GET_REG_INFO = SoftWare+"/SoftWareRegister";//验证注册码
    public static String GET_REG_DETAIL = SoftWare+"/GetAllSoftWareInfo";//获取注册码详情
    public static String CHECK_REGIST = SoftWare+"/GetRegisCodeInfo_NoPhoneMessage";//获取验证码信息
    public static String SET_VERSION_INFO = SoftWare+"/SetVersionInfo";//版本变化时 上传经纬度等信息
    public static String SET_IMEI = RegisCode+"/SetRegiCodeImei";//配置IMEI
    public static String SET_REG_NUM =RegisCode+"/SetRegisCodeNumber ";//减次数


}
