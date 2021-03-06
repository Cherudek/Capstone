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
import com.example.gregorio.capstone.model.placeId.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FavouriteReviewAdapter extends RecyclerView.Adapter<FavouriteReviewAdapter.FavouriteReviewViewHold> {

    private static final String LOG_TAG = FavouriteReviewAdapter.class.getSimpleName();
    private List<Review> mReviewId = new ArrayList<>();
    private Context context;

    public FavouriteReviewAdapter() {
    }

    @NonNull
    @Override
    public FavouriteReviewViewHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.favourite_review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new FavouriteReviewViewHold(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteReviewViewHold holder, int position) {
        Review currentReview = mReviewId.get(position);
        String authorName = currentReview.getAuthorName();
        String relativeTimeDescription = currentReview.getRelativeTimeDescription();
        Integer authorRating = currentReview.getRating();
        String reviewText = currentReview.getText();
        holder.tvReviewAuthor.setText(authorName);
        holder.tvReviewTime.setText(String.valueOf(relativeTimeDescription));
        holder.tvReview.setText(reviewText);
        holder.ratingBar.setRating(authorRating);
        // Enable dynamic content description
        holder.tvReviewAuthor
                .setContentDescription(context.getString(R.string.the_reviewer_name_cd) + authorName);
        holder.tvReview
                .setContentDescription(context.getString(R.string.the_revies_is_cd) + reviewText);
        holder.ratingBar
                .setContentDescription(context.getString(R.string.the_rating_is_cd) + authorRating);
    }

    public void addAll(Optional<List<Review>> reviews) {
        if (mReviewId != null) {
            reviews.ifPresent(reviews1 -> mReviewId.addAll(reviews1));
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mReviewId.size();

    }

    static class FavouriteReviewViewHold extends RecyclerView.ViewHolder {

        private final TextView tvReview;
        private final TextView tvReviewTime;
        private final TextView tvReviewAuthor;
        private final RatingBar ratingBar;

        private FavouriteReviewViewHold(View view) {
            super(view);
            tvReview = view.findViewById(R.id.favourite_review_place_id);
            tvReviewAuthor = view.findViewById(R.id.favourite_review_author);
            tvReviewTime = view.findViewById(R.id.favourite_review_time);
            ratingBar = view.findViewById(R.id.favourite_star_rating);
        }
    }
}
