package com.example.mobilesafe;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mobilesafe.db.dao.AntivirusDao;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.List;

/**
 * Created by abc on 2016/2/17.
 */
public class AntiVirusActivity extends Activity {
    protected static final int FINISH = 1;
    private ImageView iv_scan;
    protected static final int SCANING = 0;
    private ProgressBar progressBar1;

    private PackageManager pm;

    private TextView tv_scan_stats;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case SCANING:
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    tv_scan_stats.setText("正在扫描：" + scanInfo.name);
                    TextView tv = new TextView(getApplicationContext());
                    if(scanInfo.isVirus){
                        tv.setBackgroundColor(Color.RED);
                        tv.setText("发现病毒："+scanInfo.name);
                    }else {
                        tv.setBackgroundColor(Color.WHITE);
                        tv.setText("扫描安全："+scanInfo.name);
                    }
                    ll_container.addView(tv);
                    break;
                case FINISH:
                    tv_scan_stats.setText("扫描结束");
                    iv_scan.clearAnimation();
                    break;
                default:
                    break;
            }
        }
    };

    private LinearLayout ll_container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);
        iv_scan = (ImageView) findViewById(R.id.iv_scan);
        ll_container= (LinearLayout) findViewById(R.id.ll_container);
        RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF
                , 0.5F);
        ra.setDuration(1000);
        ra.setRepeatCount(Animation.INFINITE);
        iv_scan.startAnimation(ra);

        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        tv_scan_stats= (TextView) findViewById(R.id.tv_scan_stats);
        scannerVirus();
    }

    /**
     * 扫描病毒
     */
    private void scannerVirus() {
        pm = getPackageManager();
        tv_scan_stats.setText("正在初始化杀毒引擎");
        new Thread(){
            public void run(){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<PackageInfo> packageInfos= pm.getInstalledPackages(0);
                progressBar1.setMax(packageInfos.size());
                int progress=0;
                for(PackageInfo info :packageInfos){
                    //得到apk文件完整路径
                    String sourceDir=info.applicationInfo.sourceDir;
                    String md5 = getFileMd5(sourceDir);
                    //查询md5信息，与数据库匹配
                    ScanInfo scanInfo=new ScanInfo();
                    scanInfo.name=info.applicationInfo.loadLabel(pm).toString();
                    scanInfo.packname=info.packageName;
                    if(AntivirusDao.isVirus(md5)){
                        scanInfo.isVirus=true;
                    } else {
                        scanInfo.isVirus=false;
                    }
                    Message msg = Message.obtain();
                    msg.obj=scanInfo;  //指定消息的值
                    msg.what=SCANING;               //指定消息的类型
                    handler.sendMessage(msg);

                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    progress++;
                    progressBar1.setProgress(progress);
                }
                Message msg = Message.obtain();
                msg.what=FINISH;
                handler.sendMessage(msg);
            }
        }.start();

    }

    /**
     * 扫描信息内部类
     */
    class ScanInfo{
        String packname;
        String name;
        boolean isVirus;
    }

    /**
     * 获取文件的md5值
     * @param path 文件的全路径名称
     * @return
     */
    private String getFileMd5(String path){

        try {
            // 获取一个文件的特征信息，签名信息。
            File file = new File(path);
            //MD5
            MessageDigest digest =MessageDigest.getInstance("md5");
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int len = -1;
            while((len=fis.read(bytes))!=-1){
                digest.update(bytes,0,len);
            }
            byte[] result =digest.digest();
            StringBuilder stringBuilder=new StringBuilder();
            //转化16进制
            for (byte b : result) {
                // 与运算
                int number = b & 0xff;// 加盐
                String str = Integer.toHexString(number);
                // System.out.println(str);
                if (str.length() == 1) {
                    stringBuilder.append("0");
                }
                stringBuilder.append(str);
            }
            return stringBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }
}
