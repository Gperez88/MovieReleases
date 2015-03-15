package com.gperez88.moviereleases.data;

import android.provider.BaseColumns;

/**
 * Created by GPEREZ on 3/14/2015.
 */
public class MovieContract {


    public final static class MovieEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_THUMBNAIL_URL = "thumbnail_url";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_SYNOPSIS = "synopsis";

    };
}
