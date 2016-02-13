package com.example.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.example.mobilesafe.db.BlackNumberDBOpenHelper;
import com.example.mobilesafe.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 黑名单数据库的增删改查
 * Created by abc on 2016/2/9.
 */
public class BlackNumberDao  {
    private BlackNumberDBOpenHelper helper ;

    /**
     * 构造方法
     * @param context   上下文
     */
    public BlackNumberDao(Context context){
         helper = new BlackNumberDBOpenHelper(context);
    }

    /**
     * 查询黑名单号码是否存在
     * @param number
     * @return
     */
    public boolean find(String number){
        boolean result=false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor =db.rawQuery("select * from blacknumber where number=?", new String[]{number});
        if(cursor.moveToNext()){
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }
    /**
     * 查询黑名单号码的拦截模式
     * @param number
     * @return
     */
    public String findMode(String number){
        String mode=null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor =db.rawQuery("select mode from blacknumber where number=?", new String[]{number});
        if(cursor.moveToNext()){
           mode=cursor.getString(0);
        }
        cursor.close();
        db.close();
        return mode;
    }


    /**
     * 增加黑名单信息
     * @param number 增加的号码
     * @param mode 拦截模式
     */
    public void add(String number ,String mode){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number",number);
        values.put("mode",mode);
        db.insert("blacknumber", null, values);
        db.close();
    }

    /**
     * 增加黑名单信息
     * @param number 修改的号码
     * @param mode 修改的拦截模式
     */
    public void update(String number ,String mode){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode",mode);
        db.update("blacknumber", values, "number=?", new String[]{number});
        db.close();
    }

    /**
     * 删除的黑名单信息
     * @param number 删除的的号码
     */
    public void delete(String number){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("blacknumber", "number=?", new String[]{number});
        db.close();
    }

    /**
     * 查找部分黑名单号码
     */
    public List<BlackNumberInfo> findAll(){

        List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db  = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from blacknumber order by _id desc", null); //降序查找
        while(cursor.moveToNext()){
            BlackNumberInfo info = new BlackNumberInfo();
            info.setNumber(cursor.getString(1));
            info.setMode(cursor.getString(2));
            infos.add(info);
        }
        cursor.close();
        db.close();
        return infos;
    }
    /**
     * 查找部分黑名单号码
     *
     * @param offset    从哪个位置开始获取数据
     * @param maxnumber 一次最多获取多少条记录
     */
    public List<BlackNumberInfo> findPart(int offset, int maxnumber) {

        List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from blacknumber order by _id desc limit ? offset ? ", new String[]{String.valueOf(maxnumber),String.valueOf(offset)}); //降序查找 limit offset 只能在末尾
        while (cursor.moveToNext()) {
            BlackNumberInfo info = new BlackNumberInfo();
            info.setNumber(cursor.getString(1));
            info.setMode(cursor.getString(2));
            infos.add(info);
        }
        cursor.close();
        db.close();
        return infos;
    }
}
