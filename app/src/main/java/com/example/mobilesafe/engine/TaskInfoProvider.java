package com.example.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.example.mobilesafe.R;
import com.example.mobilesafe.domain.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc on 2016/2/15.
 */
public class TaskInfoProvider {
    /**
     * 获取所有进程的信息
     * @param context
     * @return
     */
    public static List<TaskInfo> getTaskInfos(Context context){
        List<TaskInfo> infos = new ArrayList<TaskInfo>();
        PackageManager pm = context.getPackageManager();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runInfos= am.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo info:runInfos){
            TaskInfo taskInfo = new TaskInfo();
            //进程的包名
            String packName =info.processName;
            Debug.MemoryInfo[] memoryInfo =am.getProcessMemoryInfo(new int[]{info.pid});
            long memSize=memoryInfo[0].getTotalPrivateDirty()*1024;
            taskInfo.setMemsize(memSize);
            try {
                ApplicationInfo applicationInfo= pm.getApplicationInfo(packName, 0);
                Drawable icon =applicationInfo.loadIcon(pm);
                String name = applicationInfo.loadLabel(pm).toString();
                taskInfo.setIcon(icon);
                taskInfo.setName(name);
                if((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==0){
                    //用户进程
                    taskInfo.setUserTask(true);
                }else {
                    //系统进程
                    taskInfo.setUserTask(false);
                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                taskInfo.setIcon(context.getResources().getDrawable(R.mipmap.ic_launcher));
                taskInfo.setName(packName);
            }
           infos.add(taskInfo);
        }
        return infos;
    }
}
