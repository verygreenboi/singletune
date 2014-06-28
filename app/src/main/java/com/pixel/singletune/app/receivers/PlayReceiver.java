package com.pixel.singletune.app.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.pixel.singletune.app.ParseConstants;

public class PlayReceiver {

    private LocalBroadcastManager mBroadcaster;

    public PlayReceiver(Context context) {
        // Gets an instance of the support library local broadcastmanager
        mBroadcaster = LocalBroadcastManager.getInstance(context);
    }

    /**
     * Uses LocalBroadcastManager to send an {@link Intent} containing {@code status}. The
     * {@link Intent} has the action {@code BROADCAST_ACTION} and the category {@code DEFAULT}.
     *
     * @param status {@link Integer} denoting a work request status
     */
    public void broadcastIntentWithState(int status) {

        Intent localIntent = new Intent();

        // The Intent contains the custom broadcast action for this app
        localIntent.setAction(ParseConstants.BROADCAST_ACTION);

        // Puts the status into the Intent
        localIntent.putExtra(ParseConstants.EXTENDED_DATA_STATUS, status);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);

        // Broadcasts the Intent
        mBroadcaster.sendBroadcast(localIntent);

    }

    /**
     * Uses LocalBroadcastManager to send an {@link String} containing a logcat message.
     * {@link Intent} has the action {@code BROADCAST_ACTION} and the category {@code DEFAULT}.
     *
     * @param logData a {@link String} to insert into the log.
     */
    public void notifyProgress(String logData) {

        Intent localIntent = new Intent();

        // The Intent contains the custom broadcast action for this app
        localIntent.setAction(ParseConstants.BROADCAST_ACTION);

        localIntent.putExtra(ParseConstants.EXTENDED_DATA_STATUS, -1);

        // Puts log data into the Intent
        localIntent.putExtra(ParseConstants.EXTENDED_STATUS_LOG, logData);
        localIntent.addCategory(Intent.CATEGORY_DEFAULT);

        // Broadcasts the Intent
        mBroadcaster.sendBroadcast(localIntent);

    }

}


