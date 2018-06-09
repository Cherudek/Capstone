package repository;

import java.util.List;
import pojos.NearbyPlaces;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface RetrofitMapsApi {

  /*
   * Retrofit get annotation with our URL
   * And our method that will return us details of student.
   */
  @GET("api/place/nearbysearch/json?sensor=true")
  Call<List<NearbyPlaces>> getNearbyPlaces(@Query("keyword") String keyword,
      @Query("location") String location,
      @Query("radius") int radius,
      @Query("key") String key);

}
