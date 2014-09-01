package com.pixel.singletune.app.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Environment;
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
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pixel.singletune.app.ParseConstants;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.services.PlaySongService;
import com.pixel.singletune.app.subClasses.Activities;
import com.pixel.singletune.app.subClasses.Tunes;
import com.pixel.singletune.app.utils.Notifyer;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class TuneAdapter extends ArrayAdapter<Tunes> {

    public static final String COMMENT_BROADCAST = "com.pixel.singletune.app.COMMENT_BROADCAST";
    protected Context mContext;
    protected List<Tunes> mTunes;
    protected boolean isMusicPlaying = false;
    protected boolean isOnline;
    protected boolean isLiked = false;
    protected Intent mCommentIntent;
    protected String mTitle;
//    protected ImageLoader imageLoader = SingleTuneApplication.getmInstance().getmImageLoader();
    private int lastPosition = -1;
    private Target target = new Target(){

        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    File file = new File(Environment.getExternalStorageDirectory().getPath() +"/"+mTitle+".jpg");
                    try
                    {
                        file.createNewFile();
                        FileOutputStream ostream = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, ostream);
                        ostream.close();
                    }
                    catch (Exception e)
                    {
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
            holder.commentLabel = (TextView) convertView.findViewById(R.id.TuneCommentTextView);
            holder.download_count = (TextView) convertView.findViewById(R.id.tuneDownloadCountTextView);
            holder.ic_tune_like = (ImageView) convertView.findViewById(R.id.ic_tune_like);
            holder.like_text = (TextView) convertView.findViewById(R.id.TuneLikeTextView);
            holder.tune_progress = (ProgressBar) convertView.findViewById(R.id.tuneBufferProgress);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Get current tune

        final Tunes tunes = mTunes.get(position);
        String username = tunes.getArtist().getUsername();

        Picasso.with(mContext)
                .load(tunes.getCoverArt().getUrl())
                .placeholder(R.drawable.default_avatar)
                .into(holder.tuneArt, new Callback.EmptyCallback(){
                    @Override public void onSuccess() {
                        // TODO: Implement progress bar
//                        progressBar.setVisibility(View.GONE);
                    }
                    @Override
                    public void onError() {
                        // TODO: Implement progress bar
//                        progressBar.setVisibility(View.GONE);
                    }
                });
        Picasso.with(mContext)
                .load(tunes.getCoverArt().getUrl())
                .into(target);

        final String tuneURL = tunes.getSongFile().getUrl();
        holder.title.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Aaargh.ttf"));
        holder.artist.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Aaargh.ttf"));


        mTitle = tunes.getTitle();

        holder.title.setText(tunes.getTitle());
        holder.artist.setText(username);

        // PlayAction

        playAction(position, holder, tuneURL);

        /*
         *  Tune meta area
         */

        // Get like

        getLike(holder, tunes);

        // Get like count
        getLikeCount(holder, tunes);

        // Like action

        likeAction(holder, tunes);

        // Comment Action

        holder.commentLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCommentIntent = new Intent(COMMENT_BROADCAST);
                mCommentIntent.putExtra("tuneid", tunes.getObjectId());
                mContext.sendBroadcast(mCommentIntent);
            }
        });

        return convertView;
    }

    private void commentAction() {
    }

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

    private void likeAction(final ViewHolder holder, final Tunes tunes) {
        holder.like_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tunes.getArtist().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                    holder.tune_progress.setVisibility(View.VISIBLE);
                    if (!isLiked) {
                        tunes.increaseLike(1);
                        tunes.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                isLiked = true;

                                //CreateActivity
                                createActivity(tunes);

                                String message = "Your tune was liked by ";


                                JSONObject obj;

                                ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
                                query.whereEqualTo(ParseConstants.KEY_USER_ID, tunes.getArtist().getObjectId());
                                String fMessage = message + ParseUser.getCurrentUser().getUsername();
                                try{

                                    obj = new JSONObject();
                                    obj.put("msg", fMessage);
                                    obj.put("data", "You have a new like.");
                                    obj.put("action", Notifyer.LIKE_NOTIFICATION_ACTION);
                                    obj.put("channel", "Activities");

                                    ParsePush push = new ParsePush();
                                    push.setQuery(query);
                                    push.setData(obj);
                                    push.sendInBackground();

                                } catch (JSONException el) {
                                    el.printStackTrace();
                                }

                                holder.ic_tune_like.setImageResource(R.drawable.ic_tune_meta_like_pressed);
                                getLikeCount(holder, tunes);
                                holder.tune_progress.setVisibility(View.INVISIBLE);

                            }
                        });
                    } else {
                        tunes.reduceLike(1);
                        tunes.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                isLiked = false;
                                //deleteActivity
                                deleteActivity(tunes);
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

    private void deleteActivity(Tunes tunes) {
        ParseQuery<ParseObject> getLike = ParseQuery.getQuery("Activities");
        getLike.whereEqualTo("from", ParseUser.getCurrentUser());
        getLike.whereEqualTo("tune", tunes.getObjectId());
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

    private void createActivity(Tunes tunes) {
        Activities tLikeActivity = new Activities();

        tLikeActivity.setFrom(ParseUser.getCurrentUser());
        tLikeActivity.setTo(tunes.getArtist());
        tLikeActivity.setActivityType("like");
        tLikeActivity.setTuneId(tunes.getObjectId());

//        tLikeActivity.put("from", ParseUser.getCurrentUser());
//        tLikeActivity.put("to", tunes.getArtist());
//        tLikeActivity.put("activity", "like");
//        tLikeActivity.put("tune", tunes.getObjectId());
        tLikeActivity.saveInBackground();
    }

    private void getLikeCount(final ViewHolder holder, Tunes tunes) {
        ParseQuery<Tunes> cQuery = ParseQuery.getQuery(Tunes.class);
        cQuery.whereEqualTo("objectId", tunes.getObjectId());
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
        lQuery.whereEqualTo("from", ParseUser.getCurrentUser());
        lQuery.whereEqualTo("to", tunes.getArtist());
        lQuery.whereEqualTo("tune", tunes.getObjectId());
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
        mServiceIntent.putExtra("tuneURL", tuneURL);
        mServiceIntent.putExtra("tunePos", p);
        getContext().startService(mServiceIntent);
    }

    private void stopMusic() {
        Intent mServiceIntent = new Intent(getContext(), PlaySongService.class);
        getContext().stopService(mServiceIntent);
    }

    private boolean checkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        isOnline = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting()|| cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting() || cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isAvailable();
        return isOnline;
    }

    public void refill(List<Tunes> tunes){
        mTunes.clear();
        mTunes.addAll(tunes);
        notifyDataSetChanged();
    }

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
