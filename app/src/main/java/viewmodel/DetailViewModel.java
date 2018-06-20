package viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import pojosplaceid.PlaceId;
import repository.NearbyPlacesRepository;

public class DetailViewModel extends ViewModel {

  public LiveData<PlaceId> mPlaceDetails;
  public NearbyPlacesRepository mNearbyPlacesRepository;
  public String mPlaceId;
  public String mApiKey;

  public DetailViewModel(
      NearbyPlacesRepository nearbyPlacesRepository , String placeID, String mApiKey) {
      mPlaceDetails = nearbyPlacesRepository.getPlacesId(placeID, mApiKey);
  }

  public LiveData<PlaceId> getPlaceDetails(){
    return mPlaceDetails;
  }


  public LiveData<PlaceId> getNewPlaceDetails(){
    if(mPlaceDetails!=null){
      mPlaceDetails = new MutableLiveData<>();
      mPlaceDetails = mNearbyPlacesRepository.getPlacesId(mPlaceId, mApiKey);
    }
    return mPlaceDetails;
  }
}
