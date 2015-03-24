package com.gperez88.moviereleases.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.gperez88.moviereleases.app.data.MovieContract.CountryEntry;
import com.gperez88.moviereleases.app.data.MovieContract.MovieEntry;

/**
 * Created by GPEREZ on 3/14/2015.
 */
public class MovieProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_BY_ID = 101;
    static final int MOVIE_WITH_COUNTRY = 102;
    static final int MOVIE_WITH_COUNTRY_AND_SECTION = 103;
    static final int COUNTRY = 300;

    private final static SQLiteQueryBuilder movieQueryBuilder;

    static {
        movieQueryBuilder = new SQLiteQueryBuilder();

        movieQueryBuilder.setTables(MovieEntry.TABLE_NAME + " INNER JOIN " +
                CountryEntry.TABLE_NAME +
                " ON " + MovieEntry.TABLE_NAME +
                "." + MovieEntry.COLUMN_COUNTRY_ID +
                " = " + CountryEntry.TABLE_NAME +
                "." + CountryEntry._ID);
    }

    private static final String sMovieIdSelection =
            MovieEntry.TABLE_NAME +
                    "." + MovieEntry._ID + " = ?";

    private static final String sCountryCodeSelection =
            CountryEntry.TABLE_NAME +
                    "." + CountryEntry.COLUMN_CODE + " = ? ";

    private static final String sCountryCodeAndSectionSelection =
            CountryEntry.TABLE_NAME +
                    "." + CountryEntry.COLUMN_CODE + " = ? AND " +
                    MovieEntry.COLUMN_SECTION + " = ? ";

    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {
        long movieId = ContentUris.parseId(uri);

        String[] selectionArgs;
        String selection;

        selection = sMovieIdSelection;
        selectionArgs = new String[]{Long.toString(movieId)};

        return movieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMovieByCodeCountry(Uri uri, String[] projection, String sortOrder) {
        String countryCode = MovieEntry.getCodeCountryFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sCountryCodeSelection;
        selectionArgs = new String[]{countryCode};

        return movieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMovieByCodeCountryAndSection(Uri uri, String[] projection, String sortOrder) {
        String countryCode = MovieEntry.getCodeCountryFromUri(uri);
        String section = MovieEntry.getSectionFromUri(uri);

        String[] selectionArgs;
        String selection;

        selection = sCountryCodeAndSectionSelection;
        selectionArgs = new String[]{countryCode, section};

        return movieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_BY_ID);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIE_WITH_COUNTRY);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*/*", MOVIE_WITH_COUNTRY_AND_SECTION);

        matcher.addURI(authority, MovieContract.PATH_COUNTRY, COUNTRY);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case MOVIE_BY_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_WITH_COUNTRY:
                return MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_COUNTRY_AND_SECTION:
                return MovieEntry.CONTENT_TYPE;
            case MOVIE:
                return MovieEntry.CONTENT_TYPE;
            case COUNTRY:
                return CountryEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            // "movie/#"
            case MOVIE_BY_ID:{
                retCursor = getMovieById(uri,projection,sortOrder);
                break;
            }
            // "movie/*"
            case MOVIE_WITH_COUNTRY: {
                retCursor = getMovieByCodeCountry(uri, projection, sortOrder);
                break;
            }
            // "movie/*/*"
            case MOVIE_WITH_COUNTRY_AND_SECTION: {
                retCursor = getMovieByCodeCountryAndSection(uri, projection, sortOrder);
                break;
            }
            // "movie"
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "country"
            case COUNTRY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CountryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case COUNTRY: {
                long _id = db.insert(CountryEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = CountryEntry.buildCountryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case COUNTRY:
                rowsDeleted = db.delete(
                        CountryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case COUNTRY:
                rowsUpdated = db.update(CountryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
