package com.gperez88.moviereleases.app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.gperez88.moviereleases.app.data.MovieContract.MovieEntry;
import com.gperez88.moviereleases.app.data.MovieContract.CountryEntry;

/**
 * Created by GPEREZ on 3/14/2015.
 */
public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_COUNTRY = 101;
    static final int COUNTRY = 300;

    private final static SQLiteQueryBuilder  movieQueryBuilder;

    static{
        movieQueryBuilder = new SQLiteQueryBuilder();

        movieQueryBuilder.setTables(MovieEntry.TABLE_NAME + "INNER JOIN " +
                        CountryEntry.TABLE_NAME +
                        " ON " + MovieEntry.TABLE_NAME +
                        "." + MovieEntry.COLUMN_COUNTRY_ID +
                        " = " + CountryEntry.TABLE_NAME +
                        "." + CountryEntry._ID);
    }

    private static final String sCountryCodeSelection =
            CountryEntry.TABLE_NAME+
                    "." + CountryEntry.COLUMN_CODE + " = ? ";

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    static UriMatcher buildUriMatcher() {
        return null;
    }
}
