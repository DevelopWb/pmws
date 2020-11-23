// TestAidl.aidl
package com.leng.hiddencamera.aidl.service;

// Declare any non-default types here with import statements

interface TestAidl {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
      //相互唤醒服务
        void wakeUp(String title, String discription, int iconRes);
}
