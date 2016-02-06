package com.example.mobilesafe.db.dao;

import android.app.DownloadManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by abc on 2016/2/6.
 */
public class NumberAddressQueryUtils {

    private static String path = "data/data/com.example.mobilesafe/files/address.db";
    private static String address;

    /**
     * 传一个号码去数据库查询返回归属地
     *
     * @param number
     * @return
     */
    public static String queryNumber(String number) {
        //path   把address.db拷贝到data/data/《包》/files/address.db
        address = number;
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        //手机号码正则表达式
        if (number.matches("^1[3458]\\d{9}$")) {
            //是手机号

            Cursor cursor = sqLiteDatabase.rawQuery("select location from data2 where id=" +
                    "(select outkey from data1 where id =?)"
                    , new String[]{number.substring(0, 7)});
            while (cursor.moveToNext()) {
                String location = cursor.getString(0);
                address = location;
            }
            cursor.close();
        } else {
            //其他号码
            switch (number.length()) {
                case 3:
                    address = "特殊号码";
                    break;
                case 4:
                    address = "模拟器";
                    break;
                case 5:
                    address = "客服电话";
                    break;
                case 7:
                    address = "本地号码";
                    break;
                case 8:
                    address = "本地号码";
                    break;
                default:
                    //长途电话   大于十位
                    if (number.length() > 10 && number.startsWith("0")) {
                        //010-
                        if (number.length() == 11) {
                            Cursor cursor = sqLiteDatabase.rawQuery("select location from data2 where area=?", new String[]{number.substring(1, 3)});
                            while (cursor.moveToNext()) {
                                String location = cursor.getString(0);
                                address = location.substring(0, location.length() - 2);
                            }
                            cursor.close();
                        } else {
                            Cursor cursor = sqLiteDatabase.rawQuery("select location from data2 where area=?", new String[]{number.substring(1, 4)});
                            while (cursor.moveToNext()) {
                                String location = cursor.getString(0);
                                address = location.substring(0, location.length() - 2);
                            }
                            cursor.close();
                        }
                    }
                    break;
            }

        }
        return address;
    }
}
