package com.pixel.singletune.app.ui;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by mrsmith on 8/5/14.
 */
public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(final int layoutResID) {
        super.setContentView(layoutResID);
    }
}
