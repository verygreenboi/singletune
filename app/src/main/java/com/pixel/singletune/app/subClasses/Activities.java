package com.pixel.singletune.app.subClasses;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by mrsmith on 8/6/14.
 * Subclass for Activities class on Parse.com
 */

@ParseClassName("Activities")
public class Activities extends ParseObject {
    public Activities(){

    }

    public String getActivityType(){
        return getString("activity");
    }

    public void setActivityType(String activity){
        put("activity", activity);
    }

    public ParseUser getFrom(){
        return getParseUser("from");
    }

    public void setFrom(ParseUser user){
        put("from", user);
    }

    public ParseUser getTo(){
        return getParseUser("to");
    }

    public void setTo(ParseUser user){
        put("to", user);
    }

    public String getTuneId(){
        return getString("tune");
    }

    public void setTuneId(String t){
        put("tune", t);
    }

}
