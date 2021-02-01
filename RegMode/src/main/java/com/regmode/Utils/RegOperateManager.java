package com.regmode.Utils;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.basenetlib.RequestStatus;
import com.juntai.wisdom.basecomponent.utils.FileUtils;
import com.juntai.wisdom.basecomponent.utils.HawkProperty;
import com.juntai.wisdom.basecomponent.utils.ToastUtils;
import com.orhanobut.hawk.Hawk;
import com.regmode.AppHttpUrl;
import com.regmode.R;
import com.regmode.RegLatestContact;
import com.regmode.RegLatestPresent;
import com.regmode.adapter.CommonProgressDialog;
import com.regmode.bean.AppInfoBean;
import com.regmode.bean.RegCodeBean;
import com.tencent.bugly.beta.download.DownloadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import static android.content.Context.MODE_PRIVATE;

/**
 * 注册码操作工具
 * Created by Administrator on 2017/3/30.
 */

public class RegOperateManager extends BaseReg implements RequestStatus {
    public static String APP_MARK = "PMWS";//软件标识
    private CommonProgressDialog mProgressDialog;
    private String nearestVersion;
    private Context context;
    public static String username;//登录名
    private AMapLocationClient locationClient = null;
    private String Lat;
    private String Lng;
    private String Addr;
    private Dialog dialog_Reg;
    private ProgressDialog progressDialog;
    private RegLatestContact.CancelCallBack cancelCallBack;
    private boolean firstCite = true;//第一次被引用
    public static RegOperateManager regOperateUtil;
    private RegLatestPresent present;
    private String input;


    public RegOperateManager(Context context, RegLatestContact.CancelCallBack cancelCallBack) {
        SoftReference<Context> mContextSoft = new SoftReference<>(context);
        this.context = mContextSoft.get();
        this.cancelCallBack = cancelCallBack;
        present = new RegLatestPresent();
        if (firstCite) {
            firstCite = false;
            initReg(context);
        }else {
            checkRegStatus();
        }

    }

    public void destroy() {
        context = null;
    }


    /**
     * 初始化
     *
     * @param context
     */
    private void initReg(Context context) {
        String strreg = Hawk.get(HawkProperty.REG_CODE);
        initLocation();
        if (strreg == null || TextUtils.isEmpty(strreg)) {
            showRegDialog();
        } else {
            checkRegStatus();
        }
    }


    //    /**
    //     * 保存注册码状态
    //     *
    //     * @param str 状态描述
    //     */
    //    private void SaveRegStatus(String str) {
    //        SharedPreferences.Editor editor = sp.edit();
    //        editor.putString("REGSTATUS", str);
    //        editor.commit();
    //    }

    //    /**
    //     * 检查有没有未减掉的注册码次数
    //     */
    //    public void CheckUnMinusedRegSizeToMinus() {
    //        if (isTheRegStatusOkNoToast(context)) {
    //            if (sp.getInt("UNMINUSEDSIZE", 0) > 0) {
    //                if (isNumberLimit) {
    //                    present.setRegisCodeNumber(strreg, sp.getInt("UNMINUSEDSIZE", 0), this);
    //                    sp.edit().putInt("UNMINUSEDSIZE", 0).commit();
    //                }
    //
    //            }
    //        }
    //
    //    }

    //查看注册码未减的次数
    public void UnMinusedRegSizeToCommit() {
        //        if (REGSIZE > 1) {
        //            SharedPreferences.Editor et = sp.edit();
        //            et.putInt("UNMINUSEDSIZE", REGSIZE - 1);
        //            et.commit();
        //        }
    }

    /**
     * 判定注册码状态是否正常
     *
     * @return
     */
    public boolean isTheRegStatusOk(String reg_status) {
        //  "注册码已经禁用"
        if (reg_status.contains("已禁用")) {
            warnRegStatus("注册码不可用，请联系管理员", "");
            return false;
        } else if (reg_status.contains("次数用尽")) {
            warnRegStatus("注册码次数已用完，请联系管理员", "");
            return false;
        } else if (reg_status.contains("已过期")) {
            warnRegStatus("注册码已过期，请联系管理员", "");
            return false;
        } else if (reg_status.contains("不存在")) {
            warnRegStatus("注册码不存在，请联系管理员", "");
            return false;
        } else {
            warnRegStatus(reg_status, "");
            return true;
        }
    }


