package com.pixel.singletune.app.fragments;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.pixel.singletune.app.ParseConstants;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.SingleTuneApplication;
import com.pixel.singletune.app.services.PlaySongService;

import java.util.List;


public class TimelineFragment extends BaseListFragment {

    private String userid[];

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

    @Override
    protected void getTunes() {
        super.getTunes();
        mCurrentUser = ParseUser.getCurrentUser();
        mFollowersRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        mFollowersRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null){
                  for (int i = 0; i < users.size(); ++i){

                  }
                }
            }
        });
    }
}
