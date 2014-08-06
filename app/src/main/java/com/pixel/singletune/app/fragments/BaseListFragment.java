package com.pixel.singletune.app.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.pixel.singletune.app.ParseConstants;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.adapters.TuneAdapter;
import com.pixel.singletune.app.subClasses.Tunes;
import com.pixel.singletune.app.ui.MainActivity;

import java.util.List;

/**
 * Created by mrsmith on 8/6/14.
 */
public abstract class BaseListFragment extends ListFragment{

    protected static final String TAG = MainActivity.class.getSimpleName() + "List fragment";

    protected SwipeRefreshLayout.OnRefreshListener OnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getTunes();
            Log.i(TAG, "Has refreshed");
        }
    };
    protected static final String PIN_LABEL = "tunes";
    protected ParseRelation<ParseUser> mFollowersRelation;
    protected ParseUser mCurrentUser;
    protected List<ParseUser> mFriends;
    protected List<Tunes> mTunes;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            showPD(bufferIntent);
        }
    };
    protected boolean isInCache;
    // Progress dialog and bcast receiver
    boolean mBufferBroadcastIsRegistered;

    private void showPD(Intent bufferIntent) {
        String bufferValue = bufferIntent.getStringExtra("Buffering");
        int tunePos = bufferIntent.getExtras().getInt("tunePos");
        int bufferIntValue = Integer.parseInt(bufferValue);

        ActivityCircle(tunePos, bufferIntValue);
    }

    private void ActivityCircle(int p, int actionCode) {
        // Wanted position
        // 1st position
        int fp = getListView().getFirstVisiblePosition() - getListView().getHeaderViewsCount();
        // Wanted child
        int wc = p - fp;
        if (wc < 0 || wc >= getListView().getChildCount()) {
            Log.w(TAG, getString(R.string.position_not_displayed_message));
        } else {
            ProgressBar pb;
            View wv = getListView().getChildAt(wc);
            pb = (ProgressBar) wv.findViewById(R.id.tuneBufferProgress);
            switch (actionCode) {
                case 0:
                    if (pb.getVisibility() == View.VISIBLE) {
                        pb.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 1:
                    pb.setVisibility(View.VISIBLE);
                    break;
            }
        }

    }

    protected void getTunes() {
        ParseQuery<Tunes> query = ParseQuery.getQuery(Tunes.class);
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        isInCache = query.hasCachedResult();
        if (isInCache)
            query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.include("parent");
        query.findInBackground(new FindCallback<Tunes>() {
            @Override
            public void done(List<Tunes> tunes, ParseException e) {
                if (mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                try {
                    getActivity().setProgressBarIndeterminateVisibility(false);
                } catch (Exception err) {
                    err.printStackTrace();
                }

                if (e == null) {
                    mTunes = tunes;
                    if (getListView().getAdapter() == null) {
                        TuneAdapter adapter = new TuneAdapter(getListView().getContext(), mTunes);
                        setListAdapter(adapter);
                    } else {
                        ((TuneAdapter)getListView().getAdapter()).refill(mTunes);
                    }
                }
            }
        });
    }
}
