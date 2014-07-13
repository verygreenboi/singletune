package com.pixel.singletune.app.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.ParseFacebookUtils;
import com.pixel.singletune.app.R;

public class FBRegister extends Activity implements Animation.AnimationListener {

    protected TextView mSalutation;
    protected Animation mFadeOut;
    protected ProgressBar mSpinner;
    protected RelativeLayout mPreloaderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fbregister);

        String fontPath = "fonts/Megrim.ttf";

        TextView title = (TextView) findViewById(R.id.appTitle);

        FontifyTextView(fontPath, title);

        ActionBar ab = getActionBar();
        ab.setDisplayShowTitleEnabled(false);

        mSalutation = (TextView)findViewById(R.id.welcome_text);
        mSpinner = (ProgressBar)findViewById(R.id.spinner);
        mPreloaderLayout = (RelativeLayout)findViewById(R.id.preloader);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                String fName = user.getFirstName();
                String salutation = "Welcome " + fName;
                mSalutation.setText(salutation);

                // Animation
                mFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_fade_out);

                mSpinner.setVisibility(View.INVISIBLE);

                mPreloaderLayout.startAnimation(mFadeOut);
            }
        });
    }

    public void FontifyTextView(String font, TextView tv) {
        // Font path

        Typeface tf = Typeface.createFromAsset(getAssets(), font);

        tv.setTypeface(tf);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == mFadeOut){
            try {
                mPreloaderLayout.setVisibility(View.INVISIBLE);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
