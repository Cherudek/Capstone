package viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import repository.NearbyPlacesRepository;

public class GoogleMapViewModel extends ViewModel {

  private final static String LOG_TAG = GoogleMapViewModel.class.getSimpleName();

  private LiveData<GoogleMap> googleMapLiveData;

  private MutableLiveData<GoogleMap> googleMapMutableLiveData;

  public MutableLiveData<GoogleMap> getCurrentMap() {
    if (googleMapMutableLiveData == null) {
      googleMapMutableLiveData = new MutableLiveData<>();
      Log.i(LOG_TAG, "googleMapMutableLiveData value is: " + googleMapMutableLiveData.getValue());
    }
    return googleMapMutableLiveData;
  }

  public GoogleMapViewModel(NearbyPlacesRepository nearbyPlacesRepository, GoogleMap googleMap) {
    googleMapLiveData = nearbyPlacesRepository.getGoogleMap(googleMap);
  }

  public LiveData<GoogleMap> getGoogleMap(){
    return googleMapLiveData;
  }
}
