package viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.List;
import pojos.NearbyPlaces;
import repository.NearbyPlacesRepository;

public class QueryNearbyPlacesViewModel extends ViewModel {

  public LiveData<NearbyPlaces> mNearbyPlaces;
  public NearbyPlacesRepository mNearbyPlacesRepository;
  public String mKeyword;
  public String mLatitude;
  public String mLongitude;
  public int mRadius;
  public String mApiKey;
  public List<MarkerOptions> mMarkersOptions;



  public QueryNearbyPlacesViewModel(NearbyPlacesRepository nearbyPlacesRepository, String keyword,
      String latitude, String longitude, int radius, String apiKey) {
      mNearbyPlaces = nearbyPlacesRepository.getNearbyPlaces(keyword,
          latitude + "," + longitude, radius, apiKey);
  }

  public LiveData<NearbyPlaces> getNewPlaces() {
    if (mNearbyPlaces != null) {
      mNearbyPlaces = new MutableLiveData<>();
      mNearbyPlaces = mNearbyPlacesRepository.getNearbyPlaces(mKeyword, mLatitude + "," + mLongitude, mRadius, mApiKey);
    }
    return mNearbyPlaces;
  }

  public LiveData<NearbyPlaces> getData(){
    return mNearbyPlaces;
  }


}
