package com.gperez88.moviereleases.app.services;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by GPEREZ on 3/16/2015.
 */
public class MovieService {
    private static final String LOG_TAG = MovieService.class.getSimpleName();

    //http://api.rottentomatoes.com
    private static final String MOVIE_BASE_URL = "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/";
    private static final String LIMIT_PARAM = "limit";
    private static final String PAGE_LIMIT_PARAM = "page_limit";
    private static final String PAGE_PARAM = "page";
    private static final String COUNTRY_PARAM = "country";
    private static final String API_KEY_PARAM = "apikey";

    private static final String API_KEY = "5szncvhqdyhrkwecqsrtacwr";

    public static final String SECTION_BOX_OFFICE = "box_office.json";
    public static final String SECTION_IN_THEATERS = "in_theaters.json";
    public static final String SECTION_OPENING = "opening.json";
    public static final String SECTION_UNCOMING = "upcoming.json";

    public static String getMoviesBoxOffice(String codeCountry, int limit) {
        Uri builtUri = generateBuiltUri(codeCountry, SECTION_BOX_OFFICE, limit);
        return callBaseService(builtUri);
    }

    public static String getMoviesInTheaters(String codeCountry, int pageLimit, int page) {
        Uri builtUri = generateBuiltUri(codeCountry, SECTION_IN_THEATERS, pageLimit, page);
        return callBaseService(builtUri);
    }

    public static String getMoviesOpening(String codeCountry, int limit) {
        Uri builtUri = generateBuiltUri(codeCountry, SECTION_OPENING, limit);
        return callBaseService(builtUri);
    }

    public static String getMoviesUpComing(String codeCountry, int pageLimit, int page) {
        Uri builtUri = generateBuiltUri(codeCountry, SECTION_UNCOMING, pageLimit, page);
        return callBaseService(builtUri);
    }

    //overload
    private static Uri generateBuiltUri(String codeCountry, String section, int limit) {
        return generateBuiltUri(codeCountry, section, 0, 0, limit);
    }

    //overload
    private static Uri generateBuiltUri(String codeCountry, String section, int pageLimit, int page) {
        return generateBuiltUri(codeCountry, section, pageLimit, page, 0);
    }

    private static Uri generateBuiltUri(String codeCountry, String section, int pageLimit, int page, int limit) {
        HashMap<String, String> params = new HashMap<>();
        params.put(COUNTRY_PARAM, codeCountry);
        params.put(API_KEY_PARAM, API_KEY);

        //if it is zero, it is not receiving these parameters.
        if (pageLimit > 0 && page > 0) {
            params.put(PAGE_LIMIT_PARAM, Integer.toString(pageLimit));
            params.put(PAGE_PARAM, Integer.toString(page));
        }

        //if it is zero, it is no receiving these parameter.
        if (limit > 0) {
            params.put(LIMIT_PARAM, Integer.toString(limit));
        }

        Uri.Builder builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon();
        builtUri.appendEncodedPath(section);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            builtUri.appendQueryParameter(entry.getKey(), entry.getValue());
        }

        return builtUri.build();
    }

    private static String callBaseService(Uri uri) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJsonStr = null;

        try {

            URL url = new URL(uri.toString());

            Log.v(LOG_TAG, "Buit URI " + uri.toString());

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
