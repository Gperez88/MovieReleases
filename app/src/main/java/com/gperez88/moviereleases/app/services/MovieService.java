package com.gperez88.moviereleases.app.services;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by GPEREZ on 3/16/2015.
 */
public class MovieService {
    private static final String LOG_TAG = MovieService.class.getSimpleName();

    private static final String API_KEY = "5szncvhqdyhrkwecqsrtacwr";

    public static String getMovies(String codeCountry){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr = null;

        try {
            final String MOVIE_BASE_URL = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/in_theaters.json?";
            final String COUNTRY_PARAM = "country";
            final String API_KEY_PARAM = "apikey";

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendQueryParameter(COUNTRY_PARAM, codeCountry)
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.v(LOG_TAG, "Buit URI " + builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            moviesJsonStr = buffer.toString();

            Log.v(LOG_TAG, "Movie JSON String: " + moviesJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return moviesJsonStr;
    }
}
