package adapters;

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
  private List<Review> mReviewId = new ArrayList<>();
  private Context context;


  public ReviewAdapter(int numberOfItems){

  }

  @NonNull
  @Override
  public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    context = parent.getContext();
    int layoutIdForListItem = R.layout.rewiew_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    boolean shouldAttachToParentImmediately = false;
    View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
    return new ReviewViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
    Review currentReview = mReviewId.get(position);
    String authorName = currentReview.getAuthorName();
    String relativeTimeDescription = currentReview.getRelativeTimeDescription();
    Integer authorRating = currentReview.getRating();
    String reviewText = currentReview.getText();
    holder.tvReviewAuthor.setText(authorName);
    holder.tvReviewTime.setText(String.valueOf(relativeTimeDescription));
    holder.tvReview.setText(reviewText);
    holder.ratingBar.setRating(authorRating);
    // Content Description
    holder.ratingBar
        .setContentDescription(context.getString(R.string.the_rating_is_cd) + authorRating);
    holder.tvReviewAuthor
        .setContentDescription(context.getString(R.string.the_name_is_cd) + authorName);
    holder.tvReview
        .setContentDescription(context.getString(R.string.the_revies_is_cd) + reviewText);
    holder.tvReviewTime.setContentDescription(
        context.getString(R.string.review_time_cd) + relativeTimeDescription);

  }

  public void addAll(List<Review> reviews) {
    if (mReviewId != null){
      mReviewId.clear();
      mReviewId.addAll(reviews);
      notifyDataSetChanged();
    }
  }

  @Override
  public int getItemCount() {
    return mReviewId.size();
  }

  static class ReviewViewHolder extends RecyclerView.ViewHolder {

    public final TextView tvReview;
    public final TextView tvReviewTime;
    public final TextView tvReviewAuthor;
    public final RatingBar ratingBar;

    public ReviewViewHolder(View view) {
      super(view);
      tvReview = view.findViewById(R.id.review_place_id);
      tvReviewAuthor = view.findViewById(R.id.review_author);
      tvReviewTime = view.findViewById(R.id.review_time);
      ratingBar = view.findViewById(R.id.star_rating);
    }
  }
}
