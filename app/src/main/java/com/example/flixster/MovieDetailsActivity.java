package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.databinding.ActivityMainBinding;
import com.example.flixster.databinding.ActivityMovieDetailsBinding;
import com.example.flixster.models.Movie;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {

    Movie movie;

    // the view objects
    TextView tvDetailTitle;
    TextView tvDetailOverview;
    RatingBar rbVoteAverage;
    ImageView ivVideo;
    String youtubeKey;
    public static String MOVIE_VIDEO = "";
    public static final int DISPLAY_MOVIE_CODE = 20;
    public static final String YOUTUBE_KEY = "YoutubeKey";

    // radius and margin for rounded transformation to image
    int radius = 10; // corner radius, higher value = more rounded
    int margin = 0; // crop margin, set to 0 for corners with no crop

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // replacing this line below with view binding: setContentView(R.layout.activity_movie_details)
        // activity_movie_details.xml -> ActivityMovieDetailsBinding
        ActivityMovieDetailsBinding binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        // unwrap movie that was passed in
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailOverview = findViewById(R.id.tvDetailOverview);
        rbVoteAverage = findViewById(R.id.rbVoteAverage);
        ivVideo = findViewById(R.id.ivVideo);



        tvDetailTitle.setText(movie.getTitle());
        tvDetailOverview.setText(movie.getOverview());
        Glide.with(this)
                .load(movie.getBackdropPath())
                .placeholder(R.drawable.flicks_backdrop_placeholder)
                .transform(new RoundedCornersTransformation(radius, margin))
                .into(ivVideo);

        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage / 2.0f);

        MOVIE_VIDEO = String.format("https://api.themoviedb.org/3/movie/%s/videos?api_key=585766a816164944e743abb85aa6bddd&language=en-US", movie.getId());

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(MOVIE_VIDEO, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    JSONObject first = results.getJSONObject(0);
                    youtubeKey = first.getString("key");

                } catch (JSONException e) {
                    Log.e("MovieDetailsActivity", "Hit json exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d("MovieDetailsActivity", "onFailure");

            }
        });

        ivVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                intent.putExtra(YOUTUBE_KEY, youtubeKey);
                startActivityForResult(intent, DISPLAY_MOVIE_CODE);
            }
        });







    }


}