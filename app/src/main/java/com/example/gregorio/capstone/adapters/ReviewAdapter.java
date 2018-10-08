package com.example.gregorio.capstone.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.gregorio.capstone.R;

import java.util.ArrayList;
import java.util.List;

import pojosplaceid.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();
    private List<Review> reviewId = new ArrayList<>();
    private Context context;

    public ReviewAdapter() {
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.rewiew_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review currentReview = reviewId.get(position);
        String authorName = currentReview.getAuthorName();
        String relativeTimeDescription = currentReview.getRelativeTimeDescription();
        Integer authorRating = currentReview.getRating();
        String reviewText = currentReview.getText();
        holder.reviewAuthor.setText(authorName);
        holder.reviewTime.setText(String.valueOf(relativeTimeDescription));
        holder.review.setText(reviewText);
        holder.ratingBar.setRating(authorRating);
        // Content Description
        holder.ratingBar
                .setContentDescription(context.getString(R.string.the_rating_is_cd) + authorRating);
        holder.reviewAuthor
                .setContentDescription(context.getString(R.string.the_name_is_cd) + authorName);
        holder.review
                .setContentDescription(context.getString(R.string.the_revies_is_cd) + reviewText);
        holder.reviewTime.setContentDescription(
                context.getString(R.string.review_time_cd) + relativeTimeDescription);
    }

    public void addAll(List<Review> reviews) {
        if (reviewId != null) {
            reviewId.clear();
            reviewId.addAll(reviews);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return reviewId.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {

        final RatingBar ratingBar;
        private final TextView review;
        private final TextView reviewTime;
        private final TextView reviewAuthor;

        ReviewViewHolder(View view) {
            super(view);
            review = view.findViewById(R.id.review_place_id);
            reviewAuthor = view.findViewById(R.id.review_author);
            reviewTime = view.findViewById(R.id.review_time);
            ratingBar = view.findViewById(R.id.star_rating);
        }
    }
}
