package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.gregorio.capstone.R;
import java.util.ArrayList;
import java.util.List;
import pojosplaceid.Result;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

  private static final String LOG_TAG = FoodAdapter.class.getSimpleName();
  private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
  private List<Result> foodPlaceId = new ArrayList<>();
  private String mApiKey;
  private Context context;

  public FoodAdapter(String apiKey) {
    this.mApiKey = apiKey;
  }

  @NonNull
  @Override
  public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    context = parent.getContext();
    int layoutIdForListItem = R.layout.food_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    boolean shouldAttachToParentImmediately = false;
    View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
    return new FoodViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
    Result currentPlaceId = foodPlaceId.get(position);
    String photoReference = currentPlaceId.getPhotos().get(0).getPhotoReference();
    String address = currentPlaceId.getVicinity();
    String name = currentPlaceId.getName();
    String placeId = currentPlaceId.getPlaceId();
    String photoUrl =
        PHOTO_PLACE_URL + "maxwidth=100&photoreference=" + photoReference + "&key=" + mApiKey;
    Glide.with(context)
        .load(photoUrl)
        .into(holder.mImage);
    holder.mName.setText(name);
    holder.mAddress.setText(address);
    // Enable dynamic content description
    holder.mImage.setContentDescription("Image View for " + name);
    holder.mAddress.setContentDescription("The address is: " + address);
    holder.mName.setContentDescription("The Name of the place is: " + name);
  }

  @Override
  public int getItemCount() {
    return foodPlaceId.size();
  }

  public void addAll(List<Result> result) {
    if (foodPlaceId != null) {
      foodPlaceId.clear();
    }
    foodPlaceId.addAll(result);
    notifyDataSetChanged();
  }


  public class FoodViewHolder extends RecyclerView.ViewHolder {

    public final ImageView mImage;
    public final TextView mName;
    public final TextView mAddress;

    public FoodViewHolder(View itemView) {
      super(itemView);
      mImage = itemView.findViewById(R.id.food_photo_place_id);
      mName = itemView.findViewById(R.id.food_place_name);
      mAddress = itemView.findViewById(R.id.food_place_address);

    }

  }


}
