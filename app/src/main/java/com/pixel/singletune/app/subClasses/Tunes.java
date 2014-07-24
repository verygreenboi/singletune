package com.pixel.singletune.app.subClasses;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.pixel.singletune.app.ParseConstants;

/**
 * Created by mrsmith on 4/27/14.
 */
@ParseClassName("Tunes")
public class Tunes extends ParseObject {
    public Tunes() {

    }

    public String getTitle() {
        return getString("Title");
    }

    public void setTitle(String title) {
        put("Title", title);
    }

    public ParseUser getArtist() {
        return getParseUser("parent");
    }

    public void setArtist(ParseUser user) {
        put("parent", user);
    }

    public ParseFile getSongFile() {
        return getParseFile("file");
    }

    public void setSongFile(ParseFile file) {
        put("file", file);
    }

    public ParseFile getCoverArt() {
        return getParseFile("coverArt");
    }

    public void setCoverArt(ParseFile file) {
        put("coverArt", file);
    }

    public String getFileType() {
        return getString(ParseConstants.KEY_FILE_TYPE);
    }

    public void setFileType(String fileType) {
        put(ParseConstants.KEY_FILE_TYPE, fileType);
    }

    public int getLikeCount(){
        return getInt("Likes");
    }

    public void increaseLike(int likeCount){
        increment("Likes", +likeCount);
    }
    public void reduceLike(int likeCount){
        if (getLikeCount() > 0) {
            increment("Likes", -likeCount);
        }
    }

}
