package com.pixel.singletune.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
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
import com.pixel.singletune.app.models.Tune;
import com.pixel.singletune.app.services.PlaySongService;
import com.pixel.singletune.app.subClasses.Tunes;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TuneAdapter extends ArrayAdapter<Tune> {

    protected boolean isMusicPlaying = false;
    protected boolean isLiked = false;
    protected Intent mCommentIntent;
    protected String mTitle;
    String mUsername;
    String mTuneURL;
    String mTuneArtUrl;
    String mTuneId;
    String mUserId;
    ParseUser mParseUser;
    private Tunes mTune;

    public TuneAdapter(Context c, ArrayList<Tune> tunes){
        super(c, 0 , tunes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tune t = getItem(position);
        final ViewHolder holder;
        if (convertView == null){

            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.tune_item, parent, false);

            holder.tuneArt = (ImageView) convertView.findViewById(R.id.tuneImage);
            holder.artist = (TextView) convertView.findViewById(R.id.tuneListViewArtist);
            holder.title = (TextView) convertView.findViewById(R.id.tuneListViewTitle);
            holder.btnPlay = (ImageButton) convertView.findViewById(R.id.btnPlay);
            holder.like_count = (TextView) convertView.findViewById(R.id.tuneLikeCountTextView);
            holder.comment_count = (TextView) convertView.findViewById(R.id.tuneCommentCountTextView);
            holder.commentLabel = (TextView) convertView.findViewById(R.id.TuneCommentTextView);
            holder.download_count = (TextView) convertView.findViewById(R.id.tuneDownloadCountTextView);
            holder.ic_tune_like = (ImageView) convertView.findViewById(R.id.ic_tune_like);
            holder.like_text = (TextView) convertView.findViewById(R.id.TuneLikeTextView);
            holder.tune_progress = (ProgressBar) convertView.findViewById(R.id.tuneBufferProgress);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        mTuneId = t.getTuneObjectId();
        mTitle = t.getTitle();
        mTuneURL = t.getTuneAudioUrl();
        mTuneArtUrl = t.getTuneArtUrl();
        mUserId = t.getArtisteObjectId();
        mUsername = t.getArtisteName();

        ParseQuery<Tunes> tQuery = ParseQuery.getQuery(Tunes.class);
        tQuery.whereEqualTo("objectId", mTuneId);
        tQuery.getFirstInBackground(new GetCallback<Tunes>() {
            @Override
            public void done(Tunes tunes, ParseException e) {
                if (e == null) {
                    mTune = tunes;
                }
            }
        });

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", mUserId);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null){
                    mParseUser = parseUser;
                }
            }
        });

        holder.title.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Aaargh.ttf"));
        holder.artist.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Aaargh.ttf"));

        holder.title.setText(mTitle);
        holder.artist.setText(mUsername);

        Picasso.with(getContext()).
                load(mTuneArtUrl).
                placeholder(R.drawable.default_avatar).
                into(holder.tuneArt, new Callback() {
                    @Override
                    public void onSuccess() {
                        // TODO: Implement progress bar
                    }

                    @Override
                    public void onError() {
                        // TODO: Implement progress bar
                    }
                });
//
        //PlayAction
//
        playAction(position, holder, mTuneURL);

        // Get like

        getLike(holder);

        // Get like count
        getLikeCount(holder, mTuneId);

        return convertView;

    }

//    public static final String COMMENT_BROADCAST = "com.pixel.singletune.app.COMMENT_BROADCAST";
//
//    private Target target = new Target(){
//
//        @Override
//        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//                    File file = new File(Environment.getExternalStorageDirectory().getPath() +"/"+mTitle+".jpg");
//                    try
//                    {
//                        file.createNewFile();
//                        FileOutputStream ostream = new FileOutputStream(file);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
//                        ostream.close();
//                    }
//                    catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }
//
//                }
//            }).start();
//        }
//
//        @Override
//        public void onBitmapFailed(Drawable errorDrawable) {
//
//        }
//
//        @Override
//        public void onPrepareLoad(Drawable placeHolderDrawable) {
//            if (placeHolderDrawable != null) {
//            }
//        }
//    };
//
//    private void commentAction() {
//    }
//
    private void playAction(final int position, final ViewHolder holder, final String tuneURL) {
        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isMusicPlaying) {
                    holder.btnPlay.setImageResource(R.drawable.btn_pause);
                    isMusicPlaying = true;
                    stopMusic();
                    playMusic(tuneURL, position);

                } else {
                    isMusicPlaying = false;
                    holder.btnPlay.setImageResource(R.drawable.btn_play);
                    stopMusic();
                }
            }
        });
    }
