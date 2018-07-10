package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.gregorio.capstone.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import pojosplaceid.Result;
import pojosplaceid.Review;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouriteViewHolder> {

  private static final String LOG_TAG = FavouritesAdapter.class.getSimpleName();
  private List<Result> favouritesPlaceId = new ArrayList<>();
  private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
  private String mApiKey;
  private int mFavouriteSize;
  private  final FavouriteAdapterOnClickHandler mClickHandler;

  public FavouritesAdapter(FavouriteAdapterOnClickHandler clickHandler, int size, String apiKey){
    mApiKey = apiKey;
    mFavouriteSize = size;
    this.mClickHandler = clickHandler;
  }


  @NonNull
  @Override
  public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    int layoutIdForListItem = R.layout.favourite_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    boolean shouldAttachToParentImmediately = false;
    View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
    return new FavouriteViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
    Result currentPlaceId = favouritesPlaceId.get(position);
    String photoReference = currentPlaceId.getPhotos().get(0).getPhotoReference();
    String address = currentPlaceId.getVicinity();
    String name = currentPlaceId.getName();
    String placeId = currentPlaceId.getPlaceId();

    Log.i(LOG_TAG, " onBindViewHolder PlaceId = " + placeId);

    String photoUrl = PHOTO_PLACE_URL + "maxwidth=100&photoreference=" + photoReference + "&key=" + mApiKey;
    Picasso.get().load(photoUrl)
        .placeholder(R.color.gray)
        .error(R.drawable.coming_soon)
        .into(holder.mFavouriteImage);

    holder.mFavouriteName.setText(name);
    holder.mFavouriteAddress.setText(address);
  }

  public void addAll(List<Result> result) {
    if(favouritesPlaceId!=null)
      favouritesPlaceId.clear();
      favouritesPlaceId.addAll(result);
      Log.i(LOG_TAG, "addAll PlaceId = " + favouritesPlaceId);
      notifyDataSetChanged();
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
    void onClick(Result result);
  }

  class FavouriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public final ImageView mFavouriteImage;
    public final TextView mFavouriteName;
    public final TextView mFavouriteAddress;

    public FavouriteViewHolder(View itemView){
      super(itemView);
      mFavouriteImage = itemView.findViewById(R.id.favourite_photo_place_id);
      mFavouriteName = itemView.findViewById(R.id.favourite_place_name);
      mFavouriteAddress = itemView.findViewById(R.id.favourite_place_address);
      itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
      int adapterPosition = getAdapterPosition();
      Result result = favouritesPlaceId.get(adapterPosition);
      String placeId = result.getPlaceId();
      Log.i(LOG_TAG,"The Place id clicked is" + placeId);
      mClickHandler.onClick(result);

    }
  }
}
