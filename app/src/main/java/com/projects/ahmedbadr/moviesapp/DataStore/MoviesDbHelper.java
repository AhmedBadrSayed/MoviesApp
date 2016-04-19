package com.projects.ahmedbadr.moviesapp.DataStore;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Ahmed Badr for MoviesApp on 6/4/2016.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "movies.db";
    public MoviesContract moviesContract;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " + moviesContract.TABLE_NAME + " (" +
                moviesContract.COLUMN_MOVIE_ID + " INTEGER PRIMARY KEY," +
                moviesContract.COLUMN_MOVIE_TITTLE + " TEXT NOT NULL, " +
                moviesContract.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                moviesContract.COLUMN_MOVIE_VOTE_AVERAGE + " REAL NOT NULL, " +
                moviesContract.COLUMN_MOVIE_DATE + " INTEGER NOT NULL, " +
                moviesContract.COLUMN_MOVIE_POSTERPATH + " INTEGER NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }
}

