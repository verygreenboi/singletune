package com.pixel.singletune.app.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pixel.singletune.app.R;
import com.pixel.singletune.app.adapters.CustomTuneAdapter;
import com.pixel.singletune.app.helpers.TuneDbHelper;
import com.pixel.singletune.app.interfaces.OnFragmentInteractionListener;
import com.pixel.singletune.app.providers.TuneContentProvider;

/**
 * Created by mrsmith on 9/2/14.
 */
public class TunesFragment extends ListFragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private OnFragmentInteractionListener mListener;

    private CustomTuneAdapter adapter;

    //    Empty constructor

    public TunesFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new CustomTuneAdapter(getActivity(), R.layout.tune_list);
        setListAdapter(adapter);
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tune_list, container, false);
    }

    @Override
     public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (OnFragmentInteractionListener) activity;
        }
        catch (ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (null != mListener){
            mListener.onItemSelected(id);
        }
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        CursorLoader loader = new CursorLoader(
                getActivity(),
                TuneContentProvider.TUNE_URI,
                new String[]{TuneDbHelper.T_ID, TuneDbHelper.TUNE_TITLE},
                null,
                null,
                null
        );
        return loader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> cursorLoader, Cursor cursor) {

        adapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> cursorLoader) {

        adapter.swapCursor(null);

    }
}
