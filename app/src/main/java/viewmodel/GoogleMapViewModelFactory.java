package viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import com.google.android.gms.maps.GoogleMap;
import repository.NearbyPlacesRepository;

public class GoogleMapViewModelFactory extends ViewModelProvider.NewInstanceFactory {
  private final NearbyPlacesRepository nearbyPlacesRepository;
  private final GoogleMap googleMap;

  public GoogleMapViewModelFactory(NearbyPlacesRepository nearbyPlacesRepository,
      GoogleMap googleMap) {
    this.nearbyPlacesRepository = nearbyPlacesRepository;
    this.googleMap = googleMap;
  }

  @NonNull
  @Override
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    return (T) new GoogleMapViewModel(nearbyPlacesRepository, googleMap);
  }
}
