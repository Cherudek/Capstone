package repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;
import android.widget.Toast;
import javax.inject.Singleton;
import pojos.NearbyPlaces;
import pojosplaceid.PlaceId;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
public class NearbyPlacesRepository {

  private final static String LOG_TAG = NearbyPlacesRepository.class.getSimpleName();
  private RetrofitMapsApi retrofitMapsApi;
  private static NearbyPlacesRepository nearbyPlacesRepository;

  public NearbyPlacesRepository() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(RetrofitMapsApi.url)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    retrofitMapsApi = retrofit.create(RetrofitMapsApi.class);
  }

  public synchronized static NearbyPlacesRepository getInstance() {
       if (nearbyPlacesRepository==null){
         nearbyPlacesRepository = new NearbyPlacesRepository();
       }
    return nearbyPlacesRepository;
  }

  // Retrofit Call to get a list of Nearby Places

  public LiveData<NearbyPlaces> getNearbyPlaces(String keyword, String location, int radius,
      String key) {
    final MutableLiveData<NearbyPlaces> data = new MutableLiveData<>();

    retrofitMapsApi.getNearbyPlaces(keyword, location, radius, key).enqueue(
        new Callback<NearbyPlaces>() {
          @Override
          public void onResponse(Call<NearbyPlaces> call,
              Response<NearbyPlaces> response) {
            Log.i(LOG_TAG, "The Retrofit Response is: " + response.toString());
            data.setValue(response.body());
          }

          @Override
          public void onFailure(Call<NearbyPlaces> call, Throwable t) {
            Log.d(LOG_TAG, "onFailure" + t.toString());
            data.setValue(null);
          }
        });
    return data;
  }

  // Retrofit Call to get Details of a Place by its ID

  public LiveData<PlaceId> getPlacesId(String placeId, String apiKey) {
    final MutableLiveData<PlaceId> dataPlaceId = new MutableLiveData<>();

    retrofitMapsApi.getPlacesId(placeId, apiKey).enqueue(new Callback<PlaceId>() {

      @Override
      public void onResponse(Call<PlaceId> call, Response<PlaceId> response) {
        Log.i(LOG_TAG, "The Retrofit Response is: " + response.toString());
        dataPlaceId.setValue(response.body());
      }

      @Override
      public void onFailure(Call<PlaceId> call, Throwable t) {
        Log.d(LOG_TAG, "onFailure" + t.toString());
        dataPlaceId.setValue(null);
      }
    });
    return dataPlaceId;
  }

}
