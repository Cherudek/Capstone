package com.example.gregorio.capstone.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.gregorio.capstone.model.NearbyPlaces;
import com.example.gregorio.capstone.network.NearbyPlacesRepository;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class QueryNearbyPlacesViewModel extends ViewModel {

    public LiveData<NearbyPlaces> nearbyPlaces;
    public NearbyPlacesRepository nearbyPlacesRepository;
    public String keyword;
    public String latitude;
    public String longitude;
    public int radius;
    public String apiKey;
    public List<MarkerOptions> markerOptions;

    public QueryNearbyPlacesViewModel(NearbyPlacesRepository nearbyPlacesRepository, String keyword,
                                      String latitude, String longitude, int radius, String apiKey) {
        nearbyPlaces = nearbyPlacesRepository.getNearbyPlaces(keyword,
                latitude + "," + longitude, radius, apiKey);
    }

    public LiveData<NearbyPlaces> getData() {
        return nearbyPlaces;
    }

    public LiveData<NearbyPlaces> getNewPlaces() {
        if (nearbyPlaces != null) {
            nearbyPlaces = new MutableLiveData<>();
            nearbyPlaces = nearbyPlacesRepository.getNearbyPlaces(keyword, latitude + "," + longitude, radius, apiKey);
        }
        return nearbyPlaces;
    }
}
