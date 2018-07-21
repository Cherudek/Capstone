package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.gregorio.capstone.R;
import java.util.ArrayList;
import java.util.List;
import pojosplaceid.Photo;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

  private static final String LOG_TAG = PhotoAdapter.class.getSimpleName();
  private List<Photo> mPhotoId = new ArrayList<>();
  private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
  private String mApiKey;
  private Context context;

  public PhotoAdapter(int numberOfItems, String apiKey){
    int mNumberOfItems = numberOfItems;
    mApiKey = apiKey;
  }

  @NonNull
  @Override
  public PhotoAdapter.PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    context = parent.getContext();
    int layoutIdForListItem = R.layout.photo_item;
    LayoutInflater inflater = LayoutInflater.from(context);
    boolean shouldAttachToParentImmediately = false;
    View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
    return new PhotoViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull PhotoAdapter.PhotoViewHolder holder, int position) {
      Photo currentPhoto = mPhotoId.get(position);
      String photoReference = currentPhoto.getPhotoReference();
      String photoUrl = PHOTO_PLACE_URL + "maxwidth=600&photoreference=" + photoReference + "&key=" + mApiKey;
    Glide.with(context)
        .load(photoUrl)
        .into(holder.mImageView);
    holder.mImageView.setContentDescription("Photo Galleries for your the detail place selected");
  }

  public void addAll(List<Photo> photos) {
    if (mPhotoId != null)
      mPhotoId.clear();
    mPhotoId.addAll(photos);
    notifyDataSetChanged();
  }

  @Override
  public int getItemCount() {
    return mPhotoId.size();
  }

  static class PhotoViewHolder extends RecyclerView.ViewHolder {

    public final ImageView mImageView;

    public PhotoViewHolder(View imageView) {
      super(imageView);
      mImageView = imageView.findViewById(R.id.photo_place_id);
    }


  }
}
