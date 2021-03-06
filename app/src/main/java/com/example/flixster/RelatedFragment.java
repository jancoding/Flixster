package com.example.flixster;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RelatedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RelatedFragment extends Fragment {


    // tag for page argument
    public static final String ARG_PAGE = "ARG_PAGE";
    // list of related movies
    ArrayList<Movie> related = new ArrayList<>();
    // adapter for recycler view for related movies
    MovieAdapter relatedAdapter;

    public RelatedFragment() {
        // Required empty public constructor
    }

    // static method for new instance of RelatedFragment that takes in page argument
    public static RelatedFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        RelatedFragment fragment = new RelatedFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_related, container, false);
        // if necessary sets up related view
        if (getActivity() instanceof MovieDetailsActivity) {
            setUpRelated(view);
        }
        return view;
    }

    public void setUpRelated(View view) {
        RecyclerView rvRelated = view.findViewById(R.id.rvRelated);
        // Create the adapter
        relatedAdapter = new MovieAdapter(getActivity(), related);
        // Set the adapter on the recycler view
        rvRelated.setAdapter(relatedAdapter);
        // Set a Layout Manager the recycler view
        rvRelated.setLayoutManager(new LinearLayoutManager(getActivity()));
        // adds vertical space
        rvRelated.addItemDecoration(new VerticalSpaceItemDecoration(48));
        // retrieves and displays related movies
        getRelatedMovies();

    }

    // retrieves and displays related movies
    public void getRelatedMovies() {

        // Unwraps the movie passed in to get related movies of
        Movie movie = (Movie) Parcels.unwrap(getActivity().getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        String RELATED_MOVIES = String.format("https://api.themoviedb.org/3/movie/%s/similar?api_key=585766a816164944e743abb85aa6bddd&language=en-US&page=1", movie.getId());

        // HTTP Request to get related movies
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(RELATED_MOVIES, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    related.addAll(Movie.fromJsonArray(results));
                    relatedAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
            }
        });
    }

    // Used for vertical space between Recycler View components
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