package com.pixel.singletune.app.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.services.PlaySongService;
import com.pixel.singletune.app.subClasses.Tunes;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mrsmith on 5/29/14.
 */
public class TuneAdapter extends ArrayAdapter<Tunes> {

    protected Context mContext;
    protected List<Tunes> mTunes;
    protected boolean isMusicPlaying = false;
    protected boolean isOnline;
    protected boolean isLiked = false;

    public TuneAdapter(Context context, List<Tunes> tunes) {
        super(context, R.layout.tune_item, tunes);
        mContext = context;
        mTunes = tunes;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.tune_item, null);
            holder = new ViewHolder();
            holder.tuneArt = (ImageView) convertView.findViewById(R.id.tuneImage);
            holder.artist = (TextView) convertView.findViewById(R.id.tuneListViewArtist);
            holder.title = (TextView) convertView.findViewById(R.id.tuneListViewTitle);
            holder.btnPlay = (ImageButton) convertView.findViewById(R.id.btnPlay);
            holder.like_count = (TextView) convertView.findViewById(R.id.tuneLikeCountTextView);
            holder.comment_count = (TextView) convertView.findViewById(R.id.tuneCommentCountTextView);
            holder.download_count = (TextView) convertView.findViewById(R.id.tuneDownloadCountTextView);
            holder.ic_tune_like = (ImageView) convertView.findViewById(R.id.ic_tune_like);
            holder.like_text = (TextView) convertView.findViewById(R.id.TuneLikeTextView);
            holder.tune_progress =(ProgressBar) convertView.findViewById(R.id.tuneBufferProgress);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Get current tune

        final Tunes tunes = mTunes.get(position);
        String username = tunes.getArtist().getUsername();

        try {
            String tuneArtURL = tunes.getCoverArt().getUrl();
            if (tuneArtURL.isEmpty()) {
                holder.tuneArt.setImageResource(R.drawable.tune_placeholder);
            } else {
                Picasso.with(mContext).load(tuneArtURL).placeholder(R.drawable.tune_placeholder).into(holder.tuneArt);
            }
        } catch (Exception e) {
            holder.tuneArt.setImageResource(R.drawable.tune_placeholder);
        }

        final String tuneURL = tunes.getSongFile().getUrl();
        holder.title.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Aaargh.ttf"));
        holder.artist.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Aaargh.ttf"));

        holder.title.setText(tunes.getTitle());
        holder.artist.setText(username);

        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                 *   Creates a new Intent to start IntentService
                 *
                 */

                if (!isMusicPlaying) {
                    holder.btnPlay.setImageResource(R.drawable.btn_pause);
                    isMusicPlaying = true;
                    checkConnectivity();
                    if (isOnline) {
                        stopMusic();
                        playMusic(tuneURL, position);
                    } else {
                        ab(holder);
                    }

                } else {
                    isMusicPlaying = false;
                    holder.btnPlay.setImageResource(R.drawable.btn_play);
                    stopMusic();
                }
            }
        });

        /*
         *  Tune meta area
         */

        // Get like

        getLike(holder, tunes);

        // Get like count
        getLikeCount(holder, tunes);

        // Like action

        likeAction(holder, tunes);

        return convertView;
    }

    private void likeAction(final ViewHolder holder, final Tunes tunes) {
        holder.like_text.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.tune_progress.setVisibility(View.VISIBLE);
                if (!isLiked){
                    tunes.increaseLike(1);
                    tunes.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            holder.tune_progress.setVisibility(View.INVISIBLE);
                            isLiked = true;
                            holder.ic_tune_like.setImageResource(R.drawable.ic_tune_meta_like_pressed);
                            getLikeCount(holder, tunes);
                        }
                    });
                }
                else {
                    tunes.reduceLike(1);
                    tunes.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            holder.tune_progress.setVisibility(View.INVISIBLE);
                            isLiked = false;
                            holder.ic_tune_like.setImageResource(R.drawable.ic_tune_meta_like);
                            getLikeCount(holder, tunes);
                        }
                    });
                }
            }
        });
    }

    private void getLikeCount(final ViewHolder holder, Tunes tunes) {
        ParseQuery<Tunes> cQuery = ParseQuery.getQuery(Tunes.class);
        cQuery.whereEqualTo("objectId", tunes.getObjectId().toString());
        cQuery.getFirstInBackground( new GetCallback<Tunes>() {
            @Override
            public void done(Tunes tunes, ParseException e) {
                if (e == null){
                    try {
                        holder.like_count.setText(String.valueOf(tunes.getLikeCount()));
                    }
                    catch (Exception ce){
                        ce.printStackTrace();
                        holder.like_count.setText("0");
                    }
                }
            }
        });
    }

    private void ab(ViewHolder holder) {
        AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
        ad.setMessage(R.string.not_connected_error_message)
                .setTitle(R.string.not_connected_error_title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = ad.create();
        holder.btnPlay.setImageResource(R.drawable.btn_play);
        dialog.show();
    }

    private void getLike(final ViewHolder holder, Tunes tunes) {
        ParseQuery<ParseObject> lQuery = ParseQuery.getQuery("Activities");
        lQuery.whereEqualTo("from", ParseUser.getCurrentUser().getObjectId());
        lQuery.whereEqualTo("to", tunes.getArtist().getObjectId());
        lQuery.whereEqualTo("activity", "like");
        lQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject lObject, ParseException e) {
                if (e == null) {
                    // record found
                    isLiked = true;
                    holder.ic_tune_like.setImageResource(R.drawable.ic_tune_meta_like_pressed);
                }
            }
        });
    }

    private void playMusic(String tuneURL, int p) {
        Intent mServiceIntent = new Intent(getContext(), PlaySongService.class);
        mServiceIntent.putExtra("tuneURL", tuneURL);
        mServiceIntent.putExtra("tunePos", p);
        getContext().startService(mServiceIntent);
    }

    private void stopMusic() {
        Intent mServiceIntent = new Intent(getContext(), PlaySongService.class);
        getContext().stopService(mServiceIntent);
    }

    private void checkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting() || cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting()) {
            isOnline = true;
        } else {
            isOnline = false;
        }
    }

    public static class ViewHolder {
        ImageView tuneArt;
        TextView title;
        TextView artist;
        ImageButton btnPlay;
        TextView like_count;
        TextView like_text;
        TextView comment_count;
        TextView download_count;
        ImageView ic_tune_like;
        ProgressBar tune_progress;
    }
}
