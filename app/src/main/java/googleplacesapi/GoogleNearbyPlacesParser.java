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
import java.util.HashMap;
import java.util.List;
import pojos.NearbyPlaces;

public class GoogleNearbyPlacesParser {

  public GoogleNearbyPlacesParser() {
  }

  private final static String LOG_TAG = GoogleNearbyPlacesParser.class.getSimpleName();
  private List<MarkerOptions> markersOptions = new ArrayList<>();
  private Integer markerCounter = -1;

  public List<MarkerOptions> drawLocationMap(NearbyPlaces nearbyPlaces, GoogleMap map, LatLng mCurrentLocation, HashMap<Marker, Integer> eventMarkerMap) {
    try {
      if(eventMarkerMap!=null){
        eventMarkerMap.clear();
      }
      map.clear();
      if(markersOptions!=null){
        markersOptions.clear();
        // Check the size of the marker is not bigget than the size of the Places returned
        if(markerCounter > markersOptions.size()){
          markerCounter = -1;
        }
      }

      // This loop will go through all the results and add marker on each location.
      for (int i = 0; i < nearbyPlaces.getResults().size(); i++) {

        Double lat = nearbyPlaces.getResults().get(i).getGeometry().getLocation().getLat();
        Double lng = nearbyPlaces.getResults().get(i).getGeometry().getLocation().getLng();
        String placeName = nearbyPlaces.getResults().get(i).getName();
        String vicinity = nearbyPlaces.getResults().get(i).getVicinity();
        String icon = nearbyPlaces.getResults().get(i).getIcon();
        String placeId = nearbyPlaces.getResults().get(i).getPlaceId();
        Uri iconUri = Uri.parse(icon);
        iconUri.getPath();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        LatLng latLng = new LatLng(lat, lng);
        // Position of Marker on Map
        markerOptions.position(latLng);
        // Adding Title (Name of the place) and Vicinity (address) to the Marker
        markerOptions.title(placeName);
        markerCounter = markerCounter + 1;
        markerOptions.snippet(String.valueOf(markerCounter));
        // Adding Marker to the Map.
        Marker marker = map.addMarker(markerOptions);
        //Mark Counters to add a Tag to help retrieve the right item and pass it to the DetailViewModel
        //markerCounter = markerCounter + 1;
        eventMarkerMap.put(marker, markerCounter);
        marker.setTag(markerCounter);
        markersOptions.add(markerOptions);
      }
      // Construct a CameraPosition focusing on the current location View and animate the camera to that position.
      CameraPosition cameraPosition = new CameraPosition.Builder()
          .target(mCurrentLocation)      // Sets the center of the map to the current user View
          .zoom(14)                   // Sets the zoom
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
