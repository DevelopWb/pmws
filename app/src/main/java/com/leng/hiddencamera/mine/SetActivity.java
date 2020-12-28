package com.leng.hiddencamera.mine;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.juntai.wisdom.basecomponent.mvp.IView;
import com.juntai.wisdom.basecomponent.utils.GridDividerItemDecoration;
import com.leng.hiddencamera.R;
import com.leng.hiddencamera.base.BaseAppActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

public class SetActivity extends BaseAppActivity<MinePresent> implements IView {

    private RecyclerView mRecyclerview;
    private SmartRefreshLayout mSmartrefreshlayout;
    private MyMenuAdapter mMenuAdapter;

    @Override
    protected MinePresent createPresenter() {
        return new MinePresent();
    }


    @Override
    public int getLayoutView() {
        return R.layout.activity_set;
    }

    @Override
    public void initView() {
        setTitleName("设置");
        initLeftBackTv(false);
        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        mSmartrefreshlayout = (SmartRefreshLayout) findViewById(R.id.smartrefreshlayout);
        mSmartrefreshlayout.setEnableRefresh(false);
        mSmartrefreshlayout.setEnableLoadMore(false);
        mMenuAdapter = new MyMenuAdapter(R.layout.item_my_center_menu);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        mRecyclerview.setLayoutManager(gridLayoutManager);
        mRecyclerview.addItemDecoration(new GridDividerItemDecoration(mContext));
        mRecyclerview.setAdapter(mMenuAdapter);
        mMenuAdapter.setNewData(mPresenter.getMineMenus());
    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccess(String tag, Object o) {

    }
}
