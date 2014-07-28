package com.pixel.singletune.app.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;
import com.pixel.singletune.app.R;
import com.pixel.singletune.app.helpers.FileHelper;
import com.pixel.singletune.app.subClasses.Tunes;
import com.pixel.singletune.app.ui.MainActivity;

public class songUploadService extends Service {

    public static final String BROADCAST_UPLOAD_TUNE = "broadcast_upload_tune";
    private static final int myNotificationId = 1;
    private String artURIString;
    private String mp3URIString;
    private String title;
    private String artFileName;
    private String mp3FileName;
    private String mp3FileType;

    @Override
    public int onStartCommand(final Intent i, int flags, int startId) {

        final Tunes tune = new Tunes();

        // get bytes from intent
        artFileName = i.getExtras().getString("artName");

        mp3URIString = i.getExtras().getString("mp3URI");
        mp3FileName = i.getExtras().getString("mp3Name");
        mp3FileType = i.getExtras().getString("mp3Type");

        Uri mp3URI = Uri.parse(mp3URIString);

        byte[] fileBytes = FileHelper.getByteArrayFromFile(getApplicationContext(), mp3URI);
        title = i.getExtras().getString("title");

        final ParseFile file = new ParseFile(mp3FileName, fileBytes);

        showNotification();

        Log.d(BROADCAST_UPLOAD_TUNE, "Uploading");

        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                tune.setSongFile(file);
                tune.setFileType(mp3FileType);

                Log.d(BROADCAST_UPLOAD_TUNE, "File saved");

                try {
                    artURIString = i.getExtras().getString("artMediaUri");
                    Uri artUri = Uri.parse(artURIString);
                    final byte[] aByte = FileHelper.getByteArrayFromFile(getApplicationContext(), artUri);
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
                        Log.d(BROADCAST_UPLOAD_TUNE, "Tune saved");
                        stopSelf();
                    }
                });

            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer i) {
                Log.d(BROADCAST_UPLOAD_TUNE, String.valueOf(i));
            }
        });

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void showNotification() {
        PendingIntent notificationIntent = preparePendingIntent();
        Notification notification = createNotification(notificationIntent);
        displayNotification(notification);
    }

    private Notification createNotification(PendingIntent notificationIntent){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        return builder
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(notificationIntent)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setContentTitle(getString(R.string.nt_send_tune_title))
                .setContentText(getString(R.string.nt_send_tune_text))
                .setProgress(0, 0, true)
                .setPriority(-1)
                .setOngoing(true)
                .build();
    }

    @SuppressLint("InlinedApi")
    private PendingIntent preparePendingIntent() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return PendingIntent.getActivity(
                getApplicationContext(),
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void displayNotification(Notification notification) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(myNotificationId , notification);
    }

}
