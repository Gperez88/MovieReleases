package com.gperez88.moviereleases.app.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.gperez88.moviereleases.app.R;
import com.gperez88.moviereleases.app.adapters.CursorFragmentPagerAdapter;
import com.gperez88.moviereleases.app.adapters.MovieFragmentPagerAdapter;
import com.gperez88.moviereleases.app.data.MovieContract;
import com.gperez88.moviereleases.app.fragments.MoviesFragment;
import com.gperez88.moviereleases.app.services.MovieSyncAdapter;
import com.gperez88.moviereleases.app.utils.MovieUtils;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int MOVIE_TYPE_LOADER = 0;
    private static final String POSITION_VIEWPAGER = "position_viewpager";

    private CursorFragmentPagerAdapter moviePagerAdapter;
    private ViewPager viewPagerMovie;
    private PagerSlidingTabStrip pagerSlidingTabStrip;

    private String mSyncInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Fresco.initialize(this);

        mSyncInterval = MovieUtils.getPreferredSyncInterval(this);

        moviePagerAdapter = new MovieFragmentPagerAdapter(this, getSupportFragmentManager(), null);
        viewPagerMovie = (ViewPager) findViewById(R.id.viewPager_movie);
        viewPagerMovie.setAdapter(moviePagerAdapter);

        pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.pagerslingtagstring_movie);
        pagerSlidingTabStrip.setViewPager(viewPagerMovie);

        getSupportActionBar().setElevation(0f);
        MovieSyncAdapter.initializeSyncAdapter(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION_VIEWPAGER, viewPagerMovie.getCurrentItem());
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().initLoader(MOVIE_TYPE_LOADER, null, this);

        String syncInterval = MovieUtils.getPreferredSyncInterval(this);
        if (syncInterval != null && !syncInterval.equals(mSyncInterval)) {
            MoviesFragment moviesFragment = (MoviesFragment) findFragmentByPosition(viewPagerMovie.getCurrentItem());
            if (null != moviesFragment) {
                MovieSyncAdapter.configurePeriodicSync(this);
            }
            mSyncInterval = syncInterval;
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
            startActivity(new Intent(this, SettingsActivity.class));
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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviePagerAdapter.swapCursor(null);
    }

    private Fragment findFragmentByPosition(int position) {
        return getSupportFragmentManager().findFragmentByTag(
                "android:switcher:" + viewPagerMovie.getId() + ":"
                        + moviePagerAdapter.getItemId(position));
    }
}
