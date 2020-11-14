package com.juntai.wisdom.basecomponent.utils;

import com.juntai.wisdom.basecomponent.app.BaseApplication;

/**
 * activity - action
 */
public class ActionConfig {
    /**登录*/
    //public static String ACTION_LOGIN = BaseApplication.app.getPackageName() + ".login";
    /**地图查看*/
    public final static String ACTION_LOCATION_LOOK = BaseApplication.app.getPackageName() + ".im.location.look";
    /**地图选择*/
    public final static String ACTION_LOCATION_SELTION = BaseApplication.app.getPackageName() + ".im.location.seltion";
//    com.juntai.wisdom.bdmap.act

    /*=====================================广播==================================*/
    /**需要重新登录*/
    public static final String BROAD_LOGIN = BaseApplication.app.getPackageName() + ".login_error";
    /**视频录制*/
    public static final String BROAD_VIDEO = BaseApplication.app.getPackageName() + ".VideoBroadcastReceiver";
    /**案件详情*/
    public static final String BROAD_CASE_DETAILS = BaseApplication.app.getPackageName() + ".CaseDetails";
    /**未读消息更新*/
    public static final String UN_READ_MESSAG_TAG = BaseApplication.app.getPackageName() + ".unReadTag";

    /*====================================服务====================================*/
    /**地图选择*/
    public final static String ACTION_SERVICE_LOCATION = BaseApplication.app.getPackageName() + ".service.location_icon";

}
