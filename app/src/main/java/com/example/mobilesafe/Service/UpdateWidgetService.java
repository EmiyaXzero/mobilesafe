package com.example.mobilesafe.Service;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.mobilesafe.R;
import com.example.mobilesafe.receiver.MyWidget;
import com.example.mobilesafe.utils.SystemInfoUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by abc on 2016/2/16.
 */
public class UpdateWidgetService extends Service {
    private ScreenOffReceiver offreceiver;
    private ScreenOnReceiver onreceiver;
    private Timer timer;

    private TimerTask timerTask;

    private AppWidgetManager awm;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        onreceiver=new ScreenOnReceiver();
        offreceiver=new ScreenOffReceiver();
        registerReceiver(onreceiver,new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(offreceiver,new IntentFilter(Intent.ACTION_SCREEN_OFF));

        awm=AppWidgetManager.getInstance(this);
        startTimer();
        super.onCreate();
    }

    private void startTimer() {
        if(timer==null&&timerTask==null) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    //设置更新组件
                    Log.d("TAG","更新了");
                    ComponentName provider = new ComponentName(UpdateWidgetService.this, MyWidget.class);
                    RemoteViews view = new RemoteViews(getPackageName(), R.layout.process_widget);
                    view.setTextViewText(R.id.process_count, "正在运行的进程:" +
                            SystemInfoUtils.getRunningProcessCount(getApplicationContext()) + "个");
                    view.setTextViewText(R.id.process_memory, "可用内存:" +
                            Formatter.formatFileSize
                                    (getApplicationContext(), SystemInfoUtils.getAvailMemory(getApplicationContext())));
                    //描述一个动作，这个动作是由另外的一个应用程序执行
                    //自定义一个广播事件，杀死后台应用事件
                    Intent intent = new Intent();
                    intent.setAction("com.example.mobilesafe.killall");
                    PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    view.setOnClickPendingIntent(R.id.btn_clear, pi);
                    awm.updateAppWidget(provider, view);
                }
            };
            timer.schedule(timerTask, 0, 3000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onreceiver);
        unregisterReceiver(offreceiver);
        onreceiver=null;
        offreceiver=null;
        stopTimer();
    }

    private void stopTimer() {
        if(timer!=null&&timerTask!=null) {
            timer.cancel();
            timerTask.cancel();
            timer = null;
            timerTask = null;
        }
    }

    private class ScreenOffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG", "锁屏了");
            stopTimer();
        }
    }

    private class ScreenOnReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG", "解锁了了");
            startTimer();
        }
    }
}
