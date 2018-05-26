package com.example.gregorio.capstone;

import static com.google.android.gms.location.places.Places.getPlaceDetectionClient;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment {

  public static final String TAG = "MapFragmentViewModel";

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
  // not granted.
  private final LatLng mDefaultLocation = new LatLng(51.508530, -0.076132);
  private final LatLng mNBH = new LatLng(51.5189618, -0.1450063);

  private static final int DEFAULT_ZOOM = 15;

  public MapFragment(){
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_map, container, false);

    TextView textView = rootView.findViewById(R.id.maptv);
    textView.setText("MAP FRAGMENT");

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

        // Check Permissions
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

          // No explanation needed; request the permission
          ActivityCompat.requestPermissions(getActivity(),
              new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
              MY_LOCATION_REQUEST_CODE);
          // MY_LOCATION_REQUEST_CODE is an
          // app-defined int constant. The callback method gets the
          // result of the request.
        } else {
          // If the Location Permission id Granted Enable Location on the Map
          mMap.setMyLocationEnabled(true);
          mMap.setOnMarkerClickListener(onMarkerClickListener);

          // Set Up Markers on the Map
          if(mMap!=null) {
            // Tower of London Marker
            Marker towerOfLondon = mMap.addMarker(new MarkerOptions().position(mDefaultLocation)
                .title("Tower Of London"));

            // New Broadcasting House Marker
            Marker NewBH = mMap.addMarker(new MarkerOptions()
                .position(mNBH)
                .title("New Broadcasting House")
                .snippet("New BH is cool")
                .icon(BitmapDescriptorFactory
                    .fromResource(R.mipmap.bbc_marker)));
          }
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

    return rootView;
  }
}
