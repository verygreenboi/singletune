package com.pixel.singletune.app.utils;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by mrsmith on 7/31/14.
 * To determine whether phone or tablet.
 */
public class UIUtils{

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}
