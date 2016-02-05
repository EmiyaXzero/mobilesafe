package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by abc on 2016/2/1.
 */
public class LostFindActivity extends Activity {
    private  SharedPreferences sp;
    private  TextView tv_safe_number;
    private ImageView iv_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断一下，是否做过设置向导，如果没有就跳转到设置向导页面设置
        sp=getSharedPreferences("config",MODE_PRIVATE);
        if(sp.getBoolean("configed", false)) {
            //主界面
            setContentView(R.layout.activity_lostfind);
            if(!TextUtils.isEmpty(sp.getString("phone",null))){
                //判断是否有安全号码
                tv_safe_number= (TextView) findViewById(R.id.tv_safe_number);
                iv_lock= (ImageView) findViewById(R.id.iv_lock);
                tv_safe_number.setText(sp.getString("phone",null));
                //判断是否保护状态
                if(sp.getBoolean("protecting",false)){
                    iv_lock.setImageResource(R.drawable.lock);
                }else {
                    iv_lock.setImageResource(R.drawable.unlock);
                }
            }
        }else {
            Intent intent = new Intent(this,Setup1Activity.class);
            startActivity(intent);
            finish();
        }



    }
    /**
     * 重新进入设置界面
     */
    public void reEnterSet(View view){
        Intent  intent = new Intent(this,Setup1Activity.class);
        startActivity(intent);
    }
}
