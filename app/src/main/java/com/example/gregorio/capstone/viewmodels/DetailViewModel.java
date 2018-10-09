package com.example.gregorio.capstone.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.example.gregorio.capstone.model.placeId.PlaceId;
import com.example.gregorio.capstone.network.NearbyPlacesRepository;

public class DetailViewModel extends ViewModel {

    public LiveData<PlaceId> placeDetails;
    public NearbyPlacesRepository nearbyPlacesRepository;
    public String placeId;
    public String apiKey;

  public DetailViewModel(
      NearbyPlacesRepository nearbyPlacesRepository , String placeID, String mApiKey) {
      placeDetails = nearbyPlacesRepository.getPlacesId(placeID, mApiKey);
  }

  public LiveData<PlaceId> getPlaceDetails(){
      return placeDetails;
  }

  public LiveData<PlaceId> getNewPlaceDetails(){
      if (placeDetails != null) {
          placeDetails = new MutableLiveData<>();
          placeDetails = nearbyPlacesRepository.getPlacesId(placeId, apiKey);
    }
      return placeDetails;
  }
}
