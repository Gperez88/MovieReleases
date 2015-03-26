package com.gperez88.moviereleases.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.gperez88.moviereleases.app.data.MovieContract.MovieEntry;
import com.gperez88.moviereleases.app.data.MovieContract.TypeMovieEntry;

import java.util.HashSet;

/**
 * Created by GPEREZ on 3/15/2015.
 */
public class TestDB extends AndroidTestCase {

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() {
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(MovieEntry.TABLE_NAME);
        tableNameHashSet.add(TypeMovieEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        db.close();
    }

    public long testTypeMovieTable() {
        MovieDbHelper movieDbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        ContentValues contentValues = TestUtils.createTestTypeMovieValues();

        long typeMovieRowId;
        typeMovieRowId = db.insert(TypeMovieEntry.TABLE_NAME, null, contentValues);

        assertTrue(typeMovieRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                TypeMovieEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue("Error: No Records returned from type movie query", cursor.moveToFirst());

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtils.validateCurrentRecord("Error: Type movie Query Validation Failed",
                cursor, contentValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from location query",
                cursor.moveToNext());

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();

        return typeMovieRowId;
    }

    public void testMovieTable() {
        long typeMovieRowId = testTypeMovieTable();

        // Make sure we have a valid row ID.
        assertFalse("Error: Type Movie Not Inserted Correctly", typeMovieRowId == -1L);

        // First step: Get reference to writable database
        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Second Step (Movie): Create movie values
        ContentValues weatherValues = TestUtils.createTestMovieValues(typeMovieRowId);

        // Third Step (Movie): Insert ContentValues into database and get a row ID back
        long movieRowId = db.insert(MovieEntry.TABLE_NAME, null, weatherValues);
        assertTrue(movieRowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        // A cursor is your primary interface to the query results.
        Cursor movieCursor = db.query(
                MovieEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );


        // Move the cursor to the first valid database row and check to see if we have any rows
        assertTrue("Error: No Records returned from location query", movieCursor.moveToFirst());

        // Fifth Step: Validate the movie Query
        TestUtils.validateCurrentRecord("testInsertReadDb movieEntry failed to validate",
                movieCursor, weatherValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from movie query",
                movieCursor.moveToNext());

        // Sixth Step: Close cursor and database
        movieCursor.close();
        dbHelper.close();
    }
}
