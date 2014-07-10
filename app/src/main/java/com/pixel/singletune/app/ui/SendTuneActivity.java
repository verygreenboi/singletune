package com.pixel.singletune.app.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;
import com.pixel.singletune.app.ParseConstants;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.helpers.FileHelper;
import com.pixel.singletune.app.subClasses.Tunes;
import com.squareup.picasso.Picasso;

/**
 * Created by mrsmith on 5/26/14.
 */
public class SendTuneActivity extends Activity {

    private static final String TAG = SendTuneActivity.class.getSimpleName();
    private static final int PICK_PHOTO_REQUEST = 0;
    protected Uri artMediaUri;
    protected Uri mMediaUri;
    protected String mFileType;
    protected String artFileType;
    protected MenuItem mSendMenuItem;
    protected EditText mTitle;
    protected EditText mCaption;
    protected ImageButton mArtImageButton;
    protected String title;
    protected String caption;

    //Setup broadcast identifier and intent
    public static final String BROADCAST_UPLOAD = "com.pixel.singletune.app.broadcastupload";
    protected Intent mSendTuneIntent;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_sendtune);

        mSendTuneIntent = new Intent(BROADCAST_UPLOAD);

        mMediaUri = getIntent().getData();
        mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);

    }

    protected void onResume(){
        super.onResume();

        mArtImageButton = (ImageButton) findViewById(R.id.tune_art);
        mArtImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent caIntent = new Intent(Intent.ACTION_GET_CONTENT);
                caIntent.setType("image/*");
                caIntent.putExtra(ParseConstants.KEY_FILE_TYPE, ParseConstants.TYPE_IMAGE);
                startActivityForResult(caIntent, PICK_PHOTO_REQUEST);
            }
        });
    }
//
//    public class CropSquareTransformation implements Transformation {
//        @Override public Bitmap transform(Bitmap source) {
//            int size = Math.min(source.getWidth(), source.getHeight());
//            int x = (source.getWidth() - size) / 2;
//            int y = (source.getHeight() - size) / 2;
//            Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
//            if (result != source) {
//                source.recycle();
//            }
//            return result;
//        }
//
//        @Override public String key() { return "square()"; }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PHOTO_REQUEST){
            artMediaUri = data.getData();
            artFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);
            mArtImageButton = (ImageButton) findViewById(R.id.tune_art);


            Picasso.with(this)
                    .load(artMediaUri)
                    .fit()
                    .centerCrop()
                    .into(mArtImageButton);

            Log.i(TAG, "Media URI: " + artMediaUri);
        }
        else if (resultCode != RESULT_CANCELED){
            Picasso.with(this)
                    .load(R.drawable.default_avatar)
                    .fit()
                    .centerCrop()
                    .into(mArtImageButton);
        }
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
                    Tunes tune = new Tunes();
                    sendTune(title, tune);
                    finish();
                    return true;
                }

        }
        return super.onOptionsItemSelected(item);
    }

    private void sendTune(String title, final Tunes tune) {
        tune.setArtist(ParseUser.getCurrentUser());
        tune.setTitle(title);
        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

        String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
        final ParseFile file = new ParseFile(fileName, fileBytes);

        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                mSendTuneIntent.putExtra(ParseConstants.KEY_UPLOADING, "1");
                sendBroadcast(mSendTuneIntent);

                tune.setSongFile(file);
                tune.setFileType(mFileType);
                if (artMediaUri != null){
                    byte[] fb = FileHelper.getByteArrayFromFile(getApplicationContext(), artMediaUri);
                    String fn = FileHelper.getFileName(getApplicationContext(), artMediaUri, ParseConstants.TYPE_IMAGE);
                    ParseFile ia = new ParseFile(fn, fb);
                    tune.setCoverArt(ia);
                }

                tune.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        mSendTuneIntent.putExtra(ParseConstants.KEY_UPLOADING, "0");
                        sendBroadcast(mSendTuneIntent);
                        Log.d(TAG, "Done saving tune");
                    }
                });
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer integer) {
                Log.d(TAG, String.valueOf(integer));
            }
        });


    }


}
