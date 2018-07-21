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

public class FavouriteReviewAdapter extends RecyclerView.Adapter<FavouriteReviewAdapter.FavouriteReviewViewHold> {

  private static final String LOG_TAG = FavouriteReviewAdapter.class.getSimpleName();
  private List<Review> mReviewId = new ArrayList<>();

  public FavouriteReviewAdapter() {
  }

  @NonNull
  @Override
  public FavouriteReviewViewHold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    int layoutIdForListItem = R.layout.favourite_review_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    boolean shouldAttachToParentImmediately = false;
    View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
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
    holder.tvReviewAuthor.setContentDescription("The Reviewer's name is " + authorName);
    holder.tvReview.setContentDescription("The Review is: " + reviewText);
    holder.ratingBar.setContentDescription("The Rating of the place is: " + authorRating);
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

  static class FavouriteReviewViewHold extends RecyclerView.ViewHolder {

    public final TextView tvReview;
    public final TextView tvReviewTime;
    public final TextView tvReviewAuthor;
    public final RatingBar ratingBar;

    public FavouriteReviewViewHold(View view) {
      super(view);
      tvReview = view.findViewById(R.id.favourite_review_place_id);
      tvReviewAuthor = view.findViewById(R.id.favourite_review_author);
      tvReviewTime = view.findViewById(R.id.favourite_review_time);
      ratingBar = view.findViewById(R.id.favourite_star_rating);
    }
  }
}
