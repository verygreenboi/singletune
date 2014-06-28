package com.pixel.singletune.app.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.SingleTuneApplication;

public class LoginActivity extends Activity {


    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLoginButton;

    protected TextView mSignupTextView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        String fontPath = "fonts/Megrim.ttf";

        TextView title = (TextView) findViewById(R.id.appTitle);

        FontifyTextView(fontPath, title);


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

        // Login user

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = ProgressDialog.show(LoginActivity.this, "",
                        "Signing in...", true);

                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                username = username.trim().toLowerCase();
                password = password.trim();

                if (username.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(R.string.login_error_message)
                            .setTitle(R.string.login_error_label)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    // Close progress dialog
                    progressDialog.dismiss();
                    dialog.show();
                } else {
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                // Success

                                // Close progress dialog
                                progressDialog.dismiss();

                                // Associate the device with a user
                                SingleTuneApplication.UpdateParseInstallation(user);

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                 /* TODO Clean up returned error message */
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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
        });

    }

    public void FontifyTextView(String font, TextView tv) {
        // Font path

        Typeface tf = Typeface.createFromAsset(getAssets(), font);

        tv.setTypeface(tf);
    }

}
