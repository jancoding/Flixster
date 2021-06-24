package com.example.flixster.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flixster.R;
import com.example.flixster.models.Movie;
import com.example.flixster.models.Review;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeoutException;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    Context context;
    List<Review> reviews;

    public ReviewsAdapter(Context context, List<Review> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View reviewView = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ViewHolder(reviewView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvAuthor;
        TextView tvContent;
        TextView tvDate;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvDate = itemView.findViewById(R.id.tvDate);

        }

        public void bind(Review review) {
            tvAuthor.setText(review.getAuthorName());
            tvContent.setText(review.getContent());
            tvDate.setText(review.getDate().split("T", 2)[0]);


        }

    }


}


