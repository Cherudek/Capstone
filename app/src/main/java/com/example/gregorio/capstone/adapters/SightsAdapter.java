package com.example.gregorio.capstone.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gregorio.capstone.R;
import com.example.gregorio.capstone.model.placeId.Result;

import java.util.ArrayList;
import java.util.List;

public class SightsAdapter extends RecyclerView.Adapter<SightsAdapter.SightsViewHolder> {

    private static final String LOG_TAG = FavouritesAdapter.class.getSimpleName();
    private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    private final AdapterOnClickHandler clickHandler;
    private List<Result> sightsPlaceId = new ArrayList<>();
    private String apiKey;
    private Context context;

    public SightsAdapter(AdapterOnClickHandler adapter, String apiKey) {
        this.clickHandler = adapter;
        this.apiKey = apiKey;
    }

    @NonNull
    @Override
    public SightsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.sights_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new SightsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SightsViewHolder holder, int position) {
        Result currentPlaceId = sightsPlaceId.get(position);
        String photoReference = currentPlaceId.getPhotos().get(0).getPhotoReference();
        String address = currentPlaceId.getVicinity();
        String name = currentPlaceId.getName();
        Double rating = currentPlaceId.getRating();
        String photoUrl =
                PHOTO_PLACE_URL + "maxwidth=100&photoreference=" + photoReference + "&key=" + apiKey;
        Glide.with(context)
                .load(photoUrl)
                .into(holder.sightImage);
        holder.sightName.setText(name);
        holder.sightAddress.setText(address);
        holder.sightImage.setContentDescription(context.getString(R.string.the_image_view_cd) + name);
        holder.sightAddress
                .setContentDescription(context.getString(R.string.the_address_is_cd) + address);
        holder.sightName.setContentDescription(context.getString(R.string.the_name_is_cd) + name);
        holder.ratingBar.setRating(rating.floatValue());
    }


    @Override
    public int getItemCount() {
        return sightsPlaceId.size();
    }

    public void addAll(List<Result> result) {
        if (sightsPlaceId != null) {
            sightsPlaceId.clear();
            sightsPlaceId.addAll(result);
        }
        notifyDataSetChanged();
    }

    public class SightsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView sightImage;
        private final TextView sightName;
        private final TextView sightAddress;
        private final RatingBar ratingBar;


        private SightsViewHolder(View itemView) {
            super(itemView);
            sightImage = itemView.findViewById(R.id.sights_photo_place_id);
            sightName = itemView.findViewById(R.id.sights_place_name);
            sightAddress = itemView.findViewById(R.id.sights_place_address);
            ratingBar = itemView.findViewById(R.id.ratingSights);
            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Result result = sightsPlaceId.get(adapterPosition);
            String placeId = result.getPlaceId();
            Log.i(LOG_TAG, "The Place id clicked is" + placeId);
            clickHandler.onClick(result);
        }
    }
}
