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

public class ClubsAdapter extends RecyclerView.Adapter<ClubsAdapter.ViewHolder> {

  private static final String LOG_TAG = ClubsAdapter.class.getSimpleName();
  private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
  private List<Result> clubPlaceId = new ArrayList<>();
  private String mApiKey;
  private Context context;
  private final AdapterOnClickHandler mClickHandler;
  //private final RequestManager glide;


  public ClubsAdapter(AdapterOnClickHandler adapterOnClickHandler, String apiKey) {
    this.mApiKey = apiKey;
    this.mClickHandler = adapterOnClickHandler;
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    context = parent.getContext();
    int layoutIdForListItem = R.layout.clubs_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    boolean shouldAttachToParentImmediately = false;
    View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    Result currentPlaceId = clubPlaceId.get(position);
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
    holder.mImage.setContentDescription(context.getString(R.string.the_image_view_cd) + name);
    holder.mAddress.setContentDescription(context.getString(R.string.the_address_is_cd) + address);
    holder.mName.setContentDescription(context.getString(R.string.the_name_is_cd) + name);
  }

  @Override
  public int getItemCount() {
    return clubPlaceId.size();
  }

  public void addAll(List<Result> result) {
    if (clubPlaceId != null) {
      clubPlaceId.clear();
    }
    clubPlaceId.addAll(result);
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final ImageView mImage;
    public final TextView mName;
    public final TextView mAddress;

    public ViewHolder(View itemView) {
      super(itemView);
      mImage = itemView.findViewById(R.id.clubs_photo_place_id);
      mName = itemView.findViewById(R.id.club_place_name);
      mAddress = itemView.findViewById(R.id.club_place_address);
      itemView.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
      int adapterPosition = getAdapterPosition();
      Result result = clubPlaceId.get(adapterPosition);
      String placeId = result.getPlaceId();
      Log.i(LOG_TAG, "The Place id clicked is" + placeId);
      mClickHandler.onClick(result);
    }
  }

}
