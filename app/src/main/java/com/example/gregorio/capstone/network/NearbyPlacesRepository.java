package com.example.gregorio.capstone.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.example.gregorio.capstone.model.NearbyPlaces;
import com.example.gregorio.capstone.model.placeId.PlaceId;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NearbyPlacesRepository {

    private final static String LOG_TAG = NearbyPlacesRepository.class.getSimpleName();
    private static NearbyPlacesRepository nearbyPlacesRepository;
    public String placeId;
    public String location;
    public String key;
    private RetrofitMapsApi retrofitMapsApi;
    private MutableLiveData<NearbyPlaces> data;
    private MutableLiveData<PlaceId> dataPlaceId;

    private NearbyPlacesRepository() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitMapsApi.url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitMapsApi = retrofit.create(RetrofitMapsApi.class);
    }

    public synchronized static NearbyPlacesRepository getInstance() {
        if (nearbyPlacesRepository == null) {
            nearbyPlacesRepository = new NearbyPlacesRepository();
        }
        return nearbyPlacesRepository;
    }

    // Retrofit Call to get a list of Nearby Places
    public LiveData<NearbyPlaces> getNearbyPlaces(String keyword, String location, int radius,
                                                  String key) {
        this.key = key;
        this.location = location;

        retrofitMapsApi.getNearbyPlaces(keyword, location, radius, key).enqueue(
                new Callback<NearbyPlaces>() {
                    @Override
                    public void onResponse(Call<NearbyPlaces> call,
                                           Response<NearbyPlaces> response) {
                        Log.d(LOG_TAG, "The Retrofit Response is: " + response.toString());
                        data.setValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<NearbyPlaces> call, Throwable t) {
                        Log.d(LOG_TAG, "onFailure" + t.toString());
                        data.setValue(null);
                    }
                });
        data = new MutableLiveData<>();
        return data;
    }

    // Retrofit Call to get Details of a Place by its ID
    public LiveData<PlaceId> getPlacesId(String placeId, String apiKey) {
        this.placeId = placeId;
        this.key = apiKey;
        dataPlaceId = new MutableLiveData<>();

        retrofitMapsApi.getPlacesId(placeId, key).enqueue(new Callback<PlaceId>() {
            @Override
            public void onResponse(Call<PlaceId> call, Response<PlaceId> response) {
                Log.d(LOG_TAG, "The Retrofit Response is: " + response.toString());
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
