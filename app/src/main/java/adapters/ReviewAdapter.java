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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import pojosplaceid.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

  private static final String LOG_TAG = RecyclerView.class.getSimpleName();
  private List<Review> mReviewId = new ArrayList<>();


  public ReviewAdapter(int numberOfItems){

  }

  @NonNull
  @Override
  public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
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
    Integer time = currentReview.getTime();
    SimpleDateFormat newFormat = new SimpleDateFormat("yyyyMMdd");
    String formattedDate = newFormat.format(time);
    String reviewText = currentReview.getText();
    holder.tvReviewAuthor.setText(authorName);
    holder.tvReviewTime.setText(String.valueOf(relativeTimeDescription));
    holder.tvReview.setText(reviewText);
    holder.ratingBar.setRating(authorRating);

  }

  public void addAll(List<Review> reviews) {
    if (mReviewId != null)
      mReviewId.clear();
    mReviewId.addAll(reviews);
    notifyDataSetChanged();
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
