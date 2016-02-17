package com.example.mobilesafe.engine;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.example.mobilesafe.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc on 2016/2/14.
 * 业务方法提供手机里面的所有的应用程序信息
 */
public class AppInfoProvider {
    /**
     * 获取所有的应用信息
     * @return
     */
    public static  List<AppInfo>getAppInfos(Context context){
        List<AppInfo> infos = new ArrayList<AppInfo>();
        PackageManager pm =context.getPackageManager();
        //所有安装在系统上的应用程序信息
        List<PackageInfo> packageInfos =pm.getInstalledPackages(0);
        for(PackageInfo p :packageInfos){
            // P 相当于应用程序的清单文件
            AppInfo info = new AppInfo();
            String packageName = p.packageName;
            String name=p.applicationInfo.loadLabel(pm).toString();
            Drawable icon=p.applicationInfo.loadIcon(pm);
            int flags=p.applicationInfo.flags;
            if((flags&p.applicationInfo.FLAG_SYSTEM)==0){
                //用户程序
                info.setUserApp(true);
            }else {
                //系统程序
                info.setUserApp(false);
            }
            if((flags&p.applicationInfo.FLAG_EXTERNAL_STORAGE)==0){
                //手机内存里
                info.setInRom(true);
            }else {
                //SD卡里
                info.setInRom(false);
            }
            /**
             *操作系统分配给应用程序的一个固定编号，一旦程序安装上来就固定不变
             */
            info.setUid(p.applicationInfo.uid);
            info.setIcon(icon);
            info.setName(name);
            info.setPackname(packageName);
            infos.add(info);
        }

        return infos;
    }
}
