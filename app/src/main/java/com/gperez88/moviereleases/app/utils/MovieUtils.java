package com.gperez88.moviereleases.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import com.gperez88.moviereleases.app.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by GPEREZ on 3/16/2015.
 */
public class MovieUtils {
    private static final String LOG_TAG = MovieUtils.class.getSimpleName();
    public static final int SECONDS_IN_DAY = 86400;

    public static String getPreferredSyncInterval(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sync_interval_key),
                context.getString(R.string.pref_sync_interval_default_value));
    }

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static String formatDate(String dateStr) {
        SimpleDateFormat stringToDateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SimpleDateFormat millisecondsToDateFormatter = new SimpleDateFormat("MMMM yyyy");

        try {
            long dateMilliseconds = stringToDateFormatter.parse(dateStr).getTime();
            return millisecondsToDateFormatter.format(new Date(dateMilliseconds));
        } catch (ParseException e) {
            Log.d(LOG_TAG, "Error parse date: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    public static String formatDuration(Context context, int duration) {
        return String.format(context.getString(R.string.format_duration), duration);
    }
}
