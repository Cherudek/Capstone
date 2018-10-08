package com.example.gregorio.capstone;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gregorio.capstone.adapters.FavouritePhotoAdapter;
import com.example.gregorio.capstone.adapters.FavouriteReviewAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pojosplaceid.Result;
import pojosplaceid.Review;
import viewmodel.FavouriteDetailSharedViewModel;

public class FavouriteDetailFragment extends Fragment implements OnMapReadyCallback {

    public static final String LOG_TAG = FavouriteDetailFragment.class.getSimpleName();
    private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    private static final String MAPVIEW_DETAIL_BUNDLE_KEY = "MapViewDetailBundle";
    private static final String FIREBASE_USERS_NODE = "users";
    private static final String FIREBASE_ROOT_NODE = "checkouts";
    private static final String FIREBASE_FAVOURITES_NODE = "Favourites";
    @BindView(R.id.map_favourite_detail)
    MapView detailMap;
    @BindView(R.id.favourite_detail_image)
    ImageView ivPhotoView;
    @BindView(R.id.favourite_place_address)
    TextView tvAddress;
    @BindView(R.id.favourite_place_mame)
    TextView tvName;
    @BindView(R.id.favourite_place_url)
    TextView tvWebAddress;
    @BindView(R.id.remove_from_favourites_button)
    FloatingActionButton removeFavourite;
    @BindView(R.id.favourite_weekday_opening_hours)
    TextView tvOpeningHours;
    @BindView(R.id.favourite_open_now)
    TextView tvOpenNow;
    @BindView(R.id.favourite_telephone_no)
    TextView tvTelephone;
    @BindView(R.id.favourite_photo_gallery)
    RecyclerView rvPhotoGallery;
    @BindView(R.id.favourite_reviews)
    RecyclerView rvReviews;
    private String favouriteName;
    private String favouriteAddress;
    private Double favouritePriceLevel;
    private String photoReference = "";
    private String apiKey;
    private Result resultFromFavourites;
    private OnMarkerClickListener onMarkerClickListener;
    private FirebaseAuth auth;
    private String userID;
    private DatabaseReference placesDatabaseReference;
    private LatLng latLng;

    public FavouriteDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FavouriteDetailSharedViewModel favouriteDetailSharedViewModel = ViewModelProviders
                .of(getActivity()).get(FavouriteDetailSharedViewModel.class);
        favouriteDetailSharedViewModel.getSelected().observe(this, result -> {
            //Update UI
            String name = result.getName();
            resultFromFavourites = result;
            Log.i(LOG_TAG, "Name: " + name);
        });

