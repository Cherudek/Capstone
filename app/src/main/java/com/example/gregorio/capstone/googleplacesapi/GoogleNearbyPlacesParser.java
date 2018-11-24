package com.example.gregorio.capstone.googleplacesapi;

import android.net.Uri;

import com.example.gregorio.capstone.model.NearbyPlaces;
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

public class GoogleNearbyPlacesParser {

    private final static String LOG_TAG = GoogleNearbyPlacesParser.class.getSimpleName();
    private List<MarkerOptions> markersOptions = new ArrayList<>();
    private Integer mapMarkerCounter = -1;

    public GoogleNearbyPlacesParser() {
    }

    public List<MarkerOptions> drawLocationMap(NearbyPlaces nearbyPlaces, GoogleMap map, LatLng currentLocation, HashMap<Marker, Integer> eventMarkerMap) {

        try {
            if (eventMarkerMap != null) {
                eventMarkerMap.clear();
            }
            map.clear();
            if (markersOptions != null) {
                markersOptions.clear();
                // Check the size of the marker is not bigger than the size of the Places returned
                if (mapMarkerCounter > markersOptions.size()) {
                    mapMarkerCounter = -1;
                }
            }

            drawMarkersOnMap(nearbyPlaces, map, eventMarkerMap);
            setCameraPosition(currentLocation, map);

        } catch (Exception e) {
            // Log.d("onResponse", "drawLocationMap There is an error: " + e.getMessage());
            e.printStackTrace();
        }
        //  Log.d(LOG_TAG, "Marker Options Size is: " + markersOptions.size());
        return markersOptions;
    }

    private void drawMarkersOnMap(NearbyPlaces nearbyPlaces, GoogleMap map, HashMap<Marker, Integer> eventMarkerMap) {
        MarkerOptions markerOptions = new MarkerOptions();
        Double lat;
        Double lng;
        String placeName;
        String icon;
        Uri iconUri;
        LatLng latLng;
        Marker marker;

        for (int i = 0; i < nearbyPlaces.getResults().size(); i++) {
            lat = nearbyPlaces.getResults().get(i).getGeometry().getLocation().getLat();
            lng = nearbyPlaces.getResults().get(i).getGeometry().getLocation().getLng();
            placeName = nearbyPlaces.getResults().get(i).getName();
            icon = nearbyPlaces.getResults().get(i).getIcon();
            iconUri = Uri.parse(icon);
            iconUri.getPath();
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            latLng = new LatLng(lat, lng);
            // Position of Marker on Map
            markerOptions.position(latLng);
            // Adding Title (Name of the place) and Vicinity (address) to the Marker
            markerOptions.title(placeName);
            mapMarkerCounter = mapMarkerCounter + 1;
            markerOptions.snippet(String.valueOf(mapMarkerCounter));
            // Adding Marker to the Map.
            marker = map.addMarker(markerOptions);
            //Mark Counters to add a Tag to help retrieve the right item and pass it to the DetailViewModel
            //mapMarkerCounter = mapMarkerCounter + 1;
            eventMarkerMap.put(marker, mapMarkerCounter);
            marker.setTag(mapMarkerCounter);
            markersOptions.add(markerOptions);
        }
    }

    private void setCameraPosition(LatLng currentLocation, GoogleMap map) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLocation)
                .zoom(14)
                .bearing(0)
                .tilt(0)
                .build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
