package com.leng.hiddencamera.home;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.widget.Toast;

import com.leng.hiddencamera.BuildConfig;
import com.leng.hiddencamera.R;
import com.leng.hiddencamera.mine.PmwsSetActivity;
import com.leng.hiddencamera.mine.SetActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * @aouther tobato
 * @description ���� ����ҳ
 * @date 2020/10/5 21:20
 */
public class SplashActivity extends RxAppCompatActivity {
    public static final int REQUEST_OVERLAY_PERMISSION = 1004;  // ������

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
        if (PmwsSetActivity.sIsRecording) {
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
                            //����Ȩ��ͨ��

                        } else {
                            //��һ��Ȩ��ûͨ��
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            boolean canDraw = Settings.canDrawOverlays(SplashActivity.this);
                            if (canDraw) {
                                startActivity(new Intent(SplashActivity.this, SetActivity.class));
                                finish();
                            }else {
                                // ��Android 6.0��Android��Ҫ��̬��ȡȨ�ޣ���û��Ȩ�ޣ���ʾ��ȡ.
                                final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
                            }
                        }else {
                            startActivity(new Intent(SplashActivity.this, SetActivity.class));
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
                    startActivity(new Intent(SplashActivity.this, SetActivity.class));
                    finish();
                }else {
                    finish();
                }
            }
        }
    }
}
