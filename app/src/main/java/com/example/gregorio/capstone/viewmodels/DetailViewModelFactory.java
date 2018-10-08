package com.example.gregorio.capstone.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.gregorio.capstone.network.NearbyPlacesRepository;

public class DetailViewModelFactory extends ViewModelProvider.NewInstanceFactory{

  private final NearbyPlacesRepository nearbyPlacesRepository;
  private final String placeId;
  private final String apiKey;

  public DetailViewModelFactory(NearbyPlacesRepository nearbyPlacesRepository,
      String placeId, String apiKey) {
    this.nearbyPlacesRepository = nearbyPlacesRepository;
    this.placeId = placeId;
    this.apiKey = apiKey;
  }

  @NonNull
  @Override
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    return (T) new DetailViewModel(nearbyPlacesRepository, placeId, apiKey);
  }
}
