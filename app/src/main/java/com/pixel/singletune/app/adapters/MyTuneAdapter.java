package com.pixel.singletune.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pixel.singletune.app.R;
import com.pixel.singletune.app.helpers.TuneDbHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by mrsmith on 9/2/14.
 */
public class MyTuneAdapter extends CursorAdapter {


    protected String mTitle;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private Target target = new Target() {

        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + mTitle + ".jpg");
                    try {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
                        ostream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            if (placeHolderDrawable != null) {
            }
        }

    };

    public MyTuneAdapter(Context ctx, Cursor c){
        super(ctx, c);
        mContext = ctx;
        mLayoutInflater = LayoutInflater.from(ctx);
    }

    @Override
    public View newView(Context mContext, Cursor cursor, ViewGroup viewGroup) {
        View v = mLayoutInflater.inflate(R.layout.tune_item, viewGroup, false);
        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c) {

        mContext = context;

        mTitle = c.getString(c.getColumnIndexOrThrow(TuneDbHelper.TUNE_TITLE));
        String artist = c.getString(c.getColumnIndexOrThrow(TuneDbHelper.ARTISTE_NAME));
        String lCount = c.getString(c.getColumnIndexOrThrow(TuneDbHelper.LIKE_COUNT));
        String cCount = c.getString(c.getColumnIndexOrThrow(TuneDbHelper.COMMENT_COUNT));
        String tArtPath = c.getString(c.getColumnIndexOrThrow(TuneDbHelper.TUNE_ART_URL));
        String tunePath = c.getString(c.getColumnIndexOrThrow(TuneDbHelper.TUNE_AUDIO_URL));

        TextView titleText = (TextView) v.findViewById(R.id.tuneListViewTitle);
        if (titleText != null)
            titleText.setText(mTitle);

        TextView artistName = (TextView) v.findViewById(R.id.tuneListViewArtist);
        if (artistName != null)
            artistName.setText(artist);

        TextView lCountView = (TextView) v.findViewById(R.id.tuneLikeCountTextView);
        if (lCountView != null)
            lCountView.setText(lCount);

        TextView commentCountView = (TextView) v.findViewById(R.id.tuneCommentCountTextView);
        if (commentCountView !=null)
            commentCountView.setText(cCount);

        ImageView tuneArt = (ImageView) v.findViewById(R.id.tuneImage);
        tuneArt.setVisibility(View.INVISIBLE);
        if (tArtPath != null && tArtPath.length() != 0 && tuneArt != null)
            Picasso.with(mContext)
                    .load(tArtPath)
                    .placeholder(R.drawable.default_avatar)
                    .into(tuneArt, new Callback.EmptyCallback(){
                        @Override public void onSuccess(){

                        }
                        @Override public void onError(){

                        }
                    });
            tuneArt.setVisibility(View.VISIBLE);
            Picasso.with(mContext).load(tArtPath).into(target);

    }
}
