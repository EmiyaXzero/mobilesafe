package com.example.mobilesafe;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by abc on 2016/2/5.
 */
public class SelectContactActivity extends Activity {
    private ListView list_select_contact;
    private List<Map<String, String>> contactInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        list_select_contact = (ListView) findViewById(R.id.list_select_contact);
        List<Map<String,String>> data=getContactInfo();
        list_select_contact.setAdapter(new SimpleAdapter(this,data,R.layout.contact_item_view,new String[]{"name","phone"},new int[]{R.id.tv_name,R.id.tv_phone}));
    }

    /**
     * 从数据库中获取联系人
     * @return
     */
    public List<Map<String, String>> getContactInfo() {
        //所有联系人
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        //得到一个内容解析器
        ContentResolver resolver = getContentResolver();
        //设置数据库的uri
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri uriData = Uri.parse("content://com.android.contacts/data");
        Cursor cursor = resolver.query(uri,new String[]{"contact_id"},null,null,null);
        while(cursor.moveToNext()){
            String contact_id=cursor.getString(0);

            if(contact_id!=null){
                Map<String,String> map = new HashMap<String,String>();
                //注意起名
                Cursor phoneCursor = resolver.query(uriData,new String[]{"data1","mimetype"},
                        "contact_id=?",new String[]{contact_id},null);
                while(phoneCursor.moveToNext()){
                    String data=phoneCursor.getString(0);
                    String type=phoneCursor.getString(1);
                    if(type.equals("vnd.android.cursor.item/name")){
                        //姓名
                        map.put("name",data);
                    }else if(type.equals("vnd.android.cursor.item/phone_v2")){
                        //号码
                        map.put("phone",data);
                    }
                }
                list.add(map);
                phoneCursor.close();
            }
        }
        cursor.close();
        return list;
    }
}
