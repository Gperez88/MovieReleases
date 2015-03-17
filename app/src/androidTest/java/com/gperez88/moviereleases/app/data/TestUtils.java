package com.gperez88.moviereleases.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.gperez88.moviereleases.app.data.MovieContract.CountryEntry;
import com.gperez88.moviereleases.app.data.MovieContract.MovieEntry;

import java.util.Map;
import java.util.Set;

/**
 * Created by GPEREZ on 3/15/2015.
 */
public class TestUtils extends AndroidTestCase {

    static ContentValues createTestCountryValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(CountryEntry.COLUMN_CODE, "DO");
        testValues.put(CountryEntry.COLUMN_NAME, "Dominican Republic");

        return testValues;
    }

    static ContentValues createTestMovieValues(long countryRowId) {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(MovieEntry.COLUMN_THUMBNAIL_URL, "http://resizing.flixster.com/YKGbQ15uM3ETr8gBGCoO0g4ML0Y=/54x80/dkpu1ddg7pbsk.cloudfront.net/movie/11/18/15/11181570_ori.jpg");
        testValues.put(MovieEntry.COLUMN_TITLE, "Cinderella");
        testValues.put(MovieEntry.COLUMN_YEAR, 2015);
        testValues.put(MovieEntry.COLUMN_RELEASE_DATE, "2015-03-13");
        testValues.put(MovieEntry.COLUMN_SYNOPSIS, "Cate Blanchett stars in this new vision of the Cinderella tale from director Kenneth Branagh and the screenwriting team of Chris Weitz and Aline Brosh McKenna for Disney Pictures. ~ Jeremy Wheeler, Rovi");
        testValues.put(MovieEntry.COLUMN_COUNTRY_ID, countryRowId);

        return testValues;
    }

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
    Students: You can uncomment this function once you have finished creating the
    LocationEntry part of the WeatherContract as well as the WeatherDbHelper.
 */
    static long insertTestCountryValues(Context context) {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtils.createTestCountryValues();

        long countryRowId;
        countryRowId = db.insert(CountryEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert North Pole Location Values", countryRowId != -1);

        return countryRowId;
    }
}
