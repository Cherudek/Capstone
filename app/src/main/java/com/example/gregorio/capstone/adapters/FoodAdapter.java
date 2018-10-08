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
import com.example.gregorio.capstone.model.placeId.Result;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private static final String LOG_TAG = FoodAdapter.class.getSimpleName();
    private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    private final AdapterOnClickHandler mClickHandler;
    private List<Result> foodPlaceId = new ArrayList<>();
    private String apiKey;
    private Context context;

    public FoodAdapter(AdapterOnClickHandler adapterOnClickHandler, String apiKey) {
        this.apiKey = apiKey;
        this.mClickHandler = adapterOnClickHandler;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.food_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Result currentPlaceId = foodPlaceId.get(position);
        String photoReference = currentPlaceId.getPhotos().get(0).getPhotoReference();
        String address = currentPlaceId.getVicinity();
        String name = currentPlaceId.getName();
        String photoUrl =
                PHOTO_PLACE_URL + "maxwidth=100&photoreference=" + photoReference + "&key=" + apiKey;
        Glide.with(context)
                .load(photoUrl)
                .into(holder.image);
        holder.name.setText(name);
        holder.address.setText(address);
        // Enable dynamic content description
        holder.image.setContentDescription(context.getString(R.string.the_image_view_cd) + name);
        holder.address.setContentDescription(context.getString(R.string.the_address_is_cd) + address);
        holder.name.setContentDescription(context.getString(R.string.the_name_is_cd) + name);
    }

    @Override
    public int getItemCount() {
        return foodPlaceId.size();
    }

    public void addAll(List<Result> result) {
        if (foodPlaceId != null) {
            foodPlaceId.clear();
        }
        foodPlaceId.addAll(result);
        notifyDataSetChanged();
    }

    public interface AdapterOnClickHandler {
        void onClick(Result result);
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView image;
        private final TextView name;
        private final TextView address;

        private FoodViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.food_photo_place_id);
            name = itemView.findViewById(R.id.food_place_name);
            address = itemView.findViewById(R.id.food_place_address);
            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Result result = foodPlaceId.get(adapterPosition);
            String placeId = result.getPlaceId();
            Log.i(LOG_TAG, "The Place id clicked is" + placeId);
            mClickHandler.onClick(result);
        }
    }
}
