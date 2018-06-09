package com.example.gregorio.capstone;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.net.Uri;
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
import com.example.gregorio.capstone.databinding.FragmentMapBinding;
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
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import googleplacesapi.GoogleMapsApi;
import java.util.List;
import permissions.LocationPermission;
import pojos.NearbyPlaces;
import viewmodel.NearbyPlacesListViewModel;

public class MapFragment extends Fragment implements SearchView.OnQueryTextListener,
    MenuItem.OnActionExpandListener {

  public static final String LOG_TAG = MapFragment.class.getSimpleName();
  public static final String MAP_TAG = "Current Map Tag";

  private GoogleMap mMap;
  private MapView mapView;
  private LatLngBounds.Builder mBounds = new LatLngBounds.Builder();
  private static final int REQUEST_PLACE_PICKER = 1;
  private static final LatLng PiazzaCastello = new LatLng(45.0710394, 7.6862986);
  // A default location (London, Uk) and default zoom to use when location permission is
  private final LatLng mDefaultLocation = new LatLng(51.508530, -0.076132);
  private final LatLng mNBH = new LatLng(51.5189618, -0.1450063);
  private final LatLng PiazzaSanCarloTurin = new LatLng(45.0671652, 7.681715);
  private OnMarkerClickListener onMarkerClickListener;
  private OnInfoWindowClickListener onInfoWindowClickListener;
  private OnClickListener pickerClickListener;

  private Double latitude;
  private Double longitude;
  private LatLng mCurrentLocation;
  private String apiKey;
  private Task<Location> location;
  // private BuildRetrofitGetResponse buildRetrofitAndGetResponse;
  private static final String FIREBASE_URL = "https://turin-guide-1526861835739.firebaseio.com/";
  private static final String FIREBASE_ROOT_NODE = "checkouts";
  private DatabaseReference mPlacesDatabaseReference;
  private FirebaseDatabase mFirebaseDatabase;
  private FloatingActionButton checkOutBtn;
  private LocationPermission locationPermission;
  private Context mContext;
  private Boolean locationGranted;
  private CameraPosition cameraPosition;

  private NearbyPlacesListViewModel nearbyPlacesListViewModel;

  private FragmentMapBinding fragmentMapBinding;
  private OnFragmentInteractionListener mListener;

  public MapFragment(){
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = getActivity();
    setHasOptionsMenu(true);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (savedInstanceState != null) {
      GoogleMapOptions googleMapOptions = new GoogleMapOptions();
      cameraPosition = savedInstanceState.getParcelable(MAP_TAG);
      googleMapOptions.camera(cameraPosition);

      nearbyPlacesListViewModel = ViewModelProviders.of(this).get(NearbyPlacesListViewModel.class);

      // Create the observer which updates the UI.
      final Observer<List<NearbyPlaces>> nameObserver = new Observer<List<NearbyPlaces>>() {
        @Override
        public void onChanged(@Nullable List<NearbyPlaces> nearbyPlaces) {
          try {
            Log.i(LOG_TAG, "The Retrofit Response is: " + nearbyPlaces.toString());
            // This loop will go through all the results and add marker on each location.
            for (int i = 0; i < nearbyPlaces.size(); i++) {
              Double lat = nearbyPlaces.get(i).getResults().get(i).getGeometry().getLocation()
                  .getLat();
              Double lng = nearbyPlaces.get(i).getResults().get(i).getGeometry().getLocation()
                  .getLng();
              String placeName = nearbyPlaces.get(i).getResults().get(i).getName();
              String vicinity = nearbyPlaces.get(i).getResults().get(i).getVicinity();
              String id = nearbyPlaces.get(i).getResults().get(i).getId();
              String icon = nearbyPlaces.get(i).getResults().get(i).getIcon();
              List photos = nearbyPlaces.get(i).getResults().get(i).getPhotos();
              int photoSize = photos.size();
              Log.i(LOG_TAG, "Photo Size Array is: " + photoSize);
//            Object photo = photos.get(1);
//            photo.toString();

              Uri iconUri = Uri.parse(icon);
              iconUri.getPath();
              Log.i(LOG_TAG, "The Icon Id is: " + icon);
              MarkerOptions markerOptions = new MarkerOptions();
              LatLng latLng = new LatLng(lat, lng);
              // Position of Marker on Map
              markerOptions.position(latLng);
              // Adding Title (Name of the place) and Vicinity (address) to the Marker
              markerOptions.title(placeName);
              markerOptions.snippet(vicinity);

              // Adding Marker to the Map.
              Marker m = mMap.addMarker(markerOptions);
              // Adding colour to the marker
              markerOptions
                  .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
              // Construct a CameraPosition focusing on the current location View and animate the camera to that position.
              mCurrentLocation = new LatLng(latitude, longitude);
              CameraPosition cameraPosition = new CameraPosition.Builder()
                  .target(
                      mCurrentLocation)      // Sets the center of the map to the current user View
                  .zoom(15)                   // Sets the zoom
                  .bearing(0)                // Sets the orientation of the camera to east
                  .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                  .build();                   // Creates a CameraPosition from the builder
              mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
          } catch (Exception e) {
            Log.d("onResponse", "There is an error");
            e.printStackTrace();
          }

        }
      };

      nearbyPlacesListViewModel.getNearbyPlacesListObservable().observe(this, nameObserver);

    }
  }


  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable final Bundle savedInstanceState) {

    fragmentMapBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);

    final View rootView = inflater.inflate(R.layout.fragment_map, container, false);
    checkOutBtn = rootView.findViewById(R.id.checkout_button);
    //searchEditText = rootView.findViewById(R.id.menu_search);
    apiKey = getString(com.example.gregorio.capstone.R.string.google_maps_key);
    mapView = rootView.findViewById(R.id.map);
    mapView.onCreate(savedInstanceState);
//    mapView.onResume(); // needed to get the map to display immediately
//    try {
//      MapsInitializer.initialize(getActivity().getApplicationContext());
//    } catch (Exception e) {
//      e.printStackTrace();
//    }

    checkOutBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        checkOut(rootView);
      }
    });

    locationPermission = new LocationPermission();
    locationGranted = locationPermission.checkLocalPermission(mContext, getActivity());

    setUpMap();

    // Check if the Google Play Services are available or not
    GoogleMapsApi googleMapsApi = new GoogleMapsApi();
    googleMapsApi.CheckGooglePlayServices(mContext, getActivity());
    googleMapsApi.GoogleApiClient(mContext);

    // @OnMarkerClickListener added to the map
    onMarkerClickListener = new OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(Marker marker) {
        String title = marker.getTitle();
        String markerId = marker.getId();
        // Toast to confirm the New Fragment
        Toast toast = Toast
            .makeText(mContext, "You clicked on " + title, Toast.LENGTH_SHORT);
        toast.show();
        return false;
      }
    };

    onInfoWindowClickListener = new OnInfoWindowClickListener() {
      @Override
      public void onInfoWindowClick(Marker marker) {
        onButtonPressed(marker);
      }
    };

    // Enable disk persistence
    //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    // Initialize Firebase components
    mFirebaseDatabase = FirebaseDatabase.getInstance();
    mPlacesDatabaseReference = mFirebaseDatabase.getReference().child("checkouts");
    // Build a new Retrofit Object for the Search Query
    // buildRetrofitAndGetResponse = new BuildRetrofitGetResponse();
    return rootView;
  }

  // TODO: Rename method, update argument and hook method into UI event
  public void onButtonPressed(Marker marker) {
    if (mListener != null) {
      mListener.onFragmentInteraction(marker);
    }
  }

  public void onPlacePickerPressed(Place place) {
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

//    // Retrofit Call to the new Query
//    buildRetrofitAndGetResponse
//        .buildRetrofitAndGetResponse(query, latitude, longitude, apiKey, mMap);

    //MVVM Retrofit Call Via ViewModel
    nearbyPlacesListViewModel.init(query, latitude, longitude, apiKey);

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
        onPlacePickerPressed(place);

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
    }
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


  @Override
  public void onPause() {
    mapView.onPause();
    super.onPause();
    cameraPosition = mMap.getCameraPosition();
  }

  @Override
  public void onResume() {
    super.onResume();
    setUpMap();
    mapView.onResume();
//    if (cameraPosition != null) {
//      mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//    }
  }

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
        mMap = googleMap;
        if (cameraPosition != null) {
          mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
          mMap.clear();
          if (locationPermission.checkLocalPermission(mContext, getActivity())) {
            // If the Location Permission id Granted Enable Location on the Map
            mMap.setMyLocationEnabled(true);
            mMap.setBuildingsEnabled(true);
            mMap.setOnMarkerClickListener(onMarkerClickListener);
            mMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
            // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
            CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(PiazzaCastello)      // Sets the center of the map to Piazza Castello
                .zoom(12)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();
            // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
          }
        }
        getLastLocation();
      }
    });
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(MAP_TAG, cameraPosition);
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
