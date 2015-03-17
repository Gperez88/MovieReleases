package com.gperez88.moviereleases.app.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.gperez88.moviereleases.app.data.MovieContract.CountryEntry;
import com.gperez88.moviereleases.app.data.MovieContract.MovieEntry;

/**
 * Created by GPEREZ on 3/16/2015.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                CountryEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Weather table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                CountryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Location table during delete", 0, cursor.getCount());
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
        String type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.gperez88.moviereleases.app/movie
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);

        String testCountry = "us";
        // content://com.gperez88.moviereleases.app/movie/us
        type = mContext.getContentResolver().getType(
                MovieEntry.buildMovieCountry(testCountry));
        // vnd.android.cursor.dir/com.gperez88.moviereleases.app/movie
        assertEquals("Error: the MovieEntry CONTENT_URI with country should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);

        String testSection = "opening";
        // content://com.gperez88.moviereleases.app/movie/us/opening
        type = mContext.getContentResolver().getType(
                MovieEntry.buildMovieCountryWithSeccion(testCountry, testSection));
        // vnd.android.cursor.dir/com.gperez88.moviereleases.app/movie
        assertEquals("Error: the MovieEntry CONTENT_URI with country and section should return MovieEntry.CONTENT_TYPE",
                MovieEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(CountryEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals("Error: the CountryEntry CONTENT_URI should return CountryEntry.CONTENT_TYPE",
                CountryEntry.CONTENT_TYPE, type);
    }

    public void testBasicWeatherQuery() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long countryRowId = TestUtils.insertTestCountryValues(mContext);
        String section = "opening";

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = TestUtils.createTestMovieValues(countryRowId, section);

        long weatherRowId = db.insert(MovieEntry.TABLE_NAME, null, weatherValues);
        assertTrue("Unable to Insert MovieEntry into the Database", weatherRowId != -1);

        db.close();

        // Test the basic content provider query
        Cursor weatherCursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtils.validateCursor("testBasicMovieQuery", weatherCursor, weatherValues);
    }
}
