package com.pixel.singletune.app.adapters;

/**
 * Created by smith on 3/30/14.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pixel.singletune.app.R;
import com.pixel.singletune.app.fragments.PopularFragment;
import com.pixel.singletune.app.fragments.TimelineFragment;

import java.util.Locale;

/**
 * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    protected Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        switch (position) {
            case 0:
                return new TimelineFragment();
            case 1:
                return new PopularFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        // TODO: Increase this to three later when you figure out how to do the trending page
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return mContext.getString(R.string.title_section1).toUpperCase(l);
            case 1:
                return mContext.getString(R.string.title_section2).toUpperCase(l);
//            case 2:
//                return mContext.getString(R.string.title_section3).toUpperCase(l);
        }
        return null;
    }
}