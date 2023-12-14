package com.leng.hiddencamera.home;

import com.leng.hiddencamera.util.PmwsLog;

/**
 * @Author: tobato
 * @Description: 作用描述
 * @CreateDate: 2020/12/5 11:19
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/12/5 11:19
 */
public class CameraRecordServiceHelper {

    /**
     * 获取摄像头预览位置
     *
     * @param cameraId
     * @return
     */
    public static int getCameraDisplayOrientation(int cameraId) {
        int orientation = 0;
        if (cameraId == 0) {
            orientation = 90;
        } else if (cameraId == 1) {
            orientation = 90;
        } else if (cameraId == 2) {
            orientation = 90;
        }
        PmwsLog.d("Change preview orientation, cameraId: " + cameraId
                + ", orientation: " + orientation);

        return orientation;

    }

    /**
     * 获取录像预览位置
     *
     * @param cameraId
     * @return
     */
    public static int getRecorderPlayOrientation(int cameraId) {
        int orientation = 0;
        if (cameraId == 0) {
            orientation = 90;
        } else if (cameraId == 1) {
            orientation = 270;
        } else if (cameraId == 2) {
            orientation = 90;
        }
        PmwsLog.d("Change recorder orientation, cameraId: " + cameraId
                + ", orientation: " + orientation);

        return orientation;

    }

}
