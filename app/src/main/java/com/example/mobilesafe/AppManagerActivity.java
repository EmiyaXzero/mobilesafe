package com.example.mobilesafe;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.domain.AppInfo;
import com.example.mobilesafe.engine.AppInfoProvider;
import com.example.mobilesafe.utils.DensityUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc on 2016/2/14.
 */
public class AppManagerActivity extends Activity implements View.OnClickListener {
    private TextView tv_avail_rom;
    private TextView tv_avail_sd;
    private ListView lv_app_manager;
    private LinearLayout ll_loading;
    private AppAdapter adapter;
    private List<AppInfo> infos;
    /**
     * 用户软件集合
     */
    private List<AppInfo> userAppinfos;
    /**
     * 系统软件集合
     */
    private List<AppInfo> systemAppinfos;

    /**
     * 当前程序信息状态
     */
    private TextView tv_status;

    /**
     * 弹出的悬浮窗体
     */
    private PopupWindow popupWindow;
    //卸载
    private LinearLayout ll_uninstall;
    //开启
    private LinearLayout ll_start;
    //分享
    private LinearLayout ll_share;

    private AppInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_avail_rom = (TextView) findViewById(R.id.tv_avail_rom);
        tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
        lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        long sdSize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
        long romSize = getAvailMemory();//获取内存
        tv_avail_sd.setText("SD卡可用空间:" + Formatter.formatFileSize(this, sdSize)); //formatFileSize()将内存空间转换格式
        tv_avail_rom.setText("内存可用空间:" + Formatter.formatFileSize(this, romSize));
        //避免主进程阻塞 infos= AppInfoProvider.getAppInfos(this);
        ll_loading.setVisibility(View.VISIBLE);
        fillData();
        lv_app_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            /**
             *
             * @param view
             * @param firstVisibleItem 第一个可见条目在listView集合的位置
             * @param visibleItemCount
             * @param totalItemCount
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                dismissPopupWindow();


                //进行判断防止子线程还没加载完数据
                if (userAppinfos != null && systemAppinfos != null) {
                    if (firstVisibleItem > userAppinfos.size()) {
                        tv_status.setText("系统程序:" + systemAppinfos.size() + "个");
                    } else {
                        tv_status.setText("用户程序" + userAppinfos.size() + "个");
                    }
                }
            }
        });

        lv_app_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    return;
                } else if (position == userAppinfos.size() + 1) {
                    return;
                } else if (position <= userAppinfos.size()) {
                    //用户程序
                    int newPosition = position - 1;
                    info = userAppinfos.get(newPosition);
                } else {
                    //系统程序
                    int newPosition = position - 2 - userAppinfos.size();
                    info = systemAppinfos.get(newPosition);
                }
                dismissPopupWindow();

                View contentView = View.inflate(getApplicationContext(), R.layout.popup_app_item, null);
                ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
                ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);
                ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);

                ll_start.setOnClickListener(AppManagerActivity.this);
                ll_uninstall.setOnClickListener(AppManagerActivity.this);
                ll_share.setOnClickListener(AppManagerActivity.this);

                popupWindow = new PopupWindow(contentView, -2, DensityUtil.dip2px(getApplicationContext(), 70));
                //动画效果的播放必须要求窗体有背景颜色
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //透明颜色也是颜色
                int[] location = new int[2];
                view.getLocationInWindow(location);
                popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, DensityUtil.dip2px(getApplicationContext(), 150), location[1]);


                ScaleAnimation sa = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
                sa.setDuration(300);


                AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
                aa.setDuration(300);
                AnimationSet set = new AnimationSet(false);
                set.addAnimation(sa);
                set.addAnimation(aa);
                contentView.startAnimation(set);

            }
        });

    }

    /**
     * 初始化数据
     */

