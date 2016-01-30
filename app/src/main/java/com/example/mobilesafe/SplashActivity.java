package com.example.mobilesafe;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Splash界面展示产品logo 应用初始化  检查应用版本  检查当前应用程序是否合法
 */

public class SplashActivity extends Activity {
    private TextView tv_splash_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        tv_splash_version.setText("版本号:"+getVersionName());
    }
    /**
     *得到应用程序的版本名
     */
    private String getVersionName(){
        //用来管理手机apk
        PackageManager pm= getPackageManager();
        //得到指定apk功能清单文件
        try {
            //得到知道APK的功能清单文件
          PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
           return   packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }

    }

}
