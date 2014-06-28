package com.pixel.singletune.app;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;
import com.pixel.singletune.app.subClasses.Comments;
import com.pixel.singletune.app.subClasses.Tunes;
import com.pixel.singletune.app.ui.NotificationsActivity;

/**
 * Created by smith on 3/30/14.
 */
public class SingleTuneApplication extends Application {
    public static void UpdateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // CalligraphyConfig.initDefault("fonts/Roboto-Regular.ttf");
        //TODO: Remember to change these keys!!!
        ParseObject.registerSubclass(Tunes.class);
        ParseObject.registerSubclass(Comments.class);
        Parse.initialize(this, "rvPIzES8Mg0ChzNaZtrJ6udORV3ggajrjUlZMZ8e", "4WtUhM0JDyW1zmXlbjrW7HDbFthNtqBI2F44bvj2");
//        ParseFacebookUtils.initialize("297125393789149");
        PushService.setDefaultPushCallback(this, NotificationsActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }
}
