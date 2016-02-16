package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by abc on 2016/2/16.
 */
public class EnterPwdActivity extends Activity {
    private EditText et_password;
    private String packname;
    private TextView tv_name;
    private ImageView iv_icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pwd);
        et_password= (EditText) findViewById(R.id.et_password);
        iv_icon= (ImageView) findViewById(R.id.iv_icon);
        tv_name= (TextView) findViewById(R.id.tv_name);
        //得到传递的数据
        Intent intent =getIntent();
        packname =intent.getStringExtra("packname");
        PackageManager pm= getPackageManager();
        try {
            ApplicationInfo info =pm.getApplicationInfo(packname, 0);
            tv_name.setText(info.loadLabel(pm));
            iv_icon.setImageDrawable(info.loadIcon(pm));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void click(View view){
        String password=et_password.getText().toString().trim();
        if(!TextUtils.isEmpty(password)){
            if("123".equals(password)){
                //告诉看门狗密码正确，可以临时关闭
                //自定义广播事件传递消息
                Intent intent = new Intent();
                intent.setAction("com.example.mobilesafe.tempstop");
                //附带取消的信息
                intent.putExtra("packname",packname);
                //发送自定义广播，停止服务
                sendBroadcast(intent);
                finish();
            }else {
                Toast.makeText(this,"密码错误",Toast.LENGTH_SHORT).show();
                return;
            }
        }else {
            Toast.makeText(this,"密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
        //所有的activity最小化 不会执行ondestory 只执行 onstop方法。

    }
}
