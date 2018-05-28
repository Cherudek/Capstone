package com.example.gregorio.capstone;

import static com.google.android.gms.location.places.Places.getPlaceDetectionClient;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import retrofit.BuildRetrofitGetResponse;

public class MapFragment extends Fragment {

  public static final String LOG_TAG = MapFragment.class.getSimpleName();
  private GoogleMap mMap;
  private MapView mapView;
  private LatLngBounds.Builder mBounds = new LatLngBounds.Builder();
  private final int MY_LOCATION_REQUEST_CODE = 101;
  private static final int GOOGLE_API_CLIENT_ID = 0;
  private GoogleApiClient mGoogleApiClient;
  private PlaceDetectionClient mPlaceDetectionClient;
  // The entry point to the Fused Location Provider API to get location in Android.
  private FusedLocationProviderClient mFusedLocationProviderClient;
  private OnMarkerClickListener onMarkerClickListener;
  // A default location (London, Uk) and default zoom to use when location permission is
  private final LatLng mDefaultLocation = new LatLng(51.508530, -0.076132);
  private final LatLng mNBH = new LatLng(51.5189618, -0.1450063);
  private static final int DEFAULT_ZOOM = 1000;
  private final LatLng PiazzaSanCarloTurin = new LatLng(45.0671652, 7.681715);
  private Double latitude;
  private Double longitude;
  private LatLng mCurrentLocation;
  private String apiKey;
  private SearchView searchEditText;
  private Task<Location> location;
  private BuildRetrofitGetResponse buildRetrofitAndGetResponse;


  public MapFragment(){
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_map, container, false);
    TextView textView = rootView.findViewById(R.id.maptv);
    textView.setText("MAP FRAGMENT");
    textView.setVisibility(View.INVISIBLE);
    searchEditText = rootView.findViewById(R.id.editText);
    apiKey = getString(com.example.gregorio.capstone.R.string.google_maps_key);
    mapView = rootView.findViewById(R.id.map);
    mapView.onCreate(savedInstanceState);
    mapView.onResume(); // needed to get the map to display immediately
    try {
      MapsInitializer.initialize(getActivity().getApplicationContext());
    } catch (Exception e) {
      e.printStackTrace();
    }

    mapView.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkLocalPermission()) {
          // If the Location Permission id Granted Enable Location on the Map
          mMap.setMyLocationEnabled(true);
          mMap.setBuildingsEnabled(true);
          mMap.setOnMarkerClickListener(onMarkerClickListener);
          // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
          CameraPosition cameraPosition = new CameraPosition.Builder()
              .target(PiazzaSanCarloTurin)      // Sets the center of the map to Mountain View
              .zoom(12)                   // Sets the zoom
              .bearing(0)                // Sets the orientation of the camera to east
              .tilt(30)                   // Sets the tilt of the camera to 30 degrees
              .build();                   // Creates a CameraPosition from the builder
          mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
      }
    });

    // Construct a PlaceDetectionClient.
    mPlaceDetectionClient = getPlaceDetectionClient(getContext());
    // Construct a FusedLocationProviderClient.
    mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    // Set up the API client for Places API
    mGoogleApiClient = new GoogleApiClient.Builder(getContext())
        .addApi(Places.GEO_DATA_API)
        .addApi(LocationServices.API)
        .build();
    mGoogleApiClient.connect();


    // @OnMarkerClickListener added to the map
    onMarkerClickListener = new OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(Marker marker) {
        String title = marker.getTitle();
        Toast toast = Toast
            .makeText(getContext(), "You Clicked on " + title, Toast.LENGTH_SHORT);
        toast.show();
        return false;
      }
    };

    // Build a new Retrofit Object for the Search Query
    buildRetrofitAndGetResponse = new BuildRetrofitGetResponse();
    getLastLocation();

    // Search NearbyPlaces Query to Launch Retrofit call
    searchEditText.setOnQueryTextListener(new OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        // Retrofit Call to the new Query
        query = searchEditText.getQuery().toString();
        buildRetrofitAndGetResponse
            .buildRetrofitAndGetResponse(query, latitude, longitude, apiKey, mMap);
        // buildRetrofitAndGetResponse(searchEditText.getQuery().toString());
        Log.i(LOG_TAG, "The Search Query is: " + query);
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        Log.i(LOG_TAG, "New Query");
        return false;
      }
    });

    return rootView;
  }

  // Check if local permission is enabled and ask the user to enable it if not to access app functionality
  private boolean checkLocalPermission() {
    if (ContextCompat.checkSelfPermission(getContext(),
        Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {

      // Asking user if explanation is needed
      if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
          Manifest.permission.ACCESS_FINE_LOCATION)) {

        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.

        //Prompt the user once explanation has been shown
        ActivityCompat.requestPermissions(getActivity(),
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            MY_LOCATION_REQUEST_CODE);
      } else {
        // No explanation needed, we can request the permission.
        ActivityCompat.requestPermissions(getActivity(),
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            MY_LOCATION_REQUEST_CODE);
      }
      return false;
    } else {
      return true;
    }
  }

  // Get the last known location of the device
  public void getLastLocation() {
    // Get last known recent location using new Google Play Services SDK (v11+)
    if (checkLocalPermission()) {
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
}
