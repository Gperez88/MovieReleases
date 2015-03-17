package com.gperez88.moviereleases.app.tasks;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.gperez88.moviereleases.app.data.MovieContract.CountryEntry;
import com.gperez88.moviereleases.app.data.MovieContract.MovieEntry;
import com.gperez88.moviereleases.app.services.MovieService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Vector;

/**
 * Created by GPEREZ on 3/16/2015.
 */
public class MovieTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = MovieTask.class.getSimpleName();

    private final Context mContext;

    public MovieTask(Context mContext) {
        this.mContext = mContext;
    }

    public long addCountry(String codeCountrySetting, String countryName) {
        long countryId;

        Cursor countryCursor = mContext.getContentResolver().query(
                CountryEntry.CONTENT_URI,
                new String[]{CountryEntry._ID},
                CountryEntry.COLUMN_CODE + " = ?",
                new String[]{codeCountrySetting},
                null);

        if (countryCursor.moveToFirst()) {
            int locationIdIndex = countryCursor.getColumnIndex(CountryEntry._ID);
            countryId = countryCursor.getLong(locationIdIndex);
        } else {

            ContentValues countryValues = new ContentValues();

            countryValues.put(CountryEntry.COLUMN_CODE, codeCountrySetting);
            countryValues.put(CountryEntry.COLUMN_NAME, countryName);

            Uri insertedUri = mContext.getContentResolver().insert(
                    CountryEntry.CONTENT_URI,
                    countryValues
            );

            countryId = ContentUris.parseId(insertedUri);
        }

        countryCursor.close();

        return countryId;
    }

    public void getMovieDataFromJson(String[] jsonStrs, String codeCountrySetting, String countryNameSetting, String[] sections) throws JSONException {
        if (jsonStrs.length == 0 && sections.length == 0 && jsonStrs.length != sections.length)
            return;

        long countryId = addCountry(codeCountrySetting, countryNameSetting);

        for (int i = 0; i < jsonStrs.length; i++) {
            getMovieDataFromJson(jsonStrs[i], codeCountrySetting, countryNameSetting, countryId, sections[i]);
        }
    }

    public void getMovieDataFromJson(String jsonStr, String codeCountrySetting, String countryNameSetting, long countryId, String sectionUrl) throws JSONException {
        final String MOVIES = "movies";

        final String TITLE = "title";
        final String YEAR = "year";
        final String RELEASE_DATES = "release_dates";
        final String RELEASE_DATES_THEATER = "theater";
        final String SYNOPSIS = "synopsis";
        final String POSTERS = "posters";
        final String THUMBNAIL = "thumbnail";

        try {
            JSONObject movieJson = new JSONObject(jsonStr);
            JSONArray movieArray = movieJson.getJSONArray(MOVIES);

            Vector<ContentValues> cVVector = new Vector<>(movieArray.length());

            for (int i = 0; i < movieArray.length(); i++) {

                String title;
                int year;
                String releaseDates;
                String synopsis;
                String thumbnailUrl;

                JSONObject movie = movieArray.getJSONObject(i);

                title = movie.getString(TITLE);
                year = movie.getInt(YEAR);
                synopsis = movie.getString(SYNOPSIS);

                JSONObject releaseDatesJson = movie.getJSONObject(RELEASE_DATES);
                releaseDates = releaseDatesJson.getString(RELEASE_DATES_THEATER);

                JSONObject thumbnailUrlJson = movie.getJSONObject(POSTERS);
                thumbnailUrl = thumbnailUrlJson.getString(THUMBNAIL);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MovieEntry.COLUMN_YEAR, year);
                movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, releaseDates);
                movieValues.put(MovieEntry.COLUMN_SYNOPSIS, synopsis);
                movieValues.put(MovieEntry.COLUMN_THUMBNAIL_URL, thumbnailUrl);
                movieValues.put(MovieEntry.COLUMN_COUNTRY_ID, countryId);
                movieValues.put(MovieEntry.COLUMN_SECTION, sectionUrl);

                cVVector.add(movieValues);
            }

            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "MovieTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

//    public int getTotalMovieFromJson(String jsonStr) {
//        final String TOTAL = "total";
//
//        try {
//            JSONObject movieJson = new JSONObject(jsonStr);
//            return movieJson.getInt(TOTAL);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return 0;
//    }

    @Override
    protected Void doInBackground(String... params) {
        if (params.length == 0 && params.length < 3) {
            return null;
        }

        String codeCountryQuery = params[0];
        String countryNameQuery = params[1];

        int limit = 50;
        int page = 1;

        try {

            String boxOfficeJsonStr = MovieService.getMoviesBoxOffice(codeCountryQuery, limit);
            String inTheatersJsonStr = MovieService.getMoviesInTheaters(codeCountryQuery, limit, page);
            String openingJsonStr = MovieService.getMoviesOpening(codeCountryQuery, limit);
            String upcomingJsonStr = MovieService.getMoviesUpComing(codeCountryQuery, limit, page);

            //json string for sections.
            String[] jsonStrs = {boxOfficeJsonStr, inTheatersJsonStr, openingJsonStr, upcomingJsonStr};
            //sections.
            String[] sections = {MovieService.SECTION_BOX_OFFICE,
                    MovieService.SECTION_IN_THEATERS,
                    MovieService.SECTION_OPENING,
                    MovieService.SECTION_UNCOMING};

            getMovieDataFromJson(jsonStrs, codeCountryQuery, countryNameQuery, sections);

//            int pageLimit = 50;
//            int totalPage = 1;
//            int page = 1;
//            do {
//                String inTheatersJsonStr = MovieService.getMoviesInTheaters(codeCountryQuery, pageLimit, page);
//
//                getMovieDataFromJson(inTheatersJsonStr,codeCountryQuery, countryNameQuery,MovieService.SECTION_IN_THEATERS);
//
//                if(page == 1) {
//                    int totalMovie = getTotalMovieFromJson(inTheatersJsonStr);
//                    if (totalMovie > pageLimit) {
//                        totalPage = MovieUtils.roundPage(new Float(totalMovie / pageLimit));
//                    }
//                }
//                page++;
//
//            }while(page == totalPage);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        }

        return null;
    }
}
