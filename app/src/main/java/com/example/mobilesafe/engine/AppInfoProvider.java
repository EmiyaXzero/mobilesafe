package com.example.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
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
            info.setIcon(icon);
            info.setName(name);
            info.setPackname(packageName);
            infos.add(info);
        }

        return infos;
    }
}
