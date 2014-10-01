package com.pixel.singletune.app.fragments;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.pixel.singletune.app.ParseConstants;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.SingleTuneApplication;
import com.pixel.singletune.app.adapters.TuneAdapter;
import com.pixel.singletune.app.helpers.TuneDbHelper;
import com.pixel.singletune.app.interfaces.OnFragmentInteractionListener;
import com.pixel.singletune.app.models.Tune;
import com.pixel.singletune.app.subClasses.Tunes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TimelineFragment extends BaseListFragment implements LoaderManager.LoaderCallbacks<Object> {

    protected SwipeRefreshLayout.OnRefreshListener OnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getTunes();
            Log.i(TAG, "Has refreshed");
        }
    };
    private JSONArray tuneArray = new JSONArray();
    private OnFragmentInteractionListener mListener;
    private TuneAdapter adapter;
    private List<Tune> mTuneList;
    private ParseUser mCurrentUser;
    private List<ParseUser> mFriends;
    private ParseRelation mFriendRelation;
    private String[] tuneQueryIdList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentUser = ParseUser.getCurrentUser();
        getTunes();
    }

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

        // Get tunes
        tuneContentQuery();

        ArrayList<Tune> t = Tune.fromJson(tuneArray);

        adapter = new TuneAdapter(getActivity().getApplicationContext(), t);
        setListAdapter(adapter);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public Loader<Object> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> objectLoader, Object o) {

    }

    @Override
    public void onLoaderReset(Loader<Object> objectLoader) {

    }

    private void getTunes() {
        long mTuneCount = Tune.count(Tune.class, null, null);
        String tuneCreatedAt = null;

        if (mTuneCount > 0){

            List<Tune> l = Tune.findWithQuery(Tune.class, "select * from Tune order by id desc limit ?", "1");

            for (Tune lT : l){
                tuneCreatedAt = lT.getCreatedAt();
            }

            selectFriends();

            ParseQuery<Tunes> query = ParseQuery.getQuery(Tunes.class);
            query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
            query.whereGreaterThan("createdAt", tuneCreatedAt);
            try {
                query.whereContainedIn("artisteId", Arrays.asList(tuneQueryIdList));
            } catch (Exception e) {
                e.printStackTrace();
            }
            query.include("parent");
            query.findInBackground(new FindCallback<Tunes>() {
                @Override
                public void done(List<Tunes> t, ParseException e) {
                    if(mSwipeRefreshLayout.isRefreshing()){
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    if (e == null){
                        for (Tunes tune : t){
                            Tune mTunes = new Tune(
                                    tune.getTitle(),
                                    tune.getObjectId(),
                                    tune.getSongFile().getUrl(),
                                    tune.getCoverArt().getUrl(),
                                    tune.getArtist().getUsername(),
                                    tune.getArtist().getObjectId(),
                                    tune.getCreatedAt().toString()
                            );

                            mTunes.save();
                        }
                    }
                }
            });

        }else {
            ParseQuery<Tunes> query = ParseQuery.getQuery(Tunes.class);
            query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
            try {
                query.whereContainedIn("artisteId", Arrays.asList(tuneQueryIdList));
            } catch (Exception e) {
                e.printStackTrace();
            }
            query.include("parent");
            query.findInBackground(new FindCallback<Tunes>() {
                @Override
                public void done(List<Tunes> t, ParseException e) {
                    if(mSwipeRefreshLayout.isRefreshing()){
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    if (e == null){
                        for (Tunes tune : t){
                            Tune mTunes = new Tune(
                                    tune.getTitle(),
                                    tune.getObjectId(),
                                    tune.getSongFile().getUrl(),
                                    tune.getCoverArt().getUrl(),
                                    tune.getArtist().getUsername(),
                                    tune.getArtist().getObjectId(),
                                    tune.getCreatedAt().toString()
                            );

                            mTunes.save();
                        }
                    }
                }
            });
        }
    }

    private void selectFriends() {
        mFriendRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
        mFriendRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                mFriends = list;

                // Add current user to Friends list

                mFriends.add(mCurrentUser);

                int i = 0;

                tuneQueryIdList = new String[mFriends.size()];

                for (ParseUser user : mFriends){
                    tuneQueryIdList[i] = user.getObjectId();
                    i++;
                }

            }
        });

    }

    private void tuneContentQuery() {
        mTuneList = Tune.listAll(Tune.class);

        tuneArray = new JSONArray();

        for (Tune t : mTuneList){
            JSONObject tune = new JSONObject();
            String tID = t.getTuneObjectId();
            String tuneTitle = t.getTitle();
            String tuneAudioUrl = t.getTuneAudioUrl();
            String tuneArtUrl = t.getTuneArtUrl();
            String artisteObjId = t.getArtisteObjectId();
            String artisteName = t.getArtisteName();
            String createdAt = t.getCreatedAt();

            try {
                tune.put(TuneDbHelper.TUNE_OBJECT_ID, tID);
                tune.put(TuneDbHelper.TUNE_TITLE, tuneTitle);
                tune.put(TuneDbHelper.TUNE_AUDIO_URL, tuneAudioUrl);
                tune.put(TuneDbHelper.TUNE_ART_URL, tuneArtUrl);
                tune.put(TuneDbHelper.ARTISTE_OBJECT_ID, artisteObjId);
                tune.put(TuneDbHelper.ARTISTE_NAME, artisteName);
                tune.put(TuneDbHelper.CREATED_AT, createdAt);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            tuneArray.put(tune);

        }
    }
}
