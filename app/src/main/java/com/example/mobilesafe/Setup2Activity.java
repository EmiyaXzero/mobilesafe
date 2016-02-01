package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.mobilesafe.ui.SettingItemView;

/**
 * Created by abc on 2016/2/1.
 */
public class Setup2Activity extends Activity {
    private SettingItemView siv_update;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        sp=getSharedPreferences("config",MODE_PRIVATE);
        siv_update= (SettingItemView) findViewById(R.id.siv_update);
        if(sp.getBoolean("update",false)){
            siv_update.setChecked(true);
        } else {
            siv_update.setChecked(false);
        }
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                //判断是否选中
                if(siv_update.isChecked()){
                    //已经选中
                    siv_update.setChecked(false);
                    editor.putBoolean("update",false);
                }else {
                    //没选中
                    siv_update.setChecked(true);
                    editor.putBoolean("update",true);
                }
                editor.commit();

            }
        });
    }

    /**
     * 上一步点击事件
     * @param view
     */
    public void pre(View view){
        Intent  intent = new Intent(this,Setup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_tran_in, R.anim.pre_tran_out);
    }

    /**
     *下一步点击事件
     * @param view
     */
    public void next(View view){
        Intent intent = new Intent(this,Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }
}
