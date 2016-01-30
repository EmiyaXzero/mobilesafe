package com.example.mobilesafe;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.mobilesafe.com.example.mobilesafe.utils.StreamTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
        //检查升级
        checkupdate();
    }

    /**
     * 检查是否有新版本，如果有就升级
     */
    private void checkupdate() {
        //子线程请求网络
        new Thread(new Runnable() {
            @Override
            public void run() {
             //URL
                String path=getString(R.string.serverurl); //请求的地址
                try {
                    URL url=new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(4000);
                    int code = connection.getResponseCode();
                    if(code==200){
                        //联网成功
                        InputStream is= connection.getInputStream();
                        //把流转换成string
                        String response = StreamTool.readFromStream(is).toString();
                        Log.d("Message",response);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
        }).start();

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
