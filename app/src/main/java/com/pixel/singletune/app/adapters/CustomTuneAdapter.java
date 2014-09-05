package com.pixel.singletune.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.pixel.singletune.app.R;
import com.pixel.singletune.app.helpers.TuneDbHelper;
import com.pixel.singletune.app.models.Tune;
import com.squareup.picasso.Picasso;

/**
 * Created by mrsmith on 9/2/14.
 * Custom Simple cursor adapter                                                                                                                                                                                                                                                                                                                                     
 */
public class CustomTuneAdapter extends SimpleCursorAdapter {

    public CustomTuneAdapter(Context context, int layout) {
        super(context,
                layout,
                null,
                new String[]{
                        TuneDbHelper.TUNE_TITLE,
                        TuneDbHelper.ARTISTE_NAME,
                        TuneDbHelper.TUNE_ART_URL

                },
                new int[]{
                        android.R.id.text1
                },
                0
        );
    }

    @Override
    public void bindView(@NonNull View view, Context context, @NonNull Cursor cursor) {
        String content = cursor.getString(cursor.getColumnIndex(TuneDbHelper.TUNE_TABLE));
        Tune tune = new Tune(content);

        String title = tune.getTitle();
        String artiste = tune.getArtisteName();
        String tuneArt = tune.getTuneArtUrl();

        TextView titleText = (TextView) view.findViewById(R.id.tuneListViewTitle);
        titleText.setText(title);

        TextView artisteNameText = (TextView) view.findViewById(R.id.tuneListViewArtist);
        artisteNameText.setText(artiste);

        ImageView tuneImage = (ImageView) view.findViewById(R.id.tuneImage);

        Picasso.with(context).load(tuneArt).placeholder(R.drawable.default_avatar).into(tuneImage);

    }
}
