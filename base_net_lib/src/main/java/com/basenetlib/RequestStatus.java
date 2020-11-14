package com.basenetlib;

/**
 * Author:wang_sir
 * Time:2018/6/27 17:33
 * Description:This is RequestStatus
 */
public interface RequestStatus<T> {
    String REFRESH = "refresh";
    String UPDATE = "update";
    String ERROR = "error";
    String SELECT_COUPON = "select_coupon";//选择优惠券
    String WEI_XIN_PAY = "wei_xin_pay";//微信支付

    void onStart(String tag);

    void onSuccess(T t, String tag);//refresh 刷新  update 更新

    void onError(String tag);

}
