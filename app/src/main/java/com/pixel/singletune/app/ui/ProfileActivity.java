package com.pixel.singletune.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.helpers.FileHelper;
import com.pixel.singletune.app.utils.MD5Util;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ProfileActivity extends Activity {
    protected ParseUser mCurrentUser;
    protected Context mContext = ProfileActivity.this;
    @InjectView(R.id.edit_profile_button)
    Button mEditProfileButton;
    @InjectView(R.id.tune_count_meta) TextView mTuneCountMeta;
    @InjectView(R.id.friends_count_meta) TextView mFriendsCountMeta;
    @InjectView(R.id.followers_count_meta) TextView mFollowersCountMeta;
    @InjectView(R.id.profileAvatarImageView)
    ImageView mProfileAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // Find various textviews to modify
        ButterKnife.inject(this);

        mCurrentUser = ParseUser.getCurrentUser();
        String userEmail = mCurrentUser.getEmail().toLowerCase();

        Boolean fbLinked = mCurrentUser.getBoolean("FBLinked");

        if (fbLinked){
            String fbID = mCurrentUser.getString("fbID");
            String fbPicURL = FileHelper.getFacebookPicture(fbID);
            Picasso.with(mContext).load(fbPicURL).placeholder(R.drawable.default_avatar).into(mProfileAvatar);
        }

        else if (userEmail.equals("")){
            mProfileAvatar.setImageResource(R.drawable.default_avatar);
        }
        else {
            String hash = MD5Util.md5Hex(userEmail);
            String gravatarUrl = "http://www.gravatar.com/avatar/"+ hash + "?s=272&d=404";
            Picasso.with(mContext).load(gravatarUrl).placeholder(R.drawable.default_avatar).into(mProfileAvatar);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            ParseUser.logOut();
            navigateToLogin();
        }
        return super.onOptionsItemSelected(item);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
