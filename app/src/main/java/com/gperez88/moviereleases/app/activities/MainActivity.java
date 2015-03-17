package com.gperez88.moviereleases.app.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.gperez88.moviereleases.app.R;
import com.gperez88.moviereleases.app.fragments.MoviesFragment;


public class MainActivity extends ActionBarActivity {

    private final String MOVIESFRAGMENT_TAG = "MOVIESFRAGMENTTAG";

    private String mCodeCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO:mientras construyo la pantalla de setting.
        mCodeCountry = "do";
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MoviesFragment())
                    .commit();
        }
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
