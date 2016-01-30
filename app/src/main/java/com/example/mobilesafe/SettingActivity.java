package com.example.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.mobilesafe.com.example.mobilesafe.ui.SettingItemView;

/**
 * Created by abc on 2016/1/30.
 */
public class SettingActivity extends Activity {
    private SettingItemView siv_update;

    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sp=getSharedPreferences("config",MODE_PRIVATE);
        siv_update= (SettingItemView) findViewById(R.id.siv_update);
        if(sp.getBoolean("update",false)){
            siv_update.setChecked(true);
            siv_update.SetDec("自动更新已经开启");
        } else {
            siv_update.setChecked(false);
            siv_update.SetDec("自动更新已经关闭");
        }
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                //判断是否选中
                if(siv_update.isChecked()){
                    //已经选中
                    siv_update.setChecked(false);
                    siv_update.SetDec("自动更新已经关闭");
                    editor.putBoolean("update",false);
                }else {
                    //没选中
                    siv_update.setChecked(true);
                    siv_update.SetDec("自动更新已经开启");
                    editor.putBoolean("update",true);
                }
                editor.commit();

            }
        });
    }
}
