package com.leng.hiddencamera.mine;

import com.juntai.wisdom.basecomponent.mvp.BasePresenter;
import com.juntai.wisdom.basecomponent.mvp.IModel;
import com.juntai.wisdom.basecomponent.mvp.IView;
import com.juntai.wisdom.basecomponent.utils.HawkProperty;
import com.leng.hiddencamera.R;
import com.leng.hiddencamera.bean.MenuBean;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: tobato
 * @Description: 作用描述
 * @CreateDate: 2020/12/28 16:23
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/12/28 16:23
 */
public class MinePresent extends BasePresenter<IModel, IView> {

    public static final int NAME_CAMERA = 0;
    public static final int NAME_FLOAT = 1;
    public static final int NAME_PLAY = 2;
    public static final int NAME_RECORD_SPACE = 3;
    public static final int NAME_RECORD_PATH = 4;
    public static final int NAME_MODIFY_PWD = 5;
    public static final int NAME_CHANGE_ICON = 6;
    public static final int NAME_SWITCH_CAMERA = 7;
    public static final int NAME_CLEAR_FILE = 8;
    public static final int NAME_DESTROY_FILE = 9;

    @Override
    protected IModel createModel() {
        return null;
    }

    /**
     * @return
     */
    protected List<MenuBean> getMineMenus() {
        List<MenuBean> arrays = new ArrayList<>();
        arrays.add(new MenuBean(NAME_CAMERA, Hawk.get(HawkProperty.CURRENT_CAMERA, "后置"), R.mipmap.set_camera_icon));
        arrays.add(new MenuBean(NAME_FLOAT, "悬浮窗显示", R.mipmap.set_float_icon));
        arrays.add(new MenuBean(NAME_PLAY, "视频播放", R.mipmap.set_media_play_icon));
        arrays.add(new MenuBean(NAME_RECORD_SPACE, "录像间隔", R.mipmap.set_interval_icon));
        arrays.add(new MenuBean(NAME_RECORD_PATH, "存储路径", R.mipmap.set_record_path));
        arrays.add(new MenuBean(NAME_MODIFY_PWD, "更改密码", R.mipmap.set_repwd));
        arrays.add(new MenuBean(NAME_CHANGE_ICON, "伪装图标", R.mipmap.set__change_icon));
        arrays.add(new MenuBean(NAME_SWITCH_CAMERA, "音量键切换\n摄像头", R.mipmap.set_camera_swit_v));
        arrays.add(new MenuBean(NAME_CLEAR_FILE, "清除解密文件", R.mipmap.set_clear_files));
        arrays.add(new MenuBean(NAME_DESTROY_FILE, "一键文件自毁", R.mipmap.set_destroy_files));

        return arrays;
    }


}
