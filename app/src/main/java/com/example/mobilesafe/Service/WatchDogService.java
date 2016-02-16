package com.example.mobilesafe.Service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.mobilesafe.EnterPwdActivity;
import com.example.mobilesafe.db.dao.ApplockDao;

import java.util.List;

/**
 * Created by abc on 2016/2/16.
 */
public class WatchDogService extends Service {
    private ActivityManager am;
    private boolean flag;
    private ApplockDao dao;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        flag=true;
        am= (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        dao=new ApplockDao(this);
        new Thread(){
            public void run(){
                while(flag) {
                    List<ActivityManager.RunningTaskInfo> infos =am.getRunningTasks(100);
                    String packName = infos.get(0).topActivity.getPackageName();
                    Log.d("TGA",packName);
                    if(dao.find(packName)){
                        //当前应用需要保护。
                        Intent intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
                        //服务是没有栈信息的，在服务开启的activity的时候，要指定这个activity运行的任务栈
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    try{
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.start();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        flag=false;
        super.onDestroy();
    }
}

