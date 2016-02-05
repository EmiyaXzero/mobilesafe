package com.example.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by abc on 2016/2/5.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    TelephonyManager tm;
    SharedPreferences sp;

    @Override
    public void onReceive(Context context, Intent intent) {
        //读取之前保存的sim卡信息
        sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);
        String savedSim=sp.getString("sim",null);
        //读取当前的sim卡信息
        tm= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(tm.getSimSerialNumber().equals(savedSim+"abc")){
            //sim卡没有变
        }else {
            //变更了
            Log.d("changed","Sim卡已经变更");
        }
    }
}
