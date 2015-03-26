package com.gperez88.moviereleases.app.tasks;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.gperez88.moviereleases.app.data.MovieContract.MovieEntry;
import com.gperez88.moviereleases.app.data.MovieContract.MovieTypeEntry;
import com.gperez88.moviereleases.app.services.MovieService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

/**
 * Created by GPEREZ on 3/16/2015.
 */
public class MovieTask extends AsyncTask<Void, Void, Void> {
    private final String LOG_TAG = MovieTask.class.getSimpleName();

    private final Context mContext;

    public MovieTask(Context mContext) {
        this.mContext = mContext;
    }

    public long addMovieType(String movieTypeString) {
        long movieTypeId;

        Cursor movieTypeCursor = mContext.getContentResolver().query(
                MovieTypeEntry.CONTENT_URI,
                new String[]{MovieTypeEntry._ID},
                MovieTypeEntry.COLUMN_TYPE + " = ?",
                new String[]{movieTypeString},
                null);

        if (movieTypeCursor.moveToFirst()) {
            int movieTypeIdIndex = movieTypeCursor.getColumnIndex(MovieTypeEntry._ID);
            movieTypeId = movieTypeCursor.getLong(movieTypeIdIndex);
        } else {

            ContentValues movieTypeValues = new ContentValues();

            movieTypeValues.put(MovieTypeEntry.COLUMN_TYPE, movieTypeString);
            movieTypeValues.put(MovieTypeEntry.COLUMN_DESCRIPTION, movieTypeString);

            Uri insertedUri = mContext.getContentResolver().insert(
                    MovieTypeEntry.CONTENT_URI,
                    movieTypeValues
            );

            movieTypeId = ContentUris.parseId(insertedUri);
        }

        movieTypeCursor.close();

        return movieTypeId;
    }

    public void insertMovieTypeFromJson(String movieTypeJsonStr) throws JSONException {
        final String TYPE = "type";
        final String DESCRIPTION = "description";

        if (movieTypeJsonStr == null)
            return;

        try {
            JSONArray movieTypeArrayJson = new JSONArray(movieTypeJsonStr);

            Vector<ContentValues> cVVector = new Vector<>(movieTypeArrayJson.length());

            for (int i = 0; i < movieTypeArrayJson.length(); i++) {

                String type;
                String description;

                JSONObject movie = movieTypeArrayJson.getJSONObject(i);

                type = movie.getString(TYPE);
                description = movie.getString(DESCRIPTION);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieTypeEntry.COLUMN_TYPE, type);
                movieValues.put(MovieTypeEntry.COLUMN_DESCRIPTION, description);
                cVVector.add(movieValues);
            }

            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieTypeEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "MovieTask Complete. " + inserted + " MovieType Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public void insertMovieDataFromJson(String movieJsonStr) throws JSONException {

        final String TITLE = "title";
        final String THUMBNAIL_URL = "thumbnail_url";
        final String SYNOPSIS = "synopsis";
        final String RELEASE_DATE = "release_date";
        final String DURATION = "duration";
        final String MOVIE_TYPE_ID = "type_id";

        if (movieJsonStr == null)
            return;

        try {
            JSONArray movieArrayJson = new JSONArray(movieJsonStr);

            Vector<ContentValues> cVVector = new Vector<>(movieArrayJson.length());

            for (int i = 0; i < movieArrayJson.length(); i++) {

                String title;
                String thumbnailUrl;
                String synopsis;
                String releaseDates;
                String duration;
                long movieTypeId;

                JSONObject movie = movieArrayJson.getJSONObject(i);
                Log.v(LOG_TAG, "Movie Object: "+ movie.toString());

                title = movie.getString(TITLE);
                Log.d(LOG_TAG,"Title: "+ title);
                thumbnailUrl = movie.getString(THUMBNAIL_URL);
                synopsis = movie.getString(SYNOPSIS);
                releaseDates = movie.getString(RELEASE_DATE);
                duration = movie.getString(DURATION);
                movieTypeId = movie.getLong(MOVIE_TYPE_ID);


                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MovieEntry.COLUMN_THUMBNAIL_URL, thumbnailUrl);
                movieValues.put(MovieEntry.COLUMN_SYNOPSIS, synopsis);
                movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDates);
                movieValues.put(MovieEntry.COLUMN_DURATION, duration);
                movieValues.put(MovieEntry.COLUMN_TYPE_MOVIE_ID, movieTypeId);

                cVVector.add(movieValues);
            }

            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "MovieTask Complete. " + inserted + " Movie Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {

            String movieTypeJsonStr = MovieService.callMovieTypeListService();
            String movieJsonStr = MovieService.callMovieListService();

            insertMovieDataFromJson(movieJsonStr);
            insertMovieTypeFromJson(movieTypeJsonStr);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        }

        return null;
    }
}
