package com.example.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by abc on 2016/2/1.
 */
public abstract class BaseSetupActivity extends Activity {
    //定义一个手势识别器
    private GestureDetector detector;

    protected SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp=getSharedPreferences("config",MODE_PRIVATE);
        detector =new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                //屏蔽x划的很慢的情况
                if(Math.abs(velocityX)<200){
                    Toast.makeText(getApplicationContext(), "滑动得太慢了", Toast.LENGTH_SHORT).show();
                    return true;
                }
                //屏蔽斜着划
                if(Math.abs(e1.getY()-e2.getY())>100){
                    Toast.makeText(getApplicationContext(), "不能这么滑", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if(e2.getX()-e1.getX()>200){
                    //向左滑
                    showPre();
                    return true;
                }
                if (e1.getX()-e2.getX()>200){
                    //向右滑
                    showNext();
                    return true;
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });


    }

    public abstract void showNext();

    public  abstract void showPre() ;


    /**
     * 下一步的点击事件
     * @param view
     */
    public void next(View view){
        showNext();

    }

    /**
     *   上一步
     * @param view
     */
    public void pre(View view){
        showPre();

    }

    //3.使用手势识别器
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}

