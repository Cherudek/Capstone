package viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import pojos.NearbyPlaces;
import repository.NearbyPlacesRepository;

public class QueryNearbyPlacesViewModel extends ViewModel {

  private LiveData<NearbyPlaces> nearbyPlaces;


  public QueryNearbyPlacesViewModel(NearbyPlacesRepository nearbyPlacesRepository, String mKeyword,
      String latitude, String longitude, int radius, String apiKey) {
      nearbyPlaces = nearbyPlacesRepository.getNearbyPlaces(mKeyword,
          latitude + "," + longitude, radius, apiKey);

  }

  public LiveData<NearbyPlaces> getData(){
    return nearbyPlaces;
  }
}
