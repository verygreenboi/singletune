package com.pixel.singletune.app.fragments;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
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
import com.pixel.singletune.app.ParseConstants;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.SingleTuneApplication;
import com.pixel.singletune.app.adapters.TuneAdapter;
import com.pixel.singletune.app.helpers.TuneDbHelper;
import com.pixel.singletune.app.interfaces.OnFragmentInteractionListener;
import com.pixel.singletune.app.models.Tune;
import com.pixel.singletune.app.providers.TuneContentProvider;
import com.pixel.singletune.app.subClasses.Tunes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class TimelineFragment extends BaseListFragment implements LoaderManager.LoaderCallbacks<Object> {



    protected SwipeRefreshLayout.OnRefreshListener OnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getTunes();
            Log.i(TAG, "Has refreshed");
        }
    };
    List mTuneList = new ArrayList<String>();
    JSONArray tuneArray = new JSONArray();
    private OnFragmentInteractionListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        TuneAdapter adapter = new TuneAdapter(getActivity().getApplicationContext(), t);
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

                    ContentResolver cr = getActivity().getContentResolver();
                    for (Tunes tune : tunes){

                        ContentValues cv = new ContentValues(1);
                        cv.put(TuneDbHelper.TUNE_TITLE, tune.getTitle());
                        cv.put(TuneDbHelper.TUNE_OBJECT_ID, tune.getObjectId());
                        cv.put(TuneDbHelper.TUNE_AUDIO_URL, tune.getSongFile().getUrl());
                        cv.put(TuneDbHelper.TUNE_ART_URL, tune.getCoverArt().getUrl());
                        cv.put(TuneDbHelper.ARTISTE_OBJECT_ID, tune.getArtist().getObjectId());
                        cv.put(TuneDbHelper.ARTISTE_NAME, tune.getArtist().getUsername());
                        cv.put(TuneDbHelper.CREATED_AT, tune.getTuneCreatedAt().toString());

                        cr.insert(TuneContentProvider.TUNE_URI, cv);

                    }
                }
            }
        });
    }

    private void tuneContentQuery() {
        String[] colums = {
                TuneDbHelper.TUNE_OBJECT_ID,
                TuneDbHelper.TUNE_TITLE,
                TuneDbHelper.TUNE_AUDIO_URL,
                TuneDbHelper.TUNE_ART_URL,
                TuneDbHelper.ARTISTE_OBJECT_ID,
                TuneDbHelper.ARTISTE_NAME
        };

        Cursor c = getActivity().getContentResolver().query(
                TuneContentProvider.TUNE_URI,
                colums,
                null,
                null,
                null
        );

        c.moveToFirst();

        while (c.moveToNext()){
            JSONObject tune = new JSONObject();

            String tID = c.getString(c.getColumnIndex(TuneDbHelper.TUNE_OBJECT_ID));
            String tuneTitle = c.getString(c.getColumnIndex(TuneDbHelper.TUNE_TITLE));
            String tuneAudioUrl = c.getString(c.getColumnIndex(TuneDbHelper.TUNE_AUDIO_URL));
            String tuneArtUrl = c.getString(c.getColumnIndex(TuneDbHelper.TUNE_ART_URL));
            String artisteObjId = c.getString(c.getColumnIndex(TuneDbHelper.ARTISTE_OBJECT_ID));
            String artisteName = c.getString(c.getColumnIndex(TuneDbHelper.ARTISTE_NAME));

            try {
                tune.put(TuneDbHelper.TUNE_OBJECT_ID, tID);
                tune.put(TuneDbHelper.TUNE_TITLE, tuneTitle);
                tune.put(TuneDbHelper.TUNE_AUDIO_URL, tuneAudioUrl);
                tune.put(TuneDbHelper.TUNE_ART_URL, tuneArtUrl);
                tune.put(TuneDbHelper.ARTISTE_OBJECT_ID, artisteObjId);
                tune.put(TuneDbHelper.ARTISTE_NAME, artisteName);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            tuneArray.put(tune);
        }
        c.close();
    }
}
