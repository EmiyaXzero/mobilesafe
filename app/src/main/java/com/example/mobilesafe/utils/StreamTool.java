package com.example.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by abc on 2016/1/30.
 */
public class StreamTool {

    public static  StringBuilder readFromStream(InputStream is) throws IOException {
        BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();
        String line;
        if((line = bufferedReader.readLine())!=null){
            response.append(line);
        }
        return response;
    }
}
