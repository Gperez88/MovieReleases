package com.gperez88.moviereleases.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gperez88.moviereleases.app.R;
import com.gperez88.moviereleases.app.fragments.MoviesFragment;
import com.gperez88.moviereleases.app.utils.MovieUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by GPEREZ on 3/17/2015.
 */
public class MovieAdapter extends CursorAdapter {
    public static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView titleView;
        public final TextView releaseDateView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_thumbnail);
            titleView = (TextView) view.findViewById(R.id.list_item_title_textview);
            releaseDateView = (TextView) view.findViewById(R.id.list_item_release_date_textview);
        }
    }

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_movie, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String url = cursor.getString(MoviesFragment.COL_MOVIE_THUMBNAIL_URL);

        Picasso.with(mContext)
                .load(url)
                .into(viewHolder.iconView);

        String title = cursor.getString(MoviesFragment.COL_MOVIE_TITLE);
        viewHolder.titleView.setText(title);

        String date = cursor.getString(MoviesFragment.COL_MOVIE_RELEASE_DATE);
        viewHolder.releaseDateView.setText(MovieUtils.formatDate(date));
    }
}
