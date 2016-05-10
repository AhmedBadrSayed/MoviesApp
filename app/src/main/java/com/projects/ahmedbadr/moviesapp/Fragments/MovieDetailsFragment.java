package com.projects.ahmedbadr.moviesapp.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.ahmedbadr.moviesapp.Adapters.ExpandedListAdapter;
import com.projects.ahmedbadr.moviesapp.DataStore.MoviesDB;
import com.projects.ahmedbadr.moviesapp.R;
import com.projects.ahmedbadr.moviesapp.Activities.Reviews;
import com.squareup.picasso.Picasso;

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
import java.util.HashMap;
import java.util.List;

public class MovieDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();
    String MovieDetails="", PosterPath = "", MovieTitle = "", Overview = "", VoteAverage = "", Date = "", ID = "";
    ImageButton button;
    String[] DetailsArray;
    TextView title,overview,vote,date;
    ImageView poster;
    ExpandableListView expandableListView;
    ExpandedListAdapter expandedListAdapter;
    List<String> DataHeader;
    HashMap<String, List<String>> DataChild;
    ArrayList<String> TrailersArray, ReviewsArray, TrailerI, ReviewI;
    MoviesDB moviesDB;
    private static final String ARG_PARAM = null;
    private final String API_KEY = "Your_Key";

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        try{
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    TrailersArray.get(0) + "\n#"+MovieTitle);
        }catch (IndexOutOfBoundsException e){
            Log.e(LOG_TAG, "Error ", e);
        }
        return shareIntent;
    }

    public MovieDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public static MovieDetailsFragment getInstace(String movieDetails){
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM, movieDetails);
        MovieDetailsFragment detailsFragment = new MovieDetailsFragment();
        detailsFragment.setArguments(bundle);
        return detailsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_movie_details, container, false);
        SetViews(rootview);
        Intent intent = getActivity().getIntent();
        if(intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            MovieDetails = intent.getStringExtra(Intent.EXTRA_TEXT);
            viewAdapter(MovieDetails);
        }
        if(getArguments() != null){
            viewAdapter(getArguments().getString(ARG_PARAM));
        }
        moviesDB = new MoviesDB(getActivity());
        TrailersArray = new ArrayList<String>();
        ReviewsArray = new ArrayList<String>();
        DataHeader = new ArrayList<String>();
        DataChild = new HashMap<String, List<String>>();
        TrailerI = new ArrayList<String>();
        ReviewI = new ArrayList<String>();
        final String TRAILERS_URL = "http://api.themoviedb.org/3/movie/"+ID+"/videos?api_key="+API_KEY;
        final String REVIEWS_URL = "http://api.themoviedb.org/3/movie/"+ID+"/reviews?api_key="+API_KEY;
        button.setOnClickListener(this);
        if(moviesDB.isInDataBase(MovieTitle)==true){
            button.setImageResource(R.drawable.onpress);
        }else button.setImageResource(R.drawable.off);
        DataHeader.add("Trailers");
        DataHeader.add("Reviews");
        expandedListAdapter = new ExpandedListAdapter(getActivity(), DataHeader, DataChild);
        getTrailersAndReviews get_trailers = new getTrailersAndReviews();
        getTrailersAndReviews get_reviews = new getTrailersAndReviews();
        get_trailers.execute(TRAILERS_URL);
        get_reviews.execute(REVIEWS_URL);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (DataHeader.get(groupPosition).equals("Trailers")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(TrailersArray.get(childPosition)));
                        startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(TrailersArray.get(childPosition)));
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(getActivity(), Reviews.class);
                    intent.putExtra(Intent.EXTRA_TEXT, ReviewsArray.get(childPosition));
                    startActivity(intent);
                }
                return false;
            }
        });
        return rootview;
    }

    public void viewAdapter(String movieDetails) {
        DetailsArray = movieDetails.split("=");
        ID = DetailsArray[0];
        MovieTitle = DetailsArray[1];
        Overview = DetailsArray[2];
        VoteAverage = DetailsArray[3];
        Date = DetailsArray[4];
        PosterPath = DetailsArray[5];
        title.setText(MovieTitle);
        overview.setText(Overview);
        vote.setText(VoteAverage+"/10");
        date.setText(Date);
        Picasso.with(getActivity()).load(PosterPath).into(poster);
        poster.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public void SetViews(View rootview){
        title = (TextView) rootview.findViewById(R.id.movie_tittle);
        overview = (TextView) rootview.findViewById(R.id.movie_descreption);
        vote = (TextView) rootview.findViewById(R.id.movie_rate);
        date = (TextView) rootview.findViewById(R.id.movie_date);
        poster = (ImageView) rootview.findViewById(R.id.movie_image);
        button = (ImageButton) rootview.findViewById(R.id.favourits);
        expandableListView = (ExpandableListView) rootview.findViewById(R.id.movies_trailers);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == button.getId()){
            if(moviesDB.isInDataBase(MovieTitle)==true){
                moviesDB.deleteMovie(MovieTitle);
                button.setImageResource(R.drawable.off);
            }
            else if(moviesDB.isInDataBase(MovieTitle)==false){
                moviesDB.addMovie(ID, MovieTitle, Overview, VoteAverage, Date, PosterPath);
                button.setImageResource(R.drawable.onpress);
            }
        }
    }

    public class getTrailersAndReviews extends AsyncTask<String, Void, ArrayList<String>> {
        private final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

        private ArrayList<String> getTrailersFromJson(String TrailersJson) throws JSONException {

            final String TMDB_RESULTS = "results";
            final String TMDB_KEY = "key";
            final String BASIC_URL = "https://www.youtube.com/watch?v=";
            String key = "";

            JSONObject Trailers_json = new JSONObject(TrailersJson);
            JSONArray MoviesArray = Trailers_json.getJSONArray(TMDB_RESULTS);

            for (int i = 0; i < MoviesArray.length(); i++) {
                // Get the JSON object representing the movie
                JSONObject TrailersData = MoviesArray.getJSONObject(i);
                key = TrailersData.getString(TMDB_KEY);
                TrailersArray.add(i, BASIC_URL + key);
            }
            return TrailersArray;
        }

        private ArrayList<String> getReviewsFromJson(String ReviewsJson) throws JSONException {

            final String TMDB_RESULTS = "results";
            final String TMDB_CONTENT = "content";
            String content = "";

            JSONObject Reviews_json = new JSONObject(ReviewsJson);
            JSONArray MoviesArray = Reviews_json.getJSONArray(TMDB_RESULTS);

            for (int i = 0; i < MoviesArray.length(); i++) {
                // Get the JSON object representing the movie
                JSONObject ReviewsData = MoviesArray.getJSONObject(i);
                content = ReviewsData.getString(TMDB_CONTENT);
                ReviewsArray.add(i, content);
            }
            return ReviewsArray;
        }

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
                if(params[0].equals("http://api.themoviedb.org/3/movie/"+ID+"/videos?api_key="+API_KEY))
                    return getTrailersFromJson(MoviesData);
                else if(params[0].equals("http://api.themoviedb.org/3/movie/"+ID+"/reviews?api_key="+API_KEY))
                    return getReviewsFromJson(MoviesData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            TrailerI = new ArrayList<String>(TrailersArray.size());
            ReviewI = new ArrayList<String>(ReviewsArray.size());
            for(int i=0 ; i<TrailersArray.size() ; i++){
                TrailerI.add(i, "Trailer " + (i + 1));
            }
            for(int i=0 ; i<ReviewsArray.size() ; i++) {
                ReviewI.add(i, "Review " + (i + 1));
            }
            DataChild.put(DataHeader.get(0), TrailerI);
            DataChild.put(DataHeader.get(1), ReviewI);
            expandableListView.setAdapter(expandedListAdapter);
        }
    }
}