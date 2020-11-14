package com.juntai.wisdom.basecomponent.app;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import com.juntai.wisdom.basecomponent.BuildConfig;
import com.juntai.wisdom.basecomponent.R;
import com.juntai.wisdom.basecomponent.utils.LogUtil;
import com.juntai.wisdom.basecomponent.utils.NavigationBarInfo;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.interfaces.BetaPatchListener;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Locale;

public abstract class BaseApplication extends MultiDexApplication {
    public static int H, width, statusBarH;
    public static int navigationBarH;
    public static BaseApplication app;
    private RefWatcher mRefWatcher;
    ArrayList<Activity> activities = new ArrayList<>();

    /*app处于后台true,前台false*/
    public abstract void appBackground(boolean isBackground, Activity activity);

    /*热更新id*/
    public abstract String getTinkerId();

    //活动的activity数量
    int mActivityCount;
    //应用处于前台运行中
    public boolean isRun = true;
    //
    SoftReference<Activity> softReference;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        getScreen(this);
        if (BuildConfig.DEBUG) {
            //
            Logger.addLogAdapter(new AndroidLogAdapter(PrettyFormatStrategy.newBuilder().
                    tag(getString(R.string.app_name)).build()));
            LogUtil.logInit(true);
            com.juntai.wisdom.basecomponent.utils.Logger.LOG_ENABLE = true;
        }
        initLeakCanary();
        registerActivityLifecycleCallbacks(mCallbacks);
        //
        initBugly();
    }

    /**
     * 初始化bugly
     */
    public void initBugly() {
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        //
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 调试时，将第三个参数改为true
        Bugly.init(this, getTinkerId(), false, strategy);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);
        // 安装tinker
        Beta.installTinker();
        Beta.autoCheckUpgrade = true;//不会自动检查更新，Mianactivity -手动调用
        Beta.canNotifyUserRestart = true;//设置是否显示弹窗提示用户重启
        Beta.betaPatchListener = new BetaPatchListener() {
            @Override
            public void onPatchReceived(String patchFile) {
                Log.e("tinkerListener", "补丁下载地址" + patchFile);
            }

            @Override
            public void onDownloadReceived(long savedLength, long totalLength) {
                Log.e("tinkerListener", String.format(Locale.getDefault(), "%s %d%%",
                        Beta.strNotificationDownloading,
                        (int) (totalLength == 0 ? 0 : savedLength * 100 / totalLength)));
            }

            @Override
            public void onDownloadSuccess(String msg) {
                Log.e("tinkerListener", "补丁下载成功");
            }

            @Override
            public void onDownloadFailure(String msg) {
                Log.e("tinkerListener", "补丁下载失败");

            }

            @Override
            public void onApplySuccess(String msg) {
                Log.e("tinkerListener", "补丁应用成功");
            }

            @Override
            public void onApplyFailure(String msg) {
                Log.e("tinkerListener", "补丁应用失败");
            }

            @Override
            public void onPatchRollback() {
                Log.e("tinkerListener", "补丁回滚");
            }
        };
    }

    /**
     * 记录当前正在活动的activity
     *
     * @return
     */
    public Activity getNowActivity() {
        return softReference.get();
    }

    public boolean isApkInDebug(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void getScreen(Context aty) {
        //依赖于手机系统，获取到的是系统的屏幕信息；
        DisplayMetrics dm = aty.getResources().getDisplayMetrics();
        H = dm.heightPixels;
        width = dm.widthPixels;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarH = getResources().getDimensionPixelSize(resourceId);
        }

        if (NavigationBarInfo.hasNavBar(aty)){
            int navigationId = getResources().getIdentifier("navigation_bar_height","dimen","android");
            if (navigationId > 0){
                navigationBarH = getResources().getDimensionPixelSize(navigationId);
            }
        }
    }

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        mRefWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        return app.mRefWatcher;
    }

    /**
     * 下拉刷新
     * static 代码段可以防止内存泄露
     */
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(android.R.color.white, R.color.black);//全局设置主题颜色
                return new ClassicsHeader(context)
                        .setTextSizeTitle(13)
                        .setTextSizeTime(10)
                        .setDrawableArrowSize(15);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(15).setTextSizeTitle(13);
            }
        });
    }

    private ActivityLifecycleCallbacks mCallbacks = new ActivityLifecycleCallbacks() {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            //LogUtil.d("Activity-onActivityCreated = " + activity.getClass().getName());
            activities.add(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            mActivityCount++;
            if (mActivityCount == 1) {
                appBackground(false, activity);
                isRun = true;
            }
            softReference = null;
            softReference = new SoftReference<>(activity);
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            mActivityCount--;
            if (mActivityCount == 0) {
                appBackground(true, activity);
                isRun = false;
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            //LogUtil.d("Activity-onActivityDestroyed = " + activity.getClass().getName());
            activities.remove(activity);
        }
    };

    /**
     * 清理当前的所有activity
     */
    public void clearActivitys() {
        for (Activity a : activities) {
            //LogUtil.d("Activity-clearActivitys = " + a.getClass().getName());
            a.finish();
        }
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
