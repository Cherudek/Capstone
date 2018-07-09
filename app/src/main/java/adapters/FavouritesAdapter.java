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

  public FavouritesAdapter( int size, String apiKey){
    mApiKey = apiKey;
    mFavouriteSize = size;

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

  static class FavouriteViewHolder extends RecyclerView.ViewHolder{

    public final ImageView mFavouriteImage;
    public final TextView mFavouriteName;
    public final TextView mFavouriteAddress;

    public FavouriteViewHolder(View view){
      super(view);
      mFavouriteImage = view.findViewById(R.id.favourite_photo_place_id);
      mFavouriteName = view.findViewById(R.id.favourite_place_name);
      mFavouriteAddress = view.findViewById(R.id.favourite_place_address);

    }
  }
}
