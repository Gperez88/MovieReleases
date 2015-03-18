package com.gperez88.moviereleases.app.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.gperez88.moviereleases.app.R;
import com.gperez88.moviereleases.app.adapters.MoviePagerAdapter;
import com.gperez88.moviereleases.app.fragments.MoviesFragment;
import com.gperez88.moviereleases.app.utils.view.SlidingTabLayout;


public class MainActivity extends ActionBarActivity {

    private final String MOVIESFRAGMENT_TAG = "MOVIESFRAGMENTTAG";

    private String mCodeCountry;
    private MoviePagerAdapter moviePagerAdapter;
    private ViewPager viewPagerMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviePagerAdapter = new MoviePagerAdapter(getSupportFragmentManager());
        viewPagerMovie = (ViewPager) findViewById(R.id.viewPager_movie);
        viewPagerMovie.setAdapter(moviePagerAdapter);
        //TODO:mientras construyo la pantalla de setting.
        mCodeCountry = "do";

        // Give the SlidingTabLayout the ViewPager
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingTabLayout_movie);
        slidingTabLayout.setViewPager(viewPagerMovie);

         // Customize tab color
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.accent);
            }

            @Override
            public int getDividerColor(int position) {
                return getResources().getColor(android.R.color.transparent);
            }

            @Override
            public int getTabColor(int position) {
                return getResources().getColor(R.color.primary);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //TODO:mientras construyo la pantalla de setting.
        String codeCountry = "do";

        if (codeCountry != null && !codeCountry.equals(mCodeCountry)) {
            MoviesFragment moviesFragment = (MoviesFragment)getSupportFragmentManager().findFragmentByTag(MOVIESFRAGMENT_TAG);
            if ( null != moviesFragment ) {
                moviesFragment.onLocationChanged();
            }
            mCodeCountry = codeCountry;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
