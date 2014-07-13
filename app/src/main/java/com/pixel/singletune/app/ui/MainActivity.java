package com.pixel.singletune.app.ui;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.newrelic.agent.android.NewRelic;
import com.parse.ParseAnalytics;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.pixel.singletune.app.ParseConstants;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.adapters.SectionsPagerAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    // Progress dialog and bcast receiver
    boolean mUploadBroadcastIsRegistered;
    private ProgressDialog pdBuff = null;

    public static final int TAKE_VOICE_NOTE_REQUEST = 0;
    public static final int PICK_VOICE_NOTE_REQUEST = 1;
    public static final int PICK_MP3_REQUEST = 2;
    public static final int PICK_ART_REQUEST = 3;
    public static final int MEDIA_TYPE_VOICE_NOTE = 4;
    public static final int MEDIA_TYPE_MP3 = 5;
    public static final int FILE_SIZE_LIMIT = 1024*1024*10; // 10MB
    public static final int MEDIA_TYPE_VIDEO = 6;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static int TAKE_VIDEO_REQUEST = 7;

    protected Uri mMediaUri;
    protected DialogInterface.OnClickListener mDialogListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0: // Take picture
                            Intent takeVoiceNoteIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VOICE_NOTE);
                            if (mMediaUri == null) {
                                // display an error
                                Toast.makeText(MainActivity.this, R.string.error_external_storage,
                                        Toast.LENGTH_LONG).show();
                            } else {
                                takeVoiceNoteIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                startActivityForResult(takeVoiceNoteIntent, TAKE_VOICE_NOTE_REQUEST);
                            }
                            break;
                        case 1: // Take video
                            Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                            if (mMediaUri == null) {
                                // display an error
                                Toast.makeText(MainActivity.this, R.string.error_external_storage,
                                        Toast.LENGTH_LONG).show();
                            } else {
                                videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                                videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // 0 = lowest res
                                startActivityForResult(videoIntent, TAKE_VIDEO_REQUEST);
                            }
                            break;
                        case 2: // Choose MP3
                            Intent chooseMp3Intent = new Intent(Intent.ACTION_GET_CONTENT);
                            chooseMp3Intent.setType("audio/mp3");
                            Toast.makeText(MainActivity.this, R.string.mp3_size_limit, Toast.LENGTH_LONG).show();
                            startActivityForResult(chooseMp3Intent, PICK_MP3_REQUEST);
                            break;
                        case 3: // Choose voice note
                            Intent chooseVoiceNoteIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            chooseVoiceNoteIntent.setType("audio/3gpp");
                            startActivityForResult(chooseVoiceNoteIntent, PICK_VOICE_NOTE_REQUEST);
                            break;

            }
        }

        private Uri getOutputMediaFileUri(int mediaType) {
            // To be safe, you should check that the SDCard is mounted
            // using Environment.getExternalStorageState() before doing this.
            if (isExternalStorageAvailable()) {
                // get the URI

                // 1. Get the external storage directory
                String appName = MainActivity.this.getString(R.string.app_name);
                File mediaStorageDir = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        appName);

                // 2. Create our subdirectory
                if (! mediaStorageDir.exists()) {
                    if (! mediaStorageDir.mkdirs()) {
                        Log.e(TAG, "Failed to create directory.");
                        return null;
                    }
                }

                // 3. Create a file name
                // 4. Create the file
                File mediaFile;
                Date now = new Date();
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

                String path = mediaStorageDir.getPath() + File.separator;
                if (mediaType == MEDIA_TYPE_VOICE_NOTE) {
                    mediaFile = new File(path + "3GP_" + timestamp + ".3gp");
                }
                else if (mediaType == MEDIA_TYPE_VIDEO) {
                    mediaFile = new File(path + "AUD_" + timestamp + ".mp3");
                }
                else {
                    return null;
                }

                Log.d(TAG, "File: " + Uri.fromFile(mediaFile));

                // 5. Return the file's URI
                return Uri.fromFile(mediaFile);
            }
            else {
                return null;
            }
        }

        private boolean isExternalStorageAvailable() {
            String state = Environment.getExternalStorageState();

            return state.equals(Environment.MEDIA_MOUNTED);
        }
    };
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private MediaRecorder mRecorder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        NewRelic.withApplicationToken(
                "AA43ba9dddeb1cf2bcd91749e22ea72202f9a18ce8"
        ).start(this.getApplication());
        setContentView(R.layout.activity_main);

        ParseAnalytics.trackAppOpened(getIntent());

        ParseUser currentUser = ParseUser.getCurrentUser();

        if ((currentUser == null)|| !ParseFacebookUtils.isLinked(currentUser)) {
            navigateToLogin();
        } else {
//            Log.i(TAG, currentUser.getUsername());
        }


        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_VOICE_NOTE_REQUEST || requestCode == PICK_MP3_REQUEST) {
                mMediaUri = data.getData();

                Log.i(TAG, "Media URI: " + mMediaUri);
                if (requestCode == PICK_MP3_REQUEST) {
                    // make sure the file is less than 10 MB
                    int fileSize = 0;
                    InputStream inputStream = null;

                    try {
                        inputStream = getContentResolver().openInputStream(mMediaUri);
                        fileSize = inputStream.available();
                    }
                    catch (FileNotFoundException e) {
                        Toast.makeText(this, R.string.error_opening_file, Toast.LENGTH_LONG).show();
                        return;
                    }
                    catch (IOException e) {
                        Toast.makeText(this, R.string.error_opening_file, Toast.LENGTH_LONG).show();
                        return;
                    }
                    finally {
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } catch (IOException e) { /* Intentionally blank */ }
                    }

                    if (fileSize >= FILE_SIZE_LIMIT) {
                        Toast.makeText(this, R.string.error_file_size_too_large, Toast.LENGTH_LONG).show();
                        return;
                    }
                }
            }
            else {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(mMediaUri);
                sendBroadcast(mediaScanIntent);
            }

            Intent recipientsIntent = new Intent(this, SendTuneActivity.class);
            recipientsIntent.setData(mMediaUri);

            String fileType;
            if (requestCode == PICK_VOICE_NOTE_REQUEST || requestCode == TAKE_VOICE_NOTE_REQUEST) {
                fileType = ParseConstants.TYPE_VOICE_NOTE;
            }
            else {
                fileType = ParseConstants.TYPE_MP3;
            }

            recipientsIntent.putExtra(ParseConstants.KEY_FILE_TYPE, fileType);
            startActivity(recipientsIntent);
        }
        else if (resultCode != RESULT_CANCELED) {
            Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_logout:
                ParseUser.logOut();
                navigateToLogin();
                break;
            case R.id.action_profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.search:
                Intent seachIntent = new Intent(this, SearchActivity.class);
                startActivity(seachIntent);
                break;
            case R.id.add_tune:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(R.array.tune_choice, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

    }
    @Override
    protected void onPause() {

        super.onPause();
        if (mUploadBroadcastIsRegistered){
            unregisterReceiver(broadcastUploadReceiver);
            mUploadBroadcastIsRegistered = false;
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (!mUploadBroadcastIsRegistered){
            this.registerReceiver(broadcastUploadReceiver, new IntentFilter(SendTuneActivity.BROADCAST_UPLOAD));
            mUploadBroadcastIsRegistered = true;
        }
    }

    private BroadcastReceiver broadcastUploadReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            showPB(intent);
        }
    };

    private void showPB(Intent intent) {
        String uploadValue = intent.getStringExtra(ParseConstants.KEY_UPLOADING);
        int uploadValueInt = Integer.parseInt(uploadValue);
        intProg(uploadValueInt);
    }

    private void intProg(int uploadValueInt) {
        switch (uploadValueInt){
            case 0:
                setProgressBarIndeterminate(false);
                break;
            case 1:
                setProgressBarIndeterminate(true);
                break;
        }
    }
}
