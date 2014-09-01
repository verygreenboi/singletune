package com.pixel.singletune.app.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.pixel.singletune.app.R;

public class FBRegister extends Activity {

    protected TextView mSalutation;
    protected Animation mFadeOut;
    protected ProgressBar mSpinner;
    protected RelativeLayout mPreloaderLayout;
    protected LinearLayout mRegFormLayout;
    protected ParseUser mCurrentUser;
    protected GraphUser mGraphUser;
    protected Request mMeRequest;
    protected Button mCompleteRegButton;
    protected EditText mUsernameField;
    protected EditText mPasswordField;
    protected EditText mPasswordConfirmationField;
    protected String mUserEmail;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fbregister);

        Log.d("FBTAG", "OnCreate");

        String fontPath = "fonts/Megrim.ttf";

        TextView title = (TextView) findViewById(R.id.appTitle);

        FontifyTextView(fontPath, title);

        mSalutation = (TextView)findViewById(R.id.welcome_text);
        mSpinner = (ProgressBar)findViewById(R.id.spinner);
        mPreloaderLayout = (RelativeLayout)findViewById(R.id.preloader);
        mRegFormLayout = (LinearLayout)findViewById(R.id.regLinearLayout);

        //EditText

        mUsernameField = (EditText)findViewById(R.id.usernameField);
        mPasswordField = (EditText)findViewById(R.id.passwordField);
        mPasswordConfirmationField = (EditText)findViewById(R.id.passwordConfirmationField);

        // Button
        mCompleteRegButton = (Button)findViewById(R.id.completeRegButton);

        try{
            mCurrentUser = new ParseUser();

            mMeRequest = Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    mGraphUser = user;
                    String fName = user.getFirstName();
                    String salutation = "Hello " + fName;
                    mSalutation.setText(salutation);

                    // User data
                    mUserEmail = mGraphUser.getProperty("email").toString();

                    ParseQuery<ParseUser> checkUser = ParseUser.getQuery();
                    checkUser.whereEqualTo("email", mUserEmail);
                    checkUser.getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            if (e == null) {
                                showMainActivity(MainActivity.class);
//                                Log.d("FBTAG", parseUser.getUsername());
                            }
                            else {
                                e.printStackTrace();
                                // Animation
                                animation();
                            }
                        }
                    });


                    Log.d("FBTAG", fName);
                    Log.d("FBTAG", mUserEmail);
                }
            });
            Bundle params = mMeRequest.getParameters();
            params.putString("field","email, name");
            mMeRequest.setParameters(params);
            mMeRequest.executeAsync();

            Log.d("FBTAG", ParseFacebookUtils.getSession().getAccessToken().toString());

        }
        catch (Exception e){
            Toast.makeText(this, "Can't get current user", Toast.LENGTH_SHORT).show();
            Log.d("FBTAG", ParseFacebookUtils.getSession().toString());
            e.printStackTrace();
        }
    }

    private void animation() {
        mFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_fade_out);
        mSpinner.setVisibility(View.INVISIBLE);
        mRegFormLayout.setVisibility(View.VISIBLE);
        mPreloaderLayout.startAnimation(mFadeOut);
        mPreloaderLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCompleteRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ParseQuery<ParseUser> checkUser = ParseQuery.getQuery("User");
                checkUser.whereEqualTo("email", mUserEmail);
                checkUser.getFirstInBackground(new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if (e==null){
                            showMainActivity(MainActivity.class);
                        }
                        else {
                            progressDialog = ProgressDialog.show(FBRegister.this, "",
                                    "Signing up...", true);

                            //  Get Strings
                            String username = mUsernameField.getText().toString().toLowerCase();
                            String password = mPasswordField.getText().toString();
                            String confirmPassword = mPasswordConfirmationField.getText().toString();
                            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
                                AlertDialog.Builder builder = new AlertDialog.Builder(FBRegister.this);
                                builder.setMessage(R.string.signup_error_message)
                                        .setTitle(R.string.title_signup_error)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                // Close progress dialog
                                progressDialog.dismiss();
                                dialog.show();
                            }
                            else if (!password.equals(confirmPassword)){
                                AlertDialog.Builder builder = new AlertDialog.Builder(FBRegister.this);
                                builder.setMessage(R.string.signup_password_mismatch_message)
                                        .setTitle(R.string.title_signup_error)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                // Close progress dialog
                                progressDialog.dismiss();
                                dialog.show();
                            }
                            else {
//                                mCurrentUser.put("avatar", FileHelper.getFacebookPicture(mGraphUser.getId()));
                                mCurrentUser.setEmail(mUserEmail);
                                mCurrentUser.setUsername(username);
                                mCurrentUser.setPassword(password);
                                mCurrentUser.put("First_Name", mGraphUser.getFirstName());
                                mCurrentUser.put("Last_Name", mGraphUser.getLastName());
                                mCurrentUser.put("FB_Link", mGraphUser.getLink());
                                mCurrentUser.put("Birthday", mGraphUser.getBirthday());
                                mCurrentUser.put("fbID", mGraphUser.getId());
                                mCurrentUser.put("FBLinked", true);
//                    mCurrentUser.put("Location", mGraphUser.getLocation());

                                mCurrentUser.signUpInBackground(new SignUpCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            if (!ParseFacebookUtils.isLinked(mCurrentUser)) {
                                                ParseFacebookUtils.link(mCurrentUser, FBRegister.this, new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {

                                                        showMainActivity(MainActivity.class);
                                                    }
                                                });
                                            }else {
                                                // is Linked
                                                showMainActivity(MainActivity.class);
                                            }
                                        }else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(FBRegister.this);
                                            builder.setMessage(e.getMessage())
                                                    .setTitle(R.string.title_signup_error)
                                                    .setPositiveButton(android.R.string.ok, null);
                                            AlertDialog dialog = builder.create();
                                            // Close progress dialog
                                            progressDialog.dismiss();
                                            dialog.show();
                                        }
                                    }
                                });

                            }
                        }
                    }
                });
            }
        });
    }

    public void FontifyTextView(String font, TextView tv) {
        // Font path

        Typeface tf = Typeface.createFromAsset(getAssets(), font);

        tv.setTypeface(tf);
    }

    private void showMainActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}
