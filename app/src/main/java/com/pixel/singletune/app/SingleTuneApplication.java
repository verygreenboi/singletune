package com.pixel.singletune.app;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.orm.SugarApp;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.PushService;
import com.pixel.singletune.app.subClasses.Activities;
import com.pixel.singletune.app.subClasses.Comments;
import com.pixel.singletune.app.subClasses.Tunes;
import com.pixel.singletune.app.ui.NotificationsActivity;
import com.pixel.singletune.app.utils.LruBitmapCache;
import com.pixel.singletune.app.utils.UIUtils;

/**
 * Created by smith on 3/30/14.
 */
public class SingleTuneApplication extends SugarApp {
    public static final String TAG = "SingleTune";
    private static  SingleTuneApplication mInstance;
    LruBitmapCache mLruBitmapCache;
    private RequestQueue mRequestQueue;
    private  ImageLoader mImageLoader;

    public static void UpdateParseInstallation(ParseUser user) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
        installation.saveInBackground();
    }

    public static synchronized SingleTuneApplication getmInstance(){
        return mInstance;
    }

    public static boolean isTablet(Context c) {
        return UIUtils.isTablet(c);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //TODO: Remember to change these keys!!!
        ParseObject.registerSubclass(Tunes.class);
        ParseObject.registerSubclass(Comments.class);
        ParseObject.registerSubclass(Activities.class);
        Parse.initialize(this, "rvPIzES8Mg0ChzNaZtrJ6udORV3ggajrjUlZMZ8e", "4WtUhM0JDyW1zmXlbjrW7HDbFthNtqBI2F44bvj2");
        ParseFacebookUtils.initialize("297125393789149");
        ParseTwitterUtils.initialize("KWzP9gru9afljRlSklYSREDJr", "Tx9gumxoFsD6y1AHPavXGa9p1IU6lTcKuDuTlzmF6Sc3gm4STo");
        PushService.setDefaultPushCallback(this, NotificationsActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }

    public RequestQueue getmRequestQueue(){
        if (mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public ImageLoader getmImageLoader(){
        getmRequestQueue();
        if (mImageLoader == null){
            getLruBitCache();
            mImageLoader = new ImageLoader(this.getmRequestQueue(), mLruBitmapCache);
        }
        return this.mImageLoader;
    }

    public LruBitmapCache getLruBitCache(){
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();

        return  this.mLruBitmapCache;
    }

    public <T> void addToRequestQueue(Request<T> req){
        req.setTag(TAG);
        getmRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag){
        if (mRequestQueue != null){
            mRequestQueue.cancelAll(tag);
        }
    }
}
