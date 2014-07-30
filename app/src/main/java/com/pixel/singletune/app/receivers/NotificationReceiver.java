package com.pixel.singletune.app.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.pixel.singletune.app.R;
import com.pixel.singletune.app.ui.NotificationsActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by mrsmith on 6/1/14.
 */
public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "MyCustomReceiver";
    private String msg = "";
    private String tlt = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent == null)
            {
                Log.d(TAG, "Receiver intent null");
            }
            else
            {
                String action = intent.getAction();
                Log.d(TAG, "got action " + action );

                buildHash(intent, action);

            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }

        buildNotification(context);
    }

    private void buildNotification(Context context) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources()
                , R.drawable.ic_launcher);

        Intent la = new Intent(context, NotificationsActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, la, 0);

        Notification noti = new NotificationCompat.Builder(context)
                .setContentTitle(tlt)
                .setContentText(msg)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(icon)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .build();

        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(0, noti);
    }

    private void buildHash(Intent intent, String action) throws JSONException {
        String channel = intent.getExtras().getString("com.parse.Channel");
        JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

        Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
        Iterator itr = json.keys();
        while (itr.hasNext()) {
            String key = (String) itr.next();
            if (key.equals("data"))
            {
                tlt = json.getString(key);
            }
            if (key.equals("msg")){
                msg = json.getString(key);
            }
            if (key.equals("push_hash")){
                String hash = json.getString(key);
            }
            Log.d(TAG, "..." + key + " => " + json.getString(key));
        }
    }


}
