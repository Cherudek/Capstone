package com.example.gregorio.capstone;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import googleplacesapi.GoogleNearbyPlacesParser;
import googleplacesapi.GoogleMapsApi;
import java.util.List;
import permissions.LocationPermission;
import pojos.NearbyPlaces;
import repository.NearbyPlacesRepository;
import viewmodel.MapDetailSharedViewHolder;
import viewmodel.NearbyPlacesListViewModelFactory;
import viewmodel.QueryNearbyPlacesViewModel;

public class MapFragment extends Fragment implements SearchView.OnQueryTextListener,
    MenuItem.OnActionExpandListener, OnMapReadyCallback {

  public static final String LOG_TAG = MapFragment.class.getSimpleName();
  public static final String CURRENT_LATITUDE_TAG = "CURRENT LATITUDE TAG";
  public static final String CURRENT_LONGITUDE_TAG = "CURRENT LONGITUDE TAG";
  public static final String CURRENT_QUERY_TAG = "CURRENT QUERY TAG";
  private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
  private static final String MARKERS_TAG_KEY = "MarkerTagKey";

  private final int DEFAULT_ZOOM = 1500;
  @BindView(R.id.map)MapView mapView;
  @BindView(R.id.checkout_button)FloatingActionButton checkoutFap;
  private GoogleMap mMap;
  private MenuItem menuItem;
  private static final int REQUEST_PLACE_PICKER = 1;
  // A default location (Piazza Castello, Turin, Italy) and default zoom to use when location permission is
  private static final LatLng PiazzaCastello = new LatLng(45.0710394, 7.6862986);
  private OnMarkerClickListener onMarkerClickListener;
  private OnInfoWindowClickListener onInfoWindowClickListener;
  private Double latitude;
  private Double longitude;
  private LatLng mCurrentLocation;
  private String apiKey;
  private String mQuery;
  private Task<Location> location;
  private static final String FIREBASE_URL = "https://turin-guide-1526861835739.firebaseio.com/";
  private static final String FIREBASE_ROOT_NODE = "checkouts";
  private DatabaseReference mPlacesDatabaseReference;
  private FirebaseDatabase mFirebaseDatabase;
  private LocationPermission locationPermission;
  private Context mContext;
  private CameraPosition cameraPosition;
  private OnFragmentInteractionListener mListener;
  private NearbyPlacesListViewModelFactory factory;
  private QueryNearbyPlacesViewModel queryViewModel;
  private GoogleNearbyPlacesParser nearbyPlacesResponseParser;
  private NearbyPlaces mNearbyPlaces;
  private boolean mSavedInstanceisNull;
  private View rootView;
  private List<MarkerOptions> mMarkerOptions;
  private MapDetailSharedViewHolder sharedModel;
  private Integer mPlaceIdTag;
  private SearchView searchView;

  public MapFragment() {
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = getActivity();
    setHasOptionsMenu(true);
    // Check if the Google Play Services are available or not and set Up Map
    GoogleMapsApi googleMapsApi = new GoogleMapsApi();
    googleMapsApi.CheckGooglePlayServices(mContext, getActivity());
    googleMapsApi.GoogleApiClient(mContext);
    // Check if the user has granted permission to use Location Services
    locationPermission = new LocationPermission();
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable final Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.fragment_map, container, false);
    ButterKnife.bind(this, rootView);
    apiKey = getString(com.example.gregorio.capstone.R.string.google_maps_key);
    // *** IMPORTANT ***
    // MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
    // objects or sub-Bundles.
    Bundle mapViewBundle = null;
    if (savedInstanceState != null) {
      mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
      latitude = savedInstanceState.getDouble(CURRENT_LATITUDE_TAG);
      longitude = savedInstanceState.getDouble(CURRENT_LONGITUDE_TAG);
      mCurrentLocation = new LatLng(latitude, longitude);
      mQuery = savedInstanceState.getString(CURRENT_QUERY_TAG);
      Log.i(LOG_TAG, "onCreateView savedInstanceState cameraPosition:  " + mapViewBundle);
      Log.i(LOG_TAG, "onCreateView savedInstanceState latitude:  " + latitude);
      Log.i(LOG_TAG, "onCreateView savedInstanceState longitude:  " + longitude);
      Log.i(LOG_TAG, "onCreateView savedInstanceState Current String:  " + mQuery);
      Log.i(LOG_TAG, "onCreateView savedInstanceState Current Location:  " + mCurrentLocation);
      mSavedInstanceisNull = false;
    } else if (savedInstanceState == null) {
      mSavedInstanceisNull = true;
      longitude = 7.6862986;
      latitude = 7.6862986;
      mCurrentLocation = PiazzaCastello;
      mQuery = "";
    }
    mapView.onCreate(mapViewBundle);
    mapView.getMapAsync(this);
    // Instatiate the data parsing class
    nearbyPlacesResponseParser = new GoogleNearbyPlacesParser();
    // TODO: Firebase to Add Authentication and local persistence(Add Place to favourites)
    // Enable disk persistence
    //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    // Initialize Firebase components
    mFirebaseDatabase = FirebaseDatabase.getInstance();
    mPlacesDatabaseReference = mFirebaseDatabase.getReference().child("checkouts");
       // Shared View Model to send Data from this fragment to the Detail one
      sharedModel = ViewModelProviders.of(getActivity()).get(MapDetailSharedViewHolder.class);


    return rootView;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if(savedInstanceState!=null){
      mPlaceIdTag = savedInstanceState.getInt(MARKERS_TAG_KEY);
      Log.i(LOG_TAG,"mPlaceIdTag savedInstanceState is " + mPlaceIdTag);
    }

    checkoutFap.setOnClickListener(v -> {
      // launches the Place Picker Api
      MapFragment.this.checkOut();
    });
    // OnMarkerClickListener added to the map
    onMarkerClickListener = marker -> {
      marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
      return false;
    };
    // On InfoClickListener to launch NearPlaces object details event
    onInfoWindowClickListener = marker -> {
      searchView.isIconified();
      searchView.onActionViewCollapsed();
      // Retrieve the Marker Id Tag so we can call the corresponding NearbyPlace clicked on the Map
      // and save it to the SharedMapDetailViewModel
      if(mPlaceIdTag == null){
        mPlaceIdTag = (Integer) marker.getTag();
      }
      Log.i(LOG_TAG, "Marker Id Tag is: " + mPlaceIdTag);
      sharedModel.select(queryViewModel.getData().getValue().getResults().get(mPlaceIdTag));
      Log.i(LOG_TAG, "query model size is: " + queryViewModel.mNearbyPlaces.getValue().getResults().size());
      // launch the detail fragment.
      onMarkerPressedIntent(marker);
    };
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    outState.putDouble(CURRENT_LATITUDE_TAG, latitude);
    outState.putDouble(CURRENT_LONGITUDE_TAG, longitude);
    outState.putString(CURRENT_QUERY_TAG, mQuery);
    outState.putInt(MARKERS_TAG_KEY, mPlaceIdTag);
    Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
    if (mapViewBundle == null) {
      mapViewBundle = new Bundle();
      outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);

    }
    mapView.onSaveInstanceState(mapViewBundle);
  }

  public void onMarkerPressedIntent(Marker marker) {
    if (mListener != null) {
      mListener.onFragmentInteraction(marker);
    }
  }
  public void onPlacePickerPressedIntent(Place place) {
    if (mListener != null) {
      mListener.OnPlacePickerInteraction(place);
    }
  }
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement OnFragmentInteractionListener");
    }
  }
  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }


  // App bar Search View setUp
  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    menu.clear();
    inflater.inflate(R.menu.main, menu);
    menuItem = menu.findItem(R.id.menu_search);
    searchView = (SearchView) menuItem.getActionView();
    searchView.setOnQueryTextListener(this);
    searchView.setQueryHint("Search Nearby Places");
    searchView.setIconified(true);
    //searchView.setSubmitButtonEnabled(true);

  }

  // Prompt the user to check out of their location. Called when the "Check Out!" button
  // is clicked.
  public void checkOut() {
    try {
      PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
      Intent intent = intentBuilder.build(getActivity());
      startActivityForResult(intent, REQUEST_PLACE_PICKER);
    } catch (GooglePlayServicesRepairableException e) {
      GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
          REQUEST_PLACE_PICKER);
    } catch (GooglePlayServicesNotAvailableException e) {
      Toast.makeText(mContext, "Please install Google Play Services!", Toast.LENGTH_LONG)
          .show();
    }
  }

  @Override
  public boolean onQueryTextSubmit(final String query) {
    mQuery = query;
    Log.i(LOG_TAG, "The Search Query is: " + query);
      // MVVM Retrofit Call Via ViewModel Factory
    if(mNearbyPlaces==null){
      factory = new NearbyPlacesListViewModelFactory(NearbyPlacesRepository.getInstance(),
          mQuery, latitude.toString(), longitude.toString(), DEFAULT_ZOOM, apiKey);

      queryViewModel = ViewModelProviders.of(this, factory)
          .get(QueryNearbyPlacesViewModel.class);
    } else {
      queryViewModel.mNearbyPlacesRepository = NearbyPlacesRepository.getInstance();
      queryViewModel.mKeyword = mQuery;
      queryViewModel.mLatitude = latitude.toString();
      queryViewModel.mLongitude = longitude.toString();
      queryViewModel.mRadius = DEFAULT_ZOOM;
      queryViewModel.mApiKey = apiKey;
      queryViewModel.getNewPlaces();
    }

    queryViewModel.getData().observe(this, new Observer<NearbyPlaces>() {
      @Override
      public void onChanged(@Nullable NearbyPlaces nearbyPlaces) {
        if(nearbyPlaces!=null){
          mNearbyPlaces=nearbyPlaces;
          String status = nearbyPlaces.getStatus();
          if(status.matches("ZERO_RESULTS")){
            Snackbar snackbar = Snackbar.make(rootView, "No Results", Snackbar.LENGTH_LONG);
            snackbar.show();
          }
          nearbyPlaces.getResults().size();
          mMarkerOptions = nearbyPlacesResponseParser.drawLocationMap(nearbyPlaces, mMap, mCurrentLocation);
          queryViewModel.mMarkersOptions = mMarkerOptions;
          Log.i(LOG_TAG, "queryViewModel mMarkersOptions on Rotation is" + queryViewModel.mMarkersOptions.size() );
          queryViewModel.getData().removeObserver(this);
        }
        Snackbar snackbar = Snackbar.make(rootView, "Check Your Internet Connection", Snackbar.LENGTH_LONG);
        snackbar.show();

      }
    });

    return true;
  }


  @Override
  public boolean onQueryTextChange(String newText) {
    return false;
  }

  @Override
  public boolean onMenuItemActionExpand(MenuItem item) {
    return true;
  }

  @Override
  public boolean onMenuItemActionCollapse(MenuItem item) {
    return true;
  }

  // Once the user has chosen a place, onActivityResult will be called, so we need to implement that now.
  // This code checks that the intent was successful and uses PlacePicker.getPlace() to obtain the chosen Place.
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_PLACE_PICKER) {
      if (resultCode == Activity.RESULT_OK) {
        Place place = PlacePicker.getPlace(mContext, data);
        onPlacePickerPressedIntent(place);

        // TODO: MOVE THIS IN THE ADD TO FAVOURITE FRAGMENT
//        // Object to be passed to the firebase db reference.
//        Favourite favouriteObject = new Favourite(mPlaceId, mPlaceName, mPlaceWebUrl,
//            mPlaceAttributions);
//        String favourite = getString(R.string.nv_favourites);
//        mPlacesDatabaseReference.child(favourite).push().setValue(favouriteObject);
//        Snackbar snackbar = Snackbar
//            .make(getView(), "Location stored on Firebase!", Snackbar.LENGTH_SHORT);
//        snackbar.show();

      } else if (resultCode == PlacePicker.RESULT_ERROR) {
        Toast.makeText(mContext,
            "Places API failure! Check that the API is enabled for your key",
            Toast.LENGTH_LONG).show();
      } else {
        super.onActivityResult(requestCode, resultCode, data);
      }
    }
  }

  // Get the last known location of the device
  public void getLastLocation() {
    // Get last known recent location using new Google Play Services SDK (v11+)
    if (locationPermission.checkLocalPermission(mContext, getActivity())) {
      location = LocationServices.getFusedLocationProviderClient(mContext).getLastLocation();
      location
          .addOnSuccessListener(location -> {
            // GPS location can be null if GPS is switched off
            if (location != null) {
              latitude = location.getLatitude();
              longitude = location.getLongitude();
              mCurrentLocation = new LatLng(latitude, longitude);
              Log.i(LOG_TAG,
                  "The Last location is: Latitude: " + latitude + " Longitude: " + longitude);
            } else {
              latitude = PiazzaCastello.latitude;
              longitude = PiazzaCastello.longitude;
              mCurrentLocation = PiazzaCastello;
              Log.i(LOG_TAG,
                  "Could not fetch the GPS location, we set to the default one: "
                      + PiazzaCastello);
              Snackbar snackbar = Snackbar.make(rootView, "Check Your Internet Connection and Location is on", Snackbar.LENGTH_LONG);
              snackbar.show();
            }
          })
          .addOnFailureListener(e -> {
            Log.d(LOG_TAG, "Error trying to get last GPS location");
            e.printStackTrace();
            Snackbar snackbar = Snackbar.make(rootView, "Check Your Internet Connection and Location is on", Snackbar.LENGTH_LONG);
            snackbar.show();
          });
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    mapView.onResume();
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
  public void onMapReady(GoogleMap googleMap) {
    MapsInitializer.initialize(mContext);
    MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(mContext, R.raw.mapstyle_retro);
    googleMap.setMapStyle(style);
    //Once the Map is initialised we set Up an observer for changes to the Map
    // Check the Location permission is given before enabling setMyLocation to true.
    if (locationPermission.checkLocalPermission(mContext, getActivity())) {
      // If the Location Permission id Granted Enable Location on the Map
      googleMap.setMyLocationEnabled(true);
      googleMap.setBuildingsEnabled(true);
      googleMap.setOnMarkerClickListener(onMarkerClickListener);
      googleMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
      if (mSavedInstanceisNull) {
        // Construct a CameraPosition focusing on Piazza Castello, Turin Italy and animate the camera to that position.
        CameraPosition cameraPosition = new CameraPosition.Builder()
            .target(mCurrentLocation)      // Sets the center of the map to Piazza Castello
            .zoom(14)                   // Sets the zoom
            .bearing(0)                // Sets the orientation of the camera to east
            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
            .build();
        // Creates a CameraPosition from the builder
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
      }

      if(queryViewModel!=null){
        for (int i = 0; i < queryViewModel.mMarkersOptions.size(); i++) {
          Log.i(LOG_TAG, "On Rotation Map Marker size is " + queryViewModel.mMarkersOptions.size());
          MarkerOptions m = queryViewModel.mMarkersOptions.get(i);
          googleMap.addMarker(m);
        }
      }
    }
    getLastLocation();
    mMap = googleMap;

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
    void onFragmentInteraction(Marker marker);
    void OnPlacePickerInteraction(Place place);
  }
}


