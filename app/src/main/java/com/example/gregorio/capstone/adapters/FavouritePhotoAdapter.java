package com.example.gregorio.capstone.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.gregorio.capstone.R;
import com.example.gregorio.capstone.model.placeId.Photo;

import java.util.ArrayList;
import java.util.List;

public class FavouritePhotoAdapter extends RecyclerView.Adapter<FavouritePhotoAdapter.FavouritePhotoViewHolder> {

    private static final String LOG_TAG = PhotoAdapter.class.getSimpleName();
    private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    private List<Photo> photoId = new ArrayList<>();
    private String apiKey;
    private Context context;

    public FavouritePhotoAdapter(String apiKey) {
        this.apiKey = apiKey;
    }

    @NonNull
    @Override
    public FavouritePhotoAdapter.FavouritePhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.photo_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new FavouritePhotoAdapter.FavouritePhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouritePhotoViewHolder holder, int position) {
        Photo currentPhoto = photoId.get(position);
        String photoReference = currentPhoto.getPhotoReference();
        String photoUrl = PHOTO_PLACE_URL + "maxwidth=600&photoreference=" + photoReference + "&key=" + apiKey;
        Glide.with(context)
                .load(photoUrl)
                .into(holder.imageView);
        Log.i(LOG_TAG, "Photo Adapter GalleryUrl " + photoUrl);
        holder.imageView.setContentDescription(context.getString(R.string.favourite_place_image));

    }

    public void addAll(List<Photo> photos) {
        if (photoId != null) {
            photoId.clear();
            photoId.addAll(photos);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return photoId.size();
    }

    static class FavouritePhotoViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;

        private FavouritePhotoViewHolder(View imageView) {
            super(imageView);
            this.imageView = imageView.findViewById(R.id.photo_place_id);
        }
    }
}
