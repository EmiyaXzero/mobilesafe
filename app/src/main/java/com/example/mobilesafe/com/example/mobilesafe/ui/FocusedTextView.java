package com.example.mobilesafe.com.example.mobilesafe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 自带焦点的textview
 * Created by abc on 2016/1/30.
 */
public class FocusedTextView extends TextView {
    public FocusedTextView(Context context) {
        super(context);
    }

    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 实质上并没有焦点，只是欺骗系统
     * @return
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
