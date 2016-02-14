package com.example.mobilesafe;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobilesafe.domain.AppInfo;
import com.example.mobilesafe.engine.AppInfoProvider;

import java.text.Format;
import java.util.List;

/**
 * Created by abc on 2016/2/14.
 */
public class AppManagerActivity extends Activity{
    private TextView tv_avail_rom;
    private TextView tv_avail_sd;
    private ListView lv_app_manager;
    private LinearLayout ll_loading;
    private List<AppInfo>  infos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        tv_avail_rom= (TextView) findViewById(R.id.tv_avail_rom);
        tv_avail_sd= (TextView) findViewById(R.id.tv_avail_sd);
        lv_app_manager= (ListView) findViewById(R.id.lv_app_manager);
        ll_loading= (LinearLayout) findViewById(R.id.ll_loading);
        long sdSize=getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
        long romSize=getAvailMemory();//获取内存
        tv_avail_sd.setText("SD卡可用空间:" + Formatter.formatFileSize(this,sdSize)); //formatFileSize()将内存空间转换格式
        tv_avail_rom.setText("内存可用空间:"+Formatter.formatFileSize(this,romSize));
        //避免主进程阻塞 infos= AppInfoProvider.getAppInfos(this);
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(){
               public void run(){
                   infos= AppInfoProvider.getAppInfos(AppManagerActivity.this);
                   //加载ListView数据适配器
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           lv_app_manager.setAdapter(new AppAdapter());
                           ll_loading.setVisibility(View.INVISIBLE);
                       }
                   });
               };
        }.start();
    }

    private  class AppAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return infos.size();
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
            View view;
            ViewHolder viewHolder;
            if(convertView!=null){
                view =convertView;
                viewHolder= (ViewHolder) view.getTag();
           }else {
                view = View.inflate(getApplicationContext(),R.layout.list_item_appinfo,null);
                viewHolder=new ViewHolder();
                viewHolder.icon= (ImageView) view.findViewById(R.id.iv_app_icon);
                viewHolder.tv_name= (TextView) view.findViewById(R.id.tv_app_name);
                viewHolder.tv_location= (TextView) view.findViewById(R.id.tv_app_location);
                view.setTag(viewHolder);
            }
            viewHolder.tv_name.setText(infos.get(position).getName());
            viewHolder.icon.setImageDrawable(infos.get(position).getIcon());
            return view ;
        }
    }

    static class ViewHolder{
        TextView tv_name;
        TextView tv_location;
        ImageView icon;
    }

    /**
     * 获取某个目录的可用空间
     * @param path 路径
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)  //要求最低版本
    private long getAvailSpace(String path){
        StatFs statFs = new StatFs(path);
        long size =statFs.getBlockSizeLong();//获取分区的大小
        long count= statFs.getAvailableBlocksLong();//获取可用分区的个数
        statFs.getBlockCountLong();//获取所有的分区
        return size*count;
    }

    /**
     * 获取可用内存
     * @return
     */
    public long getAvailMemory() {
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        manager.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }
}
