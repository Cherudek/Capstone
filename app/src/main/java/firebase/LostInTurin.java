package firebase;

import android.app.Application;
import android.content.Context;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class LostInTurin extends Application {

  private static final String FIREBASE_URL = "https://turin-guide-1526861835739.firebaseio.com/";
  private static final String FIREBASE_ROOT_NODE = "checkouts";
  private static final String FIREBASE_FAVOURITE_CHILD_NODE_FAVOURITES = "Favourites";
  private static final String FIREBASE_FAVOURITE_CHILD_NODE_SIGHTS = "sights";
  private static final String FIREBASE_FAVOURITE_CHILD_NODE_MUSEUM = "museums";
  private static final String FIREBASE_FAVOURITE_CHILD_NODE_FOOD = "food";
  private static final String FIREBASE_FAVOURITE_CHILD_NODE_NIGHTLIFE = "nightlife";
  private static final String FIREBASE_FAVOURITE_CHILD_NODE_DRINKS = "drinks";

  private RefWatcher refWatcher;

  public static RefWatcher getRefWatcher(Context context) {
    LostInTurin application = (LostInTurin) context.getApplicationContext();
    return application.refWatcher;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return;
    }
    refWatcher = LeakCanary.install(this);
    // Normal app init code...
    // Enable disk persistence only once at the start of the app
    // and before any other Firebase instances
    FirebaseApp.initializeApp(this);
    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    DatabaseReference favouritesRef = FirebaseDatabase.getInstance()
        .getReference(FIREBASE_ROOT_NODE);
    favouritesRef.keepSynced(true);
    FacebookSdk.sdkInitialize(getApplicationContext());
    AppEventsLogger.activateApp(this);
  }
}
