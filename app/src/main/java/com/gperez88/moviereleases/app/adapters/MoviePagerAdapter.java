package com.gperez88.moviereleases.app.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gperez88.moviereleases.app.R;
import com.gperez88.moviereleases.app.fragments.MoviesFragment;
import com.gperez88.moviereleases.app.services.MovieService;

/**
 * Created by GPEREZ on 3/17/2015.
 */
public class MoviePagerAdapter extends FragmentPagerAdapter {

    public static final int NUMBER_PAGE = 4;
    public static final int POSITION_SECTION_BOX_OFFICE = 0;
    public static final int POSITION_SECTION_IN_THEATERS = 1;
    public static final int POSITION_SECTION_OPENING = 2;
    public static final int POSITION_SECTION_UNCOMING = 3;

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
}