        // Initialize FireBase components
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        placesDatabaseReference = mFirebaseDatabase.getReference().child(FIREBASE_ROOT_NODE);
        auth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourite_detail, container, false);
        ButterKnife.bind(this, rootView);
        apiKey = getContext().getResources().getString(R.string.google_api_key);
        LinearLayoutManager photosLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, true);
        rvPhotoGallery.setLayoutManager(photosLayoutManager);
        rvPhotoGallery.setHasFixedSize(true);
        LinearLayoutManager reviewsLayoutManager = new LinearLayoutManager(getContext());
        rvReviews.setLayoutManager(reviewsLayoutManager);
        rvReviews.setHasFixedSize(true);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_DETAIL_BUNDLE_KEY);
        }
        detailMap.onCreate(mapViewBundle);
        detailMap.getMapAsync(this);
        // OnMarkerClickListener added to the map
        onMarkerClickListener = marker -> {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            return false;
        };
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        removeFavourite.setContentDescription(getString(R.string.remove_from_favourites_button));
        removeFavourite.setOnClickListener(v -> {
            String childKey = resultFromFavourites.getFavourite_node_key();
            String name = resultFromFavourites.getName();
            userID = auth.getUid();
            placesDatabaseReference.child(FIREBASE_USERS_NODE).child(userID)
                    .child(FIREBASE_FAVOURITES_NODE).child(childKey).removeValue();
            Snackbar snackbar = Snackbar
                    .make(getView(), name + getString(R.string.removed_from_favourites),
                            Snackbar.LENGTH_SHORT);
            snackbar.show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        detailMap.onResume();
        if (resultFromFavourites.getPhotos() != null) {
            photoReference = resultFromFavourites.getPhotos().get(0).getPhotoReference();
        }
        String picassoPhotoUrl =
                PHOTO_PLACE_URL + "maxwidth=600&photoreference=" + photoReference + "&key=" + apiKey;
        Glide.with(this)
                .load(picassoPhotoUrl)
                .into(ivPhotoView);

        ivPhotoView.setContentDescription(getString(R.string.favourite_place_image));
        ivPhotoView.setTransitionName(resultFromFavourites.getName());

        Double mRating = resultFromFavourites.getRating();
        favouriteName = resultFromFavourites.getName();
        tvName.setText(favouriteName);
        tvName.setContentDescription(
                getString(R.string.favourite_place_name) + resultFromFavourites.getName());
        favouriteAddress = resultFromFavourites.getVicinity();
        tvAddress.setText(favouriteAddress);
        tvAddress.setContentDescription(
                getString(R.string.favourite_place_address) + resultFromFavourites.getVicinity());
        tvWebAddress.setText(resultFromFavourites.getWebsite());
        tvWebAddress.setContentDescription(
                getString(R.string.favourite_web_addrees) + resultFromFavourites.getWebsite());
        if (favouritePriceLevel != null) {
            favouritePriceLevel = resultFromFavourites.getRating();
        }
        tvTelephone.setText(resultFromFavourites.getInternationalPhoneNumber());
        tvTelephone.setContentDescription(
                getString(R.string.the_favourite_telephone_number_is) + resultFromFavourites
                        .getInternationalPhoneNumber());
        if (resultFromFavourites.getOpeningHours() != null) {
            Boolean openingHours = resultFromFavourites.getOpeningHours().getOpenNow();
            if (openingHours) {
                tvOpenNow.setText(R.string.open);
            } else {
                tvOpenNow.setText(R.string.closed);
            }
            Log.i(LOG_TAG, getString(R.string.open_now) + openingHours);
        }
        if (resultFromFavourites.getOpeningHours() != null) {
            List<String> mOpeningWeekDays = resultFromFavourites.getOpeningHours().getWeekdayText();
            StringBuilder weeklyHours = new StringBuilder();
            String openingHours = getString(R.string.opening_hours);
            weeklyHours.append(openingHours + "\n\n");
            Log.i(LOG_TAG, getString(R.string.wee_days_opening) + mOpeningWeekDays);
            for (int i = 0; i < mOpeningWeekDays.size(); i++) {
                String openDays = mOpeningWeekDays.get(i);
                weeklyHours.append(openDays).append("\n");
                tvOpeningHours.setText(weeklyHours);
            }
        }

        pojosplaceid.Location scope = resultFromFavourites.getGeometry().getLocation();
        Double lat = scope.getLat();
        Double lon = scope.getLng();
        latLng = new LatLng(lat, lon);

        FavouritePhotoAdapter mPhotoAdapter;
        if (resultFromFavourites.getPhotos() != null) {
            List<pojosplaceid.Photo> photoList = resultFromFavourites.getPhotos();
            mPhotoAdapter = new FavouritePhotoAdapter(apiKey);
            mPhotoAdapter.addAll(photoList);
        } else {
            mPhotoAdapter = new FavouritePhotoAdapter(apiKey);
        }
        rvPhotoGallery.setAdapter(mPhotoAdapter);
        List<Review> reviewsList = resultFromFavourites.getReviews();
        FavouriteReviewAdapter mReviewsAdapter = new FavouriteReviewAdapter();
        mReviewsAdapter.addAll(reviewsList);
        rvReviews.setAdapter(mReviewsAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        detailMap.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        detailMap.onStop();
    }

    @Override
    public void onPause() {
        detailMap.onPause();
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        MapStyleOptions style = MapStyleOptions
                .loadRawResourceStyle(getContext(), R.raw.mapstyle_retro);
        googleMap.setMapStyle(style);
        googleMap.setBuildingsEnabled(true);
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.title(favouriteName);
        options.snippet(favouriteAddress);
        googleMap.addMarker(options);
        googleMap.setOnMarkerClickListener(onMarkerClickListener);
        // Construct a CameraPosition focusing on Piazza Castello, Turin Italy and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Piazza Castello
                .zoom(16)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .build();
        // Creates a CameraPosition from the builder
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        detailMap.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        detailMap.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_DETAIL_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_DETAIL_BUNDLE_KEY, mapViewBundle);

        }
        detailMap.onSaveInstanceState(mapViewBundle);
    }


}
