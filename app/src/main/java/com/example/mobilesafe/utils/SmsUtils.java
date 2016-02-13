package com.example.mobilesafe.utils;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;


/**
 * Created by abc on 2016/2/13.
 * 短信工具类
 */
public class SmsUtils {
    /**
     * 备份的接口
     */
    public interface BackupCallBack {
        /**
         * 设置进度最大值
         *
         * @param max
         */
        public void beforeBackup(int max);

        /**
         * 设置进度
         *
         * @param progress
         */
        public void setProgress(int progress);
    }


    /**
     * 短信的备份
     *
     * @param context        上下文
     * @param backupCallBack 回调接口
     */
    public static void backupSms(Context context, BackupCallBack backupCallBack) throws Exception {
        //获取内容提供者
        ContentResolver resolver = context.getContentResolver();
        File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        //把用户短信一条一条读出来，按照一定格式写到文件
        XmlSerializer serializer = Xml.newSerializer(); //拿到xml的序列化器 (生成器)
        //初始化生成器
        serializer.setOutput(fileOutputStream, "utf-8"); //初始化生成器
        serializer.startDocument("utf-8", true);
        //写根节点
        serializer.startTag(null, "smss");
        //获取每一条短信
        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = resolver.query(uri, new String[]{"body", "address",
                "type", "date"}, null, null, null);
        //获取短信的总数
        int max = cursor.getCount();
        backupCallBack.beforeBackup(max);
        int process = 0;
        serializer.attribute(null, "max", max + "");
        while (cursor.moveToNext()) {
            Thread.sleep(500);
            String body = cursor.getString(0);
            String address = cursor.getString(1);
            String type = cursor.getString(2);
            String date = cursor.getString(3);
            serializer.startTag(null, "sms");
            serializer.startTag(null, "body");
            serializer.text(body);
            serializer.endTag(null, "body");

            serializer.startTag(null, "address");
            serializer.text(address);
            serializer.endTag(null, "address");

            serializer.startTag(null, "type");
            serializer.text(type);
            serializer.endTag(null, "type");

            serializer.startTag(null, "date");
            serializer.text(date);
            serializer.endTag(null, "date");

            serializer.endTag(null, "sms");
            //备份过程中增加进度
            process++;
            backupCallBack.setProgress(process);
        }
        cursor.close();
        serializer.endTag(null, "smss");
        serializer.endDocument();
        fileOutputStream.close();


    }

    /**
     * 还原短信
     *
     * @param context
     * @param flag    是否清理原来短信
     */
    public static void restoreSms(Context context, boolean flag, BackupCallBack backupCallBack) throws Exception {
        if (flag) {
            Uri uri = Uri.parse("content://sms/");
            ContentResolver resolver = context.getContentResolver();
            resolver.delete(uri, null, null);
        }
        //1.读取sd卡上的xml文件
        File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
        InputStream is = new FileInputStream(file);
        XmlPullParser xmlPullParser = Xml.newPullParser();
        xmlPullParser.setInput(is, "utf-8");


        //3.读取每一条短信
        int process = 0;
        int eventType = xmlPullParser.getEventType();
        ContentValues contentValues = new ContentValues();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:


                    break;
                case XmlPullParser.START_TAG:
                    if ("smss".equals(xmlPullParser.getName())) {
                        //2.读取max

                        String max = xmlPullParser.getAttributeValue(0);
                        Log.d("Tga", max);
                        backupCallBack.beforeBackup(Integer.valueOf(max));
                    }
                    if ("body".equals(xmlPullParser.getName())) {
                        String body = xmlPullParser.nextText();
                        contentValues.put("body", body);
                    }
                    if ("address".equals(xmlPullParser.getName())) {
                        String address = xmlPullParser.nextText();
                        contentValues.put("address", address);

                    }
                    if ("type".equals(xmlPullParser.getName())) {
                        String type = xmlPullParser.nextText();
                        contentValues.put("type", type);
                    }
                    if ("date".equals(xmlPullParser.getName())) {
                        String date = xmlPullParser.nextText();
                        contentValues.put("date", date);
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if ("sms".equals(xmlPullParser.getName())) {
                        //4.把短信添加到数据库
                        Uri uri = Uri.parse("content://sms/");
                        context.getContentResolver().insert(uri, contentValues);
                        process++;
                        backupCallBack.setProgress(process);
                        contentValues = new ContentValues();
                    }
                    break;

            }

            eventType = xmlPullParser.next();
        }


    }
}
