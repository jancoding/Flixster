package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.adapters.SampleFragmentPagerAdapter;
import com.example.flixster.databinding.ActivityMainBinding;
import com.example.flixster.models.Movie;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {

    public static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=585766a816164944e743abb85aa6bddd";
    public static final String TOP_RATED_URL = "https://api.themoviedb.org/3/movie/top_rated?api_key=585766a816164944e743abb85aa6bddd&language=en-US&page=1";
    public static final String UPCOMING_URL = "https://api.themoviedb.org/3/movie/upcoming?api_key=585766a816164944e743abb85aa6bddd&language=en-US&page=1";
    public static final String TAG = "MainActivity";
    List<Movie> movies;
    MovieAdapter movieAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // replacing this line below with view binding: setContentView(R.layout.activity_main);
        // activity_main.xml -> ActivityMainBinding
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        RecyclerView rvMovies = findViewById(R.id.rvMovies);
        movies = new ArrayList<>();

        // Create the adapter
        movieAdapter = new MovieAdapter(this, movies);
        // Set the adapter on the recycler view
        rvMovies.setAdapter(movieAdapter);
        // Set a Layout Manager the recycler view
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        rvMovies.addItemDecoration(new VerticalSpaceItemDecoration(48));


        handleRequest(0);


        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);


        // switch recycler view content based on tab
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                movies.clear();
                Log.d("MainActivity", "the position is: " + position);
                handleRequest(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void handleRequest(int type) {
        AsyncHttpClient client = new AsyncHttpClient();
        String toRequestURL;
        if (type == 0) {
            toRequestURL = NOW_PLAYING_URL;
        } else if (type == 1) {
            toRequestURL = TOP_RATED_URL;
        } else {
            toRequestURL = UPCOMING_URL;
        }
        client.get(toRequestURL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    movies.addAll(Movie.fromJsonArray(results));
                    Log.i(TAG, "Movies: " + movies.size());
                    movieAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "onFailure");
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