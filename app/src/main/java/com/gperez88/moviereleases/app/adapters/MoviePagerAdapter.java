package com.gperez88.moviereleases.app.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

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

    public MoviePagerAdapter(FragmentManager fm) {
        super(fm);
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
        // Generate title based on item position
        return getSectionNameByPosition(position);
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
