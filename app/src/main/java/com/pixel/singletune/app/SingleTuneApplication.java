package com.pixel.singletune.app;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.PushService;
import com.pixel.singletune.app.subClasses.Comments;
import com.pixel.singletune.app.subClasses.Tunes;
import com.pixel.singletune.app.ui.NotificationsActivity;

/**
 * Created by smith on 3/30/14.
 */
public class SingleTuneApplication extends Application {
    public static final String TAG = "SingleTune";

    public static void UpdateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //TODO: Remember to change these keys!!!
        ParseObject.registerSubclass(Tunes.class);
        ParseObject.registerSubclass(Comments.class);
        Parse.initialize(this, "rvPIzES8Mg0ChzNaZtrJ6udORV3ggajrjUlZMZ8e", "4WtUhM0JDyW1zmXlbjrW7HDbFthNtqBI2F44bvj2");
        ParseFacebookUtils.initialize("297125393789149");
        ParseTwitterUtils.initialize("KWzP9gru9afljRlSklYSREDJr", "Tx9gumxoFsD6y1AHPavXGa9p1IU6lTcKuDuTlzmF6Sc3gm4STo");
        PushService.setDefaultPushCallback(this, NotificationsActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }
}
