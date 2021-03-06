package com.example.gregorio.capstone.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gregorio.capstone.R;
import com.example.gregorio.capstone.adapters.PhotoAdapter;
import com.example.gregorio.capstone.adapters.ReviewAdapter;
import com.example.gregorio.capstone.model.Results;
import com.example.gregorio.capstone.model.placeId.Location;
import com.example.gregorio.capstone.model.placeId.Photo;
import com.example.gregorio.capstone.model.placeId.PlaceId;
import com.example.gregorio.capstone.model.placeId.Result;
import com.example.gregorio.capstone.model.placeId.Review;
import com.example.gregorio.capstone.network.NearbyPlacesRepository;
import com.example.gregorio.capstone.permissions.Connectivity;
import com.example.gregorio.capstone.viewmodels.DetailViewModel;
import com.example.gregorio.capstone.viewmodels.DetailViewModelFactory;
import com.example.gregorio.capstone.viewmodels.MapDetailSharedViewHolder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.gregorio.capstone.ui.MainActivity.PLACE_PICKER_PLACE_ID_TAG;


public class DetailFragment extends Fragment implements OnMapReadyCallback {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    private static final String PHOTO_URL_TAG = "PhotoUrlTag";
    private static final String NAME_TAG = "NameTag";
    private static final String ADDRESS_TAG = "AddressTag";
    private static final String API_KEY_TAG = "ApiKeyTag";
    private static final String PLACE_ID_TAG = "PlaceIdTag";
    private static final String MAPVIEW_DETAIL_BUNDLE_KEY = "Map View Bundle Key";
    private static final String FIREBASE_USERS_NODE = "users";
    private static final String FIREBASE_FAVOURITE_CHILD_NODE = "Favourites";
    @BindView(R.id.map_detail)
    MapView mapView;
    @BindView(R.id.detail_image)
    ImageView ivPhotoView;
    @BindView(R.id.place_address)
    TextView tvAddress;
    @BindView(R.id.place_mame)
    TextView tvName;
    @BindView(R.id.place_url)
    TextView tvWebAddress;
    @BindView(R.id.add_to_favourites_button)
    FloatingActionButton addToFavouritePlaces;
    @BindView(R.id.weekday_opening_hours)
    TextView tvOpeningHours;
    @BindView(R.id.open_now)
    TextView tvOpenNow;
    @BindView(R.id.telephone_no)
    TextView tvTelephone;
    @BindView(R.id.photo_gallery)
    RecyclerView rvPhotoGallery;
    @BindView(R.id.reviews)
    RecyclerView rvReviews;
    private String placeId;
    private String name;
    private String address;
    private Double rating;
    private List<String> openingWeekDays;
    private List<Photo> photoList;
    private List<Review> reviewsList;
    private String apiKey;
    private String picassoPhotoUrl;
    private PhotoAdapter photoAdapter;
    private ReviewAdapter reviewAdapter;
    private int numberOfPhotos;
    private Result favouriteResult;
    private View rootView;
    private Result result;
    private String photoReference = "";
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private OnMarkerClickListener markerClickListener;
    private LatLng latLng;

