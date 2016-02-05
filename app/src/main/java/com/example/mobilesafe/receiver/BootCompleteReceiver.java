package com.example.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
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

        sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);
        if(sp.getBoolean("protecting", false)) {
            //开启防盗保护才行
            //读取之前保存的sim卡信息
            String savedSim = sp.getString("sim", null);
            //读取当前的sim卡信息
            tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm.getSimSerialNumber().equals(savedSim+"abc")) {
                //sim卡没有变
            } else {
                //变更了
                String phone = sp.getString("phone",null);
                SmsManager.getDefault().sendTextMessage(phone,null,"SIM已经变更",null,null);

            }
        }
    }
}
