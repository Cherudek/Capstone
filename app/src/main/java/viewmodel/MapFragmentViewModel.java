//package viewmodel;
//
//import android.arch.lifecycle.ViewModel;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.model.LatLng;
//import retrofit.BuildRetrofitGetResponse;
//
//public class MapFragmentViewModel extends ViewModel {
//
//  GoogleMap map;
//  String keywords;
//  String apiKey;
//  Double latitude;
//  Double longitude;
//  LatLng latLng;
//
//  private void getNearbyPlaces() {
//    BuildRetrofitGetResponse buildRetrofitGetResponse = new BuildRetrofitGetResponse();
//    buildRetrofitGetResponse
//        .buildRetrofitAndGetResponse(keywords, latitude, longitude, apiKey, map);
//
//  }
//
//
//}
