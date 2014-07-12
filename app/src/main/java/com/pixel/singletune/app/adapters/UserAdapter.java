package com.pixel.singletune.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.utils.MD5Util;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by mrsmith on 4/28/14.
 */
public class UserAdapter extends ArrayAdapter<ParseUser> {

    protected Context mContext;
    protected List<ParseUser> mParseUsers;

    public UserAdapter(Context context, List<ParseUser> users) {
        super(context, R.layout.user_item, users);
        mContext = context;
        mParseUsers = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater.from(mContext));
            convertView = inflater.inflate(R.layout.user_item, parent, false);
            holder = new ViewHolder();
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatarImageView);
            holder.selectedAvatar = (ImageView) convertView.findViewById(R.id.selectedAvatarImageView);
            holder.usernameLabel = (TextView) convertView.findViewById(R.id.usernameLabel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        ParseUser user = mParseUsers.get(position);

        String email = user.getEmail().toLowerCase();

        if (email.equals("")) {
            holder.avatar.setImageResource(R.drawable.default_avatar);
        } else {
            String hash = MD5Util.md5Hex(email);
            String gravatarUrl = "http://www.gravatar.com/avatar/" + hash + "?s=272&d=404";
            Picasso.with(mContext).load(gravatarUrl).placeholder(R.drawable.default_avatar).into(holder.avatar);
        }
        holder.usernameLabel.setText(user.getUsername());

        GridView gridView = (GridView) parent;
        if (gridView.isItemChecked(position)) {
            holder.selectedAvatar.setVisibility(View.VISIBLE);
        } else {

            holder.selectedAvatar.setVisibility(View.INVISIBLE);
        }


//        return rowView;
        return convertView;
    }


    //
    private static class ViewHolder {
        ImageView avatar;
        ImageView selectedAvatar;
        TextView usernameLabel;
    }

    public void refill(List<ParseUser> users) {
        mParseUsers.clear();
        mParseUsers.addAll(users);
        notifyDataSetChanged();
    }
}
