package com.pixel.singletune.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by mrsmith on 4/27/14.
 */
public class StringArrayAdapter extends BaseAdapter {

    String[] usernames;
    Context context;
    LayoutInflater listItemInflater;

    public StringArrayAdapter(String[] objects, Context c){
        usernames = objects;
        context = c;
        listItemInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return usernames.length;
    }

    @Override
    public Object getItem(int i) {
        return usernames[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
            view = listItemInflater.inflate(android.R.layout.simple_list_item_1, viewGroup,false);
        TextView username = (TextView)view.findViewById(android.R.id.text1);
        username.setText(usernames[i]);
        return view;
    }
}
