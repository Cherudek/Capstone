package firebase;

import android.app.Application;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LostInTurin extends Application {

  private static final String FIREBASE_URL = "https://turin-guide-1526861835739.firebaseio.com/";
  private static final String FIREBASE_ROOT_NODE = "checkouts";
  private static final String FIREBASE_FAVOURITE_CHILD_NODE_FAVOURITES = "Favourites";
  private static final String FIREBASE_FAVOURITE_CHILD_NODE_SIGHTS = "sights";
  private static final String FIREBASE_FAVOURITE_CHILD_NODE_MUSEUM = "museums";
  private static final String FIREBASE_FAVOURITE_CHILD_NODE_FOOD = "food";
  private static final String FIREBASE_FAVOURITE_CHILD_NODE_NIGHTLIFE = "nightlife";
  private static final String FIREBASE_FAVOURITE_CHILD_NODE_DRINKS = "drinks";

  @Override
  public void onCreate() {
    super.onCreate();

    // Enable disk persistence only once at the start of the app
    // and before any other Firebase instances
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    DatabaseReference favouritesRef = FirebaseDatabase.getInstance()
        .getReference(FIREBASE_ROOT_NODE);
    favouritesRef.keepSynced(true);
  }
}
