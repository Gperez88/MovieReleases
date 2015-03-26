package com.gperez88.moviereleases.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.gperez88.moviereleases.app.data.MovieContract;
import com.gperez88.moviereleases.app.fragments.MoviesFragment;

/**
 * Created by Gaperez on 3/26/2015.
 */
public class MovieFragmentPagerAdapter extends CursorFragmentPagerAdapter {

    public MovieFragmentPagerAdapter(Context mContext, FragmentManager fm, Cursor cursor) {
        super(mContext, fm, cursor);
    }

    @Override
    public Fragment getItem(Context context, Cursor cursor) {
        MoviesFragment moviesFragment;
        try {
            int indexColumnMovieType = cursor.getColumnIndexOrThrow(MovieContract.MovieTypeEntry.COLUMN_TYPE);
            String movieType = cursor.getString(indexColumnMovieType);
            moviesFragment = MoviesFragment.create(movieType);
            return moviesFragment;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
