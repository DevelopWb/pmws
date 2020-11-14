package com.juntai.wisdom.basecomponent.widght;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.juntai.wisdom.basecomponent.R;

import java.util.List;

/**
 * @Author: tobato
 * @Description: 作用描述
 * @CreateDate: 2020/4/16 14:20
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/4/16 14:20
 */
public class BottomDialogAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public BottomDialogAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.single_text_tv, item);
    }
}
