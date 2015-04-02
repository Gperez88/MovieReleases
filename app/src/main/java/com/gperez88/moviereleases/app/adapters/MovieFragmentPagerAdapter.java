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
public class MovieFragmentPagerAdapter extends CursorFragmentPagerAdapter{
    private static final String LOG_TAG = MovieFragmentPagerAdapter.class.getSimpleName();

    public MovieFragmentPagerAdapter(Context mContext, FragmentManager fm, Cursor cursor) {
        super(mContext, fm, cursor);
    }

    @Override
    public Fragment getItem(Context context, Cursor cursor) {
        try {
            String movieType = getFieldFromCursor(cursor, MovieContract.MovieTypeEntry.COLUMN_TYPE);

            MoviesFragment moviesFragment = MoviesFragment.create(movieType);
            return moviesFragment;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getPageTitle(Context context, Cursor cursor) {
        try {
            String movieType = getFieldFromCursor(cursor, MovieContract.MovieTypeEntry.COLUMN_TYPE);
            return movieType.toUpperCase();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private <T> T getFieldFromCursor(Cursor cursor, String columnFieldName) {
        int indexColumn = cursor.getColumnIndexOrThrow(columnFieldName);
        return (T) cursor.getString(indexColumn);
    }
}
