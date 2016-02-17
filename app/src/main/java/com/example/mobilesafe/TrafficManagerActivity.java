package com.example.mobilesafe;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;

import java.util.List;

/**
 * Created by abc on 2016/2/17.
 */
public class TrafficManagerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //1.获取包管理器
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> infos =  pm.getInstalledApplications(0);
        //2.遍历手机操作系统，获取所有的uid
        for(ApplicationInfo info :infos){
            int uid = info.uid;
            //proc/uid_stat/(uid) 文件夹中
            long tx = TrafficStats.getUidTxBytes(uid);
            long rx = TrafficStats.getUidRxBytes(uid);
            //方法值返回-1表示的是应用程序没有产生流量 或者操作系统不支持流量统计
        }
        TrafficStats.getMobileRxBytes();//手机3g/2g总下载流量
        TrafficStats.getMobileTxBytes();//手机3g/2g总上传流量

        TrafficStats.getTotalTxBytes();//手机全部网络接口 包括wifi，3g、2g上传的总流量
        TrafficStats.getTotalRxBytes();//手机全部网络接口 包括wifi，3g、2g下载的总流量
        setContentView(R.layout.activity_traffic_manager);

    }
}