    private void fillData() {
        new Thread() {
            public void run() {
                infos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
                systemAppinfos = new ArrayList<AppInfo>();
                userAppinfos = new ArrayList<AppInfo>();
                for (AppInfo info : infos) {
                    if (info.isUserApp()) {
                        userAppinfos.add(info);
                    } else {
                        systemAppinfos.add(info);
                    }

                }
                //加载ListView数据适配器
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter == null) {
                            adapter = new AppAdapter();
                            lv_app_manager.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        ll_loading.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }.start();
    }

    /**
     * 清除popuowindow
     */
    private void dismissPopupWindow() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_uninstall:
                if(info.isUserApp()) {
                    uninstallApplication();
                }else {
                    Toast.makeText(this,"系统应用需要获取root权限才能卸载",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_share:
                shareApplication();
                break;
            case R.id.ll_start:
                //开启应用程序
                startApplication();
                break;
            default:
                break;
        }
    }

    /**
     * 分享程序
     */
    private void shareApplication() {
             Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "推荐您使用一款软件,名称叫："+info.getName());
        startActivity(intent);
    }

    /**
     * 卸载应用
     */
    private void uninstallApplication() {
        // <action android:name="android.intent.action.VIEW" />
        // <action android:name="android.intent.action.DELETE" />
        // <category android:name="android.intent.category.DEFAULT" />
        // <data android:scheme="package" />
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setAction("android.intent.action.DELETE");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + info.getPackname()));
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //刷新界面
        fillData();
    }

    /**
     * 开启应用
     */
    private void startApplication() {
        //查询这个应用程序入口actvity
        PackageManager pm = getPackageManager();
       /* Intent intent=new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
      查询出来手机所有具有启动能力的activity
       List<ResolveInfo> resolveInfos= pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);*/
        Intent intent = pm.getLaunchIntentForPackage(info.getPackname());
        if (intent != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "不能启动当前应用", Toast.LENGTH_SHORT).show();
        }
    }

    private class AppAdapter extends BaseAdapter {

        //控制listView 的条目
        @Override
        public int getCount() {
            //return infos.size();
            return userAppinfos.size() + 1 + systemAppinfos.size() + 1;//增加的两条标签
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                //显示系统程序有多少个的小标签
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("用户程序" + userAppinfos.size() + "个");
                return tv;
            } else if (position == userAppinfos.size() + 1) {
                //显示用户程序有多少个的小标签
                TextView tv = new TextView(getApplicationContext());
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("系统程序" + systemAppinfos.size() + "个");
                return tv;
            }

            View view;
            ViewHolder viewHolder;
            AppInfo info;
            if (position <= userAppinfos.size()) {
                info = userAppinfos.get(position - 1);
            } else {
                info = systemAppinfos.get(position - 2 - userAppinfos.size());
            }

            //不仅检查是否为空并且要判断是否能被复用
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(getApplicationContext(), R.layout.list_item_appinfo, null);
                viewHolder = new ViewHolder();
                viewHolder.icon = (ImageView) view.findViewById(R.id.iv_app_icon);
                viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
                viewHolder.tv_location = (TextView) view.findViewById(R.id.tv_app_location);
                view.setTag(viewHolder);
            }
            viewHolder.tv_name.setText(info.getName());
            viewHolder.icon.setImageDrawable(info.getIcon());
            if (info.isInRom()) {
                viewHolder.tv_location.setText("手机内存");
            } else {
                viewHolder.tv_location.setText("外部存储");
            }
            return view;
        }
    }

    static class ViewHolder {
        TextView tv_name;
        TextView tv_location;
        ImageView icon;
    }

    /**
     * 获取某个目录的可用空间
     *
     * @param path 路径
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)  //要求最低版本
    private long getAvailSpace(String path) {
        StatFs statFs = new StatFs(path);
        long size = statFs.getBlockSizeLong();//获取分区的大小
        long count = statFs.getAvailableBlocksLong();//获取可用分区的个数
        statFs.getBlockCountLong();//获取所有的分区
        return size * count;
    }

    /**
     * 获取可用内存
     *
     * @return
     */
    public long getAvailMemory() {
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        manager.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }

    @Override
    protected void onDestroy() {
        dismissPopupWindow();
        super.onDestroy();

    }
}
