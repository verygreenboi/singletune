package com.pixel.singletune.app;

import java.util.Locale;

/**
 * Created by smith on 3/31/14.
 */
public final class ParseConstants {

    // Class name
    public static final String CLASS_MESSAGES = "Messages";
    public static final String CLASS_TUNES = "Tunes";


    //      Field names
    public static final String KEY_USER = "_user";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_FRIENDS_RELATION = "friendsRelation";
    public static final String KEY_RECIPIENT_IDS = "recipientIds";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_FILE = "file";
    public static final String KEY_FILE_TYPE = "fileType";
    public static final String KEY_CREATED_AT = "createdAt";

    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_VOICE_NOTE = "voiceNote";
    public static final String TYPE_MP3 = "mp3";

    public static final String KEY_TUNE_TITLE = "Title";
    public static final String KEY_TUNE_DESC = "Description";
    public static final String BROADCAST_ACTION = "com.pixel.singletune.app.BROADCAST";
    public static final String EXTENDED_DATA_STATUS = "com.pixel.singletune.app.STATUS";
    public static final String EXTENDED_STATUS_LOG = "com.pixel.singletune.app.LOG";
    /*
     * A user-agent string that's sent to the HTTP site. It includes information about the device
     * and the build that the device is running.
     */
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android "
            + android.os.Build.VERSION.RELEASE + ";"
            + Locale.getDefault().toString() + "; " + android.os.Build.DEVICE
            + "/" + android.os.Build.ID + ")";
}
