package com.pixel.singletune.app.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pixel.singletune.app.ParseConstants;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.helpers.FileHelper;
import com.pixel.singletune.app.subClasses.Tunes;

/**
 * Created by mrsmith on 5/26/14.
 */
public class SendTuneActivity extends Activity {

    protected Uri mMediaUri;
    protected Uri mTuneArt;
    protected String mFileType;
    protected MenuItem mSendMenuItem;
    protected EditText mTitle;
    protected EditText mCaption;
    protected ImageButton mArtImageButton;
    protected String title;
    protected String caption;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_sendtune);

        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);

    }

    protected void onResume(){
        super.onResume();


    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {

        getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.send_tune, menu);
        mSendMenuItem = menu.getItem(0);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_send:
                mTitle = (EditText)findViewById(R.id.tune_title);
                mCaption = (EditText)findViewById(R.id.tune_desc);
                String title = mTitle.getText().toString();
                String caption = mCaption.getText().toString();
                if(title.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(R.string.empty_title_error_message)
                        .setTitle(R.string.empty_title_error_title)
                        .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    setProgressBarIndeterminateVisibility(true);
                    Tunes tune = new Tunes();
                    tune.setArtist(ParseUser.getCurrentUser());
                    tune.setTitle(title);
                    byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

                    String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
                    ParseFile file = new ParseFile(fileName, fileBytes);
                    tune.setSongFile(file);
                    tune.setFileType(mFileType);


                    if (tune == null) {
                        // error
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(R.string.error_selecting_file)
                                .setTitle(R.string.error_selecting_file_title)
                                .setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else {
                        tune.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                setProgressBarIndeterminateVisibility(false);
                                finish();
                            }
                        });
                    }
                    return true;
                }

        }
        return super.onOptionsItemSelected(item);
    }

}
