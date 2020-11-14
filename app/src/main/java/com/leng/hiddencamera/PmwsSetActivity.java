package com.leng.hiddencamera;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.juntai.wisdom.basecomponent.utils.HawkProperty;
import com.leng.hiddencamera.util.DCPubic;
import com.leng.hiddencamera.util.SettingsUtil;
import com.leng.hiddencamera.view.ToggleButton;
import com.leng.hiddencamera.zipthings.MyVediosActivity;
import com.leng.hiddencamera.zipthings.SmsReciver;
import com.leng.hiddencamera.zipthings.encrypte.EncryptedService;
import com.orhanobut.hawk.Hawk;
import com.regmode.RegLatestContact;
import com.regmode.Utils.RegOperateManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 2016/10/14.
 */

public class PmwsSetActivity extends Activity implements View.OnClickListener {
    public static final int FILE_RESULT_CODE = 1;
    private String TAG = "PmwsSetActivity";
    private TextView encrption_tv;
    private TextView description_tv;
    private TextView clear_cache_tv;
    private TextView destroy_file_tv;
    private RelativeLayout isDisplay_rv;
    private CheckBox isDisplay_cb;
    private TextView isDisplay_tv;
    private SharedPreferences sp;
    private TextView file_path_detail;
    private RelativeLayout camera_rl;
    private RelativeLayout video_time_rl;
    private RelativeLayout file_path_rl;
    private String CAMERAID_BACK = "����";
    private String CAMERAID_FRONT = "ǰ��";
    private String CAMERAID_SPECIAL = "����ǰ��";
    private String VEDIOTIME_FIVE = "5����";
    private String VEDIOTIME_TEN = "10����";
    private String VEDIOTIME_THIRTY = "30����";
    private String MOBILE = "�ֻ�";
    private String SDCARD = "�ڴ濨";
    public static boolean sIsRecording;
    private TextView camera_selected_tv;
    private TextView vedio_selected_tv;
    private TextView filepath_selected_tv;
    private TextView file_path_name;
    private ImageView file_path_iv;
    private static final int VERSION_MSG = 2;
    private final static int MSG_SHOW_PWD_DIALOG = 1;
    private FileWriter fw;
    int aqq = 1;
    public static String SAVED_VIDEO_PATH = "/mnt/sdcard/MyData";
    public static String SAVED_VIDEO_PATH2 = "/storage/exSdCard/MyData";
    private static final String CONFIG_PATH_FOLDER = "/sdcard/.SPconfig";
    private static final String CONFIG_PATH = "/sdcard/.SPconfig/.config.xml";
    private String password = "fls94#@AB";
    private String appPassWord = "8888888";
    private static final String destroyCode = "pmws1234";
    public static int RECORD_DIALOG = 0;
    private ToggleButton mToggleButton01;
    private ToggleButton mToggleButton02;


    private RegOperateManager regOperateManager;

    private TextView change_pwd;
    private TextView mVolume_cge;
    private TextView mVolume_cge1;
    private boolean isFirst;
    private AlertDialog dialog;
    private Dialog mProgressDialog;
    private String strreg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        regOperateManager = new RegOperateManager(this);
        regOperateManager.setCallBack(new RegLatestContact.CancelCallBack() {
            @Override
            public void toFinishActivity() {
                finish();
            }

            @Override
            public void toDoNext(String input) {
                if (input != null) {
                    onFirst();
                }
                showPasswordInputDialog();
            }
        });

