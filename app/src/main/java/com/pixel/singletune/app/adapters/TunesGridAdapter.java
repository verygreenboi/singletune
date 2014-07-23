package com.pixel.singletune.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pixel.singletune.app.R;
import com.pixel.singletune.app.subClasses.Tunes;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mrsmith on 7/23/14.
 */
public class TunesGridAdapter extends ArrayAdapter<Tunes> {

    protected Context mContext;
    protected List<Tunes> mTunes;

    public TunesGridAdapter(Context context, List<Tunes> tunes) {
        super(context,  R.layout.grid_tune_item, tunes);
        mContext = context;
        mTunes = tunes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VH holder;

        if (convertView == null){
            LayoutInflater i = (LayoutInflater.from(mContext));
            convertView = i.inflate(R.layout.grid_tune_item, parent, false);

            holder = new VH();
            holder.tuneArt = (ImageView)convertView.findViewById(R.id.tuneArt);


            convertView.setTag(holder);

        }
        else {
            holder = (VH) convertView.getTag();
        }

        Tunes tune = mTunes.get(position);

        try {
            String tuneArtURL = tune.getCoverArt().getUrl();
            if (tuneArtURL.isEmpty()) {
                holder.tuneArt.setImageResource(R.drawable.tune_placeholder);
            } else {
                Picasso.with(mContext).load(tuneArtURL).placeholder(R.drawable.tune_placeholder).into(holder.tuneArt);
            }
        } catch (Exception e) {
            holder.tuneArt.setImageResource(R.drawable.tune_placeholder);
        }

        return convertView;
    }

    private static class VH{
        TextView tuneCount;
        ImageView tuneArt;
    }

    public void refill(List<Tunes> tunes){
        mTunes.clear();
        mTunes.addAll(tunes);
        notifyDataSetChanged();
    }
}
