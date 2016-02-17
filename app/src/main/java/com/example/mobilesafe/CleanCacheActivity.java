package com.example.mobilesafe;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


/**
 * Created by abc on 2016/2/17.
 */
public class CleanCacheActivity extends Activity {
    private ProgressBar pb;
    private TextView tv_scan_status;
    private PackageManager pm;
    private LinearLayout ll_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);
        pb = (ProgressBar) findViewById(R.id.pb);
        tv_scan_status = (TextView) findViewById(R.id.tv_scan_stats);
        ll_container= (LinearLayout) findViewById(R.id.ll_container);
        scanCache();
    }

    /**
     * 扫描应用程序所有的缓存信息
     */
    private void scanCache() {
        pm = getPackageManager();
        new Thread() {
            public void run() {
                Method getPackageSizeInfoMethod = null;
                Method[] methods = PackageManager.class.getMethods();
                for (Method method : methods) {
                    if ("getPackageSizeInfo".equals(method.getName())) {
                        getPackageSizeInfoMethod = method;
                    }
                }
                List<PackageInfo> packInfos = pm.getInstalledPackages(0);
                pb.setMax(packInfos.size());
                int progress=0;
                Method myUserId = null;
                int userID = 0;
                try {
                    myUserId = UserHandle.class.getDeclaredMethod("myUserId");
                    userID = (Integer) myUserId.invoke(pm, new Object[]{});
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (PackageInfo info : packInfos) {
                    try {
                        getPackageSizeInfoMethod.invoke(pm, info.packageName, userID, new MyDataObserver());
                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progress++;
                    pb.setProgress(progress);
                }

                Log.d("aaa","aaa");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("aaa","扫描完了");
                        tv_scan_status.setText("扫描完毕....");
                    }
                });
            }
        }.start();

    }

    private class MyDataObserver extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) {
            final long cache = pStats.cacheSize;
            final String packName = pStats.packageName;
            final ApplicationInfo applicationInfo;
            try {
                applicationInfo = pm.getApplicationInfo(packName, 0);


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_scan_status.setText("正在扫描:" +applicationInfo.loadLabel(pm));
                            if (cache > 0) {
                                View view = View.inflate(getApplicationContext(),R.layout.list_item_cacheinfo,null);
                                TextView tv_cache= (TextView) view.findViewById(R.id.tv_cache_size);
                                TextView tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
                                ImageView iv_delete = (ImageView)view.findViewById(R.id.iv_delete);
                                iv_delete.setOnClickListener(new View.OnClickListener() {
                                                                 @Override
                                                                 public void onClick(View v) {
                                                                     try {
                                                                         Method method = PackageManager.class.getMethod("deleteApplicationCacheFiles",String.class, IPackageDataObserver.class);
                                                                         method.invoke(pm,packName,new MyPackDataObserver());
                                                                     } catch (Exception e) {
                                                                         e.printStackTrace();
                                                                     }
                                                                 }
                                                             }
                                    );
                                    tv_app_name.setText(applicationInfo.loadLabel(pm));
                                    tv_cache.setText("缓存大小："+Formatter.formatShortFileSize(

                                    getApplicationContext(),cache

                                    ));
                                    ll_container.addView(view);
                                }
                            }
                    });

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private class MyPackDataObserver extends IPackageDataObserver.Stub {


        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

        }
    }

    /**
     * 清理手机的全部缓存.
     * @param view
     */
    public void clearAll(View view){
        Method[] methods = PackageManager.class.getMethods();
        for(Method method:methods){
            if("freeStorageAndNotify".equals(method.getName())){
                try {
                    method.invoke(pm, Integer.MAX_VALUE,new MyPackDataObserver());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }
}


