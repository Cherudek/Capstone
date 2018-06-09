package repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import pojos.NearbyPlaces;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class NearbyPlacesRepository {

  private RetrofitMapsApi retrofitMapsApi;

  @Inject
  public NearbyPlacesRepository(RetrofitMapsApi retrofitMapsApi) {
    this.retrofitMapsApi = retrofitMapsApi;
  }

  public LiveData<List<NearbyPlaces>> getNearbyPlaces(String keyword, String location, int radius,
      String key) {
    final MutableLiveData<List<NearbyPlaces>> data = new MutableLiveData<>();

    retrofitMapsApi.getNearbyPlaces(keyword, location, radius, key).enqueue(
        new Callback<List<NearbyPlaces>>() {
          @Override
          public void onResponse(Call<List<NearbyPlaces>> call,
              Response<List<NearbyPlaces>> response) {
            data.setValue(response.body());
          }

          @Override
          public void onFailure(Call<List<NearbyPlaces>> call, Throwable t) {
            data.setValue(null);
          }
        });
    return data;
  }

}
