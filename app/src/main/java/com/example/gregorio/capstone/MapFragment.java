package com.example.gregorio.capstone;

import android.app.Activity;
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
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
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
import googleplacesapi.GoogleMapsApi;
import permissions.LocationPermission;
import pojos.Favourite;
import retrofit.BuildRetrofitGetResponse;

public class MapFragment extends Fragment {

  public static final String LOG_TAG = MapFragment.class.getSimpleName();
  private GoogleMap mMap;
  private MapView mapView;
  private LatLngBounds.Builder mBounds = new LatLngBounds.Builder();
  private static final int REQUEST_PLACE_PICKER = 1;
  private OnMarkerClickListener onMarkerClickListener;
  // A default location (London, Uk) and default zoom to use when location permission is
  private final LatLng mDefaultLocation = new LatLng(51.508530, -0.076132);
  private final LatLng mNBH = new LatLng(51.5189618, -0.1450063);
  private final LatLng PiazzaSanCarloTurin = new LatLng(45.0671652, 7.681715);
  private final LatLng PiazzaCastello = new LatLng(45.0710394, 7.6862986);


  private Double latitude;
  private Double longitude;
  private LatLng mCurrentLocation;
  private String apiKey;
  private Task<Location> location;
  private BuildRetrofitGetResponse buildRetrofitAndGetResponse;
  private static final String FIREBASE_URL = "https://turin-guide-1526861835739.firebaseio.com/";
  private static final String FIREBASE_ROOT_NODE = "checkouts";
  private DatabaseReference mPlacesDatabaseReference;
  private FirebaseDatabase mFirebaseDatabase;
  private FloatingActionButton checkOutBtn;
  private String mPlaceAttributions;
  private String mPlaceId;
  private String mPlaceName;
  private String mPlaceWebUrl;
  private LocationPermission locationPermission;

  public MapFragment(){
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    final View rootView = inflater.inflate(R.layout.fragment_map, container, false);
    checkOutBtn = rootView.findViewById(R.id.checkout_button);
    //searchEditText = rootView.findViewById(R.id.menu_search);
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

    locationPermission = new LocationPermission();

    // Sync Map to current location (if permitted) on the fragment View
    mapView.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        if (locationPermission.checkLocalPermission(getContext(), getActivity())) {
          // If the Location Permission id Granted Enable Location on the Map
          mMap.setMyLocationEnabled(true);
          mMap.setBuildingsEnabled(true);
          mMap.setOnMarkerClickListener(onMarkerClickListener);
          // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
          CameraPosition cameraPosition = new CameraPosition.Builder()
              .target(PiazzaCastello)      // Sets the center of the map to Piazza Castello
              .zoom(12)                   // Sets the zoom
              .bearing(0)                // Sets the orientation of the camera to east
              .tilt(30)                   // Sets the tilt of the camera to 30 degrees
              .build();                   // Creates a CameraPosition from the builder
          mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
      }
    });

    // Check if the Google Play Services are available or not
    GoogleMapsApi googleMapsApi = new GoogleMapsApi();
    googleMapsApi.CheckGooglePlayServices(getContext(), getActivity());
    googleMapsApi.GoogleApiClient(getContext());


    // @OnMarkerClickListener added to the map
    onMarkerClickListener = new OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(Marker marker) {
        String title = marker.getTitle();
        String markerId = marker.getId();
        // Bundle to launch the Detail Fragment
        Bundle bundle = new Bundle();
        bundle.putString("TITLE", title);
        bundle.putString("ID", markerId);
        // set DetailFragment Arguments
        DetailFragment fragobj = new DetailFragment();
        fragobj.setArguments(bundle);
        // Toast to confirm the New Fragment
        Toast toast = Toast
            .makeText(getContext(), "You clicked on " + title, Toast.LENGTH_SHORT);
        toast.show();
        return false;
      }
    };

    // Enable disk persistence
    //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    // Initialize Firebase components
    mFirebaseDatabase = FirebaseDatabase.getInstance();
    mPlacesDatabaseReference = mFirebaseDatabase.getReference().child("checkouts");
    // Build a new Retrofit Object for the Search Query
    buildRetrofitAndGetResponse = new BuildRetrofitGetResponse();

    return rootView;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    menu.clear();
    inflater.inflate(R.menu.main, menu);
    MenuItem menuItem = menu.findItem(R.id.menu_search);
    final SearchView searchView = new SearchView(getContext());
    searchView.setOnQueryTextListener(new OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        // Retrofit Call to the new Query
        getLastLocation();
        query = searchView.getQuery().toString();
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
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    //noinspection SimplifiableIfStatement
    if (id == R.id.menu_search) {
      return true;
    }
    return super.onOptionsItemSelected(item);
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


  // Get the last known location of the device
  public void getLastLocation() {
    // Get last known recent location using new Google Play Services SDK (v11+)
    if (locationPermission.checkLocalPermission(getContext(), getActivity())) {
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
