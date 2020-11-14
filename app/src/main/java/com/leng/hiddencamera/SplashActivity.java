package com.leng.hiddencamera;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import com.juntai.wisdom.basecomponent.utils.SPTools;
import com.juntai.wisdom.basecomponent.utils.ToastUtils;
import com.softconfig.activity.MainActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * @aouther tobato
 * @description 描述 启动页
 * @date 2020/10/5 21:20
 */
public class SplashActivity extends RxAppCompatActivity {
    public static final int REQUEST_OVERLAY_PERMISSION = 1004;  // 悬浮框

    String[] permissions = new String[]{
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Pingmws_SetActivity.sIsRecording) {
            // 如果在录制中，toast显示正在录制
            Toast.makeText(getBaseContext(), "正在录制中，请先停止...", Toast.LENGTH_LONG)
                    .show();
            finish();
            return;
        }
        setContentView(R.layout.activity_splash);
        new RxPermissions(this)
                .request(permissions)
                .delay(1, TimeUnit.SECONDS)
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            //所有权限通过

                        } else {
                            //有一个权限没通过
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            boolean canDraw = Settings.canDrawOverlays(SplashActivity.this);
                            if (canDraw) {
                                startActivity(new Intent(SplashActivity.this, Pingmws_SetActivity.class));
                                finish();
                            }else {
                                // 在Android 6.0后，Android需要动态获取权限，若没有权限，提示获取.
                                final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
                            }
                        }else {
                            startActivity(new Intent(SplashActivity.this, Pingmws_SetActivity.class));
                            finish();
                        }



                    }

                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean canDraw = Settings.canDrawOverlays(this);
                if (canDraw) {
                    startActivity(new Intent(SplashActivity.this, Pingmws_SetActivity.class));
                    finish();
                }else {
                    finish();
                }
            }
        }
    }
}
