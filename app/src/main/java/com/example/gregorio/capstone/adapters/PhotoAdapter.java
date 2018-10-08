package com.example.gregorio.capstone.adapters;

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
    private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    private List<Photo> photoId = new ArrayList<>();
    private String apiKey;
    private Context context;
    private OnFragmentInteractionListener clickHandler;

    public PhotoAdapter(String apiKey, OnFragmentInteractionListener clickHandler) {
        this.apiKey = apiKey;
        this.clickHandler = clickHandler;
    }

    @NonNull
    @Override
    public PhotoAdapter.PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.photo_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoAdapter.PhotoViewHolder holder, int position) {
        Photo currentPhoto = photoId.get(position);
        String photoReference = currentPhoto.getPhotoReference();
        String photoUrl = PHOTO_PLACE_URL + "maxwidth=600&photoreference=" + photoReference + "&key=" + apiKey;
        Glide.with(context)
                .load(photoUrl)
                .into(holder.imageView);
        holder.imageView.setContentDescription(context.getString(R.string.photo_gallery_detail_view));
    }

    public void addAll(List<Photo> photos) {
        if (photoId != null)
            photoId.clear();
        photoId.addAll(photos);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return photoId.size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView imageView;

        private PhotoViewHolder(View imageView) {
            super(imageView);
            this.imageView = imageView.findViewById(R.id.photo_place_id);
            itemView.setOnClickListener(this::onClick);

        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Photo photo = photoId.get(adapterPosition);
            clickHandler.onFragmentInteraction(photo);
        }
    }
}
