package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by abc on 2016/2/1.
 */
public class Setup3Activity extends BaseSetupActivity {
    private EditText et_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        et_phone= (EditText) findViewById(R.id.et_phone);
        if(!TextUtils.isEmpty(sp.getString("phone",null))){
             et_phone.setText(sp.getString("phone",null));
        }
    }


    public  void showNext(){
        if(!TextUtils.isEmpty(et_phone.getText().toString())){
            Intent intent = new Intent(this,Setup4Activity.class);
            startActivity(intent);
            finish();
            //要求在finish方法 或startActivity后面执行
            overridePendingTransition(R.anim.tran_in,R.anim.tran_out);
        }else {
            Toast.makeText(this, "请添加安全号码", Toast.LENGTH_LONG).show();
            return;
        }


    };

    public   void showPre(){
        Intent  intent = new Intent(this,Setup2Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_tran_in, R.anim.pre_tran_out);
    };

    public void selectContact(View view){
        Intent intent = new Intent(this,SelectContactActivity.class);
        startActivityForResult(intent,0);//为了得到返回值在这里使用startActivityForResult
    }

    /**
     *获取返回数据
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data==null){
            return;
        }else {
            String phone = data.getStringExtra("phone").replace("-", "");//获取传过来的phone
            et_phone.setText(phone);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("phone",phone);
            editor.commit();
        }
    }
}
