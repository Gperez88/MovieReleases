package com.gperez88.moviereleases.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.ViewGroup;

import com.gperez88.moviereleases.app.R;
import com.gperez88.moviereleases.app.data.MovieContract;
import com.gperez88.moviereleases.app.fragments.MoviesFragment;
import com.gperez88.moviereleases.app.services.MovieService;

/**
 * Created by GPEREZ on 3/17/2015.
 */
public class MoviePagerAdapter extends FragmentPagerAdapter implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PAGE_LOADER = 0;

    private static final String[] MOVIE_TYPE_COLUMNS = {
            MovieContract.MovieTypeEntry.TABLE_NAME + "." + MovieContract.MovieTypeEntry._ID,
            MovieContract.MovieTypeEntry.COLUMN_TYPE,
            MovieContract.MovieTypeEntry.COLUMN_DESCRIPTION
    };

    //indices column's
    public static final int COL_MOVIE_TYPE_ID = 0;
    public static final int COL_MOVIE_TYPE_TYPE = 1;
    public static final int COL_MOVIE_TYPE_DESCRIPTION = 2;

    public static final int NUMBER_PAGE = 4;
    public static final int POSITION_ACTION_AVENTURE = 0;
    public static final int POSITION_ANIMATION = 1;
    public static final int POSITION_COMEDY = 2;
    public static final int POSITION_TERROR = 3;

    //protected boolean mDataValid;
    protected Cursor mCursor;
    private Context mContext;

    public MoviePagerAdapter(Context mContext, FragmentManager fm) {
        super(fm);
        this.mContext = mContext;
    }

    @Override
    public Fragment getItem(int position) {
        return MoviesFragment.create(getSectionNameByPosition(position));
    }

    @Override
    public int getCount() {
        return NUMBER_PAGE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case POSITION_SECTION_BOX_OFFICE:
                return mContext.getString(R.string.box_office).toUpperCase();
            case POSITION_SECTION_IN_THEATERS:
                return mContext.getString(R.string.in_theaters).toUpperCase();
            case POSITION_SECTION_OPENING:
                return mContext.getString(R.string.opening).toUpperCase();
            case POSITION_SECTION_UNCOMING:
                return mContext.getString(R.string.upcoming).toUpperCase();
            default:
                return null;
        }
    }

    private String getSectionNameByPosition(int position){
        switch (position){
            case POSITION_SECTION_BOX_OFFICE:
                return MovieService.SECTION_BOX_OFFICE;
            case POSITION_SECTION_IN_THEATERS:
                return MovieService.SECTION_IN_THEATERS;
            case POSITION_SECTION_OPENING:
                return MovieService.SECTION_OPENING;
            case POSITION_SECTION_UNCOMING:
                return MovieService.SECTION_UNCOMING;
            default:
                return null;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MovieContract.MovieTypeEntry.COLUMN_TYPE + " DESC";

        return new CursorLoader(mContext,
                MovieContract.MovieTypeEntry.CONTENT_URI,
                MOVIE_TYPE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;
    }
}