    private OnFragmentInteractionListener listener;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            picassoPhotoUrl = savedInstanceState.getString(PHOTO_URL_TAG);
            name = savedInstanceState.getString(NAME_TAG);
            address = savedInstanceState.getString(ADDRESS_TAG);
            apiKey = savedInstanceState.getString(API_KEY_TAG);
        }

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = mFirebaseDatabase.getReference().child(FIREBASE_USERS_NODE);
        auth = FirebaseAuth.getInstance();
        apiKey = getContext().getResources().getString(R.string.google_maps_key);
        Log.i("Api Key :", apiKey);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.menu.main).collapseActionView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);
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
        mapView.onCreate(mapViewBundle);
        markerClickListener = marker -> false;
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            placeId = bundle.getString(PLACE_PICKER_PLACE_ID_TAG);
        } else {
            MapDetailSharedViewHolder detailModel = ViewModelProviders.of(Objects.requireNonNull(getActivity()))
                    .get(MapDetailSharedViewHolder.class);
            detailModel.getSelected().observe(this, this::onChanged);
        }

        addToFavouritePlaces.setContentDescription(getString(R.string.add_to_favourite_tn));
        addToFavouritePlaces.setOnClickListener(v -> {
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                String userID = currentUser.getUid();
                Log.i(LOG_TAG, "The Current User id logged is: " + userID);
                DatabaseReference pushedPostRef = databaseReference
                        .child(userID).child(FIREBASE_FAVOURITE_CHILD_NODE).push();
                pushedPostRef.setValue(favouriteResult);
                String name = favouriteResult.getName();
                String postId = pushedPostRef.getKey();
                Snackbar snackbar = Snackbar
                        .make(Objects.requireNonNull(getView()), name + " Saved to Your Favourites!", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                Snackbar snackbar = Snackbar
                        .make(Objects.requireNonNull(getView()), "Log In to add a place to your favourites!", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        DetailViewModelFactory detailViewModelFactory = new DetailViewModelFactory(
                NearbyPlacesRepository.getInstance(), placeId, apiKey);
        DetailViewModel detailViewModel = ViewModelProviders.of(this, detailViewModelFactory)
                .get(DetailViewModel.class);
        detailViewModel.getPlaceDetails().observe(this, (PlaceId placeIdMap) -> {
            if (placeIdMap != null && placeIdMap.getResult() != null) {
                result = placeIdMap.getResult();
                if (result.getGeometry() != null) {
                    Location scope = result.getGeometry().getLocation();
                    Double lat = scope.getLat();
                    Double lon = scope.getLng();
                    latLng = new LatLng(lat, lon);
                }
                mapView.getMapAsync(this);
                name = result.getName();
                tvName.setText(name);
                tvName.setContentDescription(getString(R.string.place_name_cd) + result.getName());
                address = result.getVicinity();
                tvAddress.setText(address);
                tvAddress
                        .setContentDescription(getString(R.string.the_address_is_cd) + result.getVicinity());
                if (result.getPhotos() != null) {
                    photoReference = result.getPhotos().get(0).getPhotoReference();
                }
                picassoPhotoUrl =
                        PHOTO_PLACE_URL + "maxwidth=600&photoreference=" + photoReference + "&key=" + apiKey;
                Glide.with(this).load(picassoPhotoUrl).into(ivPhotoView);
                ivPhotoView.setContentDescription(getString(R.string.the_image_view_cd));
                tvWebAddress
                        .setContentDescription(getString(R.string.website_name_cd) + result.getWebsite());
                String website = result.getWebsite();
                tvWebAddress.setText(website);
                String phoneNo = result.getInternationalPhoneNumber();
                tvTelephone.setText(phoneNo);
                tvTelephone.setContentDescription(
                        getString(R.string.telephone_number_cd) + result.getInternationalPhoneNumber());
                if (TextUtils.isEmpty(website)) {
                    tvWebAddress.setVisibility(View.GONE);
                } else {
                    tvWebAddress.setVisibility(View.VISIBLE);
                    tvWebAddress.setText(website);
                }
                if (result.getOpeningHours() != null) {
                    Boolean openingHours = result.getOpeningHours().getOpenNow();
                    if (openingHours) {
                        tvOpenNow.setText(R.string.open);
                    } else {
                        tvOpenNow.setText(R.string.closed);
                    }
                    Log.i(LOG_TAG, "Open Now " + openingHours);
                }
                if (result.getOpeningHours() != null) {
                    openingWeekDays = result.getOpeningHours().getWeekdayText();
                    StringBuilder weeklyHours = new StringBuilder();
                    weeklyHours
                            .append(getContext().getResources().getString(R.string.opening_hours) + "\n\n");
                    Log.i(LOG_TAG,
                            getContext().getResources().getString(R.string.wee_days_opening) + openingWeekDays);
                    for (int i = 0; i < openingWeekDays.size(); i++) {
                        weeklyHours.append(openingWeekDays.get(i)).append("\n");
                        tvOpeningHours.setText(weeklyHours);
                    }
                }

                if (result.getPhotos() != null) {
                    numberOfPhotos = result.getPhotos().size();
                    photoList = result.getPhotos();
                    photoAdapter = new PhotoAdapter(apiKey, listener);
                    photoAdapter.addAll(photoList);
                } else {
                    photoAdapter = new PhotoAdapter(apiKey, listener);
                }
                rvPhotoGallery.setAdapter(photoAdapter);

                if (result.getReviews() != null) {
                    int reviewSize = result.getReviews().size();
                    reviewsList = result.getReviews();
                    reviewAdapter = new ReviewAdapter();
                    reviewAdapter.addAll(reviewsList);
                    rvReviews.setAdapter(reviewAdapter);
                }
                favouriteResult = result;
            } else {
                Connectivity connectivity = new Connectivity();
                if (!connectivity.isOnline(getContext())) {
                    Snackbar snackbar = Snackbar
                            .make(rootView, com.example.gregorio.capstone.R.string.no_internet_connection,
                                    Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PHOTO_URL_TAG, picassoPhotoUrl);
        outState.putString(NAME_TAG, name);
        outState.putString(ADDRESS_TAG, address);
        outState.putString(API_KEY_TAG, apiKey);
        outState.putString(PLACE_ID_TAG, placeId);
        Bundle mapViewBundle = new Bundle();
        outState.putBundle(MAPVIEW_DETAIL_BUNDLE_KEY, mapViewBundle);
        mapView.onSaveInstanceState(mapViewBundle);
    }


//    public void onPhotoSelected(Photo photo) {
//        if (listener != null) {
//            listener.onFragmentInteraction(photo);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + context.getResources().getString(R.string.must_implement_on_frag_list));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(Objects.requireNonNull(getContext()));
        MapStyleOptions style = MapStyleOptions
                .loadRawResourceStyle(getContext(), R.raw.mapstyle_retro);
        googleMap.setMapStyle(style);
        googleMap.setBuildingsEnabled(true);
        googleMap.setTrafficEnabled(true);
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.title(name);
        options.snippet(address);
        Marker marker = googleMap.addMarker(options);
        marker.showInfoWindow();
        marker.isInfoWindowShown();
        googleMap.setOnMarkerClickListener(markerClickListener);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)           // Sets the center of the map to the detail place location
                .zoom(16)                 // Sets the zoom
                .bearing(0)               // Sets the orientation of the camera to east
                .build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void onChanged(Results item) {
        placeId = item.getPlaceId();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Photo photo);
    }
}
