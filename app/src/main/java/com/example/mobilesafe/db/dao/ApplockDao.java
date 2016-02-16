package com.example.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobilesafe.db.ApplockDBOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abc on 2016/2/16.
 * 程序锁的Dao
 */
public class ApplockDao {
    private ApplockDBOpenHelper helper;

    private Context context;
    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public ApplockDao(Context context) {
        helper = new ApplockDBOpenHelper(context);
        this.context=context;
    }

    /**
     * 添加一个要锁定应用程序的包名
     */
    public void add(String packname) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packname", packname);
        db.insert("applock", null, values);
        db.close();
        Intent intent = new Intent();
        intent.setAction("com.example.mobilesafe.applockchange");
        context.sendBroadcast(intent);
    }

    /**
     * 删除一个要锁定应用程序的包名
     */
    public void delete(String packname) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("applock", "packname=?", new String[]{packname});
        db.close();
        Intent intent = new Intent();
        intent.setAction("com.example.mobilesafe.applockchange");
        context.sendBroadcast(intent);
    }

    /**
     * 查询
     */
    public boolean find(String packname) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("applock", null, "packname=?", new String[]{packname}, null, null, null);
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    public List<String> findAll() {
        List<String> protectPacknames = new ArrayList<String>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("applock", new String[]{"packname"}, null,null, null, null, null);
        while(cursor.moveToNext()){
            protectPacknames.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return protectPacknames;
    }
}
