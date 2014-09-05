package com.pixel.singletune.app.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.pixel.singletune.app.helpers.TuneDbHelper;

/**
 * Created by mrsmith on 9/1/14.
 */
public class TuneContentProvider extends ContentProvider {

    final static int TUNES = 1;
    private static final String AUTH = "com.pixel.singletune.app.providers.TuneContentProvider";
    public static final Uri TUNE_URI = Uri.parse("content://"+AUTH+"/"+ TuneDbHelper.TUNE_TABLE);
    private static final int DATA_ALLROWS = 1;
    private static final int DATA_SINGLE_ROW = 2;
    private final static UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTH, TuneDbHelper.TUNE_TABLE, DATA_ALLROWS);
        uriMatcher.addURI(AUTH, TuneDbHelper.TUNE_TABLE+"/#", DATA_SINGLE_ROW);
    }
    SQLiteDatabase db;
    TuneDbHelper dbHelper;

    @Override
    public boolean onCreate() {

        dbHelper = new TuneDbHelper(getContext(),
                TuneDbHelper.DATABASE_NAME,
                null,
                1);

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        int count;

        switch(uriMatcher.match(uri)){
            case DATA_ALLROWS:
                qb.setTables(TuneDbHelper.TUNE_TABLE);
                break;
            case DATA_SINGLE_ROW:
                qb.setTables(TuneDbHelper.TUNE_TABLE);
                qb.appendWhere(TuneDbHelper.T_ID+" = "+ uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        db = dbHelper.getWritableDatabase();

        if (uriMatcher.match(uri)==TUNES){
            db.insert(TuneDbHelper.TUNE_TABLE, null, contentValues);
        }

        db.close();

        getContext().getContentResolver().notifyChange(uri, null);

        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
