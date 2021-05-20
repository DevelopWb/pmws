package com.leng.hiddencamera.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.juntai.wisdom.basecomponent.mvp.BasePresenter;
import com.juntai.wisdom.basecomponent.utils.ToastUtils;
import com.juntai.wisdom.video.img.ImageZoomActivity;
import com.leng.hiddencamera.BaseAppActivity;
import com.leng.hiddencamera.R;
import com.leng.hiddencamera.util.DCPubic;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @aouther tobato
 * @description 描述  图片展示
 * @date 2021/5/20 22:06
 */
public class PicsDisplayActivity extends BaseAppActivity {

    private RecyclerView mRecyclerview;
    private SmartRefreshLayout mSmartrefreshlayout;
    private PicAdapter picAdapter;

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    public int getLayoutView() {
        return R.layout.recycleview_layout;
    }

    @Override
    public void initView() {
        setTitleName("图片展示");
        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        mSmartrefreshlayout = (SmartRefreshLayout) findViewById(R.id.smartrefreshlayout);
        picAdapter = new PicAdapter(R.layout.item_pic_display);
        GridLayoutManager manager = new GridLayoutManager(mContext,3);
        mRecyclerview.setAdapter(picAdapter);
        mRecyclerview.setLayoutManager(manager);
        picAdapter.setNewData(getTestData());
    }

    @Override
    public void initData() {
        picAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArrayList<String> arrays = new ArrayList<>();
                arrays.addAll(adapter.getData());
                startActivity(new Intent(mContext, ImageZoomActivity.class).putExtra("paths", arrays).putExtra(
                        "item", position));

            }
        });
        picAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                new AlertDialog.Builder(mContext)
                        .setCancelable(false)
                        .setMessage("确定删除此照片吗")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();

                return true;
            }
        });
    }
    @Override
    public void onBackPressed() {
        DCPubic.RECORD_DIALOG = 1;
        finish();

    }

    @Override
    public void onSuccess(String tag, Object o) {

    }
}