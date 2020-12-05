package com.leng.hiddencamera.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.juntai.wisdom.basecomponent.utils.ActivityManagerTool;

/**
 * @Author: tobato
 * @Description: 作用描述
 * @CreateDate: 2020/12/5 13:56
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/12/5 13:56
 */
public abstract  class BaseAppActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManagerTool.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManagerTool.getInstance().removeActivity(this);
    }
}
