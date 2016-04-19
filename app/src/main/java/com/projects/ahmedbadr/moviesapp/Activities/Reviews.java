package com.projects.ahmedbadr.moviesapp.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.projects.ahmedbadr.moviesapp.R;

public class Reviews extends ActionBarActivity {
    String Review;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        textView = (TextView) findViewById(R.id.review_text);
        Intent intent = this.getIntent();
        if(intent!=null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            Review = intent.getStringExtra(Intent.EXTRA_TEXT);
            textView.setText(Review);
        }
    }
}
