package com.example.flixster;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.ReviewsAdapter;
import com.example.flixster.models.Movie;
import com.example.flixster.models.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PageFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static final int DISPLAY_MOVIE_CODE = 20;
    public static String MOVIE_VIDEO = "";
    public static final String YOUTUBE_KEY = "YoutubeKey";

    private int mPage;

    TextView tvDetailTitle;
    TextView tvDetailOverview;
    RatingBar rbVoteAverage;
    RecyclerView rvReviews;
    ImageView ivVideo;
    String youtubeKey;
    List<Review> reviews;
    ReviewsAdapter reviewAdapter;
    RelativeLayout rlDetails;

    // radius and margin for rounded transformation to image
    int radius = 20; // corner radius, higher value = more rounded
    int margin = 0; // crop margin, set to 0 for corners with no crop

    public static PageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);
        if (getActivity() instanceof MovieDetailsActivity) {
            setUpMovieDetails(view);
        }
        Log.d("hi", "created a fragment");
        return view;
    }

    public void setUpMovieDetails(View view) {
                // unwrap movie that was passed in
        Movie movie = (Movie) Parcels.unwrap(getActivity().getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        tvDetailTitle = view.findViewById(R.id.tvDetailTitle);
        tvDetailOverview = view.findViewById(R.id.tvDetailOverview);
        rbVoteAverage = view.findViewById(R.id.rbVoteAverage);
        ivVideo = view.findViewById(R.id.ivVideo);
        rvReviews = view.findViewById(R.id.rvReviews);
        rlDetails = view.findViewById(R.id.rlDetails);

        Log.d("testing", "" + rvReviews);


        reviews = new ArrayList<>();
        // Create the adapter
        reviewAdapter = new ReviewsAdapter(getActivity(), reviews);
        // Set the adapter on the recycler view
        rvReviews.setAdapter(reviewAdapter);
        // Set a Layout Manager the recycler view
        rvReviews.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Set vertical spacing
        rvReviews.addItemDecoration(new VerticalSpaceItemDecoration(48));

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

        String MOVIE_REVIEWS = String.format("https://api.themoviedb.org/3/movie/%s/reviews?api_key=585766a816164944e743abb85aa6bddd&language=en-US&page=1", movie.getId());

        AsyncHttpClient client_reviews = new AsyncHttpClient();
        client_reviews.get(MOVIE_REVIEWS, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JsonHttpResponseHandler.JSON json) {
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
                Intent intent = new Intent(getActivity(), MovieTrailerActivity.class);
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
