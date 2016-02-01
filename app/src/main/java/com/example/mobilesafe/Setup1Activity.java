package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by abc on 2016/2/1.
 */
public class Setup1Activity extends BaseSetupActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    public  void showNext(){
        Intent intent = new Intent(this,Setup2Activity.class);
        startActivity(intent);
        finish();
        //要求在finish方法 或startActivity后面执行
        overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
    };

    public   void showPre(){

    } ;
}
