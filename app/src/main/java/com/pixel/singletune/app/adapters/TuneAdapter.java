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
import android.widget.TextView;

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

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Get current tune

        Tunes tunes = mTunes.get(position);
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

        Object mgr = new Object();
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
                        playMusic(tuneURL);
                    } else {
                        AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                        ad.setMessage(R.string.not_connected_error_message)
                                .setTitle(R.string.not_connected_error_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = ad.create();
                        holder.btnPlay.setImageResource(R.drawable.btn_play);
                        dialog.show();
                    }

                } else {
                    isMusicPlaying = false;
                    holder.btnPlay.setImageResource(R.drawable.btn_play);
                    stopMusic();
                }
            }
        });


        return convertView;
    }

    private void playMusic(String tuneURL) {
        Intent mServiceIntent = new Intent(getContext(), PlaySongService.class);
        mServiceIntent.putExtra("tuneURL", tuneURL);
        getContext().startService(mServiceIntent);
    }

    private void stopMusic() {
        Intent mServiceIntent = new Intent(getContext(), PlaySongService.class);
        getContext().stopService(mServiceIntent);
    }


    public static class ViewHolder {
        ImageView tuneArt;
        TextView title;
        TextView artist;
        ImageButton btnPlay;
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
}
