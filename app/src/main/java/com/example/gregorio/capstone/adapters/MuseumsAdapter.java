package com.example.gregorio.capstone.adapters;

import android.content.Context;
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

public class MuseumsAdapter extends
        RecyclerView.Adapter<MuseumsAdapter.ViewHolder> {

    private static final String LOG_TAG = MuseumsAdapter.class.getSimpleName();
    private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    private final AdapterOnClickHandler mClickHandler;
    private List<Result> museumList = new ArrayList<>();
    private String apiKey;
    private Context context;

    public MuseumsAdapter(AdapterOnClickHandler adapterOnClickHandler, String apiKey) {
        this.apiKey = apiKey;
        this.mClickHandler = adapterOnClickHandler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.museums_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Result currentPlaceId = museumList.get(position);
        String photoReference = currentPlaceId.getPhotos().get(0).getPhotoReference();
        String address = currentPlaceId.getVicinity();
        String name = currentPlaceId.getName();
        Double rating = currentPlaceId.getRating();
        String photoUrl =
                PHOTO_PLACE_URL + "maxwidth=100&photoreference=" + photoReference + "&key=" + apiKey;
        Glide.with(context)
                .load(photoUrl)
                .into(holder.imageView);
        holder.name.setText(name);
        holder.address.setText(address);
        holder.imageView.setContentDescription(context.getString(R.string.the_image_view_cd) + name);
        holder.address.setContentDescription(context.getString(R.string.the_address_is_cd) + address);
        holder.name.setContentDescription(context.getString(R.string.the_name_is_cd) + name);
        holder.ratingBar.setRating(rating.floatValue());
    }

    @Override
    public int getItemCount() {
        return museumList.size();
    }

    public void addAll(List<Result> result) {
        if (museumList != null) {
            museumList.clear();
            museumList.addAll(result);
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView imageView;
        public final TextView name;
        public final TextView address;
        private final RatingBar ratingBar;


        public ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.museums_photo_place_id);
            name = view.findViewById(R.id.museums_place_name);
            address = view.findViewById(R.id.museums_place_address);
            ratingBar = view.findViewById(R.id.ratingMuseums);
            view.setOnClickListener(this::onClick);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + address.getText() + "'";
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Result result = museumList.get(adapterPosition);
            String placeId = result.getPlaceId();
            Log.i(LOG_TAG, "The Place id clicked is" + placeId);
            mClickHandler.onClick(result);
        }
    }
}
