package com.gperez88.moviereleases.app.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

/**
 * Created by GPEREZ on 3/16/2015.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();
    }

   public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testGetType() {
        // content://com.gperez88.moviereleases.app/movie/
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.gperez88.moviereleases.app/movie
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

        long movieId = 1L;
        // content://com.gperez88.moviereleases.app/movie/1
        type = mContext.getContentResolver().getType(
                MovieContract.MovieEntry.buildMovieUri(movieId));
        // vnd.android.cursor.dir/com.gperez88.moviereleases.app/movie
        assertEquals("Error: the MovieEntry CONTENT_URI with movieId should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);

        String testType = "Action & Adventure";
        // content://com.gperez88.moviereleases.app/movie/Action & Adventure
        type = mContext.getContentResolver().getType(
                MovieContract.MovieEntry.buildMovieWithType(testType));
        // vnd.android.cursor.dir/com.gperez88.moviereleases.app/movie
        assertEquals("Error: the MovieEntry CONTENT_URI with type should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);
    }

    public void testBasicWeatherQuery() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long type = 1L;

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = TestUtils.createTestMovieValues(type);

        long weatherRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, weatherValues);
        assertTrue("Unable to Insert MovieEntry into the Database", weatherRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor weatherCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtils.validateCursor("testBasicMovieQuery", weatherCursor, weatherValues);
    }
}
