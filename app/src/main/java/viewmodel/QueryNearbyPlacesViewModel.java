package viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import com.google.android.gms.maps.model.LatLng;
import java.util.List;
import javax.inject.Inject;
import pojos.NearbyPlaces;
import repository.NearbyPlacesRepository;
import repository.RetrofitMapsApi;

public class QueryNearbyPlacesViewModel extends ViewModel {

  private NearbyPlacesRepository nearbyPlacesRepository;
  private LiveData<List<NearbyPlaces>> nearbyPlaces;


  @Inject
  public QueryNearbyPlacesViewModel(NearbyPlacesRepository nearbyPlacesRepository){
    this.nearbyPlacesRepository = nearbyPlacesRepository;
  }



  public QueryNearbyPlacesViewModel(String mKeyword,
      String latitude, String longitude, int radius, String apiKey) {
      nearbyPlaces = nearbyPlacesRepository.getNearbyPlaces(mKeyword, latitude + "," + longitude, radius, apiKey);

  }

  public LiveData<List<NearbyPlaces>> getData(){
    return nearbyPlaces;
  }
}
