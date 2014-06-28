package com.pixel.singletune.app.utils;

import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.pixel.singletune.app.ParseConstants;

/**
 * Created by mrsmith on 4/27/14.
 */
public final class LinkUserToInstallationHelper {
    public static class LinkUserToInstallation {
        public static void invoke() {
            // Associate the device with a user
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("user", ParseUser.getCurrentUser());
            installation.put(ParseConstants.KEY_USER_ID, ParseUser.getCurrentUser().getObjectId());
            installation.saveInBackground();
        }
    }
}
