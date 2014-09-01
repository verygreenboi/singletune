package com.pixel.singletune.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.manuelpeinado.fadingactionbar.FadingActionBarHelper;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.pixel.singletune.app.ParseConstants;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.adapters.TunesGridAdapter;
import com.pixel.singletune.app.helpers.FileHelper;
import com.pixel.singletune.app.subClasses.Tunes;
import com.pixel.singletune.app.utils.CustomTextView;
import com.pixel.singletune.app.utils.MD5Util;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.view.View.INVISIBLE;


public class ProfileActivity extends Activity {
    protected ParseUser mCurrentUser;
    protected ParseRelation<ParseUser> mFriendsRelation;
    protected Context mContext = ProfileActivity.this;

    @InjectView(R.id.tune_grid)
        protected GridView mGridView;

    protected List<Tunes> mTunes;

    @InjectView(R.id.pb_loading)
        protected ProgressBar mProgressBar;

    @InjectView(R.id.tune_count)
        protected CustomTextView mTuneCount;

    @InjectView(R.id.following_count)
        protected CustomTextView mFollowingCount;

    @InjectView(R.id.followers_count)
        protected CustomTextView mFollowersCount;

    @InjectView(R.id.profileUsernameLabel)
        protected  CustomTextView mUsernameLabel;

    @InjectView(R.id.fullNameLabel)
        protected CustomTextView mFullNameLabel;

    @InjectView(R.id.profileAvatarImageView)
        protected ImageView mProfileAvatar;

    @InjectView(R.id.following_layout)
        protected LinearLayout mFollowingLayout;

    @InjectView(R.id.followers_layout)
        protected LinearLayout mFollowersLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FadingActionBarHelper helper = new FadingActionBarHelper()
                .actionBarBackground(R.drawable.ab_background_textured_singletune)
                .headerLayout(R.layout.profile_header)
                .parallax(true)
                .contentLayout(R.layout.profile_content);

        setContentView(helper.createView(this));
        helper.initActionBar(this);


        // Find various textviews to modify
        ButterKnife.inject(this);


        mCurrentUser = ParseUser.getCurrentUser();


        mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        String userEmail = mCurrentUser.getEmail().toLowerCase();

        Boolean fbLinked = mCurrentUser.getBoolean("FBLinked");

        mUsernameLabel.setText("@"+mCurrentUser.getUsername());

        // Set Header Image
        // TODO: Implement disk cache

        if (fbLinked){
            String fbID = mCurrentUser.getString("fbID");
            String fbPicURL = FileHelper.getFacebookPicture(fbID);
            Picasso.with(mContext).load(fbPicURL).placeholder(R.drawable.default_avatar).into(mProfileAvatar);

            String fName = mCurrentUser.getString("First_Name") +" "+ mCurrentUser.getString("Last_Name");
            mFullNameLabel.setText(fName);
        }

        else if (userEmail.equals("")){
            mProfileAvatar.setImageResource(R.drawable.default_avatar);
            mFullNameLabel.setVisibility(View.GONE);
        }
        else {
            String hash = MD5Util.md5Hex(userEmail);
            String gravatarUrl = "http://www.gravatar.com/avatar/"+ hash + "?s=272&d=404";
            Picasso.with(mContext).load(gravatarUrl).placeholder(R.drawable.default_avatar).into(mProfileAvatar);
            mFullNameLabel.setVisibility(View.GONE);
        }
        setTitle("");

        mProgressBar.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onResume(){
        super.onResume();

        // TuneCountQuery
        tuneCountQuery();

        //FollowersCount
        ParseQuery<ParseObject> fQuery = ParseQuery.getQuery("Follow");
        fQuery.whereEqualTo("to", mCurrentUser);
        fQuery.countInBackground(new CountCallback() {
            @Override
            public void done(int i, ParseException e) {
                mFollowersCount.setText(String.valueOf(i));
            }
        });

        //FollowingCount
        followingCount();

        // TuneQuery
        tuneQuery();

        mFollowingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), FriendActivity.class);
                i.putExtra("flwing", 0);
                startActivity(i);

            }
        });

    }

    private void followingCount() {
        mFriendsRelation.getQuery()
                .countInBackground(new CountCallback() {
                    @Override
                    public void done(int i, ParseException e) {
                        mFollowingCount.setText(String.valueOf(i));
                    }
                });
    }

    private void tuneCountQuery() {
        ParseQuery<Tunes> cQ = ParseQuery.getQuery(Tunes.class);
        cQ.whereEqualTo("parent", mCurrentUser);
        cQ.countInBackground(new CountCallback() {
            @Override
            public void done(int i, ParseException e) {
                if (e == null){
                    String count = String.valueOf(i);
                    mTuneCount.setText(count);
                }
            }
        });
    }

    private void tuneQuery() {
        ParseQuery<Tunes> query = ParseQuery.getQuery(Tunes.class);
        query.whereEqualTo("parent", mCurrentUser);
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.setMaxCacheAge(TimeUnit.HOURS.toHours(1));
        query.setLimit(12);
        query.findInBackground(new FindCallback<Tunes>() {
            @Override
            public void done(List<Tunes> tunes, ParseException e) {

                mProgressBar.setVisibility(INVISIBLE);

                if (e == null){
                    mTunes = tunes;
                    if (mGridView.getAdapter() == null) {
                        TunesGridAdapter adapter = new TunesGridAdapter(mContext, mTunes);
                        mGridView.setAdapter(adapter);
                    } else {
                        ((TunesGridAdapter)mGridView.getAdapter()).refill(mTunes);
                    }
                }
            }
        });
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
