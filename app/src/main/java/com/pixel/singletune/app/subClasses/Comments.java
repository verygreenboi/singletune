package com.pixel.singletune.app.subClasses;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by mrsmith on 5/30/14.
 */
@ParseClassName("Comments")
public class Comments extends ParseObject {

    public Comments() {

    }

    public String getmSenderUsername() {
        return getString("senderUsername");
    }

    public void setmSenderUsername(String mSenderUsername) {
        put("senderUsername", mSenderUsername);
    }

    public String getmComment() {
        return getString("comment");
    }

    public void setmComment(String mComment) {
        put("comment", mComment);
    }

    public String getmReceiverUsername() {
        return getString("receiverUsername");
    }

    public void setmReceiverUsername(String mReceiverUsername) {
        put("receiverUsername", mReceiverUsername);
    }

    public ParseObject getmTune() {
        return new ParseObject("tune");
    }

    public void setmTune(ParseObject mTune) {
        put("tune", mTune);
    }
}