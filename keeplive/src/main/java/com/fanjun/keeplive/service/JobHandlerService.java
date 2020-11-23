//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.fanjun.keeplive.service;

import android.app.Notification;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.app.job.JobInfo.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Build.VERSION;
import android.support.annotation.RequiresApi;
import android.util.Log;
import com.fanjun.keeplive.KeepLive;
import com.fanjun.keeplive.config.NotificationUtils;
import com.fanjun.keeplive.receiver.NotificationClickReceiver;
import com.fanjun.keeplive.utils.ServiceUtils;

@RequiresApi(
        api = 21
)
public final class JobHandlerService extends JobService {
    private JobScheduler mJobScheduler;
    private int jobId = 100;
    private boolean canRecycle = true;//是否可以守护
    private ControlServiceReceiver mControlServiceReceiver;

    public JobHandlerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //主动控制服务的监听  开启服务和关闭服务
        if (mControlServiceReceiver == null) {
            mControlServiceReceiver = new ControlServiceReceiver();
        }
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("ACTION_STOP_ALL_SERVICE");
        intentFilter3.addAction("ACTION_START_ALL_SERVICE");
        registerReceiver(mControlServiceReceiver, intentFilter3);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.startService(this);
        String action = intent.getAction();

        if (VERSION.SDK_INT >= 21) {
            this.mJobScheduler = (JobScheduler) this.getSystemService("jobscheduler");
            this.mJobScheduler.cancel(this.jobId);
            Builder builder = new Builder(this.jobId, new ComponentName(this.getPackageName(), JobHandlerService.class.getName()));
            if (VERSION.SDK_INT >= 24) {
                builder.setMinimumLatency(30000L);
                builder.setOverrideDeadline(30000L);
                builder.setMinimumLatency(30000L);
                builder.setBackoffCriteria(30000L, 0);
            } else {
                builder.setPeriodic(30000L);
            }

            builder.setRequiredNetworkType(1);
            builder.setPersisted(true);
            this.mJobScheduler.schedule(builder.build());
        }

        return START_STICKY;
    }

    private void startService(Context context) {
        Intent localIntent;
        if (VERSION.SDK_INT >= 26 && KeepLive.foregroundNotification != null) {
            localIntent = new Intent(this.getApplicationContext(), NotificationClickReceiver.class);
            localIntent.setAction("CLICK_NOTIFICATION");
            Notification notification = NotificationUtils.createNotification(this, KeepLive.foregroundNotification.getTitle(), KeepLive.foregroundNotification.getDescription(), KeepLive.foregroundNotification.getIconRes(), localIntent);
            this.startForeground(13691, notification);
        }

        localIntent = new Intent(context, LocalService.class);
        Intent guardIntent = new Intent(context, RemoteService.class);
        this.startService(localIntent);
        this.startService(guardIntent);
    }
    /**
     * 控制服务的监听
     */
    private class ControlServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (intent.getAction().equals("ACTION_START_ALL_SERVICE")) {
                //可以开启服务了
                canRecycle = true;
            } else if (intent.getAction().equals("ACTION_STOP_ALL_SERVICE")) {
                Log.d("8888888","JobHandlerService  接收到关闭服务的广播");
                //不用双进程守护了
                canRecycle = false;
            }
        }
    }
    public boolean onStartJob(JobParameters jobParameters) {
        if (canRecycle) {
            if (!ServiceUtils.isServiceRunning(this.getApplicationContext(), "com.fanjun.keeplive.service.LocalService") || !ServiceUtils.isRunningTaskExist(this.getApplicationContext(), this.getPackageName() + ":remote")) {
                this.startService(this);
            }

        }

        return false;
    }

    public boolean onStopJob(JobParameters jobParameters) {
        if (!ServiceUtils.isServiceRunning(this.getApplicationContext(), "com.fanjun.keeplive.service.LocalService") || !ServiceUtils.isRunningTaskExist(this.getApplicationContext(), this.getPackageName() + ":remote")) {
            this.startService(this);
        }

        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mControlServiceReceiver);
    }
}
