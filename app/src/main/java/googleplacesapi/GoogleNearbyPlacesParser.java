package googleplacesapi;

import android.net.Uri;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;
import pojos.NearbyPlaces;

public class GoogleNearbyPlacesParser {

  public GoogleNearbyPlacesParser() {
  }

  private final static String LOG_TAG = GoogleNearbyPlacesParser.class.getSimpleName();
  private List<MarkerOptions> markersOptions = new ArrayList<>();
  private Marker marker;
  int markerCounter = -1;
  public List<MarkerOptions> drawLocationMap(NearbyPlaces nearbyPlaces, GoogleMap map, LatLng mCurrentLocation) {
    try {
      map.clear();
      if(markersOptions!=null){
        markersOptions.clear();
      }
      // This loop will go through all the results and add marker on each location.
      for (int i = 0; i < nearbyPlaces.getResults().size(); i++) {
        Double lat = nearbyPlaces.getResults().get(i).getGeometry().getLocation().getLat();
        Double lng = nearbyPlaces.getResults().get(i).getGeometry().getLocation().getLng();
        String placeName = nearbyPlaces.getResults().get(i).getName();
        String vicinity = nearbyPlaces.getResults().get(i).getVicinity();
        String placeId = nearbyPlaces.getResults().get(i).getPlaceId();
        String icon = nearbyPlaces.getResults().get(i).getIcon();
        List photos = nearbyPlaces.getResults().get(i).getPhotos();
        int photoSize = photos.size();
        Uri iconUri = Uri.parse(icon);
        iconUri.getPath();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        LatLng latLng = new LatLng(lat, lng);
        // Position of Marker on Map
        markerOptions.position(latLng);
        // Adding Title (Name of the place) and Vicinity (address) to the Marker
        markerOptions.title(placeName);
        markerOptions.snippet(vicinity);
        // Adding Marker to the Map.
        marker = map.addMarker(markerOptions);
        //Mark Counters to add a Tag to help retrieve the right item and pass it to the DetailViewModel
        markerCounter = markerCounter + 1;
        marker.setTag(markerCounter);
        markersOptions.add(markerOptions);
      }
      // Construct a CameraPosition focusing on the current location View and animate the camera to that position.
      CameraPosition cameraPosition = new CameraPosition.Builder()
          .target(mCurrentLocation)      // Sets the center of the map to the current user View
          .zoom(15)                   // Sets the zoom
          .bearing(0)                // Sets the orientation of the camera to east
          .tilt(0)                   // Sets the tilt of the camera to 30 degrees
          .build();                   // Creates a CameraPosition from the builder
      map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    } catch (Exception e) {
      Log.d("onResponse", "drawLocationMap There is an error");
      e.printStackTrace();
    }
    Log.i(LOG_TAG, "Marker Options Size is: " + markersOptions.size());

    return markersOptions;
  }
}
