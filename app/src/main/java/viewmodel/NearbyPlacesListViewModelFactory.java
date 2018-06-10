package viewmodel;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProvider.NewInstanceFactory;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import javax.inject.Inject;
import javax.inject.Singleton;
import repository.NearbyPlacesRepository;

//
//public class NearbyPlacesListViewModelFactory extends ViewModelProvider.NewInstanceFactory {
//
//  private final String query;
//  private final String latitude;
//  private final String longitude;
//  private final String apiKey;
//  private final int radius;
//
//
//  public NearbyPlacesListViewModelFactory(String query, String latitude, String longitude,
//      int radius, String apiKey) {
//    this.query = query;
//    this.latitude = latitude;
//    this.longitude = longitude;
//    this.radius = radius;
//    this.apiKey = apiKey;
//  }
//
//  @NonNull
//  @Override
//  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//    return (T) new QueryNearbyPlacesViewModel(query, latitude, longitude, radius, apiKey);
//  }
//}
