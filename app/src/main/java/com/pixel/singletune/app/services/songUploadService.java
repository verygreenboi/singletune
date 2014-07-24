package com.pixel.singletune.app.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;
import com.pixel.singletune.app.helpers.FileHelper;
import com.pixel.singletune.app.subClasses.Tunes;

public class songUploadService extends Service {

    public static final String BROADCAST_UPLOAD_TUNE = "broadcast_upload_tune";

    private String artURIString;
    private String mp3URIString;
    private String title;
    private String artFileName;
    private String mp3FileName;
    private String mp3FileType;
    private boolean hasArt;

    @Override
    public int onStartCommand(final Intent i, int flags, int startId) {

        final Tunes tune = new Tunes();

        // get bytes from intent



        Uri artUri = Uri.parse(artURIString);
        final byte[] aByte = FileHelper.getByteArrayFromFile(getApplicationContext(), artUri);
        artFileName = i.getExtras().getString("artName");

        mp3URIString = i.getExtras().getString("mp3URI");
        mp3FileName = i.getExtras().getString("mp3Name");
        mp3FileType = i.getExtras().getString("mp3Type");

        Uri mp3URI = Uri.parse(mp3URIString);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(getApplicationContext(), mp3URI);
        title = i.getExtras().getString("title");

        final ParseFile file = new ParseFile(mp3FileName, fileBytes);



        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                tune.setSongFile(file);
                tune.setFileType(mp3FileType);

                try {
                    artURIString = i.getExtras().getString("artMediaUri");
                    ParseFile aFile = new ParseFile(artFileName, aByte);
                    tune.setCoverArt(aFile);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                tune.setTitle(title);
                tune.setArtist(ParseUser.getCurrentUser());

                tune.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        stopSelf();
                    }
                });

            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer i) {

            }
        });

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
