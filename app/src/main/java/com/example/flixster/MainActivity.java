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


    // variables for URLs for different retrieving information from API
    public static String NOW_PLAYING_URL;
    public static String TOP_RATED_URL;
    public static String UPCOMING_URL;

    // tag for log calls
    public static final String TAG = "MainActivity";

    // list of movies to display on screen and adapter for recycler view
    List<Movie> movies;
    MovieAdapter movieAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // set up all the request URLS
        String tmdb_key = getString(R.string.tmdbkey);
        NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=" + tmdb_key;
        TOP_RATED_URL = String.format("https://api.themoviedb.org/3/movie/top_rated?api_key=%s&language=en-US&page=1", tmdb_key);
        UPCOMING_URL = String.format("https://api.themoviedb.org/3/movie/upcoming?api_key=%s&language=en-US&page=1", tmdb_key);

        // ViewBinding Implementation
        // replacing this line below with view binding: setContentView(R.layout.activity_main);
        // activity_main.xml -> ActivityMainBinding
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        // layout of activity is stored in a special property called root
        View view = binding.getRoot();
        setContentView(view);

        movies = new ArrayList<>();

        // Create the adapter
        movieAdapter = new MovieAdapter(this, movies);
        // Set the adapter on the recycler view
        binding.rvMovies.setAdapter(movieAdapter);
        // Set a Layout Manager the recycler view
        binding.rvMovies.setLayoutManager(new LinearLayoutManager(this));
        // Adds vertical space between components of RecyclerView
        binding.rvMovies.addItemDecoration(new VerticalSpaceItemDecoration(48));


        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        // loads appropriate content based on tab selected
        handleRequest(tabLayout.getSelectedTabPosition());



        // switch recycler view content based on tab selected
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

    // does GET requests based on information type required
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


    // Class to help implement vertical space between RecyclerView components
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