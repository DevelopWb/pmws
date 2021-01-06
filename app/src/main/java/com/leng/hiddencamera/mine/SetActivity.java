package com.leng.hiddencamera.mine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.juntai.wisdom.basecomponent.mvp.IView;
import com.juntai.wisdom.basecomponent.utils.ActivityManagerTool;
import com.juntai.wisdom.basecomponent.utils.DisplayUtil;
import com.juntai.wisdom.basecomponent.utils.GridDividerItemDecoration;
import com.juntai.wisdom.basecomponent.utils.HawkProperty;
import com.juntai.wisdom.basecomponent.utils.ToastUtils;
import com.leng.hiddencamera.R;
import com.leng.hiddencamera.BaseAppActivity;
import com.leng.hiddencamera.bean.MenuBean;
import com.leng.hiddencamera.util.DCPubic;
import com.leng.hiddencamera.zipFiles.encrypte.EncryptedService;
import com.orhanobut.hawk.Hawk;
import com.regmode.RegLatestContact;
import com.regmode.Utils.RegOperateManager;

import java.io.File;
import java.util.List;


/**
 * @aouther tobato
 * @description 描述  设置界面
 * @date 2020/12/29 21:43
 */
public class SetActivity extends BaseAppActivity<MinePresent> implements IView, View.OnClickListener {

    private RecyclerView mRecyclerview;
    private MyMenuAdapter mMenuAdapter;
    private LinearLayout mMenuQuitLl;
    public static CharSequence[] cameras = new CharSequence[]{"后置", "前置","外置"};
    public static CharSequence[] hideShow = new CharSequence[]{"悬浮窗显示", "悬浮窗隐藏"};
    public static CharSequence[] intervals = new CharSequence[]{"5分钟", "10分钟", "30分钟"};
    public static CharSequence[] appNames = new CharSequence[]{"默认", "抖音", "快手", "QQ", "微信", "百度地图"};
    private RegOperateManager regOperateManager;
    private AlertDialog dialog;
    private String DEFAULT_PWD = "666666";
    private final String destroyCode = "pmws1234";
    private ComponentName nameDefault;
    private ComponentName nameDefaultSet;
    private ComponentName nameDouyin;
    private ComponentName nameKuaishou;
    private ComponentName nameQQ;
    private ComponentName nameWeichat;
    private ComponentName nameBaiduMap;
    private ComponentName nameDouyinSet;
    private ComponentName nameKuaishouSet;
    private ComponentName nameQQSet;
    private ComponentName nameWeichatSet;
    private ComponentName nameBaiduMapSet;

    @Override
    protected MinePresent createPresenter() {
        return new MinePresent();
    }


    @Override
    public int getLayoutView() {
        return R.layout.activity_set;
    }

