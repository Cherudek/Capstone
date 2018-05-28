package viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import firebase.FirebaseQueryLiveData;

public class PlacesViewModel extends ViewModel {

  private static final DatabaseReference PLACES_REF =
      FirebaseDatabase.getInstance().getReference("/places");

  private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(PLACES_REF);

  @NonNull
  public LiveData<DataSnapshot> getDataSnapshotLiveData() {
    return liveData;
  }

}
