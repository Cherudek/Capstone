package firebase;

import android.app.Application;
import com.example.gregorio.capstone.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LostInTurin extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Enable disk persistence only once at the start of the app
    // and before any other Firebase instances
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    String favourite = getString(R.string.nv_favourites);

    DatabaseReference favouritesRef = FirebaseDatabase.getInstance().getReference(favourite);
    favouritesRef.keepSynced(true);

  }
}
