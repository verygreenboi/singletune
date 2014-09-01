package com.pixel.singletune.app.subClasses;

import android.content.Context;
import android.net.Uri;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.pixel.singletune.app.ParseConstants;
import com.pixel.singletune.app.helpers.FileHelper;

/**
 * Created by mrsmith on 4/27/14.
 */
@ParseClassName("Tunes")
public class Tunes extends ParseObject {

    private ParseUser mCurrentUser;

    public Tunes() {}

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

    public void sendTune(final Context ctx, String title, final Tunes t, Uri mTuneUri, final String mFileType,final Uri aUri){
        mCurrentUser = ParseUser.getCurrentUser();
        t.setArtist(ParseUser.getCurrentUser());
        t.setTitle(title);
        byte[] fileBytes = FileHelper.getByteArrayFromFile(ctx, mTuneUri);

        String fileName = FileHelper.getFileName(ctx, mTuneUri, mFileType);
        final ParseFile file = new ParseFile(fileName, fileBytes);

        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                t.setSongFile(file);
                t.setFileType(mFileType);
                if (aUri != null){
                    byte[] fb = FileHelper.getByteArrayFromFile(ctx, aUri);
                    String fn = FileHelper.getFileName(ctx, aUri, ParseConstants.TYPE_IMAGE);
                    ParseFile ia = new ParseFile(fn, fb);
                    t.setCoverArt(ia);
                }
                t.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Activities activity = new Activities();
                        activity.setActivityType("tune");
                        activity.setFrom(mCurrentUser);
                        activity.setTuneId(t.getObjectId());
                    }
                });
            }
        });

    }

}
