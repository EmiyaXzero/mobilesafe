package com.example.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobilesafe.R;

/**
 * 自定义组合控件，里面有两个Textview 一个ImageView
 * Created by abc on 2016/1/30.
 */
public class SettingClickView extends RelativeLayout {
    private ImageView imageView;
    private TextView tv_desc;
    private TextView tv_title;
    private String  desc_on;
    private String desc_off;

    public SettingClickView(Context context) {
        super(context);
        initView(context);
    }
    //两个参数的构造方法是在布局文件中实例化
    public SettingClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        String title = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","dtitle");
        desc_on= attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","desc_on");
        desc_off=attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","desc_off");
        tv_title.setText(title);
    }

    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化布局文件
     */
    private void initView(Context context) {
        //把一个布局文件 变成View
        View.inflate(context, R.layout.setting_click_view,SettingClickView.this);
        tv_desc= (TextView) this.findViewById(R.id.tv_desc);
        tv_title=(TextView)this.findViewById(R.id.tv_title);
    }
    /**
     * 设置组合控件的标题
     */
    public void SetTitle(String dec){
        tv_title.setText(dec);
    }


    /**
     * 设置组合控件的描述信息
     */
    public void SetDec(String dec){
         tv_desc.setText(dec);
    }

}