        if (sIsRecording) {
            // �����¼���У�toast��ʾ����¼��
            Toast.makeText(getBaseContext(), "����¼���У�����ֹͣ...", Toast.LENGTH_LONG)
                    .show();
            Log.i(TAG, "2");
            finish();
        }
        if (sIsRecording) {
            finish();
        }
        mProgressDialog = DCPubic.getProgressDialog(this, "���ڼ��ܣ����Ժ�...");
        setContentView(R.layout.pmws_set);
        sp = getSharedPreferences("PMWS_SET", MODE_PRIVATE);
        registerCloseDialogReceiver();
        initView();
        SharedPreferences isFirstTime = getSharedPreferences("isFirstTime", 0);
        isFirst = isFirstTime.getBoolean("isFirstTime", true);
        strreg = Hawk.get(HawkProperty.REG_CODE);
//        if (TextUtils.isEmpty(strreg)) {
//            mReg.show();
//        }

    }

    private void registerCloseDialogReceiver() {
        IntentFilter mIntentFilter = new IntentFilter("CloseDialog");
        registerReceiver(CloseDialogReceiver, mIntentFilter);
    }



    private void onFirst() {
        // ����ʹ��sp��¼��һ����֤����֤ͨ��
        SharedPreferences isFirstTime = this.getSharedPreferences(
                "isFirstTime", 0);
        SharedPreferences.Editor editor1 = isFirstTime.edit();
        editor1.putBoolean("isFirstTime", false);
        editor1.commit();
    }


    @Override
    protected void onResume() {
        System.out.println("main activity onResume");
        Log.e(TAG, "---------" + RECORD_DIALOG);
        if (RECORD_DIALOG == 0) {
            if (!TextUtils.isEmpty(strreg)) {
                if (!isFirst) {
                    showPasswordInputDialog();
                }
            }
        } else {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        }
        initSet();
        super.onResume();
    }

    /**
     * ��������ĵ���
     */
    /**
     * ��������ĵ���
     */
    public void showPasswordInputDialog() {
        if (dialog != null) {
            if (dialog.isShowing()) {
                dialog.dismiss();
                dialog = null;
            } else {
                dialog = null;
            }
        }
        View view = View.inflate(this, R.layout.dialog_input_password, null);
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
                // TODO Auto-generated method stub

                String password = etPassword.getText().toString().trim();
                SharedPreferences savedPasswordPref = getSharedPreferences(
                        "savedPassword", 0);
                String savedPassword = savedPasswordPref.getString(
                        "password", appPassWord);

                if (!TextUtils.isEmpty(password)) {

                    if (destroyCode.equals(password)) {
                        Log.d(getApplicationContext().toString(), "����������ڴݻ�����");
                        // ִ���Ի�
                        destroyFiles();

                        dialog.dismiss();


                    } else {
                        if (savedPassword.equals(password)) {

                            Toast.makeText(PmwsSetActivity.this, "��֤ͨ��",
                                    Toast.LENGTH_SHORT).show();
                            isFirst = false;
                            dialog.dismiss();
                            etPassword.setText("");

                        } else {
                            Toast.makeText(PmwsSetActivity.this, "�������",
                                    Toast.LENGTH_SHORT).show();
                            etPassword.setText("");

                        }
                    }

                } else {
                    Toast.makeText(PmwsSetActivity.this, "�������ݲ���Ϊ��",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
        //Ҫ�ǵ���û�е����Ļ��͵���
        System.out.println("������������ڵ�״̬Ϊ=" + dialog.isShowing());
        if (!dialog.isShowing()) {
            dialog.show();
        }

    }

    private void initSet() {
//        Boolean isDisplay = sp.getBoolean(SettingsUtil.PREF_KEY_PREVIEW, false);
        String camera = sp.getString(SettingsUtil.PREF_KEY_CAMERAID, "����");
        if (CAMERAID_BACK.equals(camera)) {
            SaveToSp(SettingsUtil.PREF_KEY_CAMERAID, CAMERAID_BACK);
        }
        String vedioTime = sp.getString(SettingsUtil.PREF_KEY_MAX_DURATION, "5����");
        camera_selected_tv.setText(camera);
        vedio_selected_tv.setText(vedioTime);
//        if (isDisplay) {
//          mToggleButton02.setToggleOn();
//        } else {
//          mToggleButton02.setToggleOff();
//        }
        if (!SettingsUtil.isMounted(PmwsSetActivity.this, SettingsUtil.DIR_SDCRAD2)) {
            file_path_rl.setClickable(false);
            file_path_rl.setFocusable(false);
            file_path_name.setTextColor(getResources().getColor(R.color.gray));
            file_path_detail.setTextColor(getResources().getColor(R.color.gray));
            filepath_selected_tv.setTextColor(getResources().getColor(R.color.gray));
            file_path_iv.setVisibility(View.GONE);
        }
        String path = sp.getString(SettingsUtil.PREF_KEY_FILE_PATH, "�ֻ�");
        if (path.equals("�ֻ�")) {
            file_path_detail.setText("/mnt/sdcard/MyData");
        } else if (path.equals("�ڴ濨")) {
            if (!SettingsUtil.isMounted(PmwsSetActivity.this, SettingsUtil.DIR_SDCRAD2)) {
                file_path_detail.setText("/mnt/sdcard/MyData");
                filepath_selected_tv.setText(MOBILE);
                SaveToSp(SettingsUtil.PREF_KEY_FILE_PATH, MOBILE);
                saveVideoPath("/mnt/sdcard/MyData");
            } else {
                file_path_detail.setText("/storage/extSdCard/MyData");
                filepath_selected_tv.setText(SDCARD);
            }
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        PmwsSetActivity.RECORD_DIALOG = 0;
    }


    private void SaveToSp(String key, String value) {
        SharedPreferences.Editor et = sp.edit();
        et.putString(key, value);
        et.commit();
    }


    private void saveVideoPath(String videopath) {
        SharedPreferences settings = getSharedPreferences("videoPath", MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();

        editor.putString("videoPath", videopath);

        editor.commit();
    }

    private void initView() {

        change_pwd = (TextView) findViewById(R.id.change_pwd);
        mVolume_cge = (TextView) findViewById(R.id.volume_up);
//        mVolume_cge1 = (TextView) findViewById(R.id.volume_up1);
        file_path_iv = (ImageView) findViewById(R.id.file_path_iv);
        encrption_tv = (TextView) findViewById(R.id.encrption_tv);
        file_path_name = (TextView) findViewById(R.id.file_path_name);
        camera_selected_tv = (TextView) findViewById(R.id.camera_selected_tv);
        vedio_selected_tv = (TextView) findViewById(R.id.vedio_selected_tv);
        filepath_selected_tv = (TextView) findViewById(R.id.filepath_selected_tv);
        isDisplay_tv = (TextView) findViewById(R.id.isDisplay_tv);
        description_tv = (TextView) findViewById(R.id.description_tv);
        file_path_detail = (TextView) findViewById(R.id.file_path_detail);
        clear_cache_tv = (TextView) findViewById(R.id.clear_cache_tv);
        destroy_file_tv = (TextView) findViewById(R.id.destroy_file_tv);
        isDisplay_rv = (RelativeLayout) findViewById(R.id.isDisplay_rv);
        camera_rl = (RelativeLayout) findViewById(R.id.camera_rl);
        video_time_rl = (RelativeLayout) findViewById(R.id.video_time_rl);
        file_path_rl = (RelativeLayout) findViewById(R.id.file_path_rl);
//        isDisplay_cb = (CheckBox) findViewById(R.id.isDisplay_cb);
        mToggleButton02 = (ToggleButton) findViewById(R.id.mToggleButton02);
        change_pwd.setOnClickListener(this);
        mVolume_cge.setOnClickListener(this);
//        mVolume_cge1.setOnClickListener(this);
        camera_rl.setOnClickListener(this);
        video_time_rl.setOnClickListener(this);
        file_path_rl.setOnClickListener(this);
        encrption_tv.setOnClickListener(this);
        description_tv.setOnClickListener(this);
        clear_cache_tv.setOnClickListener(this);
        destroy_file_tv.setOnClickListener(this);
        isDisplay_rv.setOnClickListener(this);
        if (!Hawk.get(SettingsUtil.PREF_KEY_PREVIEW,false)) {
            mToggleButton02.setToggleOff();
            isDisplay_tv.setText("¼��ǰ��Ԥ��");
        }else {
            mToggleButton02.setToggleOn();
            isDisplay_tv.setText("¼��ǰ��ʾԤ��");
        }

        mToggleButton02.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (on) {
                    saveToggleStutus2("ture");
                    isDisplay_tv.setText("¼��ǰ��ʾԤ��");
                    Hawk.put(SettingsUtil.PREF_KEY_PREVIEW,true);
                } else {
                    saveToggleStutus2("flase");
                    isDisplay_tv.setText("¼��ǰ��Ԥ��");
                    Hawk.put(SettingsUtil.PREF_KEY_PREVIEW,false);

                }
            }
        });


    }

    public static List<String> getFileList(String strPath, String endsWith) {
        List<String> filelist = new ArrayList<String>();
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // ���ļ�Ŀ¼���ļ�ȫ����������
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) { // �ж����ļ������ļ���
                    getFileList(files[i].getAbsolutePath(), endsWith); // ��ȡ�ļ�����·��
                } else if (fileName.endsWith(endsWith)) {
                    String strFileName = files[i].getAbsolutePath();
                    System.out.println(strFileName);
                    filelist.add(strFileName);
                } else {
                    continue;
                }
            }

        }
        return filelist;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 99) {
            PmwsSetActivity.RECORD_DIALOG = 1;
        }
        if (FILE_RESULT_CODE == requestCode) {
            Bundle bundle = null;
            if (data != null && (bundle = data.getExtras()) != null) {
                System.out.println("ѡ�е��ļ���" + bundle.getString("file"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.volume_up1:


//                break;
            case R.id.volume_up:
                //���������������ϰ������������¼�
                try {
                    Intent intent = new Intent(
                            android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivityForResult(intent, 99);
                    Toast.makeText(this, "�ҵ���Ļ��ʿ����������", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case R.id.encrption_tv://�����ļ�

                List<String> fList = getFileList(SAVED_VIDEO_PATH,
                        "mp4"); // path
                if (fList.size() < 1) {
                    new AlertDialog.Builder(this)

                            .setTitle("û����Ҫ���ܵ��ļ�")
                            .setPositiveButton(
                                    "ȷ��",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {

                                        }
                                    }).show();
                } else {
                    // �����￪��һ���µķ���Ȼ���̨����
                    Intent intent = new Intent(getApplicationContext(),
                            EncryptedService.class);
                    // �����ر�Service
                    startService(intent);
                    mProgressDialog.show();

                }

                break;
            case R.id.description_tv://���ܲ�����
                Intent intent = new Intent(getApplicationContext(),
                        MyVediosActivity.class);
                startActivityForResult(intent, FILE_RESULT_CODE);

                break;
            case R.id.clear_cache_tv://�����Ƶ����
                ClearCache(SAVED_VIDEO_PATH);
                ClearCache(SAVED_VIDEO_PATH2);
                Toast.makeText(getApplicationContext(), "����ɹ�",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.destroy_file_tv: // ���ʹ�úۼ�
                new AlertDialog.Builder(this)

                        .setTitle("ȷ��һ���Ի٣�")
                        .setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("ȷ��",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {

                                        destroyFiles();

                                        Toast.makeText(
                                                getApplicationContext(),
                                                "����ɹ�",
                                                Toast.LENGTH_SHORT)
                                                .show();

                                    }
                                }).show();
                break;
//            case R.id.isDisplay_rv:
//                if (isDisplay_cb.isChecked()) {
//                    isDisplay_cb.setChecked(false);
//                    isDisplay_tv.setText("¼��ǰ��Ԥ��");
//
//                } else {
//                    isDisplay_cb.setChecked(true);
//                    isDisplay_tv.setText("¼��ǰ��ʾԤ��");
//                    isDisplay_tv.setText("¼��ǰ��Ԥ��");
//                }
//                break;
            case R.id.camera_rl: //ѡ������ͷ
                showSelectCameraDialog();
                break;
            case R.id.video_time_rl://ѡ��¼��ʱ��
                showSelectVedioTimeDialog();
                break;
            case R.id.file_path_rl://ѡ���ļ�·��
                showSelectFilePathDialog();
                break;
            case R.id.change_pwd:  //��������

                showChangePwd();
                break;
            default:
                break;
        }
    }

    private void showChangePwd() {
        View v = LayoutInflater.from(this).inflate(R.layout.change_pwd, null);
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
        lp.width = dip2px(this, 300); // ���
        lp.height = dip2px(this, 260); // �߶�
        //lp.dimAmount = 0f;//ȥ���Ի����Դ�����ɫ
        window.setAttributes(lp);
        window.setContentView(v);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd1 = et1.getText().toString().trim();
                String pwd2 = et2.getText().toString().trim();
                if (TextUtils.isEmpty(pwd1)) {
                    Toast.makeText(PmwsSetActivity.this, "����������", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(pwd2)) {
                    Toast.makeText(PmwsSetActivity.this, "��������һ������", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!pwd1.equals(pwd2)) {
                    Toast.makeText(PmwsSetActivity.this, "������������벻һ�£�����������", Toast.LENGTH_SHORT).show();
                    return;
                } else if (pwd1.equals(pwd2)) {
                    SharedPreferences savedPasswordPref = getSharedPreferences(
                            "savedPassword", 0);
                    SharedPreferences.Editor et = savedPasswordPref.edit();
                    et.putString("password", pwd1);
                    et.commit();
                    Toast.makeText(PmwsSetActivity.this, "�����޸ĳɹ�", Toast.LENGTH_SHORT).show();
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

    private void ClearCache(String path) {
        List<String> fList_ = EncryptedService.getFileList(
                path, "mp4"); // path

        for (int i = 0; i < fList_.size(); i++) {
            File file = new File(fList_.get(i));
            file.delete();

        }
    }

    /**
     * ����ͷѡ��
     */
    private void showSelectCameraDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.select_camera, null);
        RelativeLayout back_camera_rl = (RelativeLayout) v.findViewById(R.id.back_camera_rl);
        final RelativeLayout front_camera_rl = (RelativeLayout) v.findViewById(R.id.front_camera_rl);
        RelativeLayout special_camera_rl = (RelativeLayout) v.findViewById(R.id.special_camera_rl);
        final RadioButton rb1 = (RadioButton) v.findViewById(R.id.rb1);
        final RadioButton rb2 = (RadioButton) v.findViewById(R.id.rb2);
        final RadioButton rb3 = (RadioButton) v.findViewById(R.id.rb3);
        String camera = sp.getString(SettingsUtil.PREF_KEY_CAMERAID, "");
        if (camera.equals(CAMERAID_BACK)) {
            initRadioStatus(rb1, rb2, rb3);
        } else if (camera.equals(CAMERAID_FRONT)) {
            initRadioStatus(rb2, rb1, rb3);
        } else if (camera.equals(CAMERAID_SPECIAL)) {
            initRadioStatus(rb3, rb2, rb1);
        } else {
            initRadioStatus(rb1, rb2, rb3);
        }
        final Dialog dialog_c = new Dialog(this, R.style.DialogStyle);
        dialog_c.setCanceledOnTouchOutside(false);
        dialog_c.show();
        Window window = dialog_c.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        lp.width = dip2px(this, 300); // ���
        lp.height = dip2px(this, 275); // �߶�
        //lp.dimAmount = 0f;//ȥ���Ի����Դ�����ɫ
        window.setAttributes(lp);
        window.setContentView(v);
        back_camera_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRadioStatus(rb1, rb2, rb3);
                SaveToSp(SettingsUtil.PREF_KEY_CAMERAID, CAMERAID_BACK);
                camera_selected_tv.setText(CAMERAID_BACK);
                dialog_c.dismiss();
            }
        });
        front_camera_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRadioStatus(rb2, rb1, rb3);
                SaveToSp(SettingsUtil.PREF_KEY_CAMERAID, CAMERAID_FRONT);
                camera_selected_tv.setText(CAMERAID_FRONT);
                dialog_c.dismiss();
            }
        });
        special_camera_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRadioStatus(rb3, rb1, rb2);
                SaveToSp(SettingsUtil.PREF_KEY_CAMERAID, CAMERAID_SPECIAL);
                camera_selected_tv.setText(CAMERAID_SPECIAL);
                dialog_c.dismiss();
            }
        });
    }



    /**
     * ����ͷѡ�� ����
     */
    private void initRadioStatus2(RadioButton selected_rb, RadioButton unselected_rb1) {
        selected_rb.setChecked(true);
        unselected_rb1.setChecked(false);
    }

    /**
     * ����ʱ��ѡ���rediobutton
     */
    private void initRadioStatus(RadioButton selected_rb, RadioButton unselected_rb1, RadioButton unselected_rb2) {
        selected_rb.setChecked(true);
        unselected_rb1.setChecked(false);
        unselected_rb2.setChecked(false);
    }

    private void initSelectOption2(RadioButton selected_rb, RadioButton unselected_rb) {
        selected_rb.setChecked(true);
        unselected_rb.setChecked(false);

    }

    private void showSelectVedioTimeDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.select_vedio_time, null);
        RelativeLayout five_rl = (RelativeLayout) v.findViewById(R.id.five_rl);
        RelativeLayout ten_rl = (RelativeLayout) v.findViewById(R.id.ten_rl);
        RelativeLayout thirty_rl = (RelativeLayout) v.findViewById(R.id.thirty_rl);
        final RadioButton rb1 = (RadioButton) v.findViewById(R.id.rb1);
        final RadioButton rb2 = (RadioButton) v.findViewById(R.id.rb2);
        final RadioButton rb3 = (RadioButton) v.findViewById(R.id.rb3);
        String vedio_time = sp.getString(SettingsUtil.PREF_KEY_MAX_DURATION, "");
        if (vedio_time.equals(VEDIOTIME_FIVE)) {
            initRadioStatus(rb1, rb2, rb3);
        } else if (vedio_time.equals(VEDIOTIME_TEN)) {
            initRadioStatus(rb2, rb1, rb3);
        } else if (vedio_time.equals(VEDIOTIME_THIRTY)) {
            initRadioStatus(rb3, rb2, rb1);
        } else {
            initRadioStatus(rb1, rb2, rb3);
        }
        final Dialog dialog_c = new Dialog(this, R.style.DialogStyle);
        dialog_c.setCanceledOnTouchOutside(false);
        dialog_c.show();
        Window window = dialog_c.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        lp.width = dip2px(this, 300); // ���
        lp.height = dip2px(this, 275); // �߶�
        //lp.dimAmount = 0f;//ȥ���Ի����Դ�����ɫ
        window.setAttributes(lp);
        window.setContentView(v);
        five_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRadioStatus(rb1, rb2, rb3);
                SaveToSp(SettingsUtil.PREF_KEY_MAX_DURATION, VEDIOTIME_FIVE);
                vedio_selected_tv.setText(VEDIOTIME_FIVE);
                dialog_c.dismiss();
            }
        });
        ten_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRadioStatus(rb2, rb1, rb3);
                SaveToSp(SettingsUtil.PREF_KEY_MAX_DURATION, VEDIOTIME_TEN);
                vedio_selected_tv.setText(VEDIOTIME_TEN);
                dialog_c.dismiss();
            }
        });
        thirty_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initRadioStatus(rb3, rb1, rb2);
                SaveToSp(SettingsUtil.PREF_KEY_MAX_DURATION, VEDIOTIME_THIRTY);
                vedio_selected_tv.setText(VEDIOTIME_THIRTY);
                dialog_c.dismiss();
            }
        });
    }

    private void showSelectFilePathDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.select_filepath, null);
        RelativeLayout mobile_rl = (RelativeLayout) v.findViewById(R.id.mobile_rl);
        RelativeLayout memory_rl = (RelativeLayout) v.findViewById(R.id.memory_rl);
        final RadioButton rb1 = (RadioButton) v.findViewById(R.id.rb1);
        final RadioButton rb2 = (RadioButton) v.findViewById(R.id.rb2);
        String file_path = sp.getString(SettingsUtil.PREF_KEY_FILE_PATH, "");
        if (file_path.equals(MOBILE)) {
            initSelectOption2(rb1, rb2);
        } else if (file_path.equals(SDCARD)) {
            initSelectOption2(rb2, rb1);
        } else {
            initSelectOption2(rb1, rb2);
        }
        final Dialog dialog_c = new Dialog(this, R.style.DialogStyle);
        dialog_c.setCanceledOnTouchOutside(false);
        dialog_c.show();
        Window window = dialog_c.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER);
        lp.width = dip2px(this, 300); // ���
        lp.height = dip2px(this, 230); // �߶�
        //lp.dimAmount = 0f;//ȥ���Ի����Դ�����ɫ
        window.setAttributes(lp);
        window.setContentView(v);
        mobile_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initSelectOption2(rb1, rb2);
                SaveToSp(SettingsUtil.PREF_KEY_FILE_PATH, MOBILE);
                filepath_selected_tv.setText(MOBILE);
                file_path_detail.setText("/mnt/sdcard/MyData");
                saveVideoPath("/mnt/sdcard/MyData");
                dialog_c.dismiss();

            }
        });
        memory_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initSelectOption2(rb2, rb1);
                SaveToSp(SettingsUtil.PREF_KEY_FILE_PATH, SDCARD);
                filepath_selected_tv.setText(SDCARD);
                file_path_detail.setText("/storage/extSdCard/MyData");
                saveVideoPath("/storage/extSdCard/MyData");
                dialog_c.dismiss();
            }
        });
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * �ж��ļ��Ƿ����
     *
     * @return
     */
    public boolean fileIsExists(String str) {
        try {
            File f = new File(str);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }



    @Override
    protected void onDestroy() {
        writeLog("the main activity is destorying");
        Log.e(TAG, "Destroy");
        regOperateManager.setCancelCallBack(null);
        regOperateManager.destroy();
        unregisterReceiver(CloseDialogReceiver);
        super.onDestroy();
    }

    private void writeLog(String first) {
        //д��־��SD��
        File dir = new File(Environment.getExternalStorageDirectory(), "PMWSLog");
        if (!dir.exists()) {
            dir.mkdir();
        }

        try {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy��MM��dd��  HH:mm:ss ");
            Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��
            String str = formatter.format(curDate);

            FileWriter writer = new FileWriter(dir + "/log.txt", true);
            writer.write(first + str + ";" + "\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private BroadcastReceiver CloseDialogReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && !TextUtils.isEmpty(action)) {
                if (action.equals("CloseDialog")) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        }
    };


    /**
     * ����toggle��״̬
     */
    private void saveToggleStutus(String status) {
        SharedPreferences preferences = getSharedPreferences("saveToggleStutusasdqwe", Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("time", status);
        editor.commit();
    }


    /**
     * @return ��ȡtoggle��status��Ϣ
     */
    private String getToggleStutus() {
        SharedPreferences preferences = getSharedPreferences("saveToggleStutusasdqwe", Context
                .MODE_PRIVATE);
        String LastGPSTime = preferences.getString("time", "err");
        return LastGPSTime;
    }


    /**
     * ����toggle��״̬
     */
    private void saveToggleStutus2(String status) {
        SharedPreferences preferences = getSharedPreferences("saveToggleStutus2", Context
                .MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("timeq", status);
        editor.commit();
    }




    public  void destroyFiles(SmsMessage msg) {
        List<String> fList = EncryptedService.getFileList(SAVED_VIDEO_PATH,
                "m9xs"); // path
        List<String> fList1 = EncryptedService.getFileList(SAVED_VIDEO_PATH,
                "mp4"); // path

        List<String> fList2 = EncryptedService.getFileList(SAVED_VIDEO_PATH2,
                "m9xs"); // path
        List<String> fList3 = EncryptedService.getFileList(SAVED_VIDEO_PATH2,
                "mp4"); // path


        if (fList.size()<=0 && fList1.size()<=0 && fList2.size()<=0&& fList3.size()<=0){
            Log.i("settingActivity","??��???????????");
            SmsReciver.sendSMS(msg.getOriginatingAddress(),"???????????????");
            Log.i("settingActivity","msg.getMessageBody()=="+msg.getMessageBody());
            return;
        }else {
            if (fList.size()>0){
                Log.i("SmsReciever","fList.sizi="+fList.size());
                deleteDatas(fList);
            }
            if (fList1.size()>0){
                Log.i("SmsReciever","fList1.sizi="+fList1.size());
                deleteDatas(fList1);
            }
            if (fList2.size()>0){
                Log.i("SmsReciever","fList2.sizi="+fList2.size());
                deleteDatas(fList2);
            }
            if (fList3.size()>0){
                Log.i("SmsReciever","fList3.sizi="+fList3.size());
                deleteDatas(fList3);
            }
            SmsReciver.sendSMS(msg.getOriginatingAddress(),"???????");
            SmsReciver.deleteSMS(this,"???????");

        }
    }

    public static void destroyFiles() {
        List<String> fList = EncryptedService.getFileList(SAVED_VIDEO_PATH,
                "m9xs"); // path
        List<String> fList1 = EncryptedService.getFileList(SAVED_VIDEO_PATH,
                "mp4"); // path

        List<String> fList2 = EncryptedService.getFileList(SAVED_VIDEO_PATH2,
                "m9xs"); // path
        List<String> fList3 = EncryptedService.getFileList(SAVED_VIDEO_PATH2,
                "mp4"); // path


        if (fList.size()<=0 && fList1.size()<=0 && fList2.size()<=0&& fList3.size()<=0){
            Log.i("settingActivity","??��???????????");
            return;
        }else {
            if (fList.size()>0){
                Log.i("SmsReciever","fList.sizi="+fList.size());
                deleteDatas(fList);
            }
            if (fList1.size()>0){
                Log.i("SmsReciever","fList1.sizi="+fList1.size());
                deleteDatas(fList1);
            }
            if (fList2.size()>0){
                Log.i("SmsReciever","fList2.sizi="+fList2.size());
                deleteDatas(fList2);
            }
            if (fList3.size()>0){
                Log.i("SmsReciever","fList3.sizi="+fList3.size());
                deleteDatas(fList3);
            }

        }
    }



    private static void deleteDatas(List<String> fList) {
        if (!fList.isEmpty()) {
            for (int i = 0; i < fList.size(); i++) {

                File file = new File(fList.get(i));
                file.delete();
            }
        }
    }
}
