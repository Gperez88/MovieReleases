package com.gperez88.moviereleases.app;

import android.test.AndroidTestCase;

import com.gperez88.moviereleases.app.tasks.MovieTask;

/**
 * Created by GPEREZ on 3/16/2015.
 */
public class TestMovieTask extends AndroidTestCase {

    public void testService(){
        MovieTask movieTask = new MovieTask(mContext);
        String country = "do";
        movieTask.execute(country);
    }

}
