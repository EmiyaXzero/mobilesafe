package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mobilesafe.utils.SmsUtils;

/**
 * Created by abc on 2016/2/6.
 */
public class AtoolsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    /**
     *点击事件进入号码归属地查询
     * @param view
     */
    public void numberQuery(View view){
        Intent intent = new Intent(this,NumberAddressQueryActivity.class);
        startActivity(intent);
    }

    /**
     * 短信备份
     */
    public void smsBackup(View view){
        try {
            SmsUtils.backupSms(this);
            Toast.makeText(this,"备份成功",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "备份失败", Toast.LENGTH_SHORT).show();

        }
    }
    /**
     * 短信还原
     */
    public void smsRestore(View view){

    }
}
