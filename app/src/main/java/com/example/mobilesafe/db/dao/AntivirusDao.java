package com.example.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by abc on 2016/2/6.
 * 病毒数据库查询业务类
 */
public class AntivirusDao {

    private static String path = "data/data/com.example.mobilesafe/files/antivirus.db";
    private static String address;

    /**
     * 查询一个MD5是否在病毒数据库中存在
     * @param md5
     * @return
     */
    public static boolean isVirus(String md5){
        boolean result = false;
        SQLiteDatabase db  =SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READONLY);
        Cursor cursor =db.rawQuery("select * from datable where md5=?", new String[]{md5});
        if(cursor.moveToNext()){
            result=true;
        }
        cursor.close();
        db.close();

        return result;
    }
}
