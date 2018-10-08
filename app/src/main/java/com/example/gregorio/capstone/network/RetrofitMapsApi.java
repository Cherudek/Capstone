package com.example.gregorio.capstone.network;

import com.example.gregorio.capstone.model.NearbyPlaces;
import com.example.gregorio.capstone.model.placeId.PlaceId;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitMapsApi {

    String url = "https://maps.googleapis.com/maps/";

    @GET("api/place/nearbysearch/json?")
    Call<NearbyPlaces> getNearbyPlaces(@Query("keyword") String keyword,
                                       @Query("location") String location,
                                       @Query("radius") int radius,
                                       @Query("key") String key);


    @GET("api/place/details/json?")
    Call<PlaceId> getPlacesId(@Query("placeid") String placeid,
                              @Query("key") String key);
}
