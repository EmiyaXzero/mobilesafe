package com.example.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.db.dao.NumberAddressQueryUtils;

/**
 * Created by abc on 2016/2/6.
 */
public class NumberAddressQueryActivity extends Activity {
    private EditText ed_phone;
    private TextView result;

    /**
     *系统提供的振动服务器
     */
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_address_query);
        vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
        ed_phone = (EditText) findViewById(R.id.ed_phone);
        result = (TextView) findViewById(R.id.result);
        ed_phone.addTextChangedListener(new TextWatcher() {
            /**
             *当文本发生变化之前回调
             */

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * 当文本发生变化时回调
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              if(s!=null&&s.length()>=3){
                  //查询数据库并且显示结果
                  find(s.toString());
              }
            }

            /**
             *当文本发生变化后回调
             */
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void find(String s) {
        //去数据库查询号码归属地
        //查询数据库
        String location=  NumberAddressQueryUtils.queryNumber(s);
        result.setText(location);
    }

    /**
     * 查询归属地
     * @param view
     */
    public void numberQuery(View view){
        String number = ed_phone.getText().toString().trim();
        if(TextUtils.isEmpty(number)){
            Toast.makeText(this, "号码为空", Toast.LENGTH_SHORT).show();
            Animation shake = AnimationUtils.loadAnimation(this,R.anim.shake);
            ed_phone.startAnimation(shake);
            //当电话号码为空的时候振动手机
           // vibrator.vibrate(1000);
            //-1不重复  0循环
            vibrator.vibrate(new long[]{200,200,300,300,1000,2000},-1);
            return;
        }else {
              find(number);
        }
    }


}
