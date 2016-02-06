package com.example.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
}
