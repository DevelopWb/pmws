package com.leng.hiddencamera.mine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.juntai.wisdom.basecomponent.mvp.IView;
import com.juntai.wisdom.basecomponent.utils.GridDividerItemDecoration;
import com.leng.hiddencamera.R;
import com.leng.hiddencamera.base.BaseAppActivity;
import com.leng.hiddencamera.bean.MenuBean;
import com.leng.hiddencamera.zipthings.MyVediosActivity;

/**
 * @aouther tobato
 * @description 描述  设置界面
 * @date 2020/12/29 21:43
 */
public class SetActivity extends BaseAppActivity<MinePresent> implements IView, View.OnClickListener {

    private RecyclerView mRecyclerview;
    private MyMenuAdapter mMenuAdapter;
    private LinearLayout mMenuQuitLl;

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
        mMenuQuitLl = (LinearLayout) findViewById(R.id.menu_quit_ll);
        mMenuQuitLl.setOnClickListener(this);
        mImmersionBar.reset().statusBarColor(R.color.set_bg)
                .statusBarDarkFont(false)
                .init();
        initLeftBackTv(false);
        getToolbar().setBackgroundColor(ContextCompat.getColor(mContext, R.color.set_bg));
        getTitleTv().setText("隐蔽终端");
        getTitleTv().setTextColor(ContextCompat.getColor(mContext, R.color.white));
        mRecyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        mMenuAdapter = new MyMenuAdapter(R.layout.item_my_center_menu);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        mRecyclerview.setLayoutManager(gridLayoutManager);
        mRecyclerview.addItemDecoration(new GridDividerItemDecoration(mContext));
        mRecyclerview.setAdapter(mMenuAdapter);
        mMenuAdapter.setNewData(mPresenter.getMineMenus());
        mMenuAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MenuBean menuBean = (MenuBean) adapter.getData().get(position);
                switch (menuBean.getTagId()) {
                    case MinePresent.NAME_CAMERA:
                        showSelectCameraDialog();
                        break;
                    case MinePresent.NAME_FLOAT:

                        break;
                    case MinePresent.NAME_PLAY:
                        Intent intent = new Intent(getApplicationContext(),
                                MyVediosActivity.class);
                        startActivityForResult(intent, FILE_RESULT_CODE);
                        break;
                    case MinePresent.NAME_RECORD_SPACE:
                        showSelectVedioTimeDialog();
                        break;
                    case MinePresent.NAME_MODIFY_PWD:
                        showChangePwd();
                        break;
                    case MinePresent.NAME_CHANGE_ICON:
                        break;
                    case MinePresent.NAME_SWITCH_CAMERA:
                        //开启辅助服务开启障碍音量键捕获事件
                        try {
                            Intent intent = new Intent(
                                    android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                            startActivityForResult(intent, 99);
                            Toast.makeText(this, "找到指南针，开启即可", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    case MinePresent.NAME_CLEAR_FILE:
                        ClearCache(SAVED_VIDEO_PATH);
                        ClearCache(SAVED_VIDEO_PATH2);
                        Toast.makeText(getApplicationContext(), "清除成功",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case MinePresent.NAME_DESTROY_FILE:
                        new AlertDialog.Builder(this)

                                .setTitle("确认一键自毁？")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setPositiveButton("确定",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {

                                                destroyFiles();

                                                Toast.makeText(
                                                        getApplicationContext(),
                                                        "清除成功",
                                                        Toast.LENGTH_SHORT)
                                                        .show();

                                            }
                                        }).show();
                        break;
                    default:
                        break;
                }
            }
        });

    }

    @Override
    public void initData() {

    }

    @Override
    public void onSuccess(String tag, Object o) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.menu_quit_ll:
                break;
        }
    }
}
