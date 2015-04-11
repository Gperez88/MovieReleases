package com.gperez88.moviereleases.app.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gperez88.moviereleases.app.R;
import com.gperez88.moviereleases.app.fragments.MoviesFragment;

/**
 * Created by Gaperez on 3/26/2015.
 */
public class MovieFragmentPagerAdapter extends FragmentPagerAdapter  {
    private static final String LOG_TAG = MovieFragmentPagerAdapter.class.getSimpleName();

    //constants
    public static final int NUMBER_PAGE = 4;
    public static final int POSITION_ACTION_ADVENTURE = 0;
    public static final int POSITION_ANIMATION = 1;
    public static final int POSITION_COMEDY = 2;
    public static final int POSITION_TERROR = 3;

    private Context context;

    public MovieFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return MoviesFragment.create(getNameMovieType(position));
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getNameMovieType(position).toUpperCase();
    }

    @Override
    public int getCount() {
        return NUMBER_PAGE;
    }

    private String getNameMovieType(int position) {
        switch (position) {
            case POSITION_ACTION_ADVENTURE: {
                return context.getString(R.string.action_adventure_type);
            }
            case POSITION_ANIMATION: {
                return context.getString(R.string.animation_type);
            }
            case POSITION_COMEDY: {
                return context.getString(R.string.comedy_type);
            }
            case POSITION_TERROR: {
                return context.getString(R.string.terror_type);
            }
            default: {
                return context.getString(R.string.action_adventure_type);
            }
        }
    }
}
