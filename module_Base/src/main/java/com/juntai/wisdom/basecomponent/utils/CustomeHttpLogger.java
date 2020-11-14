package com.juntai.wisdom.basecomponent.utils;

import com.orhanobut.logger.Logger;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * http拦截器
 * @aouther ZhangZhenlong
 * @date 2020-3-24
 */

public class CustomeHttpLogger implements HttpLoggingInterceptor.Logger {

    private StringBuilder mMessage = new StringBuilder();

    @Override
    public void log(String message) {
        // 请求或者响应开始
        if (message.startsWith("--> POST")) {
            mMessage.setLength(0);
        }
        // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
        if ((message.startsWith("{") && message.endsWith("}"))
                || (message.startsWith("[") && message.endsWith("]"))) {
            message = JsonUtil.formatJson(JsonUtil.decodeUnicode(message));
        }
        mMessage.append(message.concat("\n"));
        // 响应结束，打印整条日志
        if (message.startsWith("<-- END HTTP")) {
            Logger.e(mMessage.toString());
            //打印完成后  记得清空message数据
            mMessage.delete(0,mMessage.length());
        }
    }
}
