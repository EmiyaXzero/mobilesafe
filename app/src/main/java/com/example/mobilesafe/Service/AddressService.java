package com.example.mobilesafe.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.mobilesafe.R;
import com.example.mobilesafe.db.dao.NumberAddressQueryUtils;

import org.w3c.dom.Text;

/**
 * Created by abc on 2016/2/6.
 */
public class AddressService extends Service {
    /**
     * 监听来电
     */
    private TelephonyManager tm;

    private OutCallReceiver receiver;
    private MyPhoneStateListener listener;

    /**
     *定义一个窗体
     */
    private WindowManager wm;
    private View view;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 服务里面的内部类
    //广播接收者的生命周期和服务一样
    class OutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 这就是我们拿到的播出去的电话号码
            String phone = getResultData();
            // 查询数据库
            String address = NumberAddressQueryUtils.queryNumber(phone);
            //Toast.makeText(context, address, Toast.LENGTH_LONG).show();
             myToast(address);
        }



    }

    /**
     * 自定义吐司
     */

    private void myToast(String address) {
        view = View.inflate(this, R.layout.address_show,null);
        TextView  tv= (TextView) view.findViewById(R.id.tv_address);
        tv.setText(address);
        //"半透明","活力橙","卫士蓝","金属灰","苹果绿"
        int [] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue
                ,R.drawable.call_locate_gray,R.drawable.call_locate_green};
        SharedPreferences sp = getSharedPreferences("config",MODE_PRIVATE);
        int which=sp.getInt("which",0);
        view.setBackgroundResource(ids[which]);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        //窗体的参数就设置好了
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        wm.addView(view,params);

    }

    private class MyPhoneStateListener extends PhoneStateListener {
        /**
         * 状态和电话号码
         */
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://电话铃声响起的时候，来电的时候
                    //根据传入的号码，查询归属地
                    String address = NumberAddressQueryUtils.queryNumber(incomingNumber);
                    //Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG).show();
                    myToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE://电话空闲状态
                    //把View移除
                    if(view!=null) {
                        wm.removeView(view);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //监听来电
        listener = new MyPhoneStateListener();
        //实例化窗体
        wm= (WindowManager) getSystemService(WINDOW_SERVICE);
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        //用代码去注册广播接受者
        receiver = new OutCallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(receiver, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消监听来电
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        listener = null;
        //用代码取消广播接受者
        unregisterReceiver(receiver);
        receiver = null;
    }



}
