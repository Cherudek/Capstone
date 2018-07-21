package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class SightsAdapter extends RecyclerView.Adapter<SightsAdapter.SightsViewHolder> {

  private static final String LOG_TAG = FavouritesAdapter.class.getSimpleName();
  private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
  private List<Result> sightsPlaceId = new ArrayList<>();
  private String mApiKey;
  private int mFavouriteSize;
  private Context context;
  private final AdapterOnClickHandler mClickHandler;


  public SightsAdapter(AdapterOnClickHandler adapter, String apiKey) {
    this.mClickHandler = adapter;
    this.mApiKey = apiKey;
  }

  @NonNull
  @Override
  public SightsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    context = parent.getContext();
    int layoutIdForListItem = R.layout.sights_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    boolean shouldAttachToParentImmediately = false;
    View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
    return new SightsViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull SightsViewHolder holder, int position) {
    Result currentPlaceId = sightsPlaceId.get(position);
    String photoReference = currentPlaceId.getPhotos().get(0).getPhotoReference();
    String address = currentPlaceId.getVicinity();
    String name = currentPlaceId.getName();
    String placeId = currentPlaceId.getPlaceId();
    String photoUrl =
        PHOTO_PLACE_URL + "maxwidth=100&photoreference=" + photoReference + "&key=" + mApiKey;
    Glide.with(context)
        .load(photoUrl)
        .into(holder.mSightImage);
    holder.mSightName.setText(name);
    holder.mSightAddress.setText(address);
    // Enable dynamic content description
    holder.mSightImage.setContentDescription("Image View for " + name);
    holder.mSightAddress.setContentDescription("The address is: " + address);
    holder.mSightName.setContentDescription("The Name of the place is: " + name);
  }

  /**
   * The interface that receives onClick messages.
   */
  public interface AdapterOnClickHandler {

    void onClick(Result result);
  }

  @Override
  public int getItemCount() {
    return sightsPlaceId.size();
  }

  public void addAll(List<Result> result) {
    if (sightsPlaceId != null) {
      sightsPlaceId.clear();
    }
    sightsPlaceId.addAll(result);
    notifyDataSetChanged();
  }

  public class SightsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final ImageView mSightImage;
    public final TextView mSightName;
    public final TextView mSightAddress;

    public SightsViewHolder(View itemView) {
      super(itemView);
      mSightImage = itemView.findViewById(R.id.sights_photo_place_id);
      mSightName = itemView.findViewById(R.id.sights_place_name);
      mSightAddress = itemView.findViewById(R.id.sights_place_address);
      itemView.setOnClickListener(this::onClick);

    }

    @Override
    public void onClick(View v) {
      int adapterPosition = getAdapterPosition();
      Result result = sightsPlaceId.get(adapterPosition);
      String placeId = result.getPlaceId();
      Log.i(LOG_TAG, "The Place id clicked is" + placeId);
      mClickHandler.onClick(result);

    }
  }
}
