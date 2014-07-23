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
import android.widget.ProgressBar;

import com.manuelpeinado.fadingactionbar.FadingActionBarHelper;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
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
    protected Context mContext = ProfileActivity.this;
    @InjectView(R.id.profileAvatarImageView)
    ImageView mProfileAvatar;
    protected GridView mGridView;
    protected List<Tunes> mTunes;
    protected ProgressBar mProgressBar;
    protected CustomTextView mTuneCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FadingActionBarHelper helper = new FadingActionBarHelper()
                .actionBarBackground(R.drawable.ab_background_textured_singletune)
                .headerLayout(R.layout.profile_header)
                .contentLayout(R.layout.profile_content);

        setContentView(helper.createView(this));
        helper.initActionBar(this);


        // Find various textviews to modify
        ButterKnife.inject(this);


        mCurrentUser = ParseUser.getCurrentUser();
        String userEmail = mCurrentUser.getEmail().toLowerCase();

        Boolean fbLinked = mCurrentUser.getBoolean("FBLinked");

        // Set Header Image

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
        setTitle(mCurrentUser.getUsername());

        //Instantiate GridView

        mGridView = (GridView)findViewById(R.id.tune_grid);

        mProgressBar = (ProgressBar)findViewById(R.id.pb_loading);

        mProgressBar.setVisibility(View.VISIBLE);

        // Counts

        mTuneCount = (CustomTextView)findViewById(R.id.tune_count);

    }

    @Override
    protected void onResume(){
        super.onResume();

        // TuneCountQuery
        tuneCountQuery();

        // TuneQuery
        tuneQuery();

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
