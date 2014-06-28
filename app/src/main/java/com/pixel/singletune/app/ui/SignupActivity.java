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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.SingleTuneApplication;


public class SignupActivity extends Activity {

    protected EditText mUsername;
    protected EditText mEmail;
    protected EditText mPassword;
    protected EditText mPasswordConfirmation;
    protected Button mSignupButton;
    protected Button mCancelButton;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);

        // Font path
        String fontPath = "fonts/Megrim.ttf";

        TextView title = (TextView) findViewById(R.id.appTitle);

        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);

        title.setTypeface(tf);

        mUsername = (EditText) findViewById(R.id.username_field);
        mEmail = (EditText) findViewById(R.id.email_field);
        mPassword = (EditText) findViewById(R.id.password_field);
        mPasswordConfirmation = (EditText) findViewById(R.id.password_confirmation_field);

        mCancelButton = (Button) findViewById(R.id.cancel_button);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSignupButton = (Button) findViewById(R.id.signup_button);

//        Signup button onClick listener
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = ProgressDialog.show(SignupActivity.this, "",
                        "Signing in...", true);

                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String passwordConfirmation = mPasswordConfirmation.getText().toString();
                String email = mEmail.getText().toString();

                username = username.trim().toLowerCase();
                password = password.trim();
                passwordConfirmation = passwordConfirmation.trim();
                email = email.trim().toLowerCase();

                if (username.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty() || email.isEmpty()) {
                    // TODO: Turn fields to red highlights
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    builder.setMessage(R.string.signup_error_message)
                            .setTitle(R.string.title_signup_error)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    // Close progress dialog
                    progressDialog.dismiss();
                    dialog.show();
                }
                // TODO This string comparism method will be helpful
                else if (!password.equals(passwordConfirmation)) {
                    // TODO: Turn fields to red highlights
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    builder.setMessage(R.string.signup_password_mismatch_message)
                            .setTitle(R.string.title_signup_error)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    // Close progress dialog
                    progressDialog.dismiss();
                    dialog.show();
                } else {
                    // Signup Here
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                // Success

                                // Associate the device with a user
                                SingleTuneApplication.UpdateParseInstallation(ParseUser.getCurrentUser());

                                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                /* TODO Clean up returned error message */
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
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

}
