//package intentservices;
//
//import static repository.NearbyPlacesRepository.KEYWORD_TAG;
//import static repository.NearbyPlacesRepository.KEY_TAG;
//import static repository.NearbyPlacesRepository.LOCATION_TAG;
//import static repository.NearbyPlacesRepository.RADIUS_TAG;
//
//import android.app.IntentService;
//import android.arch.lifecycle.MutableLiveData;
//import android.content.Intent;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import pojos.NearbyPlaces;
//import repository.RetrofitMapsApi;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//public class IntentServiceSearchPlaces extends IntentService {
//
//  private RetrofitMapsApi retrofitMapsApi;
//  private MutableLiveData<NearbyPlaces> data;
//
//  /**
//   * Creates an IntentService.  Invoked by your subclass's constructor.
//   *
//   * @param name Used to name the worker thread, important only for debugging.
//   */
//
//  private static final String LOG_TAG = IntentServiceSearchPlaces.class.getSimpleName();
//
//
//  public IntentServiceSearchPlaces() {
//    super("IntentServiceSearchPlaces");
//  }
//
//  @Override
//  protected void onHandleIntent(@Nullable Intent intent) {
//
//    String key = intent.getStringExtra(KEY_TAG);
//    String location = intent.getStringExtra(LOCATION_TAG);
//    String keyword = intent.getStringExtra(KEYWORD_TAG);
//    int radius = 1500;
//
//    Retrofit retrofit = new Retrofit.Builder()
//        .baseUrl(RetrofitMapsApi.url)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build();
//    retrofitMapsApi = retrofit.create(RetrofitMapsApi.class);
//
//    retrofitMapsApi.getNearbyPlaces(keyword, location, radius, key).enqueue(
//        new Callback<NearbyPlaces>() {
//          @Override
//          public void onResponse(Call<NearbyPlaces> call,
//              Response<NearbyPlaces> response) {
//            Log.i(LOG_TAG, "The Retrofit Response is: " + response.toString());
//            data.setValue(response.body());
//          }
//
//          @Override
//          public void onFailure(Call<NearbyPlaces> call, Throwable t) {
//            Log.d(LOG_TAG, "onFailure" + t.toString());
//            data.setValue(null);
//          }
//        });
//
//    Log.i(LOG_TAG, "onHandleIntent Thread = " + Thread.currentThread().getName());
//
//  }
//
//
//}
