package com.gperez88.moviereleases.app.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gperez88.moviereleases.app.R;
import com.gperez88.moviereleases.app.data.MovieContract;
import com.gperez88.moviereleases.app.utils.MovieUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by GPEREZ on 3/21/2015.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_GAT = DetailFragment.class.getSimpleName();
    private static final int DETAIL_LOADER = 0;


    //column's
    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_THUMBNAIL_URL,
            MovieContract.MovieEntry.COLUMN_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_DURATION,
            MovieContract.MovieEntry.COLUMN_TYPE_MOVIE_ID
    };

    //indices column's
    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_THUMBNAIL_URL = 2;
    public static final int COL_MOVIE_SYNOPSIS = 3;
    public static final int COL_MOVIE_RELEASE_DATE = 4;
    public static final int COL_MOVIE_DURATION = 5;
    public static final int COL_MOVIE_TYPE_MOVIE_ID = 6;


    private ImageView detailThumbnailImageView;
    private ImageView detailCoverImageView;
    private TextView detailTitleTextView;
    private TextView detailReleaseDateTextView;
    private TextView detailDurationTextView;
    private TextView synopsisDetailTextView;


    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        detailThumbnailImageView = (ImageView) rootView.findViewById(R.id.detail_thumbnail_imageView);
        detailCoverImageView = (ImageView) rootView.findViewById(R.id.detail_cover_imageView);
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

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
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

            Picasso.with(getActivity())
                    .load(thumbnailUrl)
                    .into(detailThumbnailImageView);

            Picasso.with(getActivity())
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.default_image)
                    .into(detailCoverImageView);

            String title = data.getString(COL_MOVIE_TITLE);
            detailTitleTextView.setText(title);

            String date = data.getString(COL_MOVIE_RELEASE_DATE);
            detailReleaseDateTextView.setText(MovieUtils.formatDate(date));

            int duration = data.getInt(COL_MOVIE_DURATION);
            detailDurationTextView.setText(MovieUtils.formatDuration(getActivity(), duration));

            String synopsis = data.getString(COL_MOVIE_SYNOPSIS);
            synopsisDetailTextView.setText(synopsis);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
