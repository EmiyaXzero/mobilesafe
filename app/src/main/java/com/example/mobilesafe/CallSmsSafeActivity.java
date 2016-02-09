package com.example.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobilesafe.db.BlackNumberDBOpenHelper;
import com.example.mobilesafe.db.dao.BlackNumberDao;
import com.example.mobilesafe.domain.BlackNumberInfo;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Random;

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
        infos=blackNumberDao.findAll();
        lv_callsms_safe= (ListView) findViewById(R.id.lv_callsms_safe);
        lv_callsms_safe.setAdapter(adapter);
    }

    private class CallSmsSafeAdapter extends BaseAdapter{

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
            View view=View.inflate(getApplicationContext(),R.layout.list_item_callsms,null);
            TextView tv_black_number = (TextView) view.findViewById(R.id.tv_black_number);
            TextView tv_black_mode = (TextView) view.findViewById(R.id.tv_black_mode);

            tv_black_number.setText(infos.get(position).getNumber());
            if("1".equals(infos.get(position).getMode())){ //避免空指针异常
                tv_black_mode.setText("电话拦截");
            }else if("2".equals(infos.get(position).getMode())){
                tv_black_mode.setText("短信拦截");
            }else if("3".equals(infos.get(position).getMode())){
                tv_black_mode.setText("全部拦截");
            }
            return view;
        }
    }
}
