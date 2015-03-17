package com.gperez88.moviereleases.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by GPEREZ on 3/14/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 4;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_COUNTRY_TABLE = "CREATE TABLE " + MovieContract.CountryEntry.TABLE_NAME + " (" +
                MovieContract.CountryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.CountryEntry.COLUMN_CODE + " TEXT NOT NULL, " +
                MovieContract.CountryEntry.COLUMN_NAME + " TEXT NOT NULL );";

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.MovieEntry.COLUMN_THUMBNAIL_URL + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_YEAR + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " DATE NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_COUNTRY_ID + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_SECTION + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + MovieContract.MovieEntry.COLUMN_COUNTRY_ID + ") REFERENCES " +
                MovieContract.CountryEntry.TABLE_NAME + " (" + MovieContract.CountryEntry._ID + "));";

        db.execSQL(SQL_CREATE_COUNTRY_TABLE);
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.CountryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
