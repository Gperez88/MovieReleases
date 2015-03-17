package com.gperez88.moviereleases.app.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.gperez88.moviereleases.app.services.MovieService;

/**
 * Created by GPEREZ on 3/16/2015.
 */
public class MovieTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = MovieTask.class.getSimpleName();

    private final Context mContext;

    public MovieTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected Void doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }

        String countryQuery = params[0];

        try {
            MovieService.getMovies(countryQuery);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
        }

        return null;
    }
}
