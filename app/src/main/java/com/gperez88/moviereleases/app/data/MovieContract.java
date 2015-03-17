package com.gperez88.moviereleases.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by GPEREZ on 3/14/2015.
 */
public class MovieContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.gperez88.moviereleases.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_COUNTRY = "country";
    public static final String PATH_MOVIE = "movie";

    public final static class CountryEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COUNTRY).build();

        //multiple items
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COUNTRY;

        //single item
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COUNTRY;

        //Table name
        public static final String TABLE_NAME = "country";

        public static final String COLUMN_CODE = "code";
        public static final String COLUMN_NAME = "name";

        public static Uri buildCountryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    };

    public final static class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        //multiple items
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        //single item
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;


        // Table name
        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_SYNOPSIS = "synopsis";
        public static final String COLUMN_COUNTRY_ID = "country_id";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieCountry(String codeCountrySetting) {
            return CONTENT_URI.buildUpon().appendPath(codeCountrySetting).build();
        }

        public static String getCountryFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    };

}
