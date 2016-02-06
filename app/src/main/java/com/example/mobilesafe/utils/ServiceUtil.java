package com.example.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by abc on 2016/2/6.
 */
public class ServiceUtil {
    /**
     * 检验服务是否允许
     */
    public static boolean isServiceRunning(Context context,String serviceName){
        //不光可以管理activity还可以管理service
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos=am.getRunningServices(100);
        for(ActivityManager.RunningServiceInfo info:infos){
            //得到正在运行的服务的名字
            String name = info.service.getClassName();
            if(name.equals(serviceName)){
                return true;
            }
        }

        return false;
    }
}
