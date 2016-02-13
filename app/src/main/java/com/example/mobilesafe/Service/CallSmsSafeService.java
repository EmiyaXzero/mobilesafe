package com.example.mobilesafe.Service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.example.mobilesafe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;

/**
 * Created by abc on 2016/2/9.
 */
public class CallSmsSafeService extends Service {
    private InnerSmsReceiver receiver;
    private BlackNumberDao   dao;

    private MyListener listener;
    private TelephonyManager tm;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dao=new BlackNumberDao(this);
        tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener=new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        receiver = new InnerSmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        listener=null;
        receiver=null;
    }

    private class InnerSmsReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG", "短信到了");
            //得到发送短信的号码
            Object[] objs= (Object[]) intent.getExtras().get("pdus");
            for(Object o :objs){
                SmsMessage smsManager= SmsMessage.createFromPdu((byte[]) o);
                //得到发件人
                String number =smsManager.getOriginatingAddress();
                String mode= dao.findMode(number);
                if("2".equals(mode)||"3".equals(mode)){
                    Log.d("TAG","拦截短信");
                    abortBroadcast();
                }
            }
        }
    }

    private class MyListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING ://铃响状态
                    String mode= dao.findMode(incomingNumber);
                    if("1".equals(mode)||"3".equals(mode)){
                        //删除呼叫记录
                        //访问另外一个应用程序联系人私有的应用程序数据库
                        //deleteCallLog(incomingNumber);
                        //观察呼叫数据库内容的变换
                        Uri uri = Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(uri, true,new CallLogObserver(new Handler(),incomingNumber));
                        //反射找到endcall
                        endCall(); //另外一个进程里运行的远程服务的方法。方法调用完后，呼叫记录可能还没生成。

                    }
                    break;
            }
        }

        private class CallLogObserver extends ContentObserver{

            private  String inComingNumber;

            /**
             * Creates a content observer.
             *
             * @param handler The handler to run {@link #onChange} on, or null if none.
             */
            public CallLogObserver(Handler handler,String inComingNumber) {
                super(handler);
                this.inComingNumber=inComingNumber;
            }

            @Override
            public void onChange(boolean selfChange) {
                Log.d("TAG", "数据库内容产生变换");
                getContentResolver().unregisterContentObserver(this);
                deleteCallLog(inComingNumber);
                super.onChange(selfChange);

            }
        }

        private void endCall() {
          //IBinder iBinder= ServiceManger.getService(TELEPHONY_SERVICE);
          //反射
            try {
                //拿到ServiceManager的字节码
                Class clazz =CallSmsSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
                Method method=clazz.getDeclaredMethod("getService", String.class);
                IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);//静态方法第一个可以是null
                ITelephony.Stub.asInterface(iBinder).endCall();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 利用内容提供者去删除呼叫记录
     * @param incomingNumber
     */

    private void deleteCallLog(String incomingNumber) {
        ContentResolver resolver =getContentResolver();
        //呼叫记录的Uri的路径
        Uri uri = Uri.parse("content://call_log/calls");
        resolver.delete(uri,"number=?",new String[]{incomingNumber});

    }

}
