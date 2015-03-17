package com.gperez88.moviereleases.app.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gperez88.moviereleases.app.R;
import com.gperez88.moviereleases.app.tasks.MovieTask;

/**
 * Created by GPEREZ on 3/16/2015.
 */
public class MoviesFragment extends Fragment {

    public MoviesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //TODO:para probar.
        updateMovie();

        return rootView;
    }

    public void onLocationChanged( ) {
        updateMovie();
    }

    private void updateMovie() {
        MovieTask movieTask = new MovieTask(getActivity());
        //TODO:mientras construyo la pantalla de setting. mejorar tambien forma de pasar la section

        String codeCountry = "do";
        String countryName = "dominican republic";
        movieTask.execute(codeCountry,countryName);
    }

}
