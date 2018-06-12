package viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import com.google.android.gms.maps.GoogleMap;
import repository.NearbyPlacesRepository;

public class GoogleMapViewModel extends ViewModel {
  private LiveData<GoogleMap> googleMapLiveData;

  public GoogleMapViewModel(NearbyPlacesRepository nearbyPlacesRepository, GoogleMap googleMap)
       {
    googleMapLiveData = nearbyPlacesRepository.getGoogleMap(googleMap);
  }

  public LiveData<GoogleMap> getGooglMap(){
    return googleMapLiveData;
  }
}
