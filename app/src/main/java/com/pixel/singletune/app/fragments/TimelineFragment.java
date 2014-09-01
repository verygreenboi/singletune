package com.pixel.singletune.app.fragments;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.pixel.singletune.app.ParseConstants;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.SingleTuneApplication;
import com.pixel.singletune.app.adapters.TuneAdapter;
import com.pixel.singletune.app.subClasses.Tunes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class TimelineFragment extends BaseListFragment {

    protected SwipeRefreshLayout.OnRefreshListener OnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getTunes();
            Log.i(TAG, "Has refreshed");
        }
    };
    private String uid[];
    private ArrayList<String> mUids = new ArrayList<String>();

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
                if (mSwipeRefreshLayout.isRefreshing()){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                try {
                    getActivity().setProgressBarIndeterminateVisibility(false);
                } catch (Exception err) {
                    err.printStackTrace();
                }

                if (e == null){
                    mTunes = tunes;
                    TuneAdapter adapter = new TuneAdapter(getListView().getContext(), mTunes);
                    setListAdapter(adapter);
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        getTunes();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
