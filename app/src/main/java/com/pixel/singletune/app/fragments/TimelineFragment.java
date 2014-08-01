package com.pixel.singletune.app.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.pixel.singletune.app.ParseConstants;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.SingleTuneApplication;
import com.pixel.singletune.app.adapters.TuneAdapter;
import com.pixel.singletune.app.services.PlaySongService;
import com.pixel.singletune.app.subClasses.Tunes;
import com.pixel.singletune.app.ui.MainActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class TimelineFragment extends ListFragment {

    private static final String TAG = MainActivity.class.getSimpleName() + "-Timeline fragment";
    protected SwipeRefreshLayout.OnRefreshListener OnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getTunes();
            Log.i(TAG, "Has refreshed");
        }
    };
    private static final String PIN_LABEL = "tunes";
    protected List<Tunes> mTunes;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    // Progress dialog and bcast receiver
    boolean mBufferBroadcastIsRegistered;
    private BroadcastReceiver broadcastBufferReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            showPD(bufferIntent);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        if (!SingleTuneApplication.isTablet(getActivity().getApplicationContext())) {
            rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        } else {
            rootView = inflater.inflate(R.layout.fragment_timeline_tablet, container, false);
        }

        getActivity().setProgressBarIndeterminateVisibility(true);

        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(OnRefreshListener);
        mSwipeRefreshLayout.setColorScheme(
                R.color.swipeRefresh1,
                R.color.swipeRefresh2,
                R.color.swipeRefresh3,
                R.color.swipeRefresh4
        );


        Log.i(TAG, "onCreateView");

        return rootView;
    }

    private void getTunes() {
        ParseQuery<Tunes> query = ParseQuery.getQuery(Tunes.class);
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.setMaxCacheAge(TimeUnit.HOURS.toHours(3));
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

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "onResume");

        getTunes();


        // Register BCASTreceiver

        if (!mBufferBroadcastIsRegistered) {
            getActivity().registerReceiver(broadcastBufferReceiver,
                    new IntentFilter(PlaySongService.BROADCAST_BUFFER));
            mBufferBroadcastIsRegistered = true;
        }
    }

    @Override
    public void onPause() {
        if (mBufferBroadcastIsRegistered) {
            getActivity().unregisterReceiver(broadcastBufferReceiver);
            mBufferBroadcastIsRegistered = false;
        }

        Log.i(TAG, getString(R.string.onPause_tag));
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, getString(R.string.onDestroyView_tag));
    }

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



}
