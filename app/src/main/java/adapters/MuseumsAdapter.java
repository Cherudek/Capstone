package adapters;

import android.content.Context;
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

/**
 * {@link RecyclerView.Adapter} that can display a {@link Result} and makes a call to the
 * specified {@link }.
 * TODO: Replace the implementation with code for your data type.
 */
public class MuseumsAdapter extends
    RecyclerView.Adapter<MuseumsAdapter.ViewHolder> {

  private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
  private List<Result> mMuseumList = new ArrayList<>();
  private String apiKey;
  private Context context;

  public MuseumsAdapter(String apiKey) {
    this.apiKey = apiKey;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    context = parent.getContext();
    int layoutIdForListItem = R.layout.museums_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    boolean shouldAttachToParentImmediately = false;
    View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {

    Result currentPlaceId = mMuseumList.get(position);
    String photoReference = currentPlaceId.getPhotos().get(0).getPhotoReference();
    String address = currentPlaceId.getVicinity();
    String name = currentPlaceId.getName();
    String placeId = currentPlaceId.getPlaceId();

    String photoUrl =
        PHOTO_PLACE_URL + "maxwidth=100&photoreference=" + photoReference + "&key=" + apiKey;
    Glide.with(context)
        .load(photoUrl)
        .into(holder.mView);
    holder.mName.setText(name);
    holder.mAddress.setText(address);
    // Enable dynamic content description
    holder.mView.setContentDescription("Image View for " + name);
    holder.mAddress.setContentDescription("The address is: " + address);
    holder.mName.setContentDescription("The Name of the place is: " + name);
  }

  @Override
  public int getItemCount() {
    return mMuseumList.size();
  }

  public void addAll(List<Result> result) {
    if (mMuseumList != null) {
      mMuseumList.clear();
    }
    mMuseumList.addAll(result);
    notifyDataSetChanged();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    public final ImageView mView;
    public final TextView mName;
    public final TextView mAddress;

    public ViewHolder(View view) {
      super(view);
      mView = view.findViewById(R.id.museums_photo_place_id);
      mName = view.findViewById(R.id.museums_place_name);
      mAddress = view.findViewById(R.id.museums_place_address);
    }

    @Override
    public String toString() {
      return super.toString() + " '" + mAddress.getText() + "'";
    }
  }
}
