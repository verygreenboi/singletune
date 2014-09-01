package com.pixel.singletune.app.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.services.PlaySongService;
import com.pixel.singletune.app.subClasses.Tunes;
import com.pixel.singletune.app.ui.MainActivity;

import java.util.List;

/**
 * Created by mrsmith on 8/6/14.
 */
public abstract class BaseListFragment extends ListFragment{

    protected static final String TAG = MainActivity.class.getSimpleName() + "List fragment";

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

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "onResume");

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
