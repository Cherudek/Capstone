package repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;
import pojos.NearbyPlaces;
import pojosplaceid.PlaceId;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NearbyPlacesRepository {

  private final static String LOG_TAG = NearbyPlacesRepository.class.getSimpleName();
  private RetrofitMapsApi retrofitMapsApi;
  private static NearbyPlacesRepository nearbyPlacesRepository;
  private MutableLiveData<NearbyPlaces> data;
  public String placeId;
  public String keyword;
  public String location;
  public String key;
  public int radius;
  private MutableLiveData<PlaceId> dataPlaceId;

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
    this.radius = radius;
    this.key = key;
    this.location = location;
    this.keyword = keyword;
    new AsyncTaskSearchNearbyPlaces().execute();
    data = new MutableLiveData<>();
    return data;
  }

  // Retrofit Call to get Details of a Place by its ID
  public LiveData<PlaceId> getPlacesId(String placeId, String apiKey) {
    new AsyncTaskSearchPlaceById().execute();
    this.placeId = placeId;
    this.key = apiKey;
    dataPlaceId = new MutableLiveData<>();
    return dataPlaceId;
  }

  // AsyncTask to Retrieve Nearby Places;

  private class AsyncTaskSearchNearbyPlaces extends android.os.AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... voids) {
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
      return null;
    }
  }

  // AsyncTask to Retrieve Places By ID;

  private class AsyncTaskSearchPlaceById extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... voids) {
      retrofitMapsApi.getPlacesId(placeId, key).enqueue(new Callback<PlaceId>() {
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
      return null;
    }
  }

}
