package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by abc on 2016/2/1.
 */
public class Setup4Activity extends BaseSetupActivity {

    private CheckBox cb_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        cb_state = (CheckBox) findViewById(R.id.cb_state);
        if(sp.getBoolean("protecting",false)){
            cb_state.setChecked(true);
            cb_state.setText("手机防盗已经开启");
        }else {
            cb_state.setText("手机防盗未开启");
            cb_state.setChecked(false);
        }
        cb_state.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if (isChecked){
                     cb_state.setText("手机防盗已经开启");
                 }else {
                     cb_state.setText("手机防盗未开启");
                 }
                //保存选中的状态
                SharedPreferences.Editor editor=sp.edit();
                editor.putBoolean("protecting",isChecked);
                editor.commit();
            }
        });
    }

    public  void showNext(){
        Intent intent = new Intent(this,LostFindActivity.class);
        startActivity(intent);
        finish();
        //要求在finish方法 或startActivity后面执行
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    };

    public   void showPre(){
        Intent  intent = new Intent(this,Setup3Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.pre_tran_in, R.anim.pre_tran_out);
    } ;

    /**
     *下一步点击事件设置完成并且sp中更新
     * @param view
     */
    public void next(View view){

        SharedPreferences.Editor editor =sp.edit();
        editor.putBoolean("configed",true);
        editor.commit();
        showNext();
    }
}
