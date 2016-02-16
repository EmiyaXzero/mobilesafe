package com.example.mobilesafe;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.domain.TaskInfo;
import com.example.mobilesafe.engine.TaskInfoProvider;
import com.example.mobilesafe.utils.SystemInfoUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by abc on 2016/2/15.
 */
public class TaskManagerActivity extends Activity {

    private TextView tv_process_count;
    private TextView tv_mem_info;
    private LinearLayout ll_loading;
    private ListView lv_task_manager;

    private List<TaskInfo> allTaskInfos;
    private List<TaskInfo> userTaskInfos;
    private List<TaskInfo> systemTaskInfos;

    private TaskManagerAdapter adapter;

    private TextView tv_status;

    private int processCount;

    private long availMemory;
    private long allMemory;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);
        sp=getSharedPreferences("config",MODE_PRIVATE);
        tv_process_count = (TextView) findViewById(R.id.tv_process_count);
        tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);
        lv_task_manager = (ListView) findViewById(R.id.lv_task_manager);
        setTitle();
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_status = (TextView) findViewById(R.id.tv_status);
        fillData();

        lv_task_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userTaskInfos != null && systemTaskInfos != null) {
                    if (firstVisibleItem > userTaskInfos.size()) {
                        tv_status.setText("系统进程：" + systemTaskInfos.size() + "个");
                    } else {
                        tv_status.setText("用户进程：" + userTaskInfos.size() + "个");

                    }
                }
            }
        });

        lv_task_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TaskInfo taskInfo;
                if (position == 0) {
                    //用户进程标签
                    return;
                } else if (position == 1 + userTaskInfos.size()) {
                    //系统进程标签
                    return;
                } else if (position <= userTaskInfos.size()) {
                    taskInfo = userTaskInfos.get(position - 1);
                } else {
                    taskInfo = systemTaskInfos.get(position - 2 - userTaskInfos.size());
                }
                if (getPackageName().equals(taskInfo.getPackname())) {
                    return;
                }
                ViewHolder viewHolder = (ViewHolder) view.getTag();
                if (taskInfo.isChecked()) {
                    taskInfo.setChecked(false);
                    viewHolder.cb_status.setChecked(false);
                } else {
                    taskInfo.setChecked(true);
                    viewHolder.cb_status.setChecked(true);
                }
            }
        });
    }

    private void setTitle() {
        processCount = SystemInfoUtils.getRunningProcessCount(this);
        tv_process_count.setText("运行的进程个数:" + processCount + "个");
        availMemory = SystemInfoUtils.getAvailMemory(this);
        allMemory = SystemInfoUtils.getAllMemory(this);
        tv_mem_info.setText("剩余/总内存:" + Formatter.formatFileSize(this, availMemory) + "/" + Formatter.formatFileSize(this, allMemory));
    }

    /**
     * 填充数据
     */
    private void fillData() {
        ll_loading.setVisibility(View.VISIBLE);
        new Thread() {
            @Override
            public void run() {
                allTaskInfos = TaskInfoProvider.getTaskInfos(getApplicationContext());
                userTaskInfos = new ArrayList<TaskInfo>();
                systemTaskInfos = new ArrayList<TaskInfo>();
                for (TaskInfo info : allTaskInfos) {
                    if (info.isUserTask()) {
                        userTaskInfos.add(info);
                    } else {
                        systemTaskInfos.add(info);
                    }

                }

                //更新设置界面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter == null) {
                            adapter = new TaskManagerAdapter();
                            lv_task_manager.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        ll_loading.setVisibility(View.INVISIBLE);
                        setTitle();

                    }
                });
            }
        }.start();
    }


    private class TaskManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if(sp.getBoolean("showSystem",false)) {
                return systemTaskInfos.size() + userTaskInfos.size() + 2;
            }else {
                return userTaskInfos.size() + 1;
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TaskInfo taskInfo;
            if (position == 0) {
                //用户进程标签
                TextView tv = new TextView(getApplicationContext());
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("用户进程:" + userTaskInfos.size() + "个");
                return tv;
            } else if (position == 1 + userTaskInfos.size()) {
                //系统进程标签
                TextView tv = new TextView(getApplicationContext());
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("系统进程:" + systemTaskInfos.size() + "个");
                return tv;
            } else if (position <= userTaskInfos.size()) {
                taskInfo = userTaskInfos.get(position - 1);
            } else {
                taskInfo = systemTaskInfos.get(position - 2 - userTaskInfos.size());
            }
            View view;
            ViewHolder viewHolder;
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            } else {
                viewHolder = new ViewHolder();
                view = View.inflate(getApplicationContext(), R.layout.list_item_taskinfo, null);
                viewHolder.iv_icon = (ImageView) view.findViewById(R.id.iv_task_icon);
                viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_task_name);
                viewHolder.tv_memsize = (TextView) view.findViewById(R.id.tv_task_memsize);
                viewHolder.cb_status = (CheckBox) view.findViewById(R.id.cb_status);
                view.setTag(viewHolder);
            }

            viewHolder.iv_icon.setImageDrawable(taskInfo.getIcon());

            viewHolder.cb_status.setChecked(taskInfo.isChecked());
            viewHolder.tv_name.setText(taskInfo.getName());
            viewHolder.tv_memsize.setText("内存占用:" + Formatter.formatShortFileSize(getApplicationContext(), taskInfo.getMemsize()));
            if(getPackageName().equals(taskInfo.getPackname())){
                viewHolder.cb_status.setVisibility(View.INVISIBLE);
            }else {
                //避免复用的时候也没有checkbox
                viewHolder.cb_status.setVisibility(View.VISIBLE);
            }
            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memsize;
        CheckBox cb_status;
    }

    /**
     * 选中全部
     */
    public void selectAll(View view) {
        for (TaskInfo info : allTaskInfos) {
            if (getPackageName().equals(info.getPackname())) {
                continue;
            }
            info.setChecked(true);
        }
        adapter.notifyDataSetChanged();

    }

    /**
     * 反选全部
     */
    public void selectOppo(View view) {
        for (TaskInfo info : allTaskInfos) {
            if (getPackageName().equals(info.getPackname())) {
                continue;
            }
            info.setChecked(!info.isChecked());
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 杀死选中全部
     */
    public void killAll(View view) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int count = 0;
        long savedMem = 0;
        List<TaskInfo> killedInfos = new ArrayList<TaskInfo>();
        for (TaskInfo info : allTaskInfos) {
            if (info.isChecked()) {
                am.killBackgroundProcesses(info.getPackname());
                if (info.isUserTask()) {
                    userTaskInfos.remove(info);
                } else {
                    systemTaskInfos.remove(info);
                }
                count++;
                savedMem+=info.getMemsize();
                killedInfos.add(info);
            }
        }
        allTaskInfos.removeAll(killedInfos);
        adapter.notifyDataSetChanged();
        Toast.makeText(
                this,
                "杀死了" + count + "个进程，释放了"
                        + Formatter.formatFileSize(this, savedMem) + "的内存", Toast.LENGTH_SHORT)
                .show();
        processCount -= count;
        tv_process_count.setText("运行的进程个数:" + processCount + "个");
        availMemory += savedMem;
        tv_mem_info.setText("剩余/总内存:" + Formatter.formatFileSize(this, availMemory) + "/" + Formatter.formatFileSize(this, allMemory));
    }

    /**
     * 进入设置
     */
    public void enterSetting(View view) {
        Intent intent = new Intent(this,TaskSettingActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        adapter.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
