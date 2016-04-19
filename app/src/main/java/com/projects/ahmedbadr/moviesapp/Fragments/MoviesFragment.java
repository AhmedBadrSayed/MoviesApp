package com.projects.ahmedbadr.moviesapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.projects.ahmedbadr.moviesapp.Adapters.MoviesGridAdapter;
import com.projects.ahmedbadr.moviesapp.DataStore.MoviesDB;
import com.projects.ahmedbadr.moviesapp.Interfaces.PostersAdapterlistener;
import com.projects.ahmedbadr.moviesapp.R;
import com.projects.ahmedbadr.moviesapp.Activities.MovieDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MoviesFragment extends Fragment implements PostersAdapterlistener {

    GridView gridView;
    ArrayList<String> MoviesDetailsArray = new ArrayList<String>();
    ArrayList<String> FavoritesPosters;
    MoviesDB moviesDB;
    MoviesGridAdapter favoritesGridAdapter;

    public MoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_refresh){
            UpdateMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View RootView =  inflater.inflate(R.layout.fragment_movies, container, false);
        moviesDB = new MoviesDB(getActivity());
        FavoritesPosters = new ArrayList<String>();
        gridView = (GridView) RootView.findViewById(R.id.posters_grid);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String Movie_Details = MoviesDetailsArray.get(position);
                moviesFragmentCallbacks moviesCallbacks
                        = (moviesFragmentCallbacks) getActivity();
                moviesCallbacks.posterClick(Movie_Details);
            }
        });
        //gridView.setAdapter(moviesAdapter);
        UpdateMovies();
        return RootView;
    }

    public void UpdateMovies(){
        getMovies get_movies = new getMovies(this);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String Url = pref.getString(getString(R.string.pref_sort_key)
                , getString(R.string.pref_sort_default));
        if(Url.equals("favorites")){
            retrieveAll();
        }
        else get_movies.execute(Url);
    }

    private void retrieveRow() {
        try {
            ArrayList<Object> row;
            row = moviesDB.getOneMovie(Long.parseLong("244786"));
        } catch (Exception e) {
            Log.e("Retrieve Error", e.toString());
            e.printStackTrace();
        }
    }

    private void retrieveAll() {

        try {
            FavoritesPosters.clear();
            ArrayList<ArrayList<Object>> row;
            row = moviesDB.getAllMovies();
            for(int i=0 ; i<row.size() ; i++) {
                FavoritesPosters.add(i, (String) row.get(i).get(5));
                MoviesDetailsArray.add(i, row.get(i).get(0) + "=" + row.get(i).get(1) + "=" + row.get(i).get(2)
                        + "=" + row.get(i).get(3) + "=" + row.get(i).get(4) + "=" + row.get(i).get(5));
            }
        } catch (Exception e) {
            Log.e("Retrieve Error", e.toString());
            e.printStackTrace();
        }

        favoritesGridAdapter = new MoviesGridAdapter(FavoritesPosters,getActivity());
        gridView.setAdapter(favoritesGridAdapter);
    }

    @Override
    public void listen(ArrayList<String> posters) {
        MoviesGridAdapter moviesGridAdapter = new MoviesGridAdapter(posters,getActivity());
        gridView.setAdapter(moviesGridAdapter);
        moviesGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        UpdateMovies();
    }

    public class getMovies extends AsyncTask<String, Void, ArrayList<String>> {

        PostersAdapterlistener postersAdapterlistener;
        public getMovies(PostersAdapterlistener postersAdapter){
            postersAdapterlistener = postersAdapter;
        }

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(),
                    "", "Loading. Please wait...", true);
        }


        private ArrayList<String> getPostersURLFromJson(String MoviesJson) throws JSONException {

            final String TMDB_RESULTS = "results";
            final String TMDB_TITLE = "original_title";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_VOTE = "vote_average";
            final String TMDB_DATE = "release_date";
            final String TMDB_PATH = "poster_path";
            final String TMDB_ID = "id";
            final String BASIC_URL = "http://image.tmdb.org/t/p/w342";

            ArrayList<String> PostersPathsArray = new ArrayList<String>();
            String PosterPath = "";
            String MovieTitle = "";
            String Overview = "";
            String VoteAverage = "";
            String Date = "";
            String id = "";

            JSONObject Movies_json = new JSONObject(MoviesJson);
            JSONArray MoviesArray = Movies_json.getJSONArray(TMDB_RESULTS);

            for(int i = 0; i < MoviesArray.length(); i++) {
                // Get the JSON object representing the movie
                JSONObject MovieData = MoviesArray.getJSONObject(i);
                MovieTitle = MovieData.getString(TMDB_TITLE);
                Overview = MovieData.getString(TMDB_OVERVIEW);
                VoteAverage = MovieData.getString(TMDB_VOTE);
                Date = MovieData.getString(TMDB_DATE);
                PosterPath = MovieData.getString(TMDB_PATH);
                id = MovieData.getString(TMDB_ID);
                PostersPathsArray.add(i,BASIC_URL+PosterPath);
                MoviesDetailsArray.add(i,id+"="+MovieTitle+"="+Overview+"="+VoteAverage+"="+Date+"="+BASIC_URL+PosterPath);
            }
            return PostersPathsArray;
        }

        private final String LOG_TAG = MoviesFragment.class.getSimpleName();

        @Override
        protected ArrayList<String> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            String MoviesData = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    System.out.println("Empty Json");
                    return null;
                }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {

                        buffer.append(line + "\n");

                    }
                    if (buffer.length() == 0) {
                        return null;
                    }

                MoviesData = buffer.toString();

                Log.v(LOG_TAG, "Movies string: " + MoviesData);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getPostersURLFromJson(MoviesData);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            if(!isConnectedToInternet() || strings!=null){
                //moviesAdapter = new MoviesGridAdapter(strings,getActivity());
                //moviesAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
                postersAdapterlistener.listen(strings);
            }
            else{
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Internet Is Required...Pls Check Your Connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++){
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
        }
        return false;
    }

    public interface moviesFragmentCallbacks{
        public void posterClick(String movieDetails);
    }
}