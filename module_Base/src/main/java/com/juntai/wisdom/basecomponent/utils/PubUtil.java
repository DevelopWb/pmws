package com.juntai.wisdom.basecomponent.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * @Author: tobato
 * @Description: 作用描述
 * @CreateDate: 2020/8/20 21:47
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/8/20 21:47
 */
public class PubUtil {
    public static final String APP_NAME = "一见云控";

    /**
     * 通知系统相册更新图库
     * @param context
     * @param imagePath
     */
    public static void sendBroadcastToAlbum(Context context, String imagePath) {
        if (context != null && imagePath != null && imagePath.length() > 0) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(imageFile);
                if (uri != null && context != null) {
                    intent.setData(uri);
                    context.sendBroadcast(intent);
                }
            }
        }
    }
}
