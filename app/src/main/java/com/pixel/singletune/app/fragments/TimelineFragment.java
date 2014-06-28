package com.pixel.singletune.app.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.pixel.singletune.app.ParseConstants;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.adapters.TuneAdapter;
import com.pixel.singletune.app.subClasses.Tunes;

import java.util.List;


public class TimelineFragment extends ListFragment {

    protected List<Tunes> mTunes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline, container, false);
        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().setProgressBarIndeterminateVisibility(true);

        ParseQuery<Tunes> query = ParseQuery.getQuery(Tunes.class);
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.include("parent");
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        query.findInBackground(new FindCallback<Tunes>() {
            @Override
            public void done(List<Tunes> tunes, ParseException e) {
                getActivity().setProgressBarIndeterminateVisibility(false);

                if (e == null){
                    mTunes = tunes;
                    String[] usernames = new String[mTunes.size()];

                    int i = 0;
                    for (Tunes tune : mTunes){
                        usernames[i] = tune.getString(ParseConstants.KEY_USER_ID);
                    }
                    TuneAdapter adapter = new TuneAdapter(getListView().getContext(), mTunes);
                    setListAdapter(adapter);
                }
            }
        });
    }

}
