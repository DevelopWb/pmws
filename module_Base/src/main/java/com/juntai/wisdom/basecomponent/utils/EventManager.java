package com.juntai.wisdom.basecomponent.utils;

import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

/**
 * @Author: tobato
 * @Description: 作用描述
 * @CreateDate: 2020/4/11 11:34
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/4/11 11:34
 */
public class EventManager {
    private static final EventBus libraryEvent = EventBus.builder().build();

    public static EventBus getLibraryEvent() {
        return libraryEvent;
    }

    public static void removeAllEvent() {
        if (libraryEvent != null) {
            libraryEvent.removeAllStickyEvents();
        }
    }

    public static void sendStringMsg(String str) {
        libraryEvent.post(str);
    }
    public static void sendImage(ImageView iv) {
        libraryEvent.post(iv);
    }
}
