package com.projects.ahmedbadr.moviesapp.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.projects.ahmedbadr.moviesapp.R;
import com.projects.ahmedbadr.moviesapp.Service.APIModel;
import com.projects.ahmedbadr.moviesapp.Service.ServiceBuilder;
import com.projects.ahmedbadr.moviesapp.Service.ServiceInterfaces;
import com.projects.ahmedbadr.moviesapp.Utilities.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoviesFragment extends Fragment {

    GridView gridView;
    List<APIModel> AllMovies;
    ArrayList<String> MoviesDetailsArray, PostersPathsArray, FavoritesPosters;
    MoviesDB moviesDB;
    ProgressDialog progressDialog;
    MoviesGridAdapter favoritesGridAdapter,moviesGridAdapter;

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
        MoviesDetailsArray = new ArrayList<>();
        PostersPathsArray = new ArrayList<>();
        FavoritesPosters = new ArrayList<>();
        AllMovies = new ArrayList<>();
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
        UpdateMovies();
        return RootView;
    }

    public void UpdateMovies(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String Url = pref.getString(getString(R.string.pref_sort_key)
                , getString(R.string.pref_sort_default));
        if(Url.equals("favorites")){
            retrieveAll();
        }
        else PerformMoviesCall(Url);
    }

    public void PerformMoviesCall(String SortType){

        //progressDialog = ProgressDialog.show(getActivity(),"", "Loading. Please wait...", true);
        ServiceBuilder builder = new ServiceBuilder();
        ServiceInterfaces.Movies movies = builder.BuildMovies();
        Call<APIModel> apiModelCall = movies.getMovies(SortType, Constants.API_KEY);
        apiModelCall.enqueue(new Callback<APIModel>() {
            @Override
            public void onResponse(Call<APIModel> call, Response<APIModel> response) {
                if (!isConnectedToInternet() || response.body()!=null ){
                    PostersPathsArray.clear();
                    AllMovies = response.body().MoviesList;
                    for(int i = 0; i < AllMovies.size(); i++) {
                        PostersPathsArray.add(i,Constants.BASIC_URL+AllMovies.get(i).poster_path);
                        MoviesDetailsArray.add(i,AllMovies.get(i).id+"="+AllMovies.get(i).original_title+"="+
                                AllMovies.get(i).overview+ "="+AllMovies.get(i).vote_average+"="+
                                AllMovies.get(i).release_date+"="+Constants.BASIC_URL+AllMovies.get(i).poster_path);
                    }
                    moviesGridAdapter = new MoviesGridAdapter(PostersPathsArray,getActivity());
                    gridView.setAdapter(moviesGridAdapter);
                    moviesGridAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<APIModel> call, Throwable t) {
                Log.v("Retrieve Error", t.toString());
                Toast.makeText(getActivity(), "Connection Failed", Toast.LENGTH_LONG).show();
            }
        });
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
    public void onStart() {
        super.onStart();
        UpdateMovies();
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