package com.leng.hiddencamera;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.juntai.wisdom.basecomponent.base.BaseMvpActivity;
import com.juntai.wisdom.basecomponent.mvp.BasePresenter;
import com.juntai.wisdom.basecomponent.utils.ActivityManagerTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @Author: tobato
 * @Description: 作用描述
 * @CreateDate: 2020/12/5 13:56
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/12/5 13:56
 */
public abstract  class BaseAppActivity<P extends BasePresenter> extends BaseMvpActivity<P> {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManagerTool.getInstance().addActivity(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        ActivityManagerTool.getInstance().removeActivity(this);
    }



}
