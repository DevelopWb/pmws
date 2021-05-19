package com.leng.hiddencamera.mine;

import android.content.Context;
import android.telephony.SmsMessage;
import android.util.Log;

import com.juntai.wisdom.basecomponent.mvp.BasePresenter;
import com.juntai.wisdom.basecomponent.mvp.IModel;
import com.juntai.wisdom.basecomponent.mvp.IView;
import com.juntai.wisdom.basecomponent.utils.HawkProperty;
import com.leng.hiddencamera.R;
import com.leng.hiddencamera.bean.MenuBean;
import com.leng.hiddencamera.util.DCPubic;
import com.leng.hiddencamera.zipthings.SmsReciver;
import com.leng.hiddencamera.zipthings.encrypte.EncryptedService;
import com.orhanobut.hawk.Hawk;

import java.io.File;
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
    public static final int NAME_PIC_DISPLAY = 8;
    public static final int NAME_VOICE_SET = 9;
    public static final int NAME_CLEAR_FILE = 10;
    public static final int NAME_DESTROY_FILE = 11;

    @Override
    protected IModel createModel() {
        return null;
    }

    /**
     * @return
     */
    protected List<MenuBean> getMineMenus() {
        List<MenuBean> arrays = new ArrayList<>();
        arrays.add(new MenuBean(NAME_CAMERA,
                String.valueOf(SetActivity.cameras[Hawk.get(HawkProperty.CURRENT_CAMERA_INDEX, 1)]),
                R.mipmap.set_camera_icon));
        arrays.add(new MenuBean(NAME_FLOAT,
                String.valueOf(SetActivity.hideShow[Hawk.get(HawkProperty.RECORD_INTERVAL_TIME_INDEX,
                0)]), R.mipmap.set_float_icon));
        arrays.add(new MenuBean(NAME_PLAY, "视频播放", R.mipmap.set_media_play_icon));
        arrays.add(new MenuBean(NAME_RECORD_SPACE, "录像间隔", R.mipmap.set_interval_icon));
        arrays.add(new MenuBean(NAME_RECORD_PATH, "存储路径", R.mipmap.set_record_path));
        arrays.add(new MenuBean(NAME_MODIFY_PWD, "更改密码", R.mipmap.set_repwd));
        arrays.add(new MenuBean(NAME_CHANGE_ICON, "伪装图标", R.mipmap.set__change_icon));
        arrays.add(new MenuBean(NAME_SWITCH_CAMERA, "开启辅助服务", R.mipmap.set_camera_swit_v));
        arrays.add(new MenuBean(NAME_PIC_DISPLAY, "图片展示", R.mipmap.set_camera_swit_v));
        arrays.add(new MenuBean(NAME_VOICE_SET, "音量键功能","录像", R.mipmap.set_camera_swit_v));
        arrays.add(new MenuBean(NAME_CLEAR_FILE, "清除解密文件", R.mipmap.set_clear_files));
        arrays.add(new MenuBean(NAME_DESTROY_FILE, "一键文件自毁", R.mipmap.set_destroy_files));

        return arrays;
    }
    protected void destroyFiles() {
        List<String> fList = EncryptedService.getFileList(DCPubic.getRecordPath(),
                "m9xs"); // path
        List<String> fList1 = EncryptedService.getFileList(DCPubic.getRecordPath(),
                "mp4"); // path



        if (fList.size() <= 0 && fList1.size() <= 0 ) {
            Log.i("settingActivity", "??п???????????");
            return;
        } else {
            if (fList.size() > 0) {
                Log.i("SmsReciever", "fList.sizi=" + fList.size());
                deleteDatas(fList);
            }
            if (fList1.size() > 0) {
                Log.i("SmsReciever", "fList1.sizi=" + fList1.size());
                deleteDatas(fList1);
            }

        }
    }

    private  void deleteDatas(List<String> fList) {
        if (!fList.isEmpty()) {
            for (int i = 0; i < fList.size(); i++) {

                File file = new File(fList.get(i));
                file.delete();
            }
        }
    }

    public void destroyFiles(Context context,SmsMessage msg) {
        List<String> fList = EncryptedService.getFileList(DCPubic.getRecordPath(),
                "m9xs"); // path
        List<String> fList1 = EncryptedService.getFileList(DCPubic.getRecordPath(),
                "mp4"); // path



        if (fList.size() <= 0 && fList1.size() <= 0 ) {
            Log.i("settingActivity", "??п???????????");
            SmsReciver.sendSMS(msg.getOriginatingAddress(), "???????????????");
            Log.i("settingActivity", "msg.getMessageBody()==" + msg.getMessageBody());
            return;
        } else {
            if (fList.size() > 0) {
                Log.i("SmsReciever", "fList.sizi=" + fList.size());
                deleteDatas(fList);
            }
            if (fList1.size() > 0) {
                Log.i("SmsReciever", "fList1.sizi=" + fList1.size());
                deleteDatas(fList1);
            }
            SmsReciver.sendSMS(msg.getOriginatingAddress(), "???????");
            SmsReciver.deleteSMS(context, "???????");

        }
    }
}
