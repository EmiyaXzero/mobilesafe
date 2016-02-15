package com.example.mobilesafe.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.List;

/**
 * Created by abc on 2016/2/15.
 * 系统信息工具类
 */
public class SystemInfoUtils {
    /**
     * 获取正在运行的进程数量
     * @param context
     * @return
     */
   public static int getRunningProcessCount(Context context){
       //包管理器相当于程序管理器 管理的是静态内容
       //PackageManager pm = context.getPackageManager();
       //相当于任务管理器
       ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
       List<ActivityManager.RunningAppProcessInfo> infos= manager.getRunningAppProcesses();
       return infos.size();
   }

    /**
     * 获取可用内存
     * @return
     */
    public static long getAvailMemory(Context context) {
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        manager.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }
    /**
     * 获取手机总内存
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static long getAllMemory(Context context) {
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        manager.getMemoryInfo(outInfo);
        return outInfo.totalMem;
    }
}
