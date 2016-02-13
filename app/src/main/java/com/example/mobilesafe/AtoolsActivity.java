package com.example.mobilesafe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mobilesafe.utils.SmsUtils;

/**
 * Created by abc on 2016/2/6.
 */
public class AtoolsActivity extends Activity {
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);

    }

    /**
     * 点击事件进入号码归属地查询
     *
     * @param view
     */
    public void numberQuery(View view) {
        Intent intent = new Intent(this, NumberAddressQueryActivity.class);
        startActivity(intent);
    }

    /**
     * 短信备份
     */
    public void smsBackup(View view) {
        pd= new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在备份短信");
        pd.show();


        //当短信过多的时候不能写在主线程，应该写在子线程好
        new Thread() {
            @Override
            public void run() {

                try {
                    SmsUtils.backupSms(AtoolsActivity.this, new SmsUtils.BackupCallBack() {
                        @Override
                        public void beforeBackup(int max) {
                             pd.setMax(max);
                        }

                        @Override
                        public void setProgress(int progress) {
                              pd.setProgress(progress);
                        }
                    });
                    //不能在子线程直接更新ui ,利用runOnUiThread更新
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AtoolsActivity.this, "备份成功", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AtoolsActivity.this, "备份失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                }finally {
                    pd.dismiss();
                }
            }

            ;
        }.start();
    }

    /**
     * 短信还原
     */
    public void smsRestore(View view) {
        pd= new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在还原短信");
        pd.show();


        //当短信过多的时候不能写在主线程，应该写在子线程好
        new Thread() {
            @Override
            public void run() {

                try {
                    SmsUtils.restoreSms(AtoolsActivity.this,true ,new SmsUtils.BackupCallBack() {
                        @Override
                        public void beforeBackup(int max) {
                            pd.setMax(max);
                        }

                        @Override
                        public void setProgress(int progress) {
                            pd.setProgress(progress);
                        }
                    });
                    //不能在子线程直接更新ui ,利用runOnUiThread更新
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AtoolsActivity.this, "还原成功", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AtoolsActivity.this, "还原失败", Toast.LENGTH_SHORT).show();
                        }
                    });

                }finally {
                    pd.dismiss();
                }
            }
        }.start();
    }
}
