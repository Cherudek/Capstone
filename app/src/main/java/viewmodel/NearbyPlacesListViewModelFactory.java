package viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import repository.NearbyPlacesRepository;

public class NearbyPlacesListViewModelFactory extends ViewModelProvider.NewInstanceFactory {

  private final NearbyPlacesRepository nearbyPlacesRepository;
  private final String query;
  private final String latitude;
  private final String longitude;
  private final String apiKey;
  private final int radius;

  public NearbyPlacesListViewModelFactory(NearbyPlacesRepository nearbyPlacesRepository, String query, String latitude,
      String longitude, int radius, String apiKey) {
    this.nearbyPlacesRepository = nearbyPlacesRepository;
    this.query = query;
    this.latitude = latitude;
    this.longitude = longitude;
    this.radius = radius;
    this.apiKey = apiKey;
  }

  @NonNull
  @Override
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    return (T) new QueryNearbyPlacesViewModel(nearbyPlacesRepository, query, latitude, longitude, radius, apiKey);



  }
}
