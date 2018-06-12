package repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import javax.inject.Singleton;
import pojos.NearbyPlaces;
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
    //TODO No need to implement this singleton in Part #2 since Dagger will handle it ...
    if (nearbyPlacesRepository == null) {
      if (nearbyPlacesRepository == null) {
        nearbyPlacesRepository = new NearbyPlacesRepository();
      }
    }
    return nearbyPlacesRepository;
  }

  public LiveData<GoogleMap> getGoogleMap(GoogleMap googleMap){
    final MutableLiveData<GoogleMap> data = new MutableLiveData<>();
    return data;
  }

  public LiveData<NearbyPlaces> getNearbyPlaces(String keyword, String location, int radius,
      String key) {
    final MutableLiveData<NearbyPlaces> data = new MutableLiveData<>();

    retrofitMapsApi.getNearbyPlaces(keyword, location, radius, key).enqueue(
        new Callback<NearbyPlaces>() {
          @Override
          public void onResponse(Call<NearbyPlaces> call,
              Response<NearbyPlaces> response) {
            Log.i(LOG_TAG, "The Retrofit Response is: " + response.toString());
            Log.i(LOG_TAG, "The Retrofit Response is Size is: " + response.body().getResults().size());
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

}
