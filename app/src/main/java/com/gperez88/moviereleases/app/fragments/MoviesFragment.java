package com.gperez88.moviereleases.app.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.gperez88.moviereleases.app.R;
import com.gperez88.moviereleases.app.activities.DetailActivity;
import com.gperez88.moviereleases.app.adapters.MovieAdapter;
import com.gperez88.moviereleases.app.data.MovieContract;

/**
 * Created by GPEREZ on 3/16/2015.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MoviesFragment.class.getSimpleName();
    private static final int MOVIE_LOADER = 0;
    private static final String ARG_SECTION_MOVIE = "arg_section_movie";

    //column's
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_THUMBNAIL_URL,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE
    };

    //index's column's
    public static final int COL_MOVIE_ID = 0;
    public static final int COL_MOVIE_TITLE = 1;
    public static final int COL_MOVIE_THUMBNAIL_URL = 2;
    public static final int COL_MOVIE_RELEASE_DATE = 3;

    private MovieAdapter movieAdapter;

    public MoviesFragment() {
    }

    public static MoviesFragment create(String movieType) {
        MoviesFragment moviesFragment = new MoviesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SECTION_MOVIE, movieType);

        moviesFragment.setArguments(args);
        return moviesFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        movieAdapter = new MovieAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.movie_gridview);
        gridView.setAdapter(movieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(MovieContract.MovieEntry.buildMovieUri(cursor.getLong(COL_MOVIE_ID)));
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String movieTypeArg = getArguments().getString(ARG_SECTION_MOVIE);

        String sortOrder = MovieContract.MovieEntry.COLUMN_TITLE + " DESC";
        Uri movieWithTypeUri = MovieContract.MovieEntry.buildMovieWithType(movieTypeArg);

        return new CursorLoader(getActivity(),
                movieWithTypeUri,
                MOVIE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        movieAdapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        movieAdapter.swapCursor(null);
    }
}
