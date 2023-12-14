package com.leng.hiddencamera.mine;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.juntai.wisdom.basecomponent.utils.ImageLoadUtil;
import com.leng.hiddencamera.R;

import java.io.File;

/**
 * @Author: tobato
 * @Description: 作用描述
 * @CreateDate: 2021/5/20 22:09
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/5/20 22:09
 */
public class PicAdapter extends BaseQuickAdapter<File, BaseViewHolder> {
    public PicAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, File item) {
        ImageLoadUtil.loadImage(mContext,item.getAbsolutePath(),helper.getView(R.id.item_pic_iv));
    }
}
