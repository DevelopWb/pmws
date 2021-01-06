package com.serenegiant.usb;

import java.nio.ByteBuffer;

/**
 * @Author: tobato
 * @Description: 作用描述
 * @CreateDate: 2020/6/24 10:20
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/6/24 10:20
 */
public interface IStatusCallback {
    void onStatus(int statusClass, int event, int selector, int statusAttribute, ByteBuffer data);
}
