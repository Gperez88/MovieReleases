package com.gperez88.moviereleases.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by GPEREZ on 3/14/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TYPE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieTypeEntry.TABLE_NAME + " (" +
                MovieContract.MovieTypeEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.MovieTypeEntry.COLUMN_TYPE + " TEXT NOT NULL, " +
                MovieContract.MovieTypeEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                " UNIQUE (" + MovieContract.MovieTypeEntry._ID + ", " +
                MovieContract.MovieTypeEntry.COLUMN_TYPE + " ) ON CONFLICT REPLACE);";

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_THUMBNAIL_URL + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " DATE NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_DURATION + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_TYPE_MOVIE_ID + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + MovieContract.MovieEntry.COLUMN_TYPE_MOVIE_ID + ") REFERENCES " +
                MovieContract.MovieTypeEntry.TABLE_NAME + " (" + MovieContract.MovieTypeEntry._ID + "), " +
                " UNIQUE (" + MovieContract.MovieEntry.COLUMN_TITLE + ", " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + ", " +
                MovieContract.MovieEntry.COLUMN_DURATION + ", " +
                MovieContract.MovieEntry.COLUMN_TYPE_MOVIE_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_TYPE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieTypeEntry.TABLE_NAME);
        onCreate(db);
    }
}
