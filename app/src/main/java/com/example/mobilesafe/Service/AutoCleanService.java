package com.example.mobilesafe.Service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

/**
 * Created by abc on 2016/2/15.
 */
public class AutoCleanService extends Service {
    private ScreenOffReceiver receiver;

    private ActivityManager am;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        am= (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        receiver = new ScreenOffReceiver();
        registerReceiver(receiver,new IntentFilter(Intent.ACTION_SCREEN_OFF));
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        receiver=null;
        super.onDestroy();
    }

    private class ScreenOffReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TGA","锁屏了");
            List<ActivityManager.RunningAppProcessInfo>runningAppProcessInfos= am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo info:runningAppProcessInfos){
                am.killBackgroundProcesses(info.processName);
            }
        }
    }
}
