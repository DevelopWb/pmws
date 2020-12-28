package com.leng.hiddencamera.mine;

import android.support.v7.widget.GridLayoutManager;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.leng.hiddencamera.bean.MenuBean;

import java.util.List;

/**
 * Describe:
 * Create by zhangzhenlong
 * 2020/3/7
 * email:954101549@qq.com
 */
public class MyMenuAdapter extends BaseQuickAdapter<MenuBean, BaseViewHolder> {
    GridLayoutManager gridLayoutManager;

    public MyMenuAdapter(int layoutResId) {
        super(layoutResId);
    }


    @Override
    protected void convert(BaseViewHolder helper, MenuBean item) {
    }
}
