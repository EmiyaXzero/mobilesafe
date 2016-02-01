package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

/**
 * Created by abc on 2016/2/1.
 */
public class Setup4Activity extends Activity {
   private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);

    }

    /**
     * 上一步点击事件
     * @param view
     */
    public void pre(View view){
        Intent intent = new Intent(this,Setup3Activity.class);
        startActivity(intent);
        finish();
    }

    /**
     *下一步点击事件设置完成并且sp中更新
     * @param view
     */
    public void next(View view){
        sp=getSharedPreferences("config",MODE_PRIVATE);
        SharedPreferences.Editor editor =sp.edit();
        editor.putBoolean("configed",true);
        editor.commit();
        Intent intent = new Intent(this,LostFindActivity.class);
        startActivity(intent);
        finish();
    }
}