    /**
     * 判定注册码状态是否正常
     *
     * @return
     */
    public boolean isTheRegStatusOkNoToast(Context context) {
        SharedPreferences sp = context.getSharedPreferences("REG", MODE_PRIVATE);
        String reg_status = sp.getString("REGSTATUS", "注册码正常");
        //  "注册码已经禁用"
        if (reg_status.equals("注册码已禁用")) {
            return false;
        } else if (reg_status.equals("注册码次数已用完")) {
            return false;
        } else if (reg_status.equals("注册码已过期")) {
            return false;
        } else if (reg_status.equals("注册码不正确")) {
            return false;
        } else {
            return true;
        }
    }


    /**
     * 检查注册码的状态
     */
    @Override
    public void checkRegStatus() {
        if (!RegPubUtils.isConnected(context.getApplicationContext())) {
            //无网络情况下 校验本地
            RegCodeBean.ModelBean regBean = Hawk.get(HawkProperty.REG_INFO);
            if (regBean == null) {
                ToastUtils.toast(context, "网络异常 请检查网络");
                return;
            }
            checkRegStatus(regBean);
        } else {
            present.checkReg((String) Hawk.get(HawkProperty.REG_CODE), APP_MARK, RegLatestContact.CHECK_REG_EVERYTIME,
                    RegOperateManager.this);
        }

    }

