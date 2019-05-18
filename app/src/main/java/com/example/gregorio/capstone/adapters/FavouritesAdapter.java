package com.example.gregorio.capstone.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
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
import java.util.Optional;

public class FavouritesAdapter extends RecyclerView.Adapter<FavouritesAdapter.FavouriteViewHolder> {

    private static final String LOG_TAG = FavouritesAdapter.class.getSimpleName();
    private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    private static final String MAX_WIDTH_100 = "maxwidth=100";
    private static final String AND_PHOTO_REFERENCE = "&photoreference=";

    private static final String KEY = "&key=";
    private static String API_KEY;
    private List<Result> favouritesPlaceId = new ArrayList<>();
    private final OnClickHandler mClickHandler;
    private Context context;
    private String photoReference = "";
    private Integer dbSize;

    public FavouritesAdapter(OnClickHandler clickHandler, int dbSize, String API_KEY) {
        this.API_KEY = API_KEY;
        this.mClickHandler = clickHandler;
        this.dbSize = dbSize;
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int layoutIdForListItem = R.layout.favourite_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new FavouriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
        Optional<Result> currentPlaceId = Optional.ofNullable(favouritesPlaceId.get(position));
        currentPlaceId.ifPresent(p -> photoReference = p.getPhotos().get(0).getPhotoReference());
        Optional<String> address = currentPlaceId.map(Result::getFormattedAddress);
        Optional<String> name = currentPlaceId.map(Result::getName);
        Optional<Double> rating = currentPlaceId.map(Result::getRating);
        Optional<String> photoUrl = Optional.of(PHOTO_PLACE_URL +
                MAX_WIDTH_100 +
                AND_PHOTO_REFERENCE +
                photoReference +
                KEY +
                API_KEY);
        photoUrl.ifPresent(s -> Glide.with(context)
                .load(photoUrl.get())
                .into(holder.favouriteImage));
        name.ifPresent(s -> {
            ViewCompat.setTransitionName(holder.favouriteImage, s);
            holder.favouriteName.setText(name.get());
            holder.favouriteImage
                    .setContentDescription(context.getString(R.string.the_image_view_cd) + name.get());
            holder.favouriteName.setContentDescription(context.getString(R.string.the_name_is_cd) + name.get());

        });
        address.ifPresent(holder.favouriteAddress::setText);
        address.orElse("");
        rating.ifPresent(aDouble -> holder.ratingBar.setRating(rating.get().longValue()));
        rating.orElse(0.00);
    }

    public void addAll(List<Result> result) {
        if (favouritesPlaceId != null) {
            favouritesPlaceId.clear();
            favouritesPlaceId.addAll(result);
        }
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        favouritesPlaceId.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Result item, int position) {
        favouritesPlaceId.add(position, item);
        notifyItemInserted(position);
    }

    @Override
    public int getItemCount() {
        return favouritesPlaceId.size();
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface OnClickHandler {
        void onClick(Result result, View view);
    }

    public class FavouriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final CardView viewForeground;
        private final ImageView favouriteImage;
        private final TextView favouriteName;
        private final TextView favouriteAddress;
        private final RatingBar ratingBar;

        private FavouriteViewHolder(View itemView) {
            super(itemView);
            favouriteImage = itemView.findViewById(R.id.favourite_photo_place_id);
            favouriteName = itemView.findViewById(R.id.favourite_place_name);
            favouriteAddress = itemView.findViewById(R.id.favourite_place_address);
            viewForeground = itemView.findViewById(R.id.favourite_card_view);
            ratingBar = itemView.findViewById(R.id.ratingFavourites);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Result result = favouritesPlaceId.get(adapterPosition);
            String placeId = result.getPlaceId();
            Log.i(LOG_TAG, "The Place id clicked is" + placeId);
            mClickHandler.onClick(result, v);
        }
    }
}
