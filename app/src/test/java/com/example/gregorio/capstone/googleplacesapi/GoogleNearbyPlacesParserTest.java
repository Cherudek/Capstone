package com.example.gregorio.capstone.googleplacesapi;

import com.example.gregorio.capstone.model.NearbyPlaces;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GoogleNearbyPlacesParserTest {

    GoogleNearbyPlacesParser SUT;
    NearbyPlaces nearbyPlaces;
    GoogleMap googleMap;
    LatLng latLng;
    HashMap<Marker, Integer> hashMap;

    @Before
    public void setUp() throws Exception {
        SUT = new GoogleNearbyPlacesParser();
        nearbyPlaces = mock(NearbyPlaces.class);
        googleMap = mock(GoogleMap.class);
    }

    @Test
    public void drawMap_NearbyPlacePresent_markerDrawn() {
        latLng = new LatLng(45.071, 7.6863);
        when(CameraUpdateFactory.newCameraPosition(createNewCameraPosition())).getMock();
        List<MarkerOptions> result = SUT.drawLocationMap(nearbyPlaces, googleMap, latLng, hashMap);
        assertThat(result, isNotNull());
    }

    private CameraPosition createNewCameraPosition() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(14)
                .bearing(0)
                .tilt(0)
                .build();
        return cameraPosition;
    }
}