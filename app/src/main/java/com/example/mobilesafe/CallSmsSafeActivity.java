package com.example.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
            View view;
            ViewHolder holder;
            //1.减少内存中view对象创建的次数
            if(convertView==null){
                //还没有产生多余的convertView
                view=View.inflate(getApplicationContext(),R.layout.list_item_callsms,null);
                //2.减少子查询的次数  内存对象的地址 记录对象的地址
                holder=new ViewHolder();
                holder.tv_number = (TextView) view.findViewById(R.id.tv_black_number);
                holder.tv_mode = (TextView) view.findViewById(R.id.tv_black_mode);
                //当孩子生出来时找到他们的引用，存放在记事本里，放在父亲的口袋里
                view.setTag(holder);
            }else{
                view =convertView;
                //只需要从口袋里拿出记事本即可
                holder = (ViewHolder) view.getTag();
            }

            holder.tv_number.setText(infos.get(position).getNumber());
            if("1".equals(infos.get(position).getMode())){ //避免空指针异常
                holder.tv_mode.setText("电话拦截");
            }else if("2".equals(infos.get(position).getMode())){
                holder.tv_mode.setText("短信拦截");
            }else if("3".equals(infos.get(position).getMode())){
                holder.tv_mode.setText("全部拦截");
            }
            return view;
        }
    }

    /**
     * View 对象的容器 记录孩子的内存地址
     *相当于几十本
     */
    class ViewHolder{
        TextView tv_number;
        TextView tv_mode;
    }
}
