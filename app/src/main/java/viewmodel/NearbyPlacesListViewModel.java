package viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import pojos.NearbyPlaces;
import repository.NearbyPlacesRepository;

public class NearbyPlacesListViewModel extends AndroidViewModel {

  private final static String LOG_TAG = NearbyPlacesListViewModel.class.getSimpleName();
  public  LiveData<NearbyPlaces> nearbyPlacesListObservable;
  public  int DEFAULT_ZOOM = 1500;
  public String mKeyword = "";
  public Double mLatitude = 0.0;
  public Double mLongitude = 0.0;
  public String mApiKey = "";

  public NearbyPlacesListViewModel(
      @NonNull Application application) {
    super(application);
    nearbyPlacesListObservable = NearbyPlacesRepository.getInstance()
        .getNearbyPlaces(mKeyword, mLatitude.toString() + "," + mLongitude.toString(), DEFAULT_ZOOM,
            mApiKey);

  }

  /**
   * Expose the LiveData Projects query so the UI can observe it.
   */
  public LiveData<NearbyPlaces> getNearbyPlacesListObservable() {
    return nearbyPlacesListObservable;
  }
}

