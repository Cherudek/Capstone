package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.gregorio.capstone.R;
import java.util.ArrayList;
import java.util.List;
import pojosplaceid.Result;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouriteViewHolder> {

  private static final String LOG_TAG = FavouritesAdapter.class.getSimpleName();
  private List<Result> favouritesPlaceId = new ArrayList<>();
  private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
  private String mApiKey;
  private Context context;
  private String photoReference = "";
  private  final FavouriteAdapterOnClickHandler mClickHandler;

  public FavouritesAdapter(FavouriteAdapterOnClickHandler clickHandler, int size, String apiKey){
    mApiKey = apiKey;
    this.mClickHandler = clickHandler;
  }


  @NonNull
  @Override
  public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    context = parent.getContext();
    int layoutIdForListItem = R.layout.favourite_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    boolean shouldAttachToParentImmediately = false;
    View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
    return new FavouriteViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
    Result currentPlaceId = favouritesPlaceId.get(position);
    if (currentPlaceId.getPhotos() != null) {
      photoReference = currentPlaceId.getPhotos().get(0).getPhotoReference();
    }
    String address = currentPlaceId.getVicinity();
    String name = currentPlaceId.getName();
    String placeId = currentPlaceId.getPlaceId();
    String photoUrl = PHOTO_PLACE_URL + "maxwidth=100&photoreference=" + photoReference + "&key=" + mApiKey;
    Glide.with(context)
        .load(photoUrl)
        .into(holder.mFavouriteImage);

    ViewCompat.setTransitionName(holder.mFavouriteImage, name);

    holder.mFavouriteName.setText(name);
    holder.mFavouriteAddress.setText(address);
    // Enable dynamic content description
    holder.mFavouriteImage
        .setContentDescription(context.getString(R.string.the_image_view_cd) + name);
    holder.mFavouriteAddress
        .setContentDescription(context.getString(R.string.the_address_is_cd) + address);
    holder.mFavouriteName.setContentDescription(context.getString(R.string.the_name_is_cd) + name);
  }

  public void addAll(List<Result> result) {
    if(favouritesPlaceId!=null)
      favouritesPlaceId.clear();
      favouritesPlaceId.addAll(result);
      Log.i(LOG_TAG, "addAll PlaceId = " + favouritesPlaceId);
      notifyDataSetChanged();
  }

  public void removeItem(int position) {
    favouritesPlaceId.remove(position);
    // notify the item removed by position
    // to perform recycler view delete animations
    // NOTE: don't call notifyDataSetChanged()
    notifyItemRemoved(position);
  }

  public void restoreItem(Result item, int position) {
    favouritesPlaceId.add(position, item);
    // notify item added by position
    notifyItemInserted(position);
  }

  @Override
  public int getItemCount() {
    Log.i(LOG_TAG, "getItemCount PlaceId Size = " + favouritesPlaceId.size());
    return favouritesPlaceId.size();
  }
  /**
   * The interface that receives onClick messages.
   */
  public interface FavouriteAdapterOnClickHandler {

    void onClick(Result result, View view);
  }

  public class FavouriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final ImageView mFavouriteImage;
    public final TextView mFavouriteName;
    public final TextView mFavouriteAddress;
    public final CardView viewForeground;
   // public final RelativeLayout viewBackground;

    public FavouriteViewHolder(View itemView){
      super(itemView);
      mFavouriteImage = itemView.findViewById(R.id.favourite_photo_place_id);
      mFavouriteName = itemView.findViewById(R.id.favourite_place_name);
      mFavouriteAddress = itemView.findViewById(R.id.favourite_place_address);
     // viewBackground = itemView.findViewById(R.id.view_background);
      viewForeground = itemView.findViewById(R.id.favourite_card_view);
      itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
      int adapterPosition = getAdapterPosition();
      Result result = favouritesPlaceId.get(adapterPosition);
      String placeId = result.getPlaceId();
      Log.i(LOG_TAG,"The Place id clicked is" + placeId);
      mClickHandler.onClick(result, v);

    }
  }
}
