package com.leng.hiddencamera.mine;

import android.support.v7.widget.GridLayoutManager;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.juntai.wisdom.basecomponent.utils.HawkProperty;
import com.leng.hiddencamera.R;
import com.leng.hiddencamera.bean.MenuBean;
import com.leng.hiddencamera.util.DCPubic;
import com.orhanobut.hawk.Hawk;

import java.util.List;

/**
 * Describe:
 * Create by zhangzhenlong
 * 2020/3/7
 * email:954101549@qq.com
 */
public class MyMenuAdapter extends BaseQuickAdapter<MenuBean, BaseViewHolder> {

    public MyMenuAdapter(int layoutResId) {
        super(layoutResId);
    }


    @Override
    protected void convert(BaseViewHolder helper, MenuBean item) {
        helper.setImageResource(R.id.menu_icon_iv, item.getResId());
        helper.setText(R.id.menu_title_tv, item.getName());

        if (MinePresent.NAME_RECORD_SPACE == item.getTagId()) {
            //录像间隔
            helper.setGone(R.id.menu_content_tv, true);
            helper.setText(R.id.menu_content_tv, Hawk.get(HawkProperty.RECORD_INTERVAL_TIME,"5分钟"));
        } else if (MinePresent.NAME_RECORD_PATH == item.getTagId()) {
            helper.setGone(R.id.menu_content_tv, true);
            helper.setText(R.id.menu_content_tv, "手机内部存储/YBZD");
        } else {
            helper.setGone(R.id.menu_content_tv, false);
        }

    }
}
