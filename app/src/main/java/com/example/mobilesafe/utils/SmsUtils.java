package com.example.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by abc on 2016/2/13.
 * 短信工具类
 *
 */
public class SmsUtils {
    /**
     * 短信的备份
     * @param context  上下文
     */
    public static void backupSms(Context context) throws IOException {
        //获取内容提供者
        ContentResolver resolver = context.getContentResolver();
        File file = new File(Environment.getExternalStorageDirectory(),"backup.xml");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        //把用户短信一条一条读出来，按照一定格式写到文件
        XmlSerializer serializer= Xml.newSerializer(); //拿到xml的序列化器 (生成器)
        //初始化生成器
        serializer.setOutput(fileOutputStream,"utf-8"); //初始化生成器
        serializer.startDocument("utf-8", true);
        //写根节点
        serializer.startTag(null,"smss");
        //获取每一条短信
        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = resolver.query(uri, new String[] { "body", "address",
                "type", "date" }, null, null, null);
        while (cursor.moveToNext()){
            String body = cursor.getString(0);
            String address = cursor.getString(1);
            String type = cursor.getString(2);
            String date = cursor.getString(3);
            serializer.startTag(null, "sms");
             serializer.startTag(null,"body");
             serializer.text(body);
             serializer.endTag(null, "body");

            serializer.startTag(null,"address");
            serializer.text(address);
            serializer.endTag(null,"address");

            serializer.startTag(null,"type");
            serializer.text(type);
            serializer.endTag(null,"type");

            serializer.startTag(null,"date");
            serializer.text(date);
            serializer.endTag(null,"date");

            serializer.endTag(null,"sms");
        }
        cursor.close();
        serializer.endTag(null,"smss");
        serializer.endDocument();
        fileOutputStream.close();


    }
}
