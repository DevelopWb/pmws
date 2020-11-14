package com.juntai.wisdom.basecomponent.widght;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.juntai.wisdom.basecomponent.R;
import com.juntai.wisdom.basecomponent.app.BaseApplication;

/**
 * 圆形小圈圈
 * @aouther Ma
 * @date 2019/3/7
 */
public class ProgressDialog extends Dialog {
    public ProgressDialog(@NonNull Context context) {
        super(context);
    }

    public ProgressDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public ProgressDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preogressdialog);
        setCanceledOnTouchOutside(false);
    }
}
