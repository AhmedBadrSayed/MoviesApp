package com.projects.ahmedbadr.moviesapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.projects.ahmedbadr.moviesapp.Fragments.MoviesFragment;
import com.projects.ahmedbadr.moviesapp.R;

import com.projects.ahmedbadr.moviesapp.Fragments.MovieDetailsFragment;

public class MainActivity extends ActionBarActivity implements MoviesFragment.moviesFragmentCallbacks {

    boolean mTwoPane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_details_container)!= null){
                mTwoPane = true;
        }else mTwoPane = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void posterClick(String movieDetails) {
        if(mTwoPane){
            MovieDetailsFragment detailsFragment = MovieDetailsFragment.getInstace(movieDetails);
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.movie_details_container, detailsFragment)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, MovieDetails.class);
            intent.putExtra(Intent.EXTRA_TEXT, movieDetails);
            startActivity(intent);
        }
    }
}
