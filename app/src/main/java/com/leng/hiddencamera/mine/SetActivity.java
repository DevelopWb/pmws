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
import com.juntai.wisdom.basecomponent.utils.HawkProperty;
import com.juntai.wisdom.basecomponent.utils.ToastUtils;
import com.leng.hiddencamera.R;
import com.leng.hiddencamera.base.BaseAppActivity;
import com.leng.hiddencamera.bean.MenuBean;
import com.orhanobut.hawk.Hawk;

import static com.leng.hiddencamera.mine.PmwsSetActivity.destroyFiles;

/**
 * @aouther tobato
 * @description 描述  设置界面
 * @date 2020/12/29 21:43
 */
public class SetActivity extends BaseAppActivity<MinePresent> implements IView, View.OnClickListener {

    private RecyclerView mRecyclerview;
    private MyMenuAdapter mMenuAdapter;
    private LinearLayout mMenuQuitLl;
    public static CharSequence[] cameras = new CharSequence[]{"前置", "后置"};
    public static CharSequence[] hideShow = new CharSequence[]{"悬浮窗显示", "悬浮窗隐藏"};
    public static CharSequence[] intervals = new CharSequence[]{"5分钟", "10分钟","30分钟"};

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
        getTitleTv().setText(getString(R.string.app_name));
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
                        choseCamera(menuBean);
                        break;
                    case MinePresent.NAME_FLOAT:
                        showOrHideFloatWindow();
                        break;
                    case MinePresent.NAME_PLAY:
                        //                        Intent intent = new Intent(getApplicationContext(),
                        //                                MyVediosActivity.class);
                        //                        startActivityForResult(intent, FILE_RESULT_CODE);
                        break;
                    case MinePresent.NAME_RECORD_SPACE:
                        showIntervals();
                        break;
                    case MinePresent.NAME_MODIFY_PWD:
                        //                        showChangePwd();
                        break;
                    case MinePresent.NAME_CHANGE_ICON:
                        break;
                    case MinePresent.NAME_SWITCH_CAMERA:
                        //开启辅助服务开启障碍音量键捕获事件
                        try {
                            Intent intent = new Intent(
                                    android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                            startActivityForResult(intent, 99);
                            Toast.makeText(mContext, "找到" + getString(R.string.app_name) + "，开启即可",
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    case MinePresent.NAME_CLEAR_FILE:
                        //                        ClearCache(SAVED_VIDEO_PATH);
                        //                        ClearCache(SAVED_VIDEO_PATH2);
                        //                        Toast.makeText(getApplicationContext(), "清除成功",
                        //                                Toast.LENGTH_SHORT).show();
                        break;
                    case MinePresent.NAME_DESTROY_FILE:
                        new AlertDialog.Builder(mContext)

                                .setTitle("确认一键自毁？")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                    @Override
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

    /**
     * chose camera
     */
    private void choseCamera(MenuBean menuBean) {
        new AlertDialog.Builder(this).setSingleChoiceItems(cameras, Hawk.get(HawkProperty.CURRENT_CAMERA_INDEX,1),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        Hawk.put(HawkProperty.CURRENT_CAMERA_INDEX,position);
                        menuBean.setName(String.valueOf(cameras[position]));
                        mMenuAdapter.notifyItemChanged(menuBean.getTagId());
                        dialog.dismiss();
                    }


                }).show();
    }
    /**
     * showOrHideFloatWindow
     */
    private void showOrHideFloatWindow() {
        new AlertDialog.Builder(this).setSingleChoiceItems(hideShow, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        ToastUtils.toast(mContext, "dfd");
                        dialog.dismiss();
                    }


                }).show();
    }
    /**
     * showIntervals
     */
    private void showIntervals() {
        new AlertDialog.Builder(this).setSingleChoiceItems(intervals, 0,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        ToastUtils.toast(mContext, "dfd");
                        dialog.dismiss();
                    }


                }).show();
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

            case R.id.menu_quit_ll:
                break;
            default:
                break;
        }
    }
}
