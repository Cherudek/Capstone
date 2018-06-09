package viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import java.util.List;
import javax.inject.Inject;
import pojos.NearbyPlaces;
import repository.NearbyPlacesRepository;

public class NearbyPlacesListViewModel extends AndroidViewModel {

  private final LiveData<List<NearbyPlaces>> nearbyPlacesListObservable;
  private final int DEFAULT_ZOOM = 1500;
  private String mKeyword;
  private Double mLatitude;
  private Double mLongitude;
  private String mApiKey;
//  private static final LatLng PiazzaCastello = new LatLng(45.0710394, 7.6862986);
//  private Double latitude =  PiazzaCastello.latitude;
//  private Double longitude =  PiazzaCastello.longitude;

  @Inject
  public NearbyPlacesListViewModel(@NonNull NearbyPlacesRepository nearbyPlacesRepository,
      @NonNull Application application) {
    super(application);
    nearbyPlacesListObservable = nearbyPlacesRepository
        .getNearbyPlaces(mKeyword, mLatitude.toString() + "," + mLongitude.toString(), DEFAULT_ZOOM,
            mApiKey);
  }

  public void init(String keyword, Double latitude, Double longitude, String apiKey) {
    this.mKeyword = keyword;
    this.mLatitude = latitude;
    this.mLongitude = longitude;
    this.mApiKey = apiKey;
  }

  /**
   * Expose the LiveData Projects query so the UI can observe it.
   */
  public LiveData<List<NearbyPlaces>> getNearbyPlacesListObservable() {
    return nearbyPlacesListObservable;
  }
}
