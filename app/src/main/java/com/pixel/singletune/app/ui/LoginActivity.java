package com.pixel.singletune.app.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.SingleTuneApplication;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends Activity {


    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLoginButton;
    protected LoginButton mFBLoginButton;

    protected TextView mSignupTextView;
    private ProgressDialog progressDialog;
    private String username;
    private String password;

    private boolean mDoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

//        String fontPath = "fonts/Megrim.ttf";
//
//        TextView title = (TextView) findViewById(R.id.appTitle);
//
//        FontifyTextView(fontPath, title);


        mSignupTextView = (TextView) findViewById(R.id.signup_text_field);
        mSignupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        mUsername = (EditText) findViewById(R.id.login_username_field);
        mPassword = (EditText) findViewById(R.id.login_password_field);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mFBLoginButton = (LoginButton)findViewById(R.id.fbLoginButton);

        mFBLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginButtonClicked();
            }
        });

        username = mUsername.getText().toString();
        password = mPassword.getText().toString();

        username = username.trim().toLowerCase();
        password = password.trim();

        // Login user

        mPassword.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == R.id.action_sign_in){
                    progressDialog = ProgressDialog.show(LoginActivity.this, "",
                            "Signing in...", true);
                    doLogin(username, password);
                    return mDoLogin = true;
                }
                return mDoLogin = false;
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = ProgressDialog.show(LoginActivity.this, "",
                        "Signing in...", true);

                doLogin(username, password);


            }
        });

    }

    private void doLogin(String username, String password) {

        if (username.isEmpty() || password.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
            builder.setMessage(R.string.login_error_message)
                    .setTitle(R.string.login_error_label)
                    .setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dismissProgressDialog();

            dialog.show();
        } else {
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        // Success

                        // Close progress dialog
                        dismissProgressDialog();

                        // Associate the device with a user
                        SingleTuneApplication.UpdateParseInstallation(user);

                        showMainActivity(MainActivity.class);
                    } else {
                     /* TODO Clean up returned error message */
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setMessage(e.getMessage())
                                .setTitle(R.string.title_signup_error)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();

                        // Close progress dialog
                        dismissProgressDialog();
                        dialog.show();
                    }
                }
            });
        }


    }

    private void dismissProgressDialog() {
        // Close progress dialog
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showMainActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }

    private void onLoginButtonClicked() {
        LoginActivity.this.progressDialog = ProgressDialog.show(
                LoginActivity.this, "", "Logging in...", true);
        List<String> permissions = Arrays.asList("basic_info", "email", "user_about_me",
                "user_relationships", "user_birthday", "user_location");
        ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                LoginActivity.this.progressDialog.dismiss();
                if (user == null) {
                    Log.d(SingleTuneApplication.TAG,
                            "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d(SingleTuneApplication.TAG,
                            "User signed up and logged in through Facebook!");
                    showMainActivity(FBRegister.class);
                } else {
                    Log.d(SingleTuneApplication.TAG,
                            "User logged in through Facebook!");
                    showMainActivity(FBRegister.class);
                }
            }
        });
    }

//    public void FontifyTextView(String font, TextView tv) {
//        // Font path
//
//        Typeface tf = Typeface.createFromAsset(getAssets(), font);
//
//        tv.setTypeface(tf);
//    }

}
