package com.example.mobilesafe.com.example.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobilesafe.R;

/**
 * 自定义组合控件，里面有两个Textview 一个CheckBox
 * Created by abc on 2016/1/30.
 */
public class SettingItemView extends RelativeLayout {
    private CheckBox checkBox;
    private TextView tv_desc;
    private TextView tv_title;

    public SettingItemView(Context context) {
        super(context);
        initView(context);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化布局文件
     */
    private void initView(Context context) {
        //把一个布局文件 变成View
        View.inflate(context, R.layout.setting_item_view,SettingItemView.this);
        checkBox= (CheckBox) this.findViewById(R.id.checkbox);
        tv_desc= (TextView) this.findViewById(R.id.tv_desc);
        tv_title=(TextView)this.findViewById(R.id.tv_title);
    }
    /**
     * 校验组合控件是否选中
     */
    public boolean isChecked(){
        return checkBox.isChecked();
    }
    /**
     * 设置组合控件的状态
     */
    public void setChecked(boolean checked){
        checkBox.setChecked(checked);
    }

    /**
     * 设置组合控件的描述信息
     */
    public void SetDec(String dec){
         tv_desc.setText(dec);
    }

}
