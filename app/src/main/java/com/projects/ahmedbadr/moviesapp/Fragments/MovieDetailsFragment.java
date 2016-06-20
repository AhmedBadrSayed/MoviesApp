package com.projects.ahmedbadr.moviesapp.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
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
import com.projects.ahmedbadr.moviesapp.Service.APIModel;
import com.projects.ahmedbadr.moviesapp.Service.ServiceBuilder;
import com.projects.ahmedbadr.moviesapp.Service.ServiceInterfaces;
import com.projects.ahmedbadr.moviesapp.Utilities.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareFavoriteIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareFavoriteIntent() {
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
        TrailersArray = new ArrayList<>();
        ReviewsArray = new ArrayList<>();
        DataHeader = new ArrayList<>();
        DataChild = new HashMap<>();
        TrailerI = new ArrayList<>();
        ReviewI = new ArrayList<>();
        button.setOnClickListener(this);
        if(moviesDB.isInDataBase(MovieTitle)==true){
            button.setImageResource(R.drawable.onpress);
        }else button.setImageResource(R.drawable.off);
        DataHeader.add("Trailers");
        DataHeader.add("Reviews");
        expandedListAdapter = new ExpandedListAdapter(getActivity(), DataHeader, DataChild);
        PerformTrailersCall(ID);
        PerformReviewsCall(ID);
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

    void PerformTrailersCall(String movieID){
        ServiceBuilder builder = new ServiceBuilder();
        ServiceInterfaces.Trailers trailers = builder.BuildTrailers();
        Call<APIModel> apiModelCall = trailers.getTrailers(movieID, Constants.API_KEY);
        apiModelCall.enqueue(new Callback<APIModel>() {
            @Override
            public void onResponse(Call<APIModel> call, Response<APIModel> response) {
                for (int i=0 ; i<response.body().MoviesList.size() ; i++){
                    TrailersArray.add(i, Constants.TRAILERS_BASIC_URL + response.body().MoviesList.get(i).key);
                }

                TrailerI = new ArrayList<>(TrailersArray.size());
                for(int i=0 ; i<TrailersArray.size() ; i++){
                    TrailerI.add(i, "Trailer " + (i + 1));
                }
                DataChild.put(DataHeader.get(0), TrailerI);
                expandableListView.setAdapter(expandedListAdapter);
            }

            @Override
            public void onFailure(Call<APIModel> call, Throwable t) {
                Log.v("Retrieve Error", t.toString());
            }
        });
    }

    void PerformReviewsCall(String movieID){
        ServiceBuilder builder = new ServiceBuilder();
        ServiceInterfaces.Reviews reviews = builder.BuildReviews();
        Call<APIModel> apiModelCall = reviews.getReviews(movieID, Constants.API_KEY);
        apiModelCall.enqueue(new Callback<APIModel>() {
            @Override
            public void onResponse(Call<APIModel> call, Response<APIModel> response) {
                for (int i=0 ; i<response.body().MoviesList.size() ; i++){
                    ReviewsArray.add(i, response.body().MoviesList.get(i).content);
                }
                ReviewI = new ArrayList<>(ReviewsArray.size());
                for(int i=0 ; i<ReviewsArray.size() ; i++) {
                    ReviewI.add(i, "Review " + (i + 1));
                }
                DataChild.put(DataHeader.get(1), ReviewI);
                expandableListView.setAdapter(expandedListAdapter);
            }

            @Override
            public void onFailure(Call<APIModel> call, Throwable t) {
                Log.v("Retrieve Error", t.toString());
            }
        });
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

}