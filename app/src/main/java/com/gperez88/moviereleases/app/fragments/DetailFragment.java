package com.gperez88.moviereleases.app.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gperez88.moviereleases.app.R;
import com.gperez88.moviereleases.app.data.MovieContract;
import com.gperez88.moviereleases.app.utils.MovieUtils;

/**
 * Created by GPEREZ on 3/21/2015.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_GAT = DetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;
    private static final String MOVIE_SHARE_HASHTAG = " #MovieReleaseApp";

    //column's
    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_THUMBNAIL_URL,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_DURATION,
    };

    //indices column's
    public static final int COL_MOVIE_TITLE = 0;
    public static final int COL_MOVIE_THUMBNAIL_URL = 1;
    public static final int COL_MOVIE_SYNOPSIS = 2;
    public static final int COL_MOVIE_RELEASE_DATE = 3;
    public static final int COL_MOVIE_DURATION = 4;

    private ShareActionProvider mShareActionProvider;
    private String mMovie;

    private SimpleDraweeView detailThumbnailImageView;
    private SimpleDraweeView detailCoverImageView;
    private TextView detailTitleTextView;
    private TextView detailReleaseDateTextView;
    private TextView detailDurationTextView;
    private TextView synopsisDetailTextView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        detailThumbnailImageView = (SimpleDraweeView) rootView.findViewById(R.id.detail_thumbnail_imageView);
        detailCoverImageView = (SimpleDraweeView) rootView.findViewById(R.id.detail_cover_imageView);
        detailTitleTextView = (TextView) rootView.findViewById(R.id.detail_title_textView);
        detailReleaseDateTextView = (TextView) rootView.findViewById(R.id.detail_release_date_textView);
        detailDurationTextView = (TextView) rootView.findViewById(R.id.detail_duration_textView);
        synopsisDetailTextView = (TextView) rootView.findViewById(R.id.synopsis_detail_textView);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        return new CursorLoader(
                getActivity(),
                intent.getData(),
                DETAIL_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String thumbnailUrl = data.getString(COL_MOVIE_THUMBNAIL_URL);
            Uri thumbnailUri = Uri.parse(thumbnailUrl);

            detailThumbnailImageView.setImageURI(thumbnailUri);
            detailCoverImageView.setImageURI(thumbnailUri);

            String title = data.getString(COL_MOVIE_TITLE);
            detailTitleTextView.setText(title);

            String date = data.getString(COL_MOVIE_RELEASE_DATE);
            detailReleaseDateTextView.setText(MovieUtils.formatDate(date));

            int duration = data.getInt(COL_MOVIE_DURATION);
            detailDurationTextView.setText(MovieUtils.formatDuration(getActivity(), duration));

            String synopsis = data.getString(COL_MOVIE_SYNOPSIS);
            synopsisDetailTextView.setText(synopsis);

            mMovie = String.format("title: %s - date release: %s - duration: %s", title, MovieUtils.formatDate(date), duration);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mMovie != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie + MOVIE_SHARE_HASHTAG);
        return shareIntent;
    }
}
