package com.pixel.singletune.app.utils;

import com.parse.ParseUser;

/**
 * Created by mrsmith on 7/28/14.
 */
public class Notifyer {

    ParseUser from;
    String userId;
    String message;
    String action;

    public Notifyer(){

    }

    public Notifyer(ParseUser from, String userId, String message, String action){
        super();
        this.from = from;
        this.userId = userId;
        this.message = message;
        this.action = action;
    }

    public void sendNotification(){

    }

}
