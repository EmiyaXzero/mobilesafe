package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.com.example.mobilesafe.utils.StreamTool;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;



/**
 * Splash界面展示产品logo 应用初始化  检查应用版本  检查当前应用程序是否合法
 */

public class SplashActivity extends Activity {
    private static final int ENTER_HOME=1;
    private static final int SHOW_UPDATE_DIALOG = 0;
    private static final int URL_ERROR = 2;
    private static final int NETWORK_ERROR = 3;
    private static final int JSON_ERROR = 4;
    private TextView tv_splash_version;
    private String description;
    /**
     * 新版本下载地址
     */
    private String apkurl;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        tv_splash_version.setText("版本号:" + getVersionName());
        //检查升级
        checkupdate();
        //增加动画
        AlphaAnimation aa =new AlphaAnimation(0.2f,1.0f);
        //设置动画时间
        aa.setDuration(500);
        findViewById(R.id.rl_root_splash).startAnimation(aa);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_UPDATE_DIALOG:
                    //显示升级对话框
                    Log.d("update","升级啊升级撒");
                    break;
                case ENTER_HOME:
                    //进入主界面
                    enterHome();
                    break;
                case URL_ERROR:
                    //URL错误
                    enterHome();
                    Toast.makeText(getApplicationContext(),"URL错误",Toast.LENGTH_SHORT).show();
                    break;
                case NETWORK_ERROR:
                    //网络错误
                    enterHome();
                    Toast.makeText(getApplicationContext(),"网络错误",Toast.LENGTH_SHORT).show();
                    break;
                case JSON_ERROR:
                    //JSON解析错误
                    enterHome();
                    Toast.makeText(SplashActivity.this,"JSON异常",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;

            }

        }
    };

    private void enterHome() {
        Intent  intent =new Intent(this,HomeActivity.class);
        startActivity(intent);
        //关闭当前页面
        finish();
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
                Message msg= Message.obtain();
                long startTime =System.currentTimeMillis();
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
                        //json 解析
                        JSONObject jsonObject = new JSONObject(response);
                        //得到服务器的信息
                        String version = jsonObject.getString("version");
                         description = jsonObject.getString("description");
                         apkurl = jsonObject.getString("apkurl");
                      //校验是否有新版本
                        if(getVersionName().equals(version)){
                            //相同 ,版本一致  进入主页面
                            msg.what=ENTER_HOME;

                        }else{
                            //有新版本，弹出升级对话框
                            msg.what=SHOW_UPDATE_DIALOG;
                        }
                    }
                } catch (MalformedURLException e) {
                    msg.what=URL_ERROR;
                    e.printStackTrace();

                } catch (IOException e) {
                    msg.what=NETWORK_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    msg.what=JSON_ERROR;
                    e.printStackTrace();
                }finally {
                    long endTime = System.currentTimeMillis();
                    //花了多久时间
                    long dTime = endTime-startTime;
                    //2000
                    if(dTime<2000){
                        try {
                            Thread.sleep(2000-dTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendMessage(msg);
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
