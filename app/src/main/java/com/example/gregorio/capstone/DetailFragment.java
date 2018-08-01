package com.example.gregorio.capstone;

import static com.example.gregorio.capstone.MainActivity.PLACE_PICKER_PLACE_ID_TAG;

import adapters.PhotoAdapter;
import adapters.ReviewAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import permissions.Connectivity;
import pojos.Photo;
import pojosplaceid.PlaceId;
import pojosplaceid.Result;
import pojosplaceid.Review;
import repository.NearbyPlacesRepository;
import viewmodel.DetailViewModel;
import viewmodel.DetailViewModelFactory;
import viewmodel.FavouriteDetailSharedViewModel;
import viewmodel.MapDetailSharedViewHolder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment implements OnMapReadyCallback {

  private static final String LOG_TAG = DetailFragment.class.getSimpleName();
  private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_TITLE = "TITLE";
  private static final String ARG_ID = "ID";
  private static final String ARG_WEB_URL = "PLACE WEB URL";
  private static final String PHOTO_URL_TAG = "PhotoUrlTag";
  private static final String NAME_TAG = "NameTag";
  private static final String ADDRESS_TAG = "AddressTag";
  private static final String API_KEY_TAG = "ApiKeyTag";
  private static final String PLACE_ID_TAG = "PlaceIdTag";

  private String mPlaceId;
  private String mName;
  private String mAddress;
  private Double mRating;
  private String mPhoneNumber;
  private String mOpeningHours;
  private List<String> mOpeningWeekDays;
  private String mTelephone;
  private Integer mPriceLevel;
  private List<Photo> photoHeader;
  private List<pojosplaceid.Photo> photoList;
  private List<Review> reviewsList;

  private String apiKey;
  private String picassoPhotoUrl;
  private PhotoAdapter mPhotoAdapter;
  private ReviewAdapter mReviewsAdapter;
  private int numberOfPhotos;
  private Result favouriteResult;
  private View rootView;
  private static final String MAPVIEW_DETAIL_BUNDLE_KEY = "Map View Bundle Key";

  private Result result;
  private FavouriteDetailSharedViewModel favouriteDetailSharedViewModel;

  private static final String FIREBASE_URL = "https://turin-guide-1526861835739.firebaseio.com/";
  private static final String FIREBASE_ROOT_NODE = "checkouts";
  private static final String FIREBASE_FAVOURITE_CHILD_NODE = "Favourites";
  private static final String FIREBASE_FAVOURITE_CHILD_NODE_SIGHTS = "sights";
  private static final String FIREBASE_FAVOURITE_CHILD_NODE_MUSEUM = "museums";
  private static final String FIREBASE_FAVOURITE_CHILD_NODE_FOOD = "food";
  private static final String FIREBASE_FAVOURITE_CHILD_NODE_NIGHTLIFE = "nightlife";
  private static final String FIREBASE_FAVOURITE_CHILD_NODE_DRINKS = "drinks";
  @BindView(R.id.map_detail)
  MapView mapView;

  private String photoReference = "";

  private DatabaseReference mPlacesDatabaseReference;
  private OnMarkerClickListener onMarkerClickListener;

  @BindView(R.id.detail_image)ImageView ivPhotoView;
  @BindView(R.id.place_address)TextView tvAddress;
  @BindView(R.id.place_mame)TextView tvName;
  @BindView(R.id.place_url)TextView tvWebAddress;
  @BindView(R.id.add_to_favourites_button)FloatingActionButton addFavourites;
  @BindView(R.id.weekday_opening_hours)TextView tvOpeningHours;
  @BindView(R.id.open_now)TextView tvOpenNow;
  @BindView(R.id.telephone_no)TextView tvTelephone;
  @BindView(R.id.photo_gallery)RecyclerView rvPhotoGallery;
  @BindView(R.id.reviews)RecyclerView rvReviews;
  private LatLng latLng;


  private OnFragmentInteractionListener mListener;

  public DetailFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment DetailFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static DetailFragment newInstance(String param1, String param2) {
    DetailFragment fragment = new DetailFragment();
    Bundle args = new Bundle();
    args.putString(ARG_TITLE, param1);
    args.putString(ARG_ID, param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if(savedInstanceState!=null){
      picassoPhotoUrl = savedInstanceState.getString(PHOTO_URL_TAG);
      mName = savedInstanceState.getString(NAME_TAG);
      mAddress = savedInstanceState.getString(ADDRESS_TAG);
      apiKey = savedInstanceState.getString(API_KEY_TAG);
    }

    // Initialize Firebase components
    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    mPlacesDatabaseReference = mFirebaseDatabase.getReference().child(FIREBASE_ROOT_NODE);
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
    mapView.onCreate(mapViewBundle);
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
    Bundle bundle = getArguments();
    if (bundle != null) {
      // Get the Data from the map object clicked in the Place Picker API
      mPlaceId = bundle.getString(PLACE_PICKER_PLACE_ID_TAG);

    } else {
      // Get the Data from the map object clicked in the Map API
      MapDetailSharedViewHolder detailModel = ViewModelProviders.of(getActivity())
          .get(MapDetailSharedViewHolder.class);
      detailModel.getSelected().observe(this, item -> {
        // Extract the Pace ID
        mPlaceId = item.getPlaceId();
      });
    }

    // Add to favourites FAB
    addFavourites.setContentDescription(getString(R.string.add_to_favourite_tn));
    // Add Place to favourites (Room Database)
    addFavourites.setOnClickListener(v -> {
      // Object to be passed to the firebase db reference.
      // Generate a reference to a new location and add some data using push()
      DatabaseReference pushedPostRef = mPlacesDatabaseReference
          .child(FIREBASE_FAVOURITE_CHILD_NODE).push();
      pushedPostRef.setValue(favouriteResult);
      String name = favouriteResult.getName();
      // Get the unique ID generated by a push()
      String postId = pushedPostRef.getKey();
      Snackbar snackbar = Snackbar
          .make(getView(), name + " Saved to Your Favourites!", Snackbar.LENGTH_LONG);
      snackbar.show();
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    mapView.onResume();
    // DetailViewModel Factory picks Api Key and Place Id to fetch data for a single Place
    DetailViewModelFactory detailViewModelFactory = new DetailViewModelFactory(
        NearbyPlacesRepository.getInstance(), mPlaceId, apiKey);
    DetailViewModel detailViewModel = ViewModelProviders.of(this, detailViewModelFactory)
        .get(DetailViewModel.class);
    detailViewModel.getPlaceDetails().observe(this, (PlaceId placeIdMap) -> {
      if (placeIdMap != null) {
        result = placeIdMap.getResult();
        if (result.getGeometry() != null) {
          pojosplaceid.Location scope = result.getGeometry().getLocation();
          Double lat = scope.getLat();
          Double lon = scope.getLng();
          latLng = new LatLng(lat, lon);
        }
        mapView.getMapAsync(this::onMapReady);
        tvName.setText(result.getName());
        tvName.setContentDescription(getString(R.string.place_name_cd) + result.getName());
        tvAddress.setText(result.getVicinity());
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
          mOpeningWeekDays = result.getOpeningHours().getWeekdayText();
          StringBuilder weeklyHours = new StringBuilder();
          weeklyHours
              .append(getContext().getResources().getString(R.string.opening_hours) + "\n\n");
          Log.i(LOG_TAG,
              getContext().getResources().getString(R.string.wee_days_opening) + mOpeningWeekDays);
          for (int i = 0; i < mOpeningWeekDays.size(); i++) {
            weeklyHours.append(mOpeningWeekDays.get(i)).append("\n");
            tvOpeningHours.setText(weeklyHours);
          }
        }

        if (result.getPhotos() != null) {
          numberOfPhotos = result.getPhotos().size();
          photoList = result.getPhotos();
          mPhotoAdapter = new PhotoAdapter(numberOfPhotos, apiKey);
          mPhotoAdapter.addAll(photoList);
        } else {
          mPhotoAdapter = new PhotoAdapter(2, apiKey);
        }
        rvPhotoGallery.setAdapter(mPhotoAdapter);

        if (result.getReviews() != null) {
          int reviewSize = result.getReviews().size();
          reviewsList = result.getReviews();
          mReviewsAdapter = new ReviewAdapter(reviewSize);
          mReviewsAdapter.addAll(reviewsList);
          rvReviews.setAdapter(mReviewsAdapter);
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
    outState.putString(NAME_TAG, mName);
    outState.putString(ADDRESS_TAG, mAddress);
    outState.putString(API_KEY_TAG, apiKey);
    outState.putString(PLACE_ID_TAG, mPlaceId);

    Bundle mapViewBundle = outState.getBundle(MAPVIEW_DETAIL_BUNDLE_KEY);
    if (mapViewBundle == null) {
      mapViewBundle = new Bundle();
      outState.putBundle(MAPVIEW_DETAIL_BUNDLE_KEY, mapViewBundle);

    }
    mapView.onSaveInstanceState(mapViewBundle);
  }



  // TODO: Rename method, update argument and hook method into UI event
  public void onButtonPressed(Uri uri) {
    if (mListener != null) {
      mListener.onFragmentInteraction(uri);
    }
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
          + context.getResources().getString(R.string.must_implement_on_frag_list));
    }
  }


  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
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
    options.title(mName);
    options.snippet(mAddress);
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
    mapView.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  /**
   * This interface must be implemented by activities that contain this
   * fragment to allow an interaction in this fragment to be communicated
   * to the activity and potentially other fragments contained in that
   * activity.
   * <p>
   * See the Android Training lesson <a href=
   * "http://developer.android.com/training/basics/fragments/communicating.html"
   * >Communicating with Other Fragments</a> for more information.
   */
  public interface OnFragmentInteractionListener {

    // TODO: Update argument type and name
    void onFragmentInteraction(Uri uri);
  }
}