    /**
     * 重置下次提醒的日期
     *
     * @param status
     */
    private void resetNextWarnTime(String status) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("NEXTWARNTIME", MODE_PRIVATE);
        SharedPreferences.Editor et = sharedPreferences.edit();
        et.putString("nextRegStatusTime" + status, "");
        et.commit();
    }


    /**
     * 判定是否提醒注册吗状态
     */
    private boolean IsTheRegStatusTime(String status) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("NEXTWARNTIME", MODE_PRIVATE);
        final String time = sharedPreferences.getString("nextRegStatusTime" + status, "");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String time2 = new SimpleDateFormat("yyyy-MM-dd").format(date);
        if (TextUtils.isEmpty(time)) {
            return true;
        } else {
            if (RegPubUtils.compareTime(time2, time)) {
                return true;
            } else {
                return false;
            }
        }

    }


    /**
     * 提醒注册码的状态
     *
     * @param text
     * @param status
     */
    private void warnRegStatus(final String text, final String status) {

        View v = LayoutInflater.from(context).inflate(R.layout.warn_reg_layout
                , null);
        final Dialog dialog_toWarn = new Dialog(context, R.style.DialogStyle);
        dialog_toWarn.setCanceledOnTouchOutside(false);
        dialog_toWarn.setCancelable(false);
        dialog_toWarn.show();
        Window window = dialog_toWarn.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        window.setAttributes(lp);
        window.setContentView(v);
        final TextView nfs_set_no_tv = (TextView) v.findViewById(R.id.warn_reg_tv);
        final TextView warn_reg_textViewreg = (TextView) v.findViewById(R.id.warn_reg_textViewreg);
        nfs_set_no_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (text != null && !TextUtils.isEmpty(text)) {
                    if (text.contains("不存在") || text.contains("已过期") || text.contains("已用完") || text.contains("不可用") || text.contains("不匹配")) {
                        if (cancelCallBack != null) {
                            cancelCallBack.toFinishActivity();
                        }
                    } else {
                        String nextTime = GetNextWarnTime(1);
                        SharedPreferences sharedPreferences = context.getSharedPreferences("NEXTWARNTIME",
                                MODE_PRIVATE);
                        SharedPreferences.Editor et = sharedPreferences.edit();
                        et.putString("nextRegStatusTime" + status, nextTime);
                        et.commit();
                        dialog_toWarn.dismiss();
                    }

                }
            }

        });
        warn_reg_textViewreg.setText(text);
    }

    /**
     * 获取下次提醒的时间,day天后
     */
    private String GetNextWarnTime(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, day);
        Date date = calendar.getTime();
        String time = new SimpleDateFormat("yyyy-MM-dd").format(date);
        return time;
    }


    /**
     * 从服务器下载apk包
     *
     * @param url
     */
    private void downAPKfromService(String url) {

        if (RegPubUtils.isConnected(context)) {
            mProgressDialog = new CommonProgressDialog(context);
            mProgressDialog.setMessage("正在下载");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            final DownloadTask downloadTask = new DownloadTask(context);
            downloadTask.execute(url);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    downloadTask.cancel(true);
                }
            });
            //downFile(url);
        }

    }

    @Override
    public void onStart(String tag) {

    }

    @Override
    public void onSuccess(Object o, String tag) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        switch (tag) {
            case RegLatestContact.CHECK_REG:
                //验证注册码
                RegCodeBean regCodeBean = (RegCodeBean) o;
                if (regCodeBean != null) {
                    String resultTag = regCodeBean.getResult();
                    if ("ok".equals(resultTag)) {
                        if (regCodeBean.getModel() != null && regCodeBean.getModel().size() > 0) {
                            RegCodeBean.ModelBean modelBean = regCodeBean.getModel().get(0);
                            String regStatus = modelBean.getRegisCodeState();

                            if ("正常".equals(regStatus)) {
                                //注册码正常
                                Hawk.put(HawkProperty.REG_CODE, input);
                                if (!RegPubUtils.PUBLIC_REGCODE.equals(input)) {
                                    //如果不是通用注册码 需要校验本地
                                    if (!checkImei(modelBean)) {
                                        return;
                                    }
                                }

                                //                                String mac = modelBean.getMAC();
                                //                                if (mac != null && !TextUtils.isEmpty(mac)) {
                                //                                    //保存mac信息
                                //                                    Hawk.put(HawkProperty.MAC_CODE, mac);
                                //                                }
                                ToastUtils.toast(context, "注册码验证成功");
                                if (cancelCallBack != null) {
                                    cancelCallBack.toDoNext(input);
                                }
                                if (dialog_Reg != null && dialog_Reg.isShowing()) {
                                    dialog_Reg.dismiss();
                                }
                            } else {
                                isTheRegStatusOk(regStatus);
                            }
                        } else {
                            isTheRegStatusOk("注册码不存在");
                        }

                    } else {
                        ToastUtils.toast(context, "服务器异常");
                    }
                }
                break;
            case RegLatestContact.CHECK_REG_EVERYTIME:
                //每次进入软件的时候校验
                //验证注册码
                RegCodeBean regBean = (RegCodeBean) o;
                if (regBean != null) {
                    String resultTag = regBean.getResult();
                    if ("ok".equals(resultTag)) {
                        if (regBean.getModel() != null && regBean.getModel().size() > 0) {
                            RegCodeBean.ModelBean modelBean = regBean.getModel().get(0);
                            Hawk.put(HawkProperty.REG_INFO, modelBean);
                            checkRegStatus(modelBean);
                        } else {
                            isTheRegStatusOk("注册码不存在");

                        }

                    } else {
                        ToastUtils.toast(context, "服务器异常");
                    }
                }
                break;
            case RegLatestContact.SET_IMEI:

                break;
            case RegLatestContact.GET_APP_VERSION_INFO:
                AppInfoBean appInfoBean = (AppInfoBean) o;
                if (appInfoBean != null) {
                    if (appInfoBean.getModel() != null && appInfoBean.getModel().size() > 0) {
                        AppInfoBean.ModelBean dataBean = appInfoBean.getModel().get(0);
                        String nearestVersion = dataBean.getSoftwareVersion();
                        String down_url = dataBean.getSoftDownloadUrl();
                        if (updateableSoftVersion(getAPPVersion(), nearestVersion)) {
                            if (IsTheTime()) {
                                warnUpgradeDialog(AppHttpUrl.BASE_URL + down_url);
                            }

                        }
                        //                        else {//将
                        //                            SharedPreferences sharedPreferences = context
                        //                            .getSharedPreferences("NEXTWARNTIME", MODE_PRIVATE);
                        //                            SharedPreferences.Editor et = sharedPreferences.edit();
                        //                            et.putString("nextTime", "");
                        //                            et.commit();
                        //                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 校验注册码的状态
     *
     * @param modelBean
     */
    private void checkRegStatus(RegCodeBean.ModelBean modelBean) {
        String regStatus = modelBean.getRegisCodeState();

        if ("正常".equals(regStatus)) {
            if (!RegPubUtils.PUBLIC_REGCODE.equals(Hawk.get(HawkProperty.REG_CODE))) {
                //如果不是通用注册码 需要校验本地
                if (!checkImei(modelBean)) {
                    return;
                }
            }
            //自动升级
            String isAutoUpdate = modelBean.getIsAutoUpdate();
            if (isAutoUpdate != null && !TextUtils.isEmpty(isAutoUpdate)) {
                if (isAutoUpdate.equals("1")) {//允许自动升级
                    getNearestVersionFromService();
                    return;
                }
            }
            //有效期限制
            String isValid = modelBean.getIsValid();
            if (isValid != null && !TextUtils.isEmpty(isValid)) {
                if (isValid.equals("0")) {//注册码限制时间
                    String ValidEnd = modelBean.getValidEnd();
                    String time = ValidEnd.split(" ")[0];
                    if (RegPubUtils.TheDayToNextDay(time) > 0 && RegPubUtils.TheDayToNextDay(time) < 8) {

                        if (IsTheRegStatusTime("isValid")) {
                            warnRegStatus("注册码有效期还剩" + RegPubUtils.TheDayToNextDay(time) +
                                    "天，请联系管理员", "isValid");
                        }
                    } else if (RegPubUtils.TheDayToNextDay(time) < 0) {
                        warnRegStatus("注册码已过期 ，请联系管理员", "");
                        return;
                    } else {//重置下次提醒的时间
                        resetNextWarnTime("isValid");
                    }
                }
            }
            //次数限制
            String isNumber = modelBean.getIsNumber();
            if (isNumber != null && !TextUtils.isEmpty(isNumber)) {
                if (isNumber.equals("0")) {//注册码有次数限制
                    String NumberTotal = modelBean.getNumber();
                    String NumberUsed = modelBean.getNumberNow();
                    int NumberNow = Integer.parseInt(NumberTotal) - Integer.parseInt(NumberUsed);
                    if (NumberNow > 0 && NumberNow < 100) {
                        if (IsTheRegStatusTime("isNumber")) {
                            warnRegStatus("注册码次数还剩" + NumberNow + "次，请联系管理员", "isNumber");
                        }

                    } else if (NumberNow < 0) {
                        warnRegStatus("注册码次数已用尽，请联系管理员", "");
                        return;
                    } else {//重置下次提醒的日期
                        resetNextWarnTime("isNumber");
                    }
                }
            }
            if (cancelCallBack != null) {
                cancelCallBack.toDoNext(null);
            }
        } else {
            isTheRegStatusOk(regStatus);
        }
    }

    /**
     * 校验imei
     *
     * @param modelBean
     */
    private boolean checkImei(RegCodeBean.ModelBean modelBean) {
        // 校验本地信息和服务器上的是否相同
        String imei = modelBean.getImei();
        if (imei != null && !TextUtils.isEmpty(imei)) {
            //校验
            String localImei = FileUtils.getFileContent("property.txt");
            if (localImei != null && !TextUtils.isEmpty(localImei)) {
                if (localImei.equals(imei)) {
                    return true;
                } else {
                    warnRegStatus("注册码绑定IMEI不匹配，请联系管理员", "");
                    return false;
                }
            } else {
                //换手机登录了 或者本地配置文件丢失
                warnRegStatus("注册码绑定IMEI不匹配，请联系管理员", "");
                return false;
            }
        } else {
            String isImei = modelBean.getIsImei();
            if ("1".equals(isImei)) {
                //将注册码用md5加密并保存本地
                FileUtils.writeToTxtFile((String) Hawk.get(HawkProperty.REG_CODE), "property.txt");
                //将加密过的注册码上传到服务器
                present.setImei((String) Hawk.get(HawkProperty.REG_CODE), FileUtils.getFileContent("property.txt"),
                        RegLatestContact.SET_IMEI, this);
            }
            return true;
        }
    }

    @Override
    public void onError(String tag) {
        Toast.makeText(context, tag, Toast.LENGTH_SHORT).show();
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void setRegistCodeNumber(int size) {
        present.setRegisCodeNumber((String) Hawk.get(HawkProperty.REG_CODE), size, this);
    }

    /**
     * 从服务器获取最新的版本
     */
    private void getNearestVersionFromService() {

        if (isConnected(context)) {
            //获取软件的key
            present.getAppVersionInfoAndKeyFromService(RegLatestContact.GET_APP_VERSION_INFO, this);
        }

    }

    /**
     * 通过软件的版本名称判定是否升级
     *
     * @param localVersionName  本地软件的版本名称
     * @param serverVersionName 服务端软件的版本名称
     * @return
     */
    private boolean updateableSoftVersion(String localVersionName, String serverVersionName) {
        if (TextUtils.isEmpty(localVersionName) || TextUtils.isEmpty(serverVersionName)) {
            return false;
        }
        String local3 = "0";
        String server3 = "0";
        String[] localVersion = localVersionName.split("\\.");
        String[] serverVersion = serverVersionName.split("\\.");
        String local1 = localVersion[0];
        String local2 = localVersion[1];
        if (localVersion.length == 3) {
            local3 = localVersion[2];
        }
        String server1 = serverVersion[0];
        String server2 = serverVersion[1];
        if (serverVersion.length == 3) {
            server3 = serverVersion[2];
        }
        if (Integer.parseInt(server1) > Integer.parseInt(local1)) {
            return true;
        }
        if (Integer.parseInt(server2) > Integer.parseInt(local2)) {
            return true;
        }
        if (Integer.parseInt(server3) > Integer.parseInt(local3)) {
            return true;
        }
        return false;
    }

    /**
     * 判定是否提醒升级
     */
    private boolean IsTheTime() {
        final String time = Hawk.get(HawkProperty.NEXT_WARN_UPDATE);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String time2 = new SimpleDateFormat("yyyy-MM-dd").format(date);
        if (TextUtils.isEmpty(time)) {
            return true;
        } else {
            if (compareTime(time2, time)) {
                return true;
            } else {
                return false;
            }
        }


    }

    /**
     * 比较两个时间串的大小
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public static boolean compareTime(String startTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Long a = sdf.parse(startTime).getTime();
            Long b = sdf.parse(endTime).getTime();
            if (a > b || a == b) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 提醒升级的对话框
     *
     * @param url
     */
    private void warnUpgradeDialog(final String url) {
        AlertDialog dialog = new AlertDialog.Builder(context).setTitle("检测到软件有新版本，是否更新？")
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        downAPKfromService(url);
                    }
                }).setNegativeButton("稍后提示", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        String nextTime = GetNextWarnTime(7);
                        Hawk.put(HawkProperty.NEXT_WARN_UPDATE, nextTime);
                    }
                }).show();
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;

        DownloadTask(Context context) {
            this.context = context;
        }

        //执行异步任务（doInBackground）之前执行，并且在ui线程中执行
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //开始下载 对话框进度条显示
            mProgressDialog.show();
            mProgressDialog.setProgress(0);
        }

        @Override
        protected String doInBackground(String... params) {
            int i = 0;
            String uri = params[0];
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(uri);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file


                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();
                mProgressDialog.setMax(fileLength);
                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(GetAPKPath());
                byte data[] = new byte[4096];
                int total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    //					if (fileLength > 0) // only if total length is known
                    //					{
                    //						i = (int) (total * 100 / fileLength);
                    //					}
                    mProgressDialog.setProgress(total);
                    //					publishProgress(i);
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null) {
                        output.close();
                    }
                    if (input != null) {
                        input.close();
                    }
                } catch (IOException ignored) {
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return "right";
        }

        //在ui线程中执行 可以操作ui
        @Override
        protected void onPostExecute(String string) {
            // TODO Auto-generated method stub
            super.onPostExecute(string);
            //下载完成 对话框进度条隐藏
            if (string.equals("right")) {
                mProgressDialog.cancel();
                Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
                installApk();
            } else {
                mProgressDialog.cancel();
                Toast.makeText(context, "下载失败", Toast.LENGTH_LONG).show();
            }


        }

        /*
         * 在doInBackground方法中已经调用publishProgress方法 更新任务的执行进度后
         * 调用这个方法 实现进度条的更新
         * */
        @Override
        protected void onProgressUpdate(Integer... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);

        }
    }

    /**
     * 安装APK
     */
    private void installApk() {
        File file = new File(GetAPKPath());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 定义下载包存储路径
     */
    private String GetAPKPath() {
        File file = new File("/mnt/sdcard/.toInstallPG");
        if (!file.exists()) {
            file.mkdir();
        }
        String path = "/mnt/sdcard/.toInstallPG" + "/" + getAPPName() + nearestVersion + ".apk";
        return path;
    }


    /**
     * 获取软件名称
     */
    public String getAPPName() {
        String appName = "";
        PackageManager pm = context.getPackageManager();//得到PackageManager对象
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(context.getPackageName(), 0);
            appName = (String) pm.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }


    /**
     * 弹出注册窗口
     */
    public void showRegDialog() {
        if (dialog_Reg != null && dialog_Reg.isShowing()) {
            dialog_Reg.dismiss();
            dialog_Reg = null;
        }
        View v = LayoutInflater.from(context).inflate(R.layout.reg_dialog, null);
        dialog_Reg = new Dialog(context, R.style.DialogStyle);
        dialog_Reg.setCanceledOnTouchOutside(false);
        dialog_Reg.show();
        dialog_Reg.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                dialog_Reg.dismiss();
                //退出程序
                if (cancelCallBack != null) {
                    cancelCallBack.toFinishActivity();
                }
            }
        });
        Window window = dialog_Reg.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        lp.width = RegPubUtils.dip2px(context, 290); // 宽度
        lp.height = RegPubUtils.dip2px(context, 200); // 高度
        lp.alpha = 0.7f; // 透明度
        window.setAttributes(lp);
        window.setContentView(v);
        final TextView reg = (TextView) v.findViewById(R.id.editTextReg);
        ImageButton ib = (ImageButton) v.findViewById(R.id.imageButtonReg);
        ImageButton.OnClickListener listener = new ImageButton.OnClickListener() {

            public void onClick(View v) {
                if (!RegPubUtils.isConnected(context.getApplicationContext())) {
                    Toast.makeText(context, "网络异常，请检查手机网络", Toast.LENGTH_LONG)
                            .show();
                    return;
                }

                input = reg.getText().toString().trim();
                if (input == null || TextUtils.isEmpty(input)) {
                    Toast.makeText(context, "请输入注册码",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // 网络验证中
                progressDialog = ProgressDialog.show(context, "请稍候",
                        "注册码验证中请不要进行其他操作", true);
                progressDialog.setCancelable(true);
                present.checkReg(input, APP_MARK, RegLatestContact.CHECK_REG, RegOperateManager.this);

            }
        };

        ib.setOnClickListener(listener);

    }


    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(context.getApplicationContext());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
        startLocation();
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        //		mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        //		AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP);//可选，
        //		设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        //		mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        return mOption;
    }

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {
        //		//根据控件的选择，重新设置定位参数
        //		resetOption();
        // 设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            stopLocation();
            locationClient.onDestroy();
            locationClient = null;
        }
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation loc) {
            if (null != loc) {
                //解析定位结果
                Lat = loc.getLatitude() + "";
                Lng = loc.getLongitude() + "";
                Addr = loc.getAddress();
                checkSavedVersion();
            }
        }
    };

    //    /**
    //     * 获取运营商sim卡的ICCID号
    //     *
    //     * @return ICCID号
    //     */
    //    public String getRegImeiIccid() {
    //        String msg = "";
    //        TelephonyManager tm = (TelephonyManager) context
    //                .getSystemService(Context.TELEPHONY_SERVICE);
    //        if (TextUtils.isEmpty(tm.getDeviceId())) {
    //            // 获取sim卡的ICCID号
    //            return tm.getSimSerialNumber();
    //        } else {
    //            return tm.getDeviceId();
    //        }
    //
    //    }

    private String getPhoneMessage() {
        String lac = "";
        String cid = "";
        String nid = "";
        String imei = "";
        String phoneNo = "";
        String imsi = "";
        String mac = "";
        StringBuffer phoneInfo_sb = new StringBuffer();
        TelephonyManager mTManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (mTManager != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            imei = mTManager.getDeviceId();
            phoneNo = mTManager.getLine1Number();
            imsi = mTManager.getSubscriberId();
        }
        mac = macAddress();
        phoneInfo_sb.append("PhoneNo:" + phoneNo + "," + "Imei:" + imei + "," + "Imsi:" + imsi + "," + "Mac:" + mac + ",");

        if (mTManager != null) {
            int phonetype = mTManager.getPhoneType();
            if (phonetype == TelephonyManager.PHONE_TYPE_GSM) {
                GsmCellLocation gcl = (GsmCellLocation) mTManager.getCellLocation();
                cid = gcl.getCid() + "";
                lac = gcl.getLac() + "";
            } else if (phonetype == TelephonyManager.PHONE_TYPE_CDMA) {
                CdmaCellLocation gcl = (CdmaCellLocation) mTManager
                        .getCellLocation();
                if (gcl != null) {
                    nid = gcl.getNetworkId() + "";// nid
                    cid = gcl.getBaseStationId() + "";// cellid
                    lac = gcl.getSystemId() + ""; // sid
                }

            }
            phoneInfo_sb.append("Lat:" + Lat + "," + "Log:" + Lng + "," + "Lac:" + lac + "," + "Cid:" + cid + "," +
                    "Nid:" + nid + "," + "Addr:" + Addr);
        }
        return phoneInfo_sb.toString();
    }

    /**
     * 获取软件版本号
     */
    private String getAPPVersion() {
        PackageManager pm = context.getPackageManager();//得到PackageManager对象
        String version_app = "";
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);//得到PackageInfo对象，封装了一些软件包的信息在里面
            version_app = pi.versionName;//获取清单文件中versionCode节点的值
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version_app;
    }


    public String macAddress() {
        String address = null;
        try {
            // 把当前机器上的访问网络接口的存入 Enumeration集合中
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface netWork = interfaces.nextElement();
                // 如果存在硬件地址并可以使用给定的当前权限访问，则返回该硬件地址（通常是 MAC）。
                byte[] by = netWork.getHardwareAddress();
                if (by == null || by.length == 0) {
                    continue;
                }
                StringBuilder builder = new StringBuilder();
                for (byte b : by) {
                    builder.append(String.format("%02X:", b));
                }
                if (builder.length() > 0) {
                    builder.deleteCharAt(builder.length() - 1);
                }
                String mac = builder.toString();
                // 从路由器上在线设备的MAC地址列表，可以印证设备Wifi的 name 是 wlan0
                if (netWork.getName().equals("wlan0")) {
                    address = mac;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return address;
    }


    //    /**
    //     * 注册码验证成功后
    //     */
    //    private void RegSuccess(String input, String guestName, String isToolTip, String isNumber) {
    //        SharedPreferences.Editor editor = sp.edit();
    //        if (input != null && guestName != null) {
    //            Toast.makeText(context, "注册码验证成功",
    //                    Toast.LENGTH_LONG).show();
    //            strreg = input;
    //            editor.putString("OBJREG", input);
    //            editor.putString("GUESTNAME", guestName);
    //        }
    //
    //        if (isToolTip.equals("0")) {//0代表不提示
    //            istoolTip = false;
    //            editor.putBoolean("ISTOOLTIP", false);
    //        } else {
    //            istoolTip = true;
    //            editor.putBoolean("ISTOOLTIP", true);
    //        }
    //        if (isNumber.equals("0")) {//0代表有次数限制
    //            isNumberLimit = true;
    //            editor.putBoolean("ISNUMBER", true);
    //        } else {
    //            isNumberLimit = false;
    //            editor.putBoolean("ISNUMBER", false);
    //        }
    //        editor.commit();
    //        if (dialog_Reg != null && dialog_Reg.isShowing()) {
    //            dialog_Reg.dismiss();
    //        }
    //
    //        if (cancelCallBack != null) {
    //            cancelCallBack.toDoNext();
    //        }
    //
    //    }


    /**
     * 检测保存本地的版本号
     */
    private void checkSavedVersion() {
        String savedVersion = Hawk.get(HawkProperty.APP_SAVED_VERSION, "");
        //        String savedVersion = "1.0";
        String nowVersion = getAPPVersion();
        if (savedVersion.equals("")) {
            Hawk.put(HawkProperty.APP_SAVED_VERSION, nowVersion);
            //上传版本信息
            String info = getInfoWhenVersionChanged(savedVersion, nowVersion);
            present.uploadVersionInfo((String) Hawk.get(HawkProperty.REG_CODE), info, this);
        } else {
            if (!savedVersion.equals(nowVersion) && Double.parseDouble(nowVersion) > Double.parseDouble(savedVersion)) {
                //上传版本信息
                String info = getInfoWhenVersionChanged(savedVersion, nowVersion);
                present.uploadVersionInfo((String) Hawk.get(HawkProperty.REG_CODE), info, this);
            }

        }

    }

    private String getInfoWhenVersionChanged(String originalVersion, String newestVersion) {
        String PhoneNo = "";
        String Imei = "";
        String time = "";
        time = RegPubUtils.getDateToString(System.currentTimeMillis());
        StringBuffer Info_sb = new StringBuffer();
        TelephonyManager mTManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (mTManager != null) {
            Imei = mTManager.getDeviceId();
            PhoneNo = mTManager.getLine1Number();
        }
        Info_sb.append("SoftName:" + getAPPName() + "," + "GuestName:" + Hawk.get(HawkProperty.APP_GUEST_NAME, "") +
                "," + "RegCode:" + Hawk.get(HawkProperty.REG_CODE) + "," + "PhoneNo:" + PhoneNo + "," + "Imei:" + Imei + "," + "Mac:" + macAddress() + "," + "Lat:" + Lat + "," + "Lng:" + Lng + "," + "Addr:" + Addr + "," + "OriginalVersion:" + originalVersion + "," + "NewestVersion:" + newestVersion + "," + "Time:" + time);

        return Info_sb.toString();
    }


    public void setCancelCallBack(RegLatestContact.CancelCallBack callBack) {
        this.cancelCallBack = callBack;
    }
    // 判断网络是否正常

    public static boolean isConnected(Context context) {
        boolean isOk = true;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobNetInfo = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiNetInfo = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiNetInfo != null && !wifiNetInfo.isConnectedOrConnecting()) {
                if (mobNetInfo != null && !mobNetInfo.isConnectedOrConnecting()) {
                    NetworkInfo info = connectivityManager
                            .getActiveNetworkInfo();
                    if (info == null) {
                        isOk = false;
                    }
                }
            }
            mobNetInfo = null;
            wifiNetInfo = null;
            connectivityManager = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOk;
    }

}
