//package retrofit;
//
//import android.util.Log;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import pojos.Example;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class BuildRetrofitGetResponse {
//
//
//  public BuildRetrofitGetResponse() {
//
//  }
//
//  private void build_retrofit_and_get_response(String type, final GoogleMap mMap) {
//
//    String url = "https://maps.googleapis.com/maps/";
//
//    Retrofit retrofit = new Retrofit.Builder()
//        .baseUrl(url)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build();
//
//    RetrofitMaps service = retrofit.create(RetrofitMaps.class);
//
//    Call<Example> call = service
//        .getNearbyPlaces(type, latitude + "," + longitude, PROXIMITY_RADIUS);
//
//    call.enqueue(new Callback<Example>() {
//
//      @Override
//      public void onResponse(Call<Example> call, Response<Example> response) {
//
//        try {
//          mMap.clear();
//          // This loop will go through all the results and add marker on each location.
//          for (int i = 0; i < response.body().getResults().size(); i++) {
//            Double lat = response.body().getResults().get(i).getGeometry().getLocation().getLat();
//            Double lng = response.body().getResults().get(i).getGeometry().getLocation().getLng();
//            String placeName = response.body().getResults().get(i).getName();
//            String vicinity = response.body().getResults().get(i).getVicinity();
//            MarkerOptions markerOptions = new MarkerOptions();
//            LatLng latLng = new LatLng(lat, lng);
//            // Position of Marker on Map
//            markerOptions.position(latLng);
//            // Adding Title to the Marker
//            markerOptions.title(placeName + " : " + vicinity);
//            // Adding Marker to the Camera.
//            Marker m = mMap.addMarker(markerOptions);
//            // Adding colour to the marker
//            markerOptions
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//            // move map camera
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//          }
//        } catch (Exception e) {
//          Log.d("onResponse", "There is an error");
//          e.printStackTrace();
//        }
//      }
//
//      @Override
//      public void onFailure(Call<Example> call, Throwable t) {
//        Log.d("onFailure", t.toString());
//      }
//    });
//  }
//}