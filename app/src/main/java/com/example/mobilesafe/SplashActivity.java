package com.example.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.utils.StreamTool;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Splash界面展示产品logo 应用初始化  检查应用版本  检查当前应用程序是否合法
 */

public class SplashActivity extends Activity {
    private static final int ENTER_HOME = 1;
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
    private String version;
    private TextView tv_update_info;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        tv_splash_version.setText("版本号:" + getVersionName());
        tv_update_info = (TextView) findViewById(R.id.tv_update_info);
        //拷贝数据库到data/data/
        copyDB("address.db");
        copyDB("antivirus.db");
        installShortCut();
        if (sp.getBoolean("update", false)) {
            //检查升级
            checkupdate();
        } else {
            //自动升级关闭
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //进入主界面
                    enterHome();
                }
            }, 2000);
        }
        //增加动画
        AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
        //设置动画时间
        aa.setDuration(500);
        findViewById(R.id.rl_root_splash).startAnimation(aa);

    }

    /**
     * 创建快捷图标
     */
    private void installShortCut() {
        SharedPreferences.Editor editor =sp.edit();
        if(sp.getBoolean("shortcut",false)){
            return ;
        }
        //发送广播的意图
        Intent intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //快捷方式 要包含三个重要的信息,名称，图标，干什么事情
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,"手机小卫士");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        //快捷方式的意图
        Intent shortCutIntent=new Intent();
        shortCutIntent.setAction("android.intent.action.MAIN");
        shortCutIntent.addCategory("android.intent.category.LAUNCHER");
        shortCutIntent.setClassName(getPackageName(), "com.example.mobilesafe.SplashActivity");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
        sendBroadcast(intent);
        editor.putBoolean("shortcut",true);
        editor.commit();
    }

    /**
     * 把address.db拷贝到data/data目录下
     */
    private void copyDB(String filename) {
        //拷贝了一次，之后就不再拷贝
        try {
            File file = new File(getFilesDir(), filename);//getFilesDir()获得file目录
            if (file.exists() && file.length() > 0) {
                //已经有不在拷贝
                Log.d("aaaaaaa", "已经拷贝了不用拷贝");
                return;
            } else {
                Log.d("aaaaa", "正在拷贝");
                InputStream is = getAssets().open(filename);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buff = new byte[1024];
                int len = 0;
                while ((len = is.read(buff)) != -1) {
                    fos.write(buff, 0, len);
                }
                is.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_UPDATE_DIALOG:
                    //显示升级对话框
                    showUpdateDialog();
                    break;
                case ENTER_HOME:
                    //进入主界面
                    enterHome();
                    break;
                case URL_ERROR:
                    //URL错误
                    enterHome();
                    Toast.makeText(getApplicationContext(), "URL错误", Toast.LENGTH_SHORT).show();
                    break;
                case NETWORK_ERROR:
                    //网络错误
                    enterHome();
                    Toast.makeText(getApplicationContext(), "网络错误", Toast.LENGTH_SHORT).show();
                    break;
                case JSON_ERROR:
                    //JSON解析错误
                    enterHome();
                    Toast.makeText(SplashActivity.this, "JSON异常", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;

            }

        }
    };

    //弹出升级对话框
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示升级");
        //builder.setCancelable(false);  //无法取消
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //进入主页面
                enterHome();
                dialog.dismiss();
            }
        });
        builder.setMessage(description);
        builder.setNegativeButton("立刻升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载apk
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    //SD卡存在
                    //afnal开源框架
                    FinalHttp finalhttp = new FinalHttp();
                    finalhttp.download(apkurl, Environment.getExternalStorageDirectory().getAbsolutePath() + "/mobilesafe" + version + ".apk", new AjaxCallBack<File>() {
                        @Override
                        public void onLoading(long count, long current) {
                            super.onLoading(count, current);
                            tv_update_info.setVisibility(View.VISIBLE);
                            //启动页面新加进度展示
                            int progress = (int) (current * 100 / count);// 当前下载百分比   count总大小
                            tv_update_info.setText("下载进度" + progress + "%");
                        }

                        @Override
                        public void onSuccess(File file) {
                            super.onSuccess(file);
                            //下载完成后安装
                            installAPK(file);
                        }

                        /**
                         * 安装apk
                         * @param file 文件
                         */
                        private void installAPK(File file) {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");

                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            t.printStackTrace();
                            Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_LONG).show();
                            super.onFailure(t, errorNo, strMsg);
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "缺少SD卡", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
        builder.setPositiveButton("稍后升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                enterHome();
            }
        });
        //还需要show
        builder.show();
    }

    //跳转主界面
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
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
                String path = getString(R.string.serverurl); //请求的地址
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    URL url = new URL(path);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(4000);
                    int code = connection.getResponseCode();
                    if (code == 200) {
                        //联网成功
                        InputStream is = connection.getInputStream();
                        //把流转换成string
                        String response = StreamTool.readFromStream(is).toString();
                        //json 解析
                        JSONObject jsonObject = new JSONObject(response);
                        //得到服务器的信息
                        version = jsonObject.getString("version");
                        description = jsonObject.getString("description");
                        apkurl = jsonObject.getString("apkurl");
                        //校验是否有新版本
                        if (getVersionName().equals(version)) {
                            //相同 ,版本一致  进入主页面
                            msg.what = ENTER_HOME;

                        } else {
                            //有新版本，弹出升级对话框
                            msg.what = SHOW_UPDATE_DIALOG;
                        }
                    }
                } catch (MalformedURLException e) {
                    msg.what = URL_ERROR;
                    e.printStackTrace();

                } catch (IOException e) {
                    msg.what = NETWORK_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    msg.what = JSON_ERROR;
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    //花了多久时间
                    long dTime = endTime - startTime;
                    //2000
                    if (dTime < 2000) {
                        try {
                            Thread.sleep(2000 - dTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    handler.sendMessage(msg);
                }

            }

            ;
        }).start();

    }


    /**
     * 得到应用程序的版本名
     */
    private String getVersionName() {
        //用来管理手机apk
        PackageManager pm = getPackageManager();
        //得到指定apk功能清单文件
        try {
            //得到知道APK的功能清单文件
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }

    }

}