//
    private void likeAction(final  ViewHolder holder, final String tunes){
        holder.like_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tunes.equals(ParseUser.getCurrentUser().getObjectId())){
                    holder.tune_progress.setVisibility(View.VISIBLE);
                    if (!isLiked){
                        mTune.increaseLike(1);
                        mTune.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                isLiked = true;
                                //CreateActivity
                                createActivity();

//                                String message = "Your tune was liked by ";

//                                Notifyer nNotify = new Notifyer(ParseUser.getCurrentUser(), tunes, message, Notifyer.LIKE_NOTIFICATION_ACTION);
//                                nNotify.sendNotification();

                                holder.ic_tune_like.setImageResource(R.drawable.ic_tune_meta_like_pressed);
                                getLikeCount(holder, tunes);
                                holder.tune_progress.setVisibility(View.INVISIBLE);
                            }
                        });
                    } else {
                        mTune.reduceLike(1);
                        mTune.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                isLiked = false;
                                //deleteActivity
                                deleteActivity();
                                holder.ic_tune_like.setImageResource(R.drawable.ic_tune_meta_like);
                                getLikeCount(holder, tunes);
                                holder.tune_progress.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }
            }
        });
    }
//
    private void deleteActivity() {
        ParseQuery<ParseObject> getLike = ParseQuery.getQuery("Activities");
        getLike.whereEqualTo("from", ParseUser.getCurrentUser());
        getLike.whereEqualTo("tune", mTuneId);
        getLike.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject Obj, ParseException e) {
                try {
                    Obj.deleteInBackground();
                } catch (Exception et) {
                    Log.e("Error", "An error has occoured", et);
                }
            }
        });
    }

    private void createActivity() {
        ParseObject tLikeActivity = new ParseObject("Activities");
        tLikeActivity.put("from", ParseUser.getCurrentUser());
        tLikeActivity.put("to", mUserId);
        tLikeActivity.put("activity", "like");
        tLikeActivity.put("tune", mTuneId);
        tLikeActivity.saveInBackground();
    }

    private void getLikeCount(final ViewHolder holder, String tuneId) {
        ParseQuery<Tunes> cQuery = ParseQuery.getQuery(Tunes.class);
        cQuery.whereEqualTo("objectId", tuneId);
        cQuery.getFirstInBackground(new GetCallback<Tunes>() {
            @Override
            public void done(Tunes tunes, ParseException e) {
                if (e == null) {
                    try {
                        holder.like_count.setText(String.valueOf(tunes.getLikeCount()));
                    } catch (Exception ce) {
//                        ce.printStackTrace();
                        holder.like_count.setText("0");
                    }
                }
            }
        });
    }
//
//    private void ab(ViewHolder holder) {
//        AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
//        ad.setMessage(R.string.not_connected_error_message)
//                .setTitle(R.string.not_connected_error_title)
//                .setPositiveButton(android.R.string.ok, null);
//        AlertDialog dialog = ad.create();
//        holder.btnPlay.setImageResource(R.drawable.btn_play);
//        dialog.show();
//    }
//
    private void getLike(final ViewHolder holder) {
        ParseQuery<ParseObject> lQuery = ParseQuery.getQuery("Activities");
        lQuery.whereEqualTo("from", ParseUser.getCurrentUser());
        lQuery.whereEqualTo("to", mParseUser);
        lQuery.whereEqualTo("tune", mTuneId);
        lQuery.whereEqualTo("activity", "like");
        lQuery.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
        lQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject lObject, ParseException e) {
                if (e == null) {
                    // record found
                    isLiked = true;
                    holder.ic_tune_like.setImageResource(R.drawable.ic_tune_meta_like_pressed);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void playMusic(String tuneURL, int p) {
        Intent mServiceIntent = new Intent(getContext(), PlaySongService.class);
        mServiceIntent.putExtra("mTuneURL", tuneURL);
        mServiceIntent.putExtra("tunePos", p);
        getContext().startService(mServiceIntent);
    }

    private void stopMusic() {
        Intent mServiceIntent = new Intent(getContext(), PlaySongService.class);
        getContext().stopService(mServiceIntent);
    }

//    public void refill(ArrayList<Tune> tunes){
//        mTunes.clear();
//        mTunes.addAll(tunes);
//        notifyDataSetChanged();
//    }
//
    public static class ViewHolder {
        ImageView tuneArt;
        TextView title,
                artist,
                like_count,
                like_text,
                comment_count,
                commentLabel,
                download_count;
        ImageButton btnPlay;
        ImageView ic_tune_like;
        ProgressBar tune_progress;
    }

}
