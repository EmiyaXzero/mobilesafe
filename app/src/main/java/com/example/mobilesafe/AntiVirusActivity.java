package com.example.mobilesafe;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by abc on 2016/2/17.
 */
public class AntiVirusActivity extends Activity {
    private ImageView iv_scan;
    private ProgressBar progressBar1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);
        iv_scan= (ImageView) findViewById(R.id.iv_scan);
        RotateAnimation ra = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF
        ,0.5F);
        ra.setDuration(1000);
        ra.setRepeatCount(Animation.INFINITE);
        iv_scan.startAnimation(ra);

        progressBar1= (ProgressBar) findViewById(R.id.progressBar1);
        progressBar1.setMax(100);

        new Thread(){
            public void run(){
                for(int i =0;i<100;i++) {
                    try {
                        Thread.sleep(100);
                        progressBar1.setProgress(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }
}
