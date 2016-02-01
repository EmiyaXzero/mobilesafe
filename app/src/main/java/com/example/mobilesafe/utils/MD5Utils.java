package com.example.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密方法
 * Created by abc on 2016/2/1.
 */
public class MD5Utils {
    public static String md5Password(String password){
        try {
            MessageDigest digest =MessageDigest.getInstance("md5");
            byte[] result = digest.digest(password.getBytes());
            StringBuffer  buffer = new StringBuffer();
            //把每一个byte做一个与运算0xff
            for(byte b:result){
                int number = b&0xff;
                String str=Integer.toHexString(number);//转换成16进制
                if(str.length()==1){
                    buffer.append("0");
                }
                buffer.append(str);
            }
            //标准md5加密后的结果
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "" ;
        }

    }
}
