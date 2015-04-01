package com.gperez88.moviereleases.app.services;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.gperez88.moviereleases.app.R;
import com.gperez88.moviereleases.app.activities.MainActivity;
import com.gperez88.moviereleases.app.data.MovieContract;
import com.gperez88.moviereleases.app.utils.MovieUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by GPEREZ on 3/29/2015.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();

    //http://perezgabriel89.com/movie/api/v1.0/
    private static final String MOVIE_BASE_URL = "http://perezgabriel89.com/movie/api/v1.0/";
    private static final String API_KEY_PARAM = "apikey";
    private static final String API_KEY = "zs1pv@movieusrs@1988";
    private static final String MOVIE_LIST = "movie";
    private static final String MOVIE_TYPE_LIST = "type_movie";

    // Interval at which to sync, in seconds.
    public static final int SECONDS_IN_DAY = 86400;
    private static final int MOVIE_NOTIFICATION_ID = 3004;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        callMovieTypeListApiService();
        callMovieListApiService();
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MovieSyncAdapter.configurePeriodicSync(context);
        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void configurePeriodicSync(Context context) {
        /*
         * Since we've created an account
         */
        int syncInterval = Integer.parseInt(MovieUtils.getPreferredSyncInterval(context));
        int syncFlexTime = syncInterval / SECONDS_IN_DAY;

        MovieSyncAdapter.configurePeriodicSync(context, syncInterval, syncFlexTime);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    public void callMovieListApiService() {
        try {
            insertMovieDataFromJson(callBaseApiService(MOVIE_LIST));
        } catch (JSONException e) {
            Log.d(LOG_TAG, "Error movies call api service ", e);
            e.printStackTrace();
        }
    }

    public void callMovieTypeListApiService() {
        try {
            insertMovieTypeFromJson(callBaseApiService(MOVIE_TYPE_LIST));
        } catch (JSONException e) {
            Log.d(LOG_TAG, "Error movie type's call api service ", e);
            e.printStackTrace();
        }
    }

    private String callBaseApiService(String appendPathString) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String jsonStr = null;

        try {

            Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendEncodedPath(appendPathString)
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .build();

            URL url = new URL(builtUri.toString());

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

    public void insertMovieTypeFromJson(String movieTypeJsonStr) throws JSONException {
        final String ID = "id";
        final String TYPE = "type";
        final String DESCRIPTION = "description";

        if (movieTypeJsonStr == null)
            return;

        try {
            JSONArray movieTypeArrayJson = new JSONArray(movieTypeJsonStr);

            Vector<ContentValues> cVVector = new Vector<>(movieTypeArrayJson.length());

            for (int i = 0; i < movieTypeArrayJson.length(); i++) {

                long id;
                String type;
                String description;

                JSONObject movie = movieTypeArrayJson.getJSONObject(i);

                id = movie.getLong(ID);
                type = movie.getString(TYPE);
                description = movie.getString(DESCRIPTION);

                ContentValues movieTypeValues = new ContentValues();

                movieTypeValues.put(MovieContract.MovieTypeEntry._ID, id);
                movieTypeValues.put(MovieContract.MovieTypeEntry.COLUMN_TYPE, type);
                movieTypeValues.put(MovieContract.MovieTypeEntry.COLUMN_DESCRIPTION, description);
                cVVector.add(movieTypeValues);
            }

            int inserted = 0;

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = getContext().getContentResolver().bulkInsert(MovieContract.MovieTypeEntry.CONTENT_URI, cvArray);
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

                title = movie.getString(TITLE);
                thumbnailUrl = movie.getString(THUMBNAIL_URL);
                synopsis = movie.getString(SYNOPSIS);
                releaseDates = movie.getString(RELEASE_DATE);
                duration = movie.getString(DURATION);
                movieTypeId = movie.getLong(MOVIE_TYPE_ID);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_THUMBNAIL_URL, thumbnailUrl);
                movieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, synopsis);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDates);
                movieValues.put(MovieContract.MovieEntry.COLUMN_DURATION, duration);
                movieValues.put(MovieContract.MovieEntry.COLUMN_TYPE_MOVIE_ID, movieTypeId);

                cVVector.add(movieValues);
            }

            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);

                notifyWeather();
            }

            Log.d(LOG_TAG, "MovieTask Complete. " + inserted + " Movie Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void notifyWeather() {
        Context context = getContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));

        if (displayNotifications) {

            Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;
            Cursor cursor = context.getContentResolver().query(movieUri, null, null, null, null);

            String lastNotificationKey = context.getString(R.string.pref_last_notification);
            int lastSyncCountMovie = prefs.getInt(lastNotificationKey, 0);

            final boolean THERE_ARE_NEW_MOVIE = lastSyncCountMovie != 0 && lastSyncCountMovie < cursor.getCount();

            if (THERE_ARE_NEW_MOVIE) {

                int newCountMovie = cursor.getCount() - lastSyncCountMovie;

                if (cursor.moveToFirst()) {
                    Resources resources = context.getResources();
                    Bitmap largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
                    String title = context.getString(R.string.app_name);
                    String contentText = String.format(context.getString(R.string.format_notification), newCountMovie);

                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getContext())
                                    .setColor(resources.getColor(R.color.primary))
                                    .setLights(Color.WHITE, 5000, 5000)
                                    .setSmallIcon(R.mipmap.ic_art_notification)
                                    .setLargeIcon(largeIcon)
                                    .setContentTitle(title)
                                    .setContentText(contentText);

                    Intent resultIntent = new Intent(context, MainActivity.class);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager =
                            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

                    mNotificationManager.notify(MOVIE_NOTIFICATION_ID, mBuilder.build());

                }
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(lastNotificationKey, cursor.getCount());
            editor.commit();

            cursor.close();
        }

    }
}
