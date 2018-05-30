package com.example.gregorio.capstone;

import static com.google.android.gms.location.places.Places.getPlaceDetectionClient;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import pojos.Favourite;
import retrofit.BuildRetrofitGetResponse;

public class MapFragment extends Fragment {

  public static final String LOG_TAG = MapFragment.class.getSimpleName();
  private GoogleMap mMap;
  private MapView mapView;
  private LatLngBounds.Builder mBounds = new LatLngBounds.Builder();
  private final int MY_LOCATION_REQUEST_CODE = 101;
  private static final int GOOGLE_API_CLIENT_ID = 0;
  private static final int REQUEST_PLACE_PICKER = 1;

  private GoogleApiClient mGoogleApiClient;
  private PlaceDetectionClient mPlaceDetectionClient;
  // The entry point to the Fused Location Provider API to get location in Android.
  private FusedLocationProviderClient mFusedLocationProviderClient;
  private OnMarkerClickListener onMarkerClickListener;
  // A default location (London, Uk) and default zoom to use when location permission is
  private final LatLng mDefaultLocation = new LatLng(51.508530, -0.076132);
  private final LatLng mNBH = new LatLng(51.5189618, -0.1450063);
  private final LatLng PiazzaSanCarloTurin = new LatLng(45.0671652, 7.681715);
  private Double latitude;
  private Double longitude;
  private String apiKey;
  private SearchView searchEditText;
  private Task<Location> location;
  private BuildRetrofitGetResponse buildRetrofitAndGetResponse;
  private static final String FIREBASE_URL = "https://turin-guide-1526861835739.firebaseio.com/";
  private static final String FIREBASE_ROOT_NODE = "checkouts";
  private DatabaseReference mPlacesDatabaseReference;
  private FirebaseDatabase mFirebaseDatabase;
  private Button checkOutBtn;
  private String mPlaceAttributions;
  private String mPlaceId;
  private String mPlaceName;
  private String mPlaceWebUrl;

  public MapFragment(){
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View rootView = inflater.inflate(R.layout.fragment_map, container, false);
    checkOutBtn = rootView.findViewById(R.id.checkout_button);
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

    checkOutBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        checkOut(rootView);
      }
    });

    // Sync Map to current location (if permitted) on the fragment View
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
            .makeText(getContext(), "You clicked on " + title, Toast.LENGTH_SHORT);
        toast.show();
        return false;
      }
    };

    // Enable disk persistence
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    // Initialize Firebase components
    mFirebaseDatabase = FirebaseDatabase.getInstance();
    mPlacesDatabaseReference = mFirebaseDatabase.getReference().child("checkouts");

    // Build a new Retrofit Object for the Search Query
    buildRetrofitAndGetResponse = new BuildRetrofitGetResponse();

    // Search NearbyPlaces Query to Launch Retrofit call
    searchEditText.setOnQueryTextListener(new OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        // Retrofit Call to the new Query
        getLastLocation();
        query = searchEditText.getQuery().toString();
        buildRetrofitAndGetResponse
            .buildRetrofitAndGetResponse(query, latitude, longitude, apiKey, mMap);
        Log.i(LOG_TAG, "The Search Query is: " + query);
        return false;
      }
      @Override
      public boolean onQueryTextChange(String newText) {
        return false;
      }
    });

    return rootView;
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
      Toast.makeText(getContext(), "Please install Google Play Services!", Toast.LENGTH_LONG)
          .show();
    }
  }

  // Once the user has chosen a place, onActivityResult will be called, so we need to implement that now.
  // This code checks that the intent was successful and uses PlacePicker.getPlace() to obtain the chosen Place.
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_PLACE_PICKER) {
      if (resultCode == Activity.RESULT_OK) {
        Place place = PlacePicker.getPlace(getContext(), data);
        mPlaceId = place.getId();
        mPlaceName = place.getName().toString();
        if (place.getAttributions() != null) {
          mPlaceAttributions = place.getAttributions().toString();
        } else {
          mPlaceAttributions = "";
        }
        if (place.getWebsiteUri() != null) {
          mPlaceWebUrl = place.getWebsiteUri().toString();
        } else {
          mPlaceWebUrl = "";
        }
        // Object to be passed to the firebase db reference.
        Favourite favouriteObject = new Favourite(mPlaceId, mPlaceName, mPlaceWebUrl,
            mPlaceAttributions);
        String favourite = getString(R.string.nv_favourites);
        mPlacesDatabaseReference.child(favourite).push().setValue(favouriteObject);
        Snackbar snackbar = Snackbar
            .make(getView(), "Location stored on Firebase!", Snackbar.LENGTH_SHORT);
        snackbar.show();
      } else if (resultCode == PlacePicker.RESULT_ERROR) {
        Toast.makeText(getContext(),
            "Places API failure! Check that the API is enabled for your key",
            Toast.LENGTH_LONG).show();
      } else {
        super.onActivityResult(requestCode, resultCode, data);
      }
    }
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
