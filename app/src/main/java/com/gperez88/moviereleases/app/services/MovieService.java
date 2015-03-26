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

    //http://perezgabriel89.com/movie/api/v1.0/
    private static final String MOVIE_BASE_URL = "http://perezgabriel89.com/movie/api/v1.0/";
    private static final String API_KEY_PARAM = "apikey";

    private static final String API_KEY = "zs1pv@movieusrs@1988";

    private static final String MOVIE_LIST = "movie";
    private static final String MOVIE_TYPE_LIST = "type_movie";

    public static String callMovieListService(){
        return callBaseService(MOVIE_LIST);
    }

    public static String callMovieTypeListService(){
        return callBaseService(MOVIE_TYPE_LIST);
    }

    private static String callBaseService(String appendPathString) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String jsonStr = null;

        try {

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendEncodedPath(appendPathString)
                    .appendQueryParameter(API_KEY_PARAM,API_KEY)
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
            jsonStr = buffer.toString();

            Log.v(LOG_TAG, "Movie JSON String: " + jsonStr);

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
        return jsonStr;
    }
}
