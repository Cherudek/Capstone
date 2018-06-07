package retrofit;

import static com.example.gregorio.capstone.MapFragment.LOG_TAG;

import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import pojos.NearbyPlaces;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BuildRetrofitGetResponse {

  private final int DEFAULT_ZOOM = 1500;
  private LatLng mCurrentLocation;

  public BuildRetrofitGetResponse() {
  }

  // Retrofit call to check the keywords of the NearbyPlaces
  public void buildRetrofitAndGetResponse(String keywords, final Double latitude,
      final Double longitude, String apiKey, final GoogleMap map) {
    String url = "https://maps.googleapis.com/maps/";
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    RetrofitMaps service = retrofit.create(RetrofitMaps.class);

    Call<NearbyPlaces> call = service
        .getNearbyPlaces(keywords, latitude + "," + longitude, DEFAULT_ZOOM, apiKey);
    call.enqueue(new Callback<NearbyPlaces>() {
      @Override
      public void onResponse(Call<NearbyPlaces> call, Response<NearbyPlaces> response) {
        try {
          map.clear();
          Log.i(LOG_TAG, "The Retrofit Response is: " + response.toString());
          // This loop will go through all the results and add marker on each location.
          for (int i = 0; i < response.body().getResults().size(); i++) {
            Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
            Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();
            String placeName = response.body().getResults().get(i).getName();
            String vicinity = response.body().getResults().get(i).getVicinity();
            String id = response.body().getResults().get(i).getId();
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = new LatLng(lat, lng);
            // Position of Marker on Map
            markerOptions.position(latLng);
            // Adding Title (Name of the place) and Vicinity (address) to the Marker
            markerOptions.title(placeName + " : " + vicinity);
            // Adding Marker to the Map.
            Marker m = map.addMarker(markerOptions);
            // Adding colour to the marker
            markerOptions
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            // Construct a CameraPosition focusing on the current location View and animate the camera to that position.
            mCurrentLocation = new LatLng(latitude, longitude);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(
                    mCurrentLocation)      // Sets the center of the map to the current user View
                .zoom(15)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
          }
        } catch (Exception e) {
          Log.d("onResponse", "There is an error");
          e.printStackTrace();
        }
      }

      @Override
      public void onFailure(Call<NearbyPlaces> call, Throwable t) {
        Log.d(LOG_TAG, "onFailure" + t.toString());
      }
    });
  }

}