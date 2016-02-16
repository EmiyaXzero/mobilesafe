package com.example.mobilesafe.Service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

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

    private String tempStopProtectPackname;

    private InnerReceiver receiver;

    private ScreenoffReceiver screenoffReceiver;

    private dataChangedReceiver dataChangedReceiver;

    private List<String> protectPacknames;

    private Intent intent;
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
        protectPacknames=dao.findAll();
        receiver=new InnerReceiver();
        registerReceiver(receiver,new IntentFilter("com.example.mobilesafe.tempstop"));
        screenoffReceiver=new ScreenoffReceiver();
        registerReceiver(screenoffReceiver,new IntentFilter(Intent.ACTION_SCREEN_OFF));

        dataChangedReceiver = new dataChangedReceiver();
        registerReceiver(dataChangedReceiver,new IntentFilter("com.example.mobilesafe.applockchange"));
        /**
         * 意图可以先初始化，提高效率
         */
        intent = new Intent(getApplicationContext(), EnterPwdActivity.class);
        //服务是没有栈信息的，在服务开启的activity的时候，要指定这个activity运行的任务栈
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        new Thread(){
            public void run(){
                while(flag) {
                    List<ActivityManager.RunningTaskInfo> infos =am.getRunningTasks(1);
                    String packName = infos.get(0).topActivity.getPackageName();
                    //if(dao.fin(packName))
                    if(protectPacknames.contains(packName)){
                        //查询数据库太慢 ,通过查询内存提高效率
                        if(packName.equals(tempStopProtectPackname)){

                        }else {
                            //当前应用需要保护。
                            //传递包名
                            intent.putExtra("packname", packName);
                            startActivity(intent);
                        }
                    }
                    try{
                        Thread.sleep(20);
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
        unregisterReceiver(receiver);
        unregisterReceiver(screenoffReceiver);
        unregisterReceiver(dataChangedReceiver);
        screenoffReceiver=null;
        receiver=null;
        dataChangedReceiver=null;
        super.onDestroy();
    }




    private class  InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            tempStopProtectPackname=intent.getStringExtra("packname");
        }
    }

    private class ScreenoffReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            tempStopProtectPackname=null;
        }
    }
    private class dataChangedReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            protectPacknames=dao.findAll();
        }
    }
}

