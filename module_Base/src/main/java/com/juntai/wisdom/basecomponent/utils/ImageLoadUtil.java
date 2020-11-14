package com.juntai.wisdom.basecomponent.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.juntai.wisdom.basecomponent.R;

import java.io.File;

/**
 * 图片加载工具
 *
 * @aouther Ma
 * @date 2019/3/5
 */
public class ImageLoadUtil {

    /**
     * 加载本地图片
     *
     * @param context
     * @param recouse
     * @param view
     */
    public static void loadImage(Context context, int recouse, ImageView view) {
        Glide.with(context)
                .load(recouse)
                .into(view);
    }

    /**
     * 加载图片
     */
    public static void loadImage(Context context, Bitmap bitmap, ImageView view) {
        Glide.with(context)
                .load(bitmap)
                .into(view);
    }

    /**
     * @param context
     * @param url
     * @param view
     */
    public static void loadImage(Context context, String url, ImageView view) {
        Glide.with(context)
                .load(url)
                .error(R.drawable.nopicture)
                .into(view);
    }

    public static void loadMapImg(Context context, String url, ImageView view) {
        Glide.with(context)
                .load(url)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.nopicture)
                .into(view);
    }

    /**
     * 内存缓存
     * @param context
     * @param url
     * @param view
     */
    public static void loadMapImgWithCache(Context context, String url, ImageView view) {
        Glide.with(context)
                .load(url)
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.nopicture)
                .into(view);
    }

    /**
     * @param context
     * @param url
     * @param view
     */
    public static void loadImageNoCrash(Context context, String url, ImageView view) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                .into(view);
    }

    /**
     * @param context
     * @param url
     * @param view
     */
    public static void loadImageNoCrash(Context context, String url, ImageView view,int loading,int error) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                .placeholder(loading)
                .dontAnimate()
                .error(error)
                .into(view);
    }

    /**
     * @param context
     * @param url
     * @param placeholder
     * @param view
     */
    public static void loadImage(Context context, String url, int placeholder, ImageView view) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().placeholder(placeholder))
                .into(view);
    }

    /**
     * @param context
     * @param url
     * @param error
     * @param placeholder
     * @param view
     */
    public static void loadImage(Context context, String url, int error, int placeholder, ImageView view) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().error(error).placeholder(placeholder))
                .into(view);
    }

    /**
     * 加载圆形图片
     *
     * @param context
     * @param url
     * @param error
     * @param placeholder
     * @param view
     */
    public static void loadCircularImage(Context context, String url, int error, int placeholder, ImageView view) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().error(error).placeholder(placeholder).circleCrop())
                .into(view);
    }

    public static void loadCentercropImage(Context context, int url, ImageView view) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().optionalCenterCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                .into(view);
    }

    public static void loadCentercropImage(Context context, String url, ImageView view) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().optionalCenterCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                .into(view);
    }

    /**
     * 加载圆形图片,无缓存
     * @param context
     * @param url
     * @param view
     * @param placeholder
     * @param error
     */
    public static void loadCirImgNoCrash(Context context, String url,ImageView view, int placeholder,int error) {
        Glide.with(context)
                .load(url)
                .apply(new RequestOptions().error(error).placeholder(placeholder).circleCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true))
                .into(view);
    }

    /**
     * 图片压缩回调
     */
    public interface ImageCompress {
        /**
         * 压缩成功
         *
         * @param file
         */
        void compressSuccessed(File file);

        void compressFailed(Throwable e);
    }

    public interface BitmapCallBack {
        void getBitmap(Bitmap bitmap);
    }

    /**
     * 获取bitmap
     * @param context
     * @param path
     * @param error
     * @param callback
     */
    public static void getBitmap(Context context, String path, int error, BitmapCallBack callback){
        Glide.with(context).asBitmap().error(error).load(path).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                LogUtil.e("onResourceReady");
                callback.getBitmap(resource);
            }
        });
//        Glide.with(context).asBitmap().error(error).load(path).into(new Target<Bitmap>() {
//
//            @Override
//            public void onStart() {
//                LogUtil.e("onStart");
//            }
//
//            @Override
//            public void onStop() {
//                LogUtil.e("onStop");
//            }
//
//            @Override
//            public void onDestroy() {
//                LogUtil.e("onDestroy");
//            }
//
//            @Override
//            public void onLoadStarted(@Nullable Drawable placeholder) {
//                LogUtil.e("onLoadStarted");
//            }
//
//            @Override
//            public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                LogUtil.e("onLoadFailed");
//            }
//
//            @Override
//            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                LogUtil.e("onResourceReady");
//                callback.getBitmap(resource);
//            }
//
//            @Override
//            public void onLoadCleared(@Nullable Drawable placeholder) {
//                LogUtil.e("onLoadCleared");
//            }
//
//            @Override
//            public void getSize(@NonNull SizeReadyCallback cb) {
//                LogUtil.e("getSize");
//            }
//
//            @Override
//            public void removeCallback(@NonNull SizeReadyCallback cb) {
//                LogUtil.e("removeCallback");
//            }
//
//            @Override
//            public void setRequest(@Nullable Request request) {
//                LogUtil.e("setRequest");
//            }
//
//            @Nullable
//            @Override
//            public Request getRequest() {
//                LogUtil.e("getRequest");
//                LogUtil.e("path-->"+path);
//                return null;
//            }
//        });
    }

}
