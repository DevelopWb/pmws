package com.juntai.wisdom.basecomponent.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.juntai.wisdom.basecomponent.mvp.BasePresenter;
import com.juntai.wisdom.basecomponent.net.FileRetrofit;
import com.juntai.wisdom.basecomponent.utils.FileCacheUtils;
import com.juntai.wisdom.basecomponent.utils.LogUtil;
import com.juntai.wisdom.basecomponent.utils.PubUtil;
import com.juntai.wisdom.basecomponent.utils.ToastUtils;
import com.juntai.wisdom.basecomponent.widght.BaseBottomDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * @Author: tobato
 * @Description: 作用描述
 * @CreateDate: 2020/4/16 16:38
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/4/16 16:38
 */
public abstract class BaseDownLoadActivity<P extends BasePresenter> extends BaseMvpActivity<P> {
    private BaseBottomDialog baseBottomDialog;
    private String notice = "图片";
    private BaseBottomDialog.OnItemClick onItemClick;

    private OnFileDownloaded  fileDownLoadCallBack;
    private String  downLoadUrl = null;//下载路径

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String rightName = getTitleRightName();
        if (!TextUtils.isEmpty(rightName)) {
            if (rightName.contains("视频")) {
                getTitleRightTv().setText(getTitleRightName());
                getTitleRightTv().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        initBottomDialog(Arrays.asList(getTitleRightName()), getDownLoadPath());
                    }
                });
            }
        }
    }

    /**
     * 设置回调
     * @param fileDownLoadCallBack
     */
    public void  setFileDownLoadCallBack(OnFileDownloaded  fileDownLoadCallBack){

       this.fileDownLoadCallBack = fileDownLoadCallBack;
    }
    /**
     * 获取标题栏右侧的内容  图片的时候可以传空
     *
     * @return
     */
    protected abstract String getTitleRightName();

    /**
     * 获取下载路径
     *
     * @return
     */
    protected abstract String getDownLoadPath();

    /**
     * 获取保存路径
     *
     * @return
     */
    private String getSavePath(String downloadPath) {
        String path = null;
        if (!TextUtils.isEmpty(downloadPath)) {
            if (downloadPath.contains(".mp4")) {
                notice = "视频";
                path = FileCacheUtils.getAppVideoPath() + downloadPath.substring(downloadPath.lastIndexOf("/") + 1,
                        downloadPath.length());
            } else {
                notice = "图片";
                if (downloadPath.contains(".jpeg") || downloadPath.contains(".jpg") || downloadPath.contains(".png") || downloadPath.contains(".svg")) {
                    path = FileCacheUtils.getAppImagePath() + downloadPath.substring(downloadPath.lastIndexOf("/") + 1, downloadPath.length());
                } else {
                    //巡检图片  直接从crm读取的"xunjiantubiao" +
                    path = FileCacheUtils.getAppImagePath() + downloadPath.substring(downloadPath.lastIndexOf("/") + 1, downloadPath.length()) + ".jpeg";

                }
            }
        }
        return path;
    }

    /**
     * 初始化dialog
     */
    public void initBottomDialog(List<String> arrays, final String downLoadPath) {
        downLoadUrl = downLoadPath;
        if (baseBottomDialog == null) {
            onItemClick = new BaseBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    downloadFileContent();
                    releaseDialog();
                }
            };
            baseBottomDialog = new BaseBottomDialog();
            baseBottomDialog.setOnBottomDialogCallBack(onItemClick);
        }
        baseBottomDialog.setData(arrays);
        baseBottomDialog.show(getSupportFragmentManager(), "arrays");

    }

    /**
     * 下载文件
     */
    public void downloadFileContent() {
        String savePath = getSavePath(downLoadUrl);
        final File file = new File(savePath);
        ToastUtils.toast(mContext, "已保存");
//        if (file.exists()) {
////            String msg = String.format("%s%s%s", notice, "已保存至", savePath);
//            ToastUtils.toast(mContext, "图片（或视频）已保存");
//            return;
//        }
        downFileLogic(downLoadUrl, file);
    }

    /**
     * 下载文件
     */
    public void downloadFileContent(String downloadPath) {
        final File file = new File(downloadPath);
        ToastUtils.toast(mContext, "已保存");
        downFileLogic(downLoadUrl, file);
    }

    /**
     * 下载文件
     * 有保存目录
     * 截图
     */
    public String downloadFileContent(String dirPath, String downloadPath) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");
        String savePath = getSavePath(dirPath, downloadPath) + sdf.format(new Date()) + ".jpg";
        final File file = new File(savePath);
        downFileLogic(downloadPath, file);
        return savePath;
    }

    /**
     * 下载文件
     * 有保存目录
     * 存放缩略图
     */
    public String downloadFileContentUnique(String dirPath, String downloadPath) {
        String savePath = getSavePath(dirPath, downloadPath) + ".jpg";
        final File file = new File(savePath);
        downFileLogic(downloadPath, file);
        return savePath;
    }

    /**
     * 获取保存路径
     * @param dirPath
     * @param downloadPath
     * @return
     */
    private String getSavePath(String dirPath, String downloadPath) {
        String savePath;
        String dir = FileCacheUtils.getAppImagePath(dirPath);
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        if (downloadPath.contains(".jpeg") || downloadPath.contains(".jpg") || downloadPath.contains(".png") || downloadPath.contains(".svg")) {
            savePath =dir + File.separator + downloadPath.substring(downloadPath.lastIndexOf(
                            "/") + 1, downloadPath.lastIndexOf("."));
        } else {
            //巡检图片  直接从crm读取的"xunjiantubiao" +
            savePath =dir + File.separator + downloadPath.substring(downloadPath.lastIndexOf("/") + 1,
                            downloadPath.length());

        }
        return savePath;
    }

    private void downFileLogic(String downloadPath, File file) {
        FileRetrofit.getInstance().getFileService()
                .getFile_GET(downloadPath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        try {
                            //responseBody里的数据只可以读取一次
                            //Log.e("ffffffff", "" + responseBody.bytes().length);
                            saveFileToLocal(file, responseBody.byteStream());
                            //                            playVideo();
                            //                            startActivity(new Intent(mContext, VideoPlayerActivity
                            //                            .class).putExtra("path",savePath));
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtil.e(e.toString());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.e(throwable.toString());
                    }
                });
    }

    /**
     * 缓存文件到本地
     *
     * @param ins
     */
    public void saveFileToLocal(File file, InputStream ins) {
        try {
            LogUtil.d("----->in");
            if (!file.exists()) {
                file.createNewFile();
            }
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
//            if (!file.getAbsolutePath().contains(FileCacheUtils.STREAM_THUMBNAIL)) {
////                String msg = String.format("%s%s%s", notice, "已下载至", file.getAbsolutePath());
//                ToastUtils.toast(mContext, "已保存");
//            }
            PubUtil.sendBroadcastToAlbum(mContext,file.getAbsolutePath());
            if (fileDownLoadCallBack != null) {
                fileDownLoadCallBack.onFileDownloaded(file.getAbsolutePath());
            }
            LogUtil.d("----->ok");
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.d("----->error-" + e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseDialog();
    }

    /**
     * 释放dialog
     */
    private void releaseDialog() {
        if (baseBottomDialog != null) {
            if (baseBottomDialog.isAdded()) {
                onItemClick = null;
                if (baseBottomDialog.getDialog().isShowing()){
                    baseBottomDialog.dismiss();
                }
            }
        }
    }

    /**
     * 文件下载成功的回调
     */
    public interface  OnFileDownloaded{

        void  onFileDownloaded(String fileName);

    }

}
