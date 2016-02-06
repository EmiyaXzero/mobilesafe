package com.example.mobilesafe.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.example.mobilesafe.db.dao.NumberAddressQueryUtils;

/**
 * Created by abc on 2016/2/6.
 */
public class AddressService extends Service {
    /**
     * 监听来电
     */
    private TelephonyManager tm;

    private MyPhoneStateListener listener;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class  MyPhoneStateListener extends PhoneStateListener{
        /**
         *
         * @param 状态
         * @param 电话号码
         */
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING://电话铃声响起的时候，来电的时候
                    //根据传入的号码，查询归属地
                    String address=NumberAddressQueryUtils.queryNumber(incomingNumber);
                    Toast.makeText(getApplicationContext(),address,Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener=new MyPhoneStateListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消监听来电
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
        listener=null;
    }
}
