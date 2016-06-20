package com.projects.ahmedbadr.moviesapp.Service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Ahmed Badr on 14/6/2016.
 */
public interface ServiceInterfaces {

    interface Movies {
        @GET("movie/{SortType}")
        Call<APIModel> getMovies(
                @Path("SortType") String SortBy,
                @Query("api_key") String API_KEY
        );
    }

    interface Trailers {
        @GET("movie/{ID}/videos")
        Call<APIModel> getTrailers(
                @Path("ID") String ID,
                @Query("api_key") String API_KEY
        );
    }

    interface Reviews {
        @GET("movie/{ID}/reviews")
        Call<APIModel> getReviews(
                @Path("ID") String ID,
                @Query("api_key") String API_KEY
        );
    }
}
