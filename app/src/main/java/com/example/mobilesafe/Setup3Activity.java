package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by abc on 2016/2/1.
 */
public class Setup3Activity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
    }


    /**
     * 上一步点击事件
     * @param view
     */
    public void pre(View view){
        Intent intent = new Intent(this,Setup2Activity.class);
        startActivity(intent);
        finish();
    }

    /**
     *下一步点击事件
     * @param view
     */
    public void next(View view){
        Intent intent = new Intent(this,Setup4Activity.class);
        startActivity(intent);
        finish();
    }
}
