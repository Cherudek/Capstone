package com.example.gregorio.capstone.adapters;

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

public class BarsAdapter extends RecyclerView.Adapter<BarsAdapter.BarsViewHolder> {

    private static final String LOG_TAG = BarsAdapter.class.getSimpleName();
    private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    private final AdapterOnClickHandler clickHandler;
    private List<Result> barPlaceId = new ArrayList<>();
    private String apiKey;
    private Context context;

    public BarsAdapter(AdapterOnClickHandler adapterOnClickHandler, String apiKey) {
        this.clickHandler = adapterOnClickHandler;
        this.apiKey = apiKey;
    }

    @NonNull
    @Override
    public BarsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.bars_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new BarsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarsViewHolder holder, int position) {
        Result currentPlaceId = barPlaceId.get(position);
        String photoReference = currentPlaceId.getPhotos().get(0).getPhotoReference();
        String address = currentPlaceId.getVicinity();
        String name = currentPlaceId.getName();
        String photoUrl =
                PHOTO_PLACE_URL + "maxwidth=100&photoreference=" + photoReference + "&key=" + apiKey;
        Glide.with(context)
                .load(photoUrl)
                .into(holder.imageView);
        holder.name.setText(name);
        holder.address.setText(address);
        // Enable dynamic content description
        holder.imageView.setContentDescription(context.getString(R.string.the_image_view_cd) + name);
        holder.address.setContentDescription(context.getString(R.string.the_address_is_cd) + address);
        holder.name.setContentDescription(context.getString(R.string.the_name_is_cd) + name);
    }

    @Override
    public int getItemCount() {
        return barPlaceId.size();
    }

    public void addAll(List<Result> result) {
        if (barPlaceId != null) {
            barPlaceId.clear();
            barPlaceId.addAll(result);
        }
        notifyDataSetChanged();
    }

    public class BarsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView imageView;
        private final TextView name;
        private final TextView address;

        private BarsViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.bars_photo_place_id);
            name = itemView.findViewById(R.id.bars_place_name);
            address = itemView.findViewById(R.id.bars_place_address);
            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Result result = barPlaceId.get(adapterPosition);
            String placeId = result.getPlaceId();
            Log.i(LOG_TAG, "The Place id clicked is" + placeId);
            clickHandler.onClick(result);
        }
    }
}
