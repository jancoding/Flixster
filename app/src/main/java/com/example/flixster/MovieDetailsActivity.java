package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.adapters.ReviewsAdapter;
import com.example.flixster.databinding.ActivityMainBinding;
import com.example.flixster.databinding.ActivityMovieDetailsBinding;
import com.example.flixster.models.Movie;
import com.example.flixster.models.Review;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {

    Movie movie;

    // the view objects
    TextView tvDetailTitle;
    TextView tvDetailOverview;
    RatingBar rbVoteAverage;
    RecyclerView rvReviews;
    ImageView ivVideo;
    String youtubeKey;
    List<Review> reviews;
    ReviewsAdapter reviewAdapter;
    RelativeLayout rlDetails;
    public static String MOVIE_VIDEO = "";
    public static String MOVIE_REVIEWS = "";
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
        rvReviews = findViewById(R.id.rvReviews);
        rlDetails = findViewById(R.id.rlDetails);


        reviews = new ArrayList<>();
        // Create the adapter
        reviewAdapter = new ReviewsAdapter(this, reviews);
        // Set the adapter on the recycler view
        rvReviews.setAdapter(reviewAdapter);
        // Set a Layout Manager the recycler view
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        // Set vertical spacing
        rvReviews.addItemDecoration(new MovieDetailsActivity.VerticalSpaceItemDecoration(48));




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
        if (voteAverage/2.0f > 4) {
            Log.d("color", "IN HERE green");
            rlDetails.setBackgroundColor(Color.rgb(175, 235, 169));

        } else if (voteAverage/2.0f > 2.5) {
            Log.d("color", "IN HERE yellow");
            rlDetails.setBackgroundColor(Color.rgb(224, 235, 127));
        } else {
            Log.d("color", "IN HERE red");
            rlDetails.setBackgroundColor(Color.rgb(240, 129, 144));
        }


        // request to get details for a specific movie, rating and youtubeKey for trailer

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

        // request to get Reviews for the specific movie

        MOVIE_REVIEWS = String.format("https://api.themoviedb.org/3/movie/%s/reviews?api_key=585766a816164944e743abb85aa6bddd&language=en-US&page=1", movie.getId());

        AsyncHttpClient client_reviews = new AsyncHttpClient();
        client_reviews.get(MOVIE_REVIEWS, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    reviews.addAll(Review.fromJsonArray(results));
                    Log.d("NumReviews", "number of reviews: " + reviews.size());
                    reviewAdapter.notifyDataSetChanged();

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




        // When backdrop image is click will lead to screen to play movie
        ivVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                intent.putExtra(YOUTUBE_KEY, youtubeKey);
                startActivityForResult(intent, DISPLAY_MOVIE_CODE);
            }
        });







    }

    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }


}