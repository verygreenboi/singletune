package com.pixel.singletune.app.utils;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pixel.singletune.app.ParseConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mrsmith on 7/28/14.
 */
public class FollowNotifiyer extends Notifyer {
    String userId;
    boolean followStatus;

    public FollowNotifiyer(ParseUser currentUser, String userId, String action, boolean followStatus) {
        super();
        this.currentUser = currentUser;
        this.userId = userId;
        this.action = action;
        this.followStatus = followStatus;
    }

    @Override
    public void sendNotification(){
        ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
        query.whereEqualTo(ParseConstants.KEY_USER_ID, userId);
        try {
            obj = new JSONObject();
            if (followStatus) {
                obj.put("msg", "Yay! " + currentUser.getUsername() + " is now following you.");
            } else {
                obj.put("msg    ", "Aww! " + currentUser.getUsername() + " has unfollowed you.");
            }
            obj.put("data", "You have a new activity");
            obj.put("action", action);
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
