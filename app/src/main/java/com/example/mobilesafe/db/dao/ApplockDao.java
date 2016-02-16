package com.example.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobilesafe.db.ApplockDBOpenHelper;

/**
 * Created by abc on 2016/2/16.
 * 程序锁的Dao
 */
public class ApplockDao {
     private ApplockDBOpenHelper helper;
    /**
     * 构造方法
     * @param context   上下文
     */
    public ApplockDao(Context context){
        helper = new ApplockDBOpenHelper(context);
    }
    /**
     * 添加一个要锁定应用程序的包名
     */
    public void add(String packname){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values =new ContentValues();
        values.put("packname",packname);
        db.insert("applock",null,values);
        db.close();
    }

    /**
     * 删除一个要锁定应用程序的包名
     */
    public void delete(String packname){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("applock", "packname=?", new String[]{packname});
        db.close();
    }

    /**
     * 查询
     */
    public boolean find(String packname){
        boolean result=false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor =db.query("applock", null, "packname=?", new String[]{packname}, null, null, null);
        if(cursor.moveToNext()){
            result= true;
        }
        db.close();
        return result;
    }
}
