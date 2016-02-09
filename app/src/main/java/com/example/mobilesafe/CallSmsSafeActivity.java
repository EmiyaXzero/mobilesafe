package com.example.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.db.dao.BlackNumberDao;
import com.example.mobilesafe.domain.BlackNumberInfo;

import java.util.List;


/**
 * Created by abc on 2016/2/9.
 */
public class CallSmsSafeActivity extends Activity {
    private BlackNumberDao blackNumberDao;
    private ListView lv_callsms_safe;
    private List<BlackNumberInfo> infos;
    private CallSmsSafeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);
        blackNumberDao = new BlackNumberDao(this);
        adapter = new CallSmsSafeAdapter();
        infos = blackNumberDao.findAll();
        lv_callsms_safe = (ListView) findViewById(R.id.lv_callsms_safe);
        lv_callsms_safe.setAdapter(adapter);
    }

    private class CallSmsSafeAdapter extends BaseAdapter {

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
        public View getView(final int position, View convertView, final ViewGroup parent) {
            View view;
            ViewHolder holder;
            //1.减少内存中view对象创建的次数
            if (convertView == null) {
                //还没有产生多余的convertView
                view = View.inflate(getApplicationContext(), R.layout.list_item_callsms, null);
                //2.减少子查询的次数  内存对象的地址 记录对象的地址
                holder = new ViewHolder();
                holder.tv_number = (TextView) view.findViewById(R.id.tv_black_number);
                holder.tv_mode = (TextView) view.findViewById(R.id.tv_black_mode);
                holder.iv_delete = (ImageView) view.findViewById(R.id.iv_delete);
                //当孩子生出来时找到他们的引用，存放在记事本里，放在父亲的口袋里
                view.setTag(holder);
            } else {
                view = convertView;
                //只需要从口袋里拿出记事本即可
                holder = (ViewHolder) view.getTag();
            }

            holder.tv_number.setText(infos.get(position).getNumber());
            if ("1".equals(infos.get(position).getMode())) { //避免空指针异常
                holder.tv_mode.setText("电话拦截");
            } else if ("2".equals(infos.get(position).getMode())) {
                holder.tv_mode.setText("短信拦截");
            } else if ("3".equals(infos.get(position).getMode())) {
                holder.tv_mode.setText("全部拦截");
            }
            holder.iv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CallSmsSafeActivity.this);
                    builder.setTitle("警告");
                    builder.setMessage("确定删除这条记录吗");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //删除数据库内容
                            blackNumberDao.delete(infos.get(position).getNumber());
                            //更新界面
                            infos.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("取消",null);
                    builder.show();


                }
            });
            return view;
        }
    }

    /**
     * View 对象的容器 记录孩子的内存地址
     * 相当于几十本
     */
    class ViewHolder {
        TextView tv_number;
        TextView tv_mode;
        ImageView iv_delete;
    }

    private EditText et_blackNumber;
    private CheckBox cb_phone;
    private CheckBox cb_sms;
    private Button bt_ok;
    private Button bt_cancel;


    public void addBlackNumber(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View contentView = View.inflate(this, R.layout.dialog_add_blacknumber, null);
        dialog.setView(contentView, 0, 0, 0, 0);
        dialog.show();
        bt_ok = (Button) contentView.findViewById(R.id.ok);
        bt_cancel = (Button) contentView.findViewById(R.id.cancel);
        et_blackNumber = (EditText) contentView.findViewById(R.id.et_black_number);
        cb_phone = (CheckBox) contentView.findViewById(R.id.cb_phone);
        cb_sms = (CheckBox) contentView.findViewById(R.id.cb_sms);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = et_blackNumber.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(), "黑名单号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                String mode;
                if (cb_phone.isChecked() && cb_sms.isChecked()) {
                    //全部拦截
                    mode = "3";
                } else if (cb_phone.isChecked()) {
                    //电话拦截
                    mode = "1";
                } else if (cb_sms.isChecked()) {
                    //短信拦截
                    mode = "2";
                } else {
                    Toast.makeText(getApplicationContext(), "请选择拦截模式", Toast.LENGTH_SHORT).show();
                    return;
                }
                blackNumberDao.add(phone, mode);
                //更新ListView内容
                BlackNumberInfo info = new BlackNumberInfo();
                info.setNumber(phone);
                info.setMode(mode);
                infos.add(0, info);
                //通知适配器数据变化
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

    }
}