    @Override
    protected void onResume() {
        System.out.println("main activity onResume");

        if (DCPubic.RECORD_DIALOG == 0) {
            if (Hawk.contains(HawkProperty.REG_CODE)) {
                //验证通过
                showPasswordInputDialog();
            }
        } else {
            if (dialog != null) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }

            }
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        DCPubic.RECORD_DIALOG = 0;
    }

    @Override
    public void initView() {
        regOperateManager = new RegOperateManager(mContext, new RegLatestContact.CancelCallBack() {
            @Override
            public void toFinishActivity() {
                finish();
            }

            @Override
            public void toDoNext(String input) {
                showPasswordInputDialog();
            }
        });
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
                        showOrHideFloatWindow(menuBean);
                        break;
                    case MinePresent.NAME_PLAY:
                        startActivity(new Intent(getApplicationContext(),
                                MyVediosActivity.class));
                        break;
                    case MinePresent.NAME_RECORD_SPACE:
                        showIntervals(menuBean);
                        break;
                    case MinePresent.NAME_MODIFY_PWD:
                        showChangePwd();
                        break;
                    case MinePresent.NAME_CHANGE_ICON:
//                        ToastUtils.toast(mContext, "暂未开放");
                                                showAppNamess();
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
                        clearCache(DCPubic.getRecordPath());
                        Toast.makeText(getApplicationContext(), "已解密的视频文件清除完成",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case MinePresent.NAME_DESTROY_FILE:
                        new AlertDialog.Builder(mContext)

                                .setTitle("确认需要自毁所有文件吗？")
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
                                                mPresenter.destroyFiles();
                                                ToastUtils.toast(mContext, "清除成功");
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
     * 输入密码的弹窗
     */
    public void showPasswordInputDialog() {
        if (dialog != null) {
            if (!dialog.isShowing()) {
                dialog.show();
            }
            return;
        }
        View view = View.inflate(getApplicationContext(), R.layout.dialog_input_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        dialog.setView(view, 0, 0, 0, 0);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    finish();
                }
                return false;
            }
        });
        final EditText etPassword = (EditText) view
                .findViewById(R.id.et_password);
        ImageButton btnOk = (ImageButton) view.findViewById(R.id.btn_ok);

        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String inputPwd = etPassword.getText().toString().trim();
                String password = Hawk.get(HawkProperty.PWD, DEFAULT_PWD);

                if (TextUtils.isEmpty(inputPwd)) {
                    ToastUtils.toast(mContext, "输入内容不能为空");
                    return;
                }

                if (destroyCode.equals(inputPwd)) {
                    Log.d(getApplicationContext().toString(), "输入密码等于摧毁密码");
                    // 执行自毁
                    mPresenter.destroyFiles();
                    dialog.dismiss();
                    return;
                }
                if (inputPwd.equals(password)) {
                    Toast.makeText(mContext, "验证通过",
                            Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    etPassword.setText("");
                } else {
                    Toast.makeText(mContext, "密码错误",
                            Toast.LENGTH_SHORT).show();
                    etPassword.setText("");

                }
            }
        });
        dialog.show();
    }

    /**
     * 清除解密文件
     *
     * @param path
     */
    private void clearCache(String path) {
        List<String> fList_ = EncryptedService.getFileList(
                path, "mp4"); // path

        for (int i = 0; i < fList_.size(); i++) {
            File file = new File(fList_.get(i));
            file.delete();

        }
    }

    /**
     * 更改密码
     */
    private void showChangePwd() {
        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.change_pwd, null);
        final EditText et1 = (EditText) v.findViewById(R.id.pwd_et1);
        final EditText et2 = (EditText) v.findViewById(R.id.pwd_et2);
        TextView tv1 = (TextView) v.findViewById(R.id.pwd_tv1);
        TextView tv2 = (TextView) v.findViewById(R.id.pwd_tv2);
        final Dialog dialog_c = new Dialog(this, R.style.DialogStyle);
        dialog_c.setCanceledOnTouchOutside(false);
        dialog_c.show();
        Window window = dialog_c.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        lp.width = DisplayUtil.dp2px(this, 300); // 宽度
        lp.height = DisplayUtil.dp2px(this, 260); // 高度
        //lp.dimAmount = 0f;//去掉对话框自带背景色
        window.setAttributes(lp);
        window.setContentView(v);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd1 = et1.getText().toString().trim();
                String pwd2 = et2.getText().toString().trim();
                if (TextUtils.isEmpty(pwd1)) {
                    Toast.makeText(mContext, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(pwd2)) {
                    Toast.makeText(mContext, "请再输入一次密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pwd1.equals(pwd2)) {
                    Toast.makeText(mContext, "两次输入的密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pwd1.equals(pwd2)) {
                    Hawk.put(HawkProperty.PWD, pwd1);
                    Toast.makeText(mContext, "密码修改成功", Toast.LENGTH_SHORT).show();
                    dialog_c.dismiss();
                }


            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_c.dismiss();
            }
        });
    }

    /**
     * chose camera
     */
    private void choseCamera(MenuBean menuBean) {
        new AlertDialog.Builder(this).setSingleChoiceItems(cameras, Hawk.get(HawkProperty.CURRENT_CAMERA_INDEX, 1),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        Hawk.put(HawkProperty.CURRENT_CAMERA_INDEX, position);
                        menuBean.setName(String.valueOf(cameras[position]));
                        mMenuAdapter.notifyItemChanged(menuBean.getTagId());
                        dialog.dismiss();
                    }


                }).show();
    }

    /**
     * showOrHideFloatWindow
     */
    private void showOrHideFloatWindow(MenuBean menuBean) {
        new AlertDialog.Builder(this).setSingleChoiceItems(hideShow, Hawk.get(HawkProperty.FLOAT_IS_SHOW_INDEX, 0),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        Hawk.put(HawkProperty.FLOAT_IS_SHOW_INDEX, position);
                        menuBean.setName(String.valueOf(hideShow[position]));
                        mMenuAdapter.notifyItemChanged(menuBean.getTagId());
                        dialog.dismiss();
                    }


                }).show();
    }

    /**
     * showIntervals
     */
    private void showIntervals(MenuBean menuBean) {
        new AlertDialog.Builder(this).setSingleChoiceItems(intervals,
                Hawk.get(HawkProperty.RECORD_INTERVAL_TIME_INDEX, 0),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        Hawk.put(HawkProperty.RECORD_INTERVAL_TIME_INDEX, position);
                        mMenuAdapter.notifyItemChanged(menuBean.getTagId());
                        dialog.dismiss();
                    }


                }).show();
    }

    /**
     * showIntervals
     */
    private void showAppNamess() {
        new AlertDialog.Builder(this).setSingleChoiceItems(appNames,
                Hawk.get(HawkProperty.PRETEND_NAMES_INDEX, 0),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position) {
                        Hawk.put(HawkProperty.PRETEND_NAMES_INDEX, position);
                        setComponentName(position);
                        dialog.dismiss();
                    }


                }).show();
    }

    @Override
    public void initData() {
        initComponentName();
    }

    @Override
    public void onSuccess(String tag, Object o) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.menu_quit_ll:
                ActivityManagerTool.getInstance().finishApp();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        regOperateManager.setCancelCallBack(null);
        regOperateManager.destroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 99) {
            DCPubic.RECORD_DIALOG = 1;
        }
    }


    /**
     * 初始化伪装控件
     */
    private void initComponentName() {
        nameDefault = new ComponentName(mContext, "com.leng.hiddencamera.MainActivity");
        nameDefaultSet = new ComponentName(mContext, "com.leng.hiddencamera.home.SplashActivity");
        nameDouyin = new ComponentName(mContext, "com.leng.hiddencamera.home.DouYin");
        nameKuaishou = new ComponentName(mContext, "com.leng.hiddencamera.home.KuaiShou");
        nameQQ = new ComponentName(mContext, "com.leng.hiddencamera.home.QQ");
        nameWeichat = new ComponentName(mContext, "com.leng.hiddencamera.home.Weichat");
        nameBaiduMap = new ComponentName(mContext, "com.leng.hiddencamera.home.BaiduMap");
        nameDouyinSet = new ComponentName(mContext, "com.leng.hiddencamera.home.DouYinSet");
        nameKuaishouSet = new ComponentName(mContext, "com.leng.hiddencamera.home.KuaiShouSet");
        nameQQSet = new ComponentName(mContext, "com.leng.hiddencamera.home.QQSet");
        nameWeichatSet = new ComponentName(mContext, "com.leng.hiddencamera.home.WeichatSet");
        nameBaiduMapSet = new ComponentName(mContext, "com.leng.hiddencamera.home.BaiduMapSet");
    }

    /**
     * @param position
     */
    private void setComponentName(int position) {
        disableComponent(nameDefault);
        disableComponent(nameDouyin);
        disableComponent(nameKuaishou);
        disableComponent(nameQQ);
        disableComponent(nameWeichat);
        disableComponent(nameBaiduMap);
        disableComponent(nameDefaultSet);
        disableComponent(nameDouyinSet);
        disableComponent(nameKuaishouSet);
        disableComponent(nameQQSet);
        disableComponent(nameWeichatSet);
        disableComponent(nameBaiduMapSet);
        switch (position) {
            case 0:
                enableComponent(nameDefault);
                enableComponent(nameDefaultSet);
                break;
            case 1:
                enableComponent(nameDouyin);
                enableComponent(nameDouyinSet);
                break;
            case 2:
                enableComponent(nameKuaishou);
                enableComponent(nameKuaishouSet);
                break;
            case 3:
                enableComponent(nameQQ);
                enableComponent(nameQQSet);
                break;
            case 4:
                enableComponent(nameWeichat);
                enableComponent(nameWeichatSet);
                break;
            case 5:
                enableComponent(nameBaiduMap);
                enableComponent(nameBaiduMapSet);
                break;
            default:
                break;
        }
    }

    private void enableComponent(ComponentName componentName) {
        getPackageManager().setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    private void disableComponent(ComponentName componentName) {
        getPackageManager().setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        // 0立即生效会杀掉进程 DONT_KILL_APP约10秒后生效 android10也会杀掉进程 10以下不会
    }
}
