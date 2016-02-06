package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.mobilesafe.Service.AddressService;
import com.example.mobilesafe.ui.SettingItemView;
import com.example.mobilesafe.utils.ServiceUtil;

/**
 * Created by abc on 2016/1/30.
 */
public class SettingActivity extends Activity {
    private Intent showAddress ;

    private SettingItemView siv_show_address;

    private SettingItemView siv_update;

    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sp=getSharedPreferences("config",MODE_PRIVATE);
        siv_update= (SettingItemView) findViewById(R.id.siv_update);
        //判断是否设置自动更新
        if(sp.getBoolean("update",false)){
            siv_update.setChecked(true);
        } else {
            siv_update.setChecked(false);
        }
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                //判断是否选中
                if(siv_update.isChecked()){
                    //已经选中
                    siv_update.setChecked(false);
                    editor.putBoolean("update",false);
                }else {
                    //没选中
                    siv_update.setChecked(true);
                    editor.putBoolean("update",true);
                }
                editor.commit();

            }
        });

        //判断是否设置显示来电归属地
        siv_show_address= (SettingItemView) findViewById(R.id.siv_show_address);
        showAddress = new Intent(this,AddressService.class);
        boolean isRunning =ServiceUtil.isServiceRunning(this,"com.example.mobilesafe.Service.AddressService");
        if(isRunning){
            //监听来电服务正在运行
            siv_show_address.setChecked(true);
        } else{
            //监听来电服务未运行
            siv_show_address.setChecked(false);
        }
        siv_show_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                //判断是否选中
                if(siv_show_address.isChecked()){
                    //已经选中
                    siv_show_address.setChecked(false);
                    stopService(showAddress);
                }else {
                    //没选中
                    siv_show_address.setChecked(true);
                    startService(showAddress);
                }
                editor.commit();

            }
        });
    }
}
