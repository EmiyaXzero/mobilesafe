package com.example.mobilesafe.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private SharedPreferences sp;
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

    private WindowManager.LayoutParams params;
    long[] mHits=new long[2];
    /**
     * 自定义吐司
     */

    private void myToast(String address) {
        view = View.inflate(this, R.layout.address_show,null);
        TextView  tv= (TextView) view.findViewById(R.id.tv_address);
        //自定义双击居中
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.arraycopy(mHits,1,mHits,0,mHits.length-1);//数组左移一位
                mHits[mHits.length-1]= SystemClock.uptimeMillis();
                if(mHits[0]>=(SystemClock.uptimeMillis()-500)){
                    //双击了，现在居中
                    params.x = wm.getDefaultDisplay().getWidth()/2-view.getWidth()/2;
                    wm.updateViewLayout(v,params);
                    SharedPreferences.Editor editor =sp.edit();
                    editor.putInt("lastX",params.x);
                    editor.commit();

                }
            }
        });


        //设置触摸事件
        view.setOnTouchListener(new View.OnTouchListener() {
            int startX;
            int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //手指按下屏幕
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //手指在屏幕移动
                        int newX = (int) event.getRawX();
                        int newY = (int) event.getRawY();
                        int dx = newX - startX;
                        int dy = newY - startY;
                        startX = newX;
                        startY = newY;
                        params.x += dx;
                        params.y += dy;
                        //考虑边界问题
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > (wm.getDefaultDisplay().getWidth() - view.getWidth())) {
                            params.x = wm.getDefaultDisplay().getWidth() - view.getWidth();
                        }
                        if (params.y > (wm.getDefaultDisplay().getHeight() - view.getHeight())) {
                            params.y = wm.getDefaultDisplay().getHeight() - view.getHeight();
                        }

                        //更新窗体布局
                        wm.updateViewLayout(v, params);
                        break;
                    case MotionEvent.ACTION_UP:
                        //手机离开屏幕   记录控件距离左上角的坐标
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("lastX", params.x);
                        editor.putInt("lastY", params.y);
                        editor.commit();
                        break;
                }
                return false;//事件处理完毕。不要让父控件，父布局相应触碰事件

            }
        });


        tv.setText(address);
        //"半透明","活力橙","卫士蓝","金属灰","苹果绿"
        int [] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue
                ,R.drawable.call_locate_gray,R.drawable.call_locate_green};
        sp = getSharedPreferences("config",MODE_PRIVATE);
        int which=sp.getInt("which",0);
        view.setBackgroundResource(ids[which]);
         params = new WindowManager.LayoutParams();
        //窗体的参数就设置好了
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;

        params.gravity= Gravity.TOP+Gravity.LEFT;//布局位置
        params.x=sp.getInt("lastX",0);
        params.y=sp.getInt("lastY",0);

        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
               //取消不可触摸 | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        //安卓系统里面具有电话优先级的窗口类型最高的优先级，并且需要权限
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
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
