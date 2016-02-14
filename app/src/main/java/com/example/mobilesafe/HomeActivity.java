package com.example.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilesafe.Service.GPSService;
import com.example.mobilesafe.utils.MD5Utils;

import net.tsz.afinal.utils.Utils;

import org.w3c.dom.Text;

/**
 * Created by abc on 2016/1/30.
 */
public class HomeActivity extends Activity {
    private GridView list_home;
    //适配器
    private MyAdapter adapter;
    //初始化功能列表名称
    private SharedPreferences sp;
    private static String [] names = {
            "手机防盗","通讯卫士","软件管理",
            "进程管理","流量统计","手机杀毒",
            "缓存清理","高级工具","设置中心"

    };

    //初始化图片id
    private static int[] ids = {
            R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,
            R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,
            R.drawable.sysoptimize,R.drawable.atools,R.drawable.settings

    };

    private EditText set_pass;
    private EditText pass_confirm;
    private Button ok;
    private Button cancel;
    private AlertDialog dialog;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sp=getSharedPreferences("config",MODE_PRIVATE);
        list_home=(GridView)findViewById(R.id.list_home);
        adapter = new MyAdapter();
        list_home.setAdapter(adapter);            //Adapter连接后台和前段的适配器
        list_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        //进入手机防盗页面
                        showLostFindDialog();
                        break;
                    case 1:
                        //进入黑名单拦截界面
                        intent = new Intent(HomeActivity.this,CallSmsSafeActivity.class);
                        startActivity(intent);
                        break;
                    case 2 :
                        //进入软件管理界面
                        intent = new Intent(HomeActivity.this,AppManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 7:
                        //进入高级工具页面
                        intent = new Intent(HomeActivity.this,AtoolsActivity.class);
                        startActivity(intent);
                        break;
                    case 8:
                        //进入设置中心
                        intent = new Intent(HomeActivity.this,SettingActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }
        //显示密码对话框
    private void showLostFindDialog() {
        //判断是否设置过密码
        if (isSetupPass()){
            //已经设置密码了 ,弹出输入对话框
            showEnterDialog();
        }else {
            //没有设置密码 ,弹出设置密码对话框
            showSetDialog();
        }

    }

    /**
     * 设置密码对话框
     */
    private void showSetDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //自定义布局文件
        View view = View.inflate(HomeActivity.this,R.layout.dialog_setup_password,null);
        set_pass= (EditText) view.findViewById(R.id.et_setup_pwd);
        pass_confirm= (EditText) view.findViewById(R.id.et_setup_confirm);
        ok= (Button) view.findViewById(R.id.ok);
        cancel= (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = set_pass.getText().toString().trim();//去掉空格
                String pass_cofirm=pass_confirm.getText().toString().trim();//去掉空格
                if(TextUtils.isEmpty(pass)||TextUtils.isEmpty(pass_cofirm)){
                    Toast.makeText(HomeActivity.this,"你的密码或确认密码为空",Toast.LENGTH_SHORT).show();
                }
                if(pass.equals(pass_cofirm)){
                    //一致保存密码，并把对话框消掉，并进入防盗页面
                   SharedPreferences.Editor editor= sp.edit();
                    editor.putString("password", MD5Utils.md5Password(pass));
                    editor.commit();
                    dialog.dismiss();
                }else {
                    Toast.makeText(HomeActivity.this,"你的密码不一致",Toast.LENGTH_SHORT).show();
                    return;

                }
            }
        });
        builder.setView(view);
        dialog=builder.show(); //添加AlterDialog 试能够取消


    }

    /**
     * 设置输入密码对话框
     */
    private void showEnterDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //自定义布局文件
        View view = View.inflate(HomeActivity.this,R.layout.dialog_enter_password,null);
        set_pass= (EditText) view.findViewById(R.id.et_setup_pwd);
        ok= (Button) view.findViewById(R.id.ok);
        cancel= (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = set_pass.getText().toString().trim();//去掉空格
                String pass_true=sp.getString("password",null);
               // Log.d("True",pass_true);
                if(TextUtils.isEmpty(pass)){
                    Toast.makeText(HomeActivity.this,"你的密码为空！",Toast.LENGTH_SHORT).show();

                }
                if(MD5Utils.md5Password(pass).equals(pass_true)){
                 //   Log.d("True","进入防盗界面");
                    dialog.dismiss();
                    Intent intent =new Intent(HomeActivity.this,LostFindActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(HomeActivity.this,"你的密码错误！",Toast.LENGTH_SHORT).show();
                    set_pass.setText("");
                }
            }
        });
        builder.setView(view);
        dialog=builder.show(); //添加AlterDialog 试能够取消
    }

    private boolean isSetupPass(){
        //判断是否有密码
     String password = sp.getString("password",null); //第二个为得到的默认值
     return !TextUtils.isEmpty(password);
    }

    private  class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public Object getItem(int position) {
            return names[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           View view= View.inflate(HomeActivity.this,R.layout.list_item_home,null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_item);
            TextView tv = (TextView) view.findViewById(R.id.tv_item);
            tv.setText(names[position]);
            iv.setImageResource(ids[position]);
            return view;
        }
    }
}
