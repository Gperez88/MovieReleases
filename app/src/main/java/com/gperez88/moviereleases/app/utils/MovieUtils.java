package com.gperez88.moviereleases.app.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by GPEREZ on 3/16/2015.
 */
public class MovieUtils {
    private static final String LOG_TAG = MovieUtils.class.getSimpleName();

    public static String formatDate(String dateStr) {
        SimpleDateFormat stringToDateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat millisecondsToDateFormatter = new SimpleDateFormat("MMMM yyyy");

        try {
            long dateMilliseconds = stringToDateFormatter.parse(dateStr).getTime();
            return millisecondsToDateFormatter.format(new Date(dateMilliseconds));
        } catch (ParseException e) {
            Log.d(LOG_TAG,"Error parse date: "+ e.getMessage());
            e.printStackTrace();
        }
        return "";
    }
}
