package com.projects.ahmedbadr.moviesapp.Service;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed Badr on 14/6/2016.
 */
public class APIModel implements Parcelable {

    @SerializedName("results")
    public List<APIModel> MoviesList =new ArrayList<>();
    public String id;
    public String poster_path;
    public String original_title;
    public String overview;
    public float vote_average;
    public String release_date;
    public String key;
    public String content;
    public String author;

    public ArrayList<String> reviewContent;
    public ArrayList<String>trailers;

    public ArrayList<String>  getTrailers() {
        return trailers;
    }

    public void setTrailers(ArrayList<String> trailers) {
        this.trailers = trailers;
    }

    public ArrayList<String>  getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(ArrayList<String> reviewContent) {
        this.reviewContent = reviewContent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPoser_Path() {
        return poster_path;
    }

    public void setPoser_Path(String PoserPath) {
        this.poster_path = PoserPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public float getVote_average() {
        return vote_average;
    }

    public void setVote_average(float vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    protected APIModel(Parcel in) {
        MoviesList = in.createTypedArrayList(APIModel.CREATOR);
        id = in.readString();
        poster_path = in.readString();
        original_title = in.readString();
        overview = in.readString();
        vote_average = in.readFloat();
        release_date = in.readString();
        key = in.readString();
        content = in.readString();
        author = in.readString();
    }

    public static final Creator<APIModel> CREATOR = new Creator<APIModel>() {
        @Override
        public APIModel createFromParcel(Parcel in) {
            return new APIModel(in);
        }

        @Override
        public APIModel[] newArray(int size) {
            return new APIModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(MoviesList);
        dest.writeString(id);
        dest.writeString(poster_path);
        dest.writeString(original_title);
        dest.writeString(overview);
        dest.writeFloat(vote_average);
        dest.writeString(release_date);
        dest.writeString(key);
        dest.writeString(content);
        dest.writeString(author);
    }
}
