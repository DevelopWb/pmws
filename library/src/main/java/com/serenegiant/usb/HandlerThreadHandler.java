package com.serenegiant.usb;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * @Author: tobato
 * @Description: 作用描述
 * @CreateDate: 2020/6/24 10:01
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/6/24 10:01
 */
public class HandlerThreadHandler   extends Handler {
    private static final String TAG = "HandlerThreadHandler";

    public static final HandlerThreadHandler createHandler() {
        return createHandler("HandlerThreadHandler");
    }

    public static final HandlerThreadHandler createHandler(String name) {
        HandlerThread thread = new HandlerThread(name);
        thread.start();
        return new HandlerThreadHandler(thread.getLooper());
    }

    public static final HandlerThreadHandler createHandler(@Nullable Handler.Callback callback) {
        return createHandler("HandlerThreadHandler", callback);
    }

    public static final HandlerThreadHandler createHandler(String name, @Nullable Handler.Callback callback) {
        HandlerThread thread = new HandlerThread(name);
        thread.start();
        return new HandlerThreadHandler(thread.getLooper(), callback);
    }

    private HandlerThreadHandler(@NonNull Looper looper) {
        super(looper);
    }

    private HandlerThreadHandler(@NonNull Looper looper, @Nullable Handler.Callback callback) {
        super(looper, callback);
    }
}

