package repository;

import pojos.NearbyPlaces;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface RetrofitMapsApi {
   String url = "https://maps.googleapis.com/maps/";
  /*
   * Retrofit get annotation with our URL
   * And our method that will return us a list of Places.
   */
  @GET("api/place/nearbysearch/json?")
  Call<NearbyPlaces> getNearbyPlaces(@Query("keyword") String keyword,
      @Query("location") String location,
      @Query("radius") int radius,
      @Query("key") String key);

  /*
   * Retrofit get annotation with our URL
   * And our method that will return the Details of a PlaceId.
   */
  @GET("api/place/details/json?")
  Call<NearbyPlaces> getPlacesId(@Query("placeid") String placeid,
      @Query("key") String key);


}
