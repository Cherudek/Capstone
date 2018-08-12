package adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.gregorio.capstone.DetailFragment.OnFragmentInteractionListener;
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
  private OnFragmentInteractionListener mClickHandler;

  public PhotoAdapter(int numberOfPhotos, String apiKey,
      OnFragmentInteractionListener mClickHandler){
    this.mApiKey = apiKey;
    this.mClickHandler = mClickHandler;
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
    holder.mImageView.setContentDescription(context.getString(R.string.photo_gallery_detail_view));
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

  class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private final ImageView mImageView;
    private PhotoViewHolder(View imageView) {
      super(imageView);
      mImageView = imageView.findViewById(R.id.photo_place_id);
      itemView.setOnClickListener(this::onClick);

    }

    @Override
    public void onClick(View v) {
      int adapterPosition = getAdapterPosition();
      Photo photo = mPhotoId.get(adapterPosition);
      mClickHandler.onFragmentInteraction(photo);
    }
  }
}
