package com.pixel.singletune.app.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.pixel.singletune.app.helpers.CustomFontHelper;

/**
 * Created by mrsmith on 7/15/14.
 */
public class CustomTextView extends TextView {

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        CustomFontHelper.setCustomFont(this, context, attrs);
    }
}
