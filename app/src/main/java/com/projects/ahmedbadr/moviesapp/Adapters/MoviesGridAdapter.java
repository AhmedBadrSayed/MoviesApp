package com.projects.ahmedbadr.moviesapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.projects.ahmedbadr.moviesapp.R;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

/**
 * Created by Ahmed Badr for Splash on 14/3/2016.
 */
public class MoviesGridAdapter extends BaseAdapter {

    Context activity;
    ArrayList<String> Posters = new ArrayList<String>();

    public MoviesGridAdapter(ArrayList<String> Posters, Context activity) {
        this.Posters = Posters;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return Posters.size();
    }

    @Override
    public Object getItem(int position) {
        return Posters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.movie_view,null);
        }
        ImageView poster_image = (ImageView) convertView.findViewById(R.id.movie_poster);
        //poster_image.setMaxHeight(278);
        Picasso.with(activity).load(Posters.get(position)).into(poster_image);
        poster_image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return convertView;
    }
}
