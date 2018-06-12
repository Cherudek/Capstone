package googleplacesapi;

import android.net.Uri;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
import pojos.NearbyPlaces;

public class GoogleLocationJsonParser {

  public GoogleLocationJsonParser() {
  }

  private final static String LOG_TAG = GoogleLocationJsonParser.class.getSimpleName();

  public void drawLocationMap(NearbyPlaces nearbyPlaces, GoogleMap map, LatLng mCurrentLocation) {

    try {
      // This loop will go through all the results and add marker on each location.
      for (int i = 0; i < nearbyPlaces.getResults().size(); i++) {
        Double lat = nearbyPlaces.getResults().get(i).getGeometry().getLocation()
            .getLat();
        Double lng = nearbyPlaces.getResults().get(i).getGeometry().getLocation()
            .getLng();
        String placeName = nearbyPlaces.getResults().get(i).getName();
        String vicinity = nearbyPlaces.getResults().get(i).getVicinity();
        String id = nearbyPlaces.getResults().get(i).getId();
        String icon = nearbyPlaces.getResults().get(i).getIcon();
        List photos = nearbyPlaces.getResults().get(i).getPhotos();
        int photoSize = photos.size();
        Log.i(LOG_TAG, "Photo Size Array is: " + photoSize);

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
        map.addMarker(markerOptions);
        // Adding colour to the marker
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        // Construct a CameraPosition focusing on the current location View and animate the camera to that position.
       // mCurrentLocation = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
            .target(mCurrentLocation)      // Sets the center of the map to the current user View
            .zoom(15)                   // Sets the zoom
            .bearing(0)                // Sets the orientation of the camera to east
            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
            .build();                   // Creates a CameraPosition from the builder
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
      }
    } catch (Exception e) {
      Log.d("onResponse", "There is an error");
      e.printStackTrace();
    }
  }
}
