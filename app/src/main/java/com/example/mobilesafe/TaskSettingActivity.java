package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;


import com.example.mobilesafe.Service.AutoCleanService;
import com.example.mobilesafe.ui.SettingItemView;
import com.example.mobilesafe.utils.ServiceUtil;

/**
 * Created by abc on 2016/2/15.
 */
public class TaskSettingActivity extends Activity {
    private SettingItemView show_system;

    private SettingItemView auto_clean;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_setting);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        show_system = (SettingItemView) findViewById(R.id.show_system);
        auto_clean = (SettingItemView) findViewById(R.id.auto_clean);

        if (sp.getBoolean("showSystem", false)) {
            show_system.setChecked(true);
        } else {
            show_system.setChecked(false);
        }
        show_system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();  //sp sharedpreference在父类已经实现
                //判断是否选中
                if (show_system.isChecked()) {
                    //已经选中
                    show_system.setChecked(false);
                    editor.putBoolean("showSystem", false);
                } else {
                    //没选中
                    show_system.setChecked(true);
                    editor.putBoolean("showSystem", true);
                }
                editor.commit();
            }
        });
        Boolean running =ServiceUtil.isServiceRunning(this,"com.example.mobilesafe.Service.AutoCleanService");
        auto_clean.setChecked(running);
        auto_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否选中

                if (auto_clean.isChecked()) {
                    //已经选中
                    stopService();
                    auto_clean.setChecked(false);
                } else {
                    //没选中
                    startService();
                    auto_clean.setChecked(true);
                }

                //锁屏的广播界面是特殊的广播事件，在清单文件配置广播接收者是不会生效的
                //只能在代码里注册才会生效

            }
        });
    }

    public void startService() {
        Intent intent = new Intent(TaskSettingActivity.this, AutoCleanService.class);
        startService(intent);
    }

    public void stopService() {
        Intent intent = new Intent(TaskSettingActivity.this, AutoCleanService.class);
        stopService(intent);
    }

    @Override
    protected void onStart() {
        Boolean running =ServiceUtil.isServiceRunning(this,"com.example.mobilesafe.Service.AutoCleanService");
        auto_clean.setChecked(running);
        super.onStart();
    }
}
