package com.projects.ahmedbadr.moviesapp.Service;

import com.projects.ahmedbadr.moviesapp.Utilities.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ahmed Badr on 14/6/2016.
 */
public class ServiceBuilder {

    public Retrofit retrofit;

    public ServiceBuilder(){
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public ServiceInterfaces.Movies BuildMovies(){
        return retrofit.create(ServiceInterfaces.Movies.class);
    }

    public ServiceInterfaces.Trailers BuildTrailers(){
        return retrofit.create(ServiceInterfaces.Trailers.class);
    }

    public ServiceInterfaces.Reviews BuildReviews(){
        return retrofit.create(ServiceInterfaces.Reviews.class);
    }
}
