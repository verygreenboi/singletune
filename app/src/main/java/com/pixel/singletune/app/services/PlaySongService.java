package com.pixel.singletune.app.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.pixel.singletune.app.R;
import com.pixel.singletune.app.ui.MainActivity;

import java.io.IOException;

public class PlaySongService extends Service
        implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener {

    private MediaPlayer mp = new MediaPlayer();
    private String setAudioLink;

    //Set notificationID
    private static final int NOTIFICATION_ID = 1;

    private boolean isPausedInCall = false;
    private PhoneStateListener psl;
    private TelephonyManager tm;

    //Setup broadcast identifier and intent
    public static final String BROADCAST_BUFFER = "com.pixel.singletune.app.broadcastbuffer";
    Intent bufferIntent;

    @Override
    public void onCreate() {

        // Instantiate bufferIntent

        bufferIntent = new Intent(BROADCAST_BUFFER);

        mp.setOnCompletionListener(this);
        mp.setOnBufferingUpdateListener(this);
        mp.setOnPreparedListener(this);
        mp.setOnErrorListener(this);
        mp.setOnSeekCompleteListener(this);
        mp.setOnBufferingUpdateListener(this);
        mp.reset();
    }

    @Override
    public int onStartCommand(Intent i, int flag, int startID) {

        /*
            Manage incoming calls
         */

        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        psl = new PhoneStateListener() {

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mp != null) {
                            pauseMedia();
                            isPausedInCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (mp != null) {
                            if (isPausedInCall) {
                                isPausedInCall = false;
                                playMedia();
                            }
                        }
                        break;
                }
            }
        };

        tm.listen(psl, PhoneStateListener.LISTEN_CALL_STATE);

        //Create Notification
        createNotification();

        //Get url from intent
        setAudioLink = i.getExtras().getString("tuneURL");

        //Reset MediaPlayer
        mp.reset();

        //Setup MediaPlayer

        if (!mp.isPlaying()) {
            try {
                mp.setDataSource(setAudioLink);

                sendBufferingBroadcast();

                //Prepare MediaPlayer
                mp.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        return START_STICKY;
    }

    private void sendBufferingBroadcast() {
        bufferIntent.putExtra("Buffering", "1");
        sendBroadcast(bufferIntent);
    }

    private void sendBufferCompleteBroadcast() {
        bufferIntent.putExtra("Buffering", "0");
        sendBroadcast(bufferIntent);
    }


    //Destroy service

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
                mp.reset();
            }
            mp.release();
        }

        if (psl != null) {
            tm.listen(psl, PhoneStateListener.LISTEN_NONE);
        }

        destroyNotification();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopMedia();
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Toast.makeText(this, R.string.progressive_error_message, Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Toast.makeText(this, R.string.dead_server_error_message, Toast.LENGTH_SHORT).show();
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Toast.makeText(this, R.string.unknown_media_error_message, Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        sendBufferCompleteBroadcast();
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }

    public void playMedia() {
        if (!mp.isPlaying()) {
            mp.start();
        }
    }

    public void pauseMedia() {
        if (mp.isPlaying()) {
            mp.pause();
        }
    }

    public void stopMedia() {
        if (mp.isPlaying()) {
            mp.stop();
        }
    }

    private void createNotification() {

        Context context = getApplicationContext();
        CharSequence contentTitle = "SingleTune";
        CharSequence contentText = "Playing...";

        Bitmap icon = BitmapFactory.decodeResource(context.getResources()
                , R.drawable.ic_launcher);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        Notification noti = new NotificationCompat.Builder(context)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setLargeIcon(icon)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();
        noti.flags = Notification.FLAG_ONGOING_EVENT;

        NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        nm.notify(0, noti);
    }

    private void destroyNotification() {
    }
}