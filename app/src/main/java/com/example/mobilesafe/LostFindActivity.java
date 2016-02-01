package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

/**
 * Created by abc on 2016/2/1.
 */
public class LostFindActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断一下，是否做过设置向导，如果没有就跳转到设置向导页面设置
        SharedPreferences sp=getSharedPreferences("config",MODE_PRIVATE);
        if(sp.getBoolean("configed",false)) {
            setContentView(R.layout.activity_lostfind);
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
