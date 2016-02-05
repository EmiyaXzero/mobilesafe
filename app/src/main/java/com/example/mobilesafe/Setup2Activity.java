package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.mobilesafe.ui.SettingItemView;

import org.w3c.dom.Text;

/**
 * Created by abc on 2016/2/1.
 */
public class Setup2Activity extends BaseSetupActivity {
    private SettingItemView siv_update;

    TelephonyManager tm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        siv_update= (SettingItemView) findViewById(R.id.siv_update);
        tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(!TextUtils.isEmpty(sp.getString("sim",null))){
            siv_update.setChecked(true);
        } else {
            siv_update.setChecked(false);
        }
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sim =tm.getSimSerialNumber();//获取sim卡序列号
                SharedPreferences.Editor editor = sp.edit();  //sp sharedpreference在父类已经实现
                //判断是否选中
                if (siv_update.isChecked()) {
                    //已经选中
                    siv_update.setChecked(false);
                    editor.putString("sim", null);
                } else {
                    //没选中
                    siv_update.setChecked(true);
                    editor.putString("sim",sim);
                }
                editor.commit();

            }
        });
    }
    public  void showNext(){
        //判断是否绑定sim卡
        String sim =sp.getString("sim",null);
        if(!TextUtils.isEmpty(sim)) {
            Intent intent = new Intent(this, Setup3Activity.class);
            startActivity(intent);
            finish();
            //要求在finish方法 或startActivity后面执行
            overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
        }else {
            Toast.makeText(this,"sim卡没有绑定",Toast.LENGTH_LONG).show();
            return;
        }

    };

    public   void showPre(){
        Intent  intent = new Intent(this,Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_tran_in, R.anim.pre_tran_out);
    } ;

}
