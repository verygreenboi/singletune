package com.pixel.singletune.app.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mrsmith on 8/11/14.
 */
public class TuneDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "data";

    public static final String TUNE_TABLE = "tune_table";
    public static final String T_ID = "_id";
    public static final String ARTISTE_OBJECT_ID = "aObjId";
    public static final String ARTISTE_NAME = "aName";
    public static final String TUNE_OBJECT_ID = "tObjId";
    public static final String TUNE_TITLE = "tTitle";
    public static final String TUNE_AUDIO_URL = "tAURL";
    public static final String TUNE_ART_URL = "tArtURL";

    private final String createDb = "create table if not exists "
            +TUNE_TABLE+" ("
            +T_ID +" integer primary key autoincrement, "
            +ARTISTE_OBJECT_ID +" text, "
            +ARTISTE_NAME +" text, "
            +TUNE_OBJECT_ID +" text, "
            +TUNE_TITLE +" text, "
            +TUNE_AUDIO_URL +" text, "
            +TUNE_ART_URL +" text); ";



    public TuneDbHelper(Context context,
                        String name,
                        SQLiteDatabase.CursorFactory factory,
                        int version) {

        super(context, DATABASE_NAME, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createDb);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table "+ TUNE_TABLE);
    }
}
