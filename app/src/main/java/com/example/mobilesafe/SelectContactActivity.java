package com.example.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

/**
 * Created by abc on 2016/2/5.
 */
public class SelectContactActivity extends Activity {
    private ListView list_select_contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        list_select_contact = (ListView) findViewById(R.id.list_select_contact);
    }
}
