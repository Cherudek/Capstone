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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import googleplacesapi.GoogleLocationJsonParser;
import googleplacesapi.GoogleMapsApi;
import java.util.List;
import permissions.LocationPermission;
import pojos.NearbyPlaces;
import repository.NearbyPlacesRepository;
import viewmodel.GoogleMapViewModel;
import viewmodel.GoogleMapViewModelFactory;
import viewmodel.NearbyPlacesListViewModel;
import viewmodel.NearbyPlacesListViewModelFactory;
import viewmodel.QueryNearbyPlacesViewModel;

public class MapFragment extends Fragment implements SearchView.OnQueryTextListener,
    MenuItem.OnActionExpandListener {

  public static final String LOG_TAG = MapFragment.class.getSimpleName();
  public static final String CAMERA_POSITION_TAG = "CURRENT MAP TAG";
  public static final String CURRENT_LATITUDE_TAG = "CURRENT LATITUDE TAG";
  public static final String CURRENT_LONGITUDE_TAG = "CURRENT LONGITUDE TAG";
  public static final String CURRENT_QUERY_TAG = "CURRENT QUERY TAG";
  private final int DEFAULT_ZOOM = 1500;
  @BindView(R.id.map)MapView mapView;
  @BindView(R.id.checkout_button)FloatingActionButton checkoutFap;
  private GoogleMap mMap;
  private LatLngBounds.Builder mBounds = new LatLngBounds.Builder();
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
  private FloatingActionButton checkOutBtn;
  private LocationPermission locationPermission;
  private Context mContext;
  private CameraPosition cameraPosition;
  public  NearbyPlacesListViewModel nearbyPlacesListViewModel;
  private OnFragmentInteractionListener mListener;
  private NearbyPlacesListViewModelFactory factory;
  private QueryNearbyPlacesViewModel queryViewModel;
  private GoogleLocationJsonParser jsonParser;
  private NearbyPlaces mNearbyPlaces;
  private GoogleMapViewModel googleMapViewModel;

  public MapFragment(){
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = getActivity();
    setHasOptionsMenu(true);
    if(savedInstanceState!=null){
      cameraPosition = savedInstanceState.getParcelable(CAMERA_POSITION_TAG);
      latitude = savedInstanceState.getDouble(CURRENT_LATITUDE_TAG);
      longitude = savedInstanceState.getDouble(CURRENT_LONGITUDE_TAG);
      String query = savedInstanceState.getString(CURRENT_QUERY_TAG);
      mCurrentLocation = new LatLng(latitude, longitude);
    }
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable final Bundle savedInstanceState) {
     final View rootView = inflater.inflate(R.layout.fragment_map, container, false);
    ButterKnife.bind(this, rootView);
    checkOutBtn = rootView.findViewById(R.id.checkout_button);
    apiKey = getString(com.example.gregorio.capstone.R.string.google_maps_key);
    mapView.onCreate(savedInstanceState);
    mapView.onResume();
    checkOutBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        // launches the Place Picker Api
        checkOut(rootView);
      }
    });
    // Check if the user has granted permission to use Location Services
    locationPermission = new LocationPermission();
    // Check if the Google Play Services are available or not and set Up Map
    GoogleMapsApi googleMapsApi = new GoogleMapsApi();
    googleMapsApi.CheckGooglePlayServices(mContext, getActivity());
    googleMapsApi.GoogleApiClient(mContext);
    setUpMap();
    // Instatiate the data parsing class
    jsonParser = new GoogleLocationJsonParser();
    // Enable the NearbyPlaces ViewModel
      nearbyPlacesListViewModel = ViewModelProviders.of(this).get(NearbyPlacesListViewModel.class);
      nearbyPlacesListViewModel.getNearbyPlacesListObservable().observe(this,
          new Observer<NearbyPlaces>() {
            @Override
            public void onChanged(@Nullable NearbyPlaces nearbyPlaces) {
              mNearbyPlaces = nearbyPlaces;
            }
          });

    // OnMarkerClickListener added to the map
    onMarkerClickListener = new OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(Marker marker) {
        return false;
      }
    };
    // On InfoClickListener to launch NearPlaces object details event
    onInfoWindowClickListener = new OnInfoWindowClickListener() {
      @Override
      public void onInfoWindowClick(Marker marker) {
        // launch the detail fragment.
        onMarkerPressedIntent(marker);
      }
    };

    // TODO: Firebase to Add Authentication and local persistence(Add Place to favourites)
    // Enable disk persistence
    //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    // Initialize Firebase components
    mFirebaseDatabase = FirebaseDatabase.getInstance();
    mPlacesDatabaseReference = mFirebaseDatabase.getReference().child("checkouts");

    return rootView;
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
    MenuItem menuItem = menu.findItem(R.id.menu_search);
    SearchView searchView = (SearchView) menuItem.getActionView();
    searchView.setOnQueryTextListener(this);
    searchView.setQueryHint("Search Nearby Places");
    searchView.setIconified(true);
    searchView.setSubmitButtonEnabled(true);
    searchView.setQueryRefinementEnabled(true);
    searchView.getQueryHint();
  }

  // Prompt the user to check out of their location. Called when the "Check Out!" button
  // is clicked.
  public void checkOut(View view) {
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
  public boolean onQueryTextSubmit(String query) {
    Log.i(LOG_TAG, "The Search Query is: " + query);
    // MVVM Retrofit Call Via ViewModel Factory
    if(nearbyPlacesListViewModel.nearbyPlacesListObservable.getValue()!=null){
      nearbyPlacesListViewModel.mLatitude = latitude;
      nearbyPlacesListViewModel.mLongitude = longitude;
      nearbyPlacesListViewModel.mKeyword = query;
      nearbyPlacesListViewModel.mApiKey = apiKey;
      nearbyPlacesListViewModel.mKeyword = mQuery;
    }


    factory = new
        NearbyPlacesListViewModelFactory(NearbyPlacesRepository.getInstance(),
        query, latitude.toString() , longitude.toString(), DEFAULT_ZOOM, apiKey);

    queryViewModel = ViewModelProviders.of(this, factory)
            .get(QueryNearbyPlacesViewModel.class);
    queryViewModel.getData().observe(this, new Observer<NearbyPlaces>() {
                 @Override
                 public void onChanged(@Nullable NearbyPlaces nearbyPlaces) {
                   Log.i(LOG_TAG, "The Query result Status is: " + nearbyPlaces.getStatus());
                   Log.i(LOG_TAG, "The Query result Size is: " + nearbyPlaces.getResults().size());
                   nearbyPlacesListViewModel.nearbyPlaces = nearbyPlaces;
                   jsonParser.drawLocationMap(nearbyPlaces, mMap, mCurrentLocation);
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
        Toast.makeText(getContext(),
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
      location = LocationServices.getFusedLocationProviderClient(getActivity()).getLastLocation();
      location
          .addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
              // GPS location can be null if GPS is switched off
              if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                mCurrentLocation = new LatLng(latitude, longitude);
                Log.i(LOG_TAG,
                    "The Last Location is: Latitude: " + latitude + " Longitude: " + longitude);
              } else {
                latitude = PiazzaCastello.latitude;
                longitude = PiazzaCastello.longitude;
                mCurrentLocation = PiazzaCastello;
                Log.i(LOG_TAG,
                    "Could not fetch the GPS location, we set to the default one: " + PiazzaCastello);

              }
            }
          })
          .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              Log.d(LOG_TAG, "Error trying to get last GPS location");
              e.printStackTrace();
            }
          });
    }

  }

  // Map SetUp on the Map View
  private void setUpMap() {
    try {
      MapsInitializer.initialize(getActivity().getApplicationContext());
    } catch (Exception e) {
      e.printStackTrace();
    }
    // Sync Map to current location (if permitted) on the fragment View
    mapView.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(GoogleMap googleMap) {

        //Once the Map is initialised we set Up an observer for changes to the Map
        setUpGoogleMapObserver(googleMap);
        // Check the Location permission is given before enabling setMyLocation to true.
        if (locationPermission.checkLocalPermission(mContext, getActivity())) {
      // If the Location Permission id Granted Enable Location on the Map
      googleMap.setMyLocationEnabled(true);
      googleMap.setBuildingsEnabled(true);
      googleMap.setOnMarkerClickListener(onMarkerClickListener);
      googleMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
      // Construct a CameraPosition focusing on Piazza Castello, Turin Italy and animate the camera to that position.
      CameraPosition cameraPosition = new CameraPosition.Builder()
          .target(PiazzaCastello)      // Sets the center of the map to Piazza Castello
          .zoom(14)                   // Sets the zoom
          .bearing(0)                // Sets the orientation of the camera to east
          .tilt(30)                   // Sets the tilt of the camera to 30 degrees
          .build();
      // Creates a CameraPosition from the builder
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
          }
        mMap = googleMap;
        getLastLocation();
      }
    });
  }

  public void setUpGoogleMapObserver(GoogleMap googleMap){
    GoogleMapViewModelFactory googleMapViewModelFactory = new GoogleMapViewModelFactory(NearbyPlacesRepository.getInstance(), googleMap);
    googleMapViewModel = ViewModelProviders.of(this,googleMapViewModelFactory).get(GoogleMapViewModel.class);
    googleMapViewModel.getGoogleMap().observe(this, new Observer<GoogleMap>() {
      @Override
      public void onChanged(@Nullable GoogleMap googleMap) {
        if(googleMap!=null){
          updateMap(googleMap);
        }
      }
    });
  }

  private boolean updateMap(GoogleMap googleMap) {
        if (nearbyPlacesListViewModel.nearbyPlacesListObservable.getValue() != null) {
          int size = nearbyPlacesListViewModel.nearbyPlaces.getResults().size();
          for (int i = 0;
              i < nearbyPlacesListViewModel.nearbyPlaces.getResults().size();
              i++) {
            Double lat = nearbyPlacesListViewModel.nearbyPlaces.getResults()
                .get(i)
                .getGeometry().getLocation()
                .getLat();
            Double lng = nearbyPlacesListViewModel.nearbyPlaces.getResults()
                .get(i)
                .getGeometry().getLocation()
                .getLng();
            String placeName = nearbyPlacesListViewModel.nearbyPlaces
                .getResults().get(i)
                .getName();
            String vicinity = nearbyPlacesListViewModel.nearbyPlaces
                .getResults().get(i)
                .getVicinity();
            String id = nearbyPlacesListViewModel.nearbyPlaces.getResults()
                .get(i).getId();
            String icon = nearbyPlacesListViewModel.nearbyPlaces.getResults()
                .get(i).getIcon();
            List photos = nearbyPlacesListViewModel.nearbyPlaces.getResults()
                .get(i)
                .getPhotos();
            int photoSize = photos.size();
            Log.i(LOG_TAG, "Photo Size Array is: " + photoSize);
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(lat, lng);
            // Position of Marker on Map
            markerOptions.position(latLng);
            // Adding Title (Name of the place) and Vicinity (address) to the Marker
            markerOptions.title(placeName);
            markerOptions.snippet(vicinity);
            // Adding Marker to the Map.
            googleMap.addMarker(markerOptions);
            Log.i(LOG_TAG, "Markers Added: " + markerOptions);
            Log.i(LOG_TAG, "Map Update is: " + googleMap.getCameraPosition());

          }
        }return true;
      }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    outState.putParcelable(CAMERA_POSITION_TAG, mMap.getCameraPosition());
    outState.putDouble(CURRENT_LATITUDE_TAG, mMap.getCameraPosition().target.longitude);
    outState.putDouble(CURRENT_LONGITUDE_TAG, mMap.getCameraPosition().target.longitude);
    super.onSaveInstanceState(outState);
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
