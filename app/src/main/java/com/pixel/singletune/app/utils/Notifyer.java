package com.pixel.singletune.app.utils;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pixel.singletune.app.ParseConstants;
import com.pixel.singletune.app.subClasses.Tunes;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mrsmith on 7/28/14.
 */
public class Notifyer {

    public static final String LIKE_NOTIFICATION_ACTION = "com.pixel.singletune.app.LIKE_NOTIFICATION";
    public static final String FOLLOW_NOTIFICATION_ACTION = "com.pixel.singletune.app.UPDATE_STATUS";
    ParseUser from;
    Tunes mTune;
    String message;
    String action;
    JSONObject obj;
    ParseUser currentUser = ParseUser.getCurrentUser();

    public Notifyer(){

    }

    public Notifyer(ParseUser from, Tunes mTune, String message, String action){
        super();
        this.from = from;
        this.mTune = mTune;
        this.message = message;
        this.action = action;
    }

    public void sendNotification(){
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereEqualTo(ParseConstants.KEY_USER_ID, mTune.getArtist().getObjectId());
        String fMessage = message + currentUser.getUsername();
        try{

            obj = new JSONObject();
            obj.put("msg", fMessage);
            obj.put("data", "You have a new like.");
            obj.put("action", LIKE_NOTIFICATION_ACTION);
            obj.put("channel", "Activities");

            ParsePush push = new ParsePush();
            push.setQuery(query);
            push.setData(obj);
            push.sendInBackground();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
