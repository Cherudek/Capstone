package com.example.gregorio.capstone;

import static com.google.android.gms.location.places.Places.getPlaceDetectionClient;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.security.Permission;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

  private GoogleMap mMap;
  private LatLngBounds.Builder mBounds = new LatLngBounds.Builder();
  private final int MY_LOCATION_REQUEST_CODE = 101;
  private static final int GOOGLE_API_CLIENT_ID = 0;

  private GoogleApiClient mGoogleApiClient;
  private PlaceDetectionClient mPlaceDetectionClient;
  // The entry point to the Fused Location Provider API to get location in Android.
  private FusedLocationProviderClient mFusedLocationProviderClient;

  // A default location (London, Uk) and default zoom to use when location permission is
  // not granted.
  private final LatLng mDefaultLocation = new LatLng(51.508530, -0.076132);
  private final LatLng mNBH = new LatLng(51.5189618,-0.1450063);

  private static final int DEFAULT_ZOOM = 15;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);


    // Construct a PlaceDetectionClient.
    mPlaceDetectionClient = getPlaceDetectionClient(this);

    // Construct a FusedLocationProviderClient.
    mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    // Set up the API client for Places API
    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addApi(Places.GEO_DATA_API)
        .addApi(LocationServices.API)
        .build();
    mGoogleApiClient.connect();


  }

  @Override
  public void onRequestPermissionsResult ( int requestCode,
      String permissions[], int[] grantResults){
    switch (requestCode) {
      case MY_LOCATION_REQUEST_CODE: {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          // permission was granted, yay! Do the
          // contacts-related task you need to do.
        } else {
          // permission denied, boo! Disable the
          // functionality that depends on this permission.
          Toast toast = Toast.makeText(this, "Location Permission not granted", Toast.LENGTH_SHORT);
          toast.show();
        }
        return;
      }
    }
  }


  private void addPointToViewPort(LatLng newPoint) {
    mBounds.include(newPoint);
    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mBounds.build(),
        findViewById(R.id.checkout_button).getHeight()));
  }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady (GoogleMap googleMap){
    mMap=googleMap;

      // Check Permissions
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
          != PackageManager.PERMISSION_GRANTED) {

        // No explanation needed; request the permission
        ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            MY_LOCATION_REQUEST_CODE);
        // MY_LOCATION_REQUEST_CODE is an
        // app-defined int constant. The callback method gets the
        // result of the request.
      } else {
        mMap.setMyLocationEnabled(true);

        if (mMap!=null){
          Marker London = mMap.addMarker(new MarkerOptions().position(mDefaultLocation)
              .title("London"));
          Marker NewBH = mMap.addMarker(new MarkerOptions()
              .position(mNBH)
              .title("New Broadcasting House")
              .snippet("New BH is cool")
              .icon(BitmapDescriptorFactory
                  .fromResource(R.mipmap.bbc_marker)));
        }
      }




    // Pad the map controls to make room for the button - note that the button may not have
    // been laid out yet.
    final Button button=findViewById(R.id.checkout_button);

    button.getViewTreeObserver().addOnGlobalLayoutListener(
    new ViewTreeObserver.OnGlobalLayoutListener(){
        @Override
        public void onGlobalLayout(){
          mMap.setPadding(0,button.getHeight(),0,0); }
    });
  }


}








