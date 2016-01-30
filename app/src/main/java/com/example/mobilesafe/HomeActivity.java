package com.example.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by abc on 2016/1/30.
 */
public class HomeActivity extends Activity {
    private GridView list_home;
    //适配器
    private MyAdapter adapter;
    //初始化功能列表名称
    private static String [] names = {
            "手机防盗","通讯卫士","软件管理",
            "进程管理","流量统计","手机杀毒",
            "缓存清理","高级工具","设置中心"

    };

    //初始化图片id
    private static int[] ids = {
            R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,
            R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,
            R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        list_home=(GridView)findViewById(R.id.list_home);
        adapter = new MyAdapter();
        list_home.setAdapter(adapter);            //Adapter连接后台和前段的适配器
    }
    private  class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return names[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           View view= View.inflate(HomeActivity.this,R.layout.list_item_home,null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_item);
            TextView tv = (TextView) view.findViewById(R.id.tv_item);
            tv.setText(names[position]);
            iv.setImageResource(ids[position]);
            return view;
        }
    }
}
