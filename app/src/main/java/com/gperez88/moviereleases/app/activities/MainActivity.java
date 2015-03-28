package com.gperez88.moviereleases.app.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.gperez88.moviereleases.app.R;
import com.gperez88.moviereleases.app.adapters.CursorFragmentPagerAdapter;
import com.gperez88.moviereleases.app.adapters.MovieFragmentPagerAdapter;
import com.gperez88.moviereleases.app.data.MovieContract;
import com.gperez88.moviereleases.app.tasks.MovieTask;
import com.gperez88.moviereleases.app.utils.view.SlidingTabLayout;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int MOVIE_TYPE_LOADER = 0;
    private final String MOVIESFRAGMENT_TAG = "MOVIESFRAGMENTTAG";

    private CursorFragmentPagerAdapter moviePagerAdapter;
    private ViewPager viewPagerMovie;
    private SlidingTabLayout slidingTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       //updateMovie();
        //set custom toolbar.
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        moviePagerAdapter = new MovieFragmentPagerAdapter(this, getSupportFragmentManager(),null);
        viewPagerMovie = (ViewPager) findViewById(R.id.viewPager_movie);
        viewPagerMovie.setAdapter(moviePagerAdapter);

        // Give the SlidingTabLayout the ViewPager
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.slidingTabLayout_movie);

        //Customize tab view
        slidingTabLayout.setCustomTabView(R.layout.custom_tab, 0);

        slidingTabLayout.setMeasureAllChildren(true);

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

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().initLoader(MOVIE_TYPE_LOADER, null, this);
/*        MoviesFragment moviesFragment = (MoviesFragment) findFragmentByPosition(viewPagerMovie.getCurrentItem());
        if (null != moviesFragment) {
            moviesFragment.onLocationChanged();
        }*/
//        String codeCountry = "do";

//        if (codeCountry != null && !codeCountry.equals(mCodeCountry)) {
//            MoviesFragment moviesFragment = (MoviesFragment) getSupportFragmentManager().findFragmentByTag(MOVIESFRAGMENT_TAG);
//            if (null != moviesFragment) {
//                moviesFragment.onLocationChanged();
//            }
//            mCodeCountry = codeCountry;
//        }
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                MovieContract.MovieTypeEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        moviePagerAdapter.swapCursor(data);
        slidingTabLayout.setViewPager(viewPagerMovie);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviePagerAdapter.swapCursor(null);
    }

    private void updateMovie() {
        MovieTask movieTask = new MovieTask(this);
        movieTask.execute();
    }
}
