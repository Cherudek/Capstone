package widget;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import pojosplaceid.Result;

public class FirebaseToWidgetService extends IntentService {

  private static final String LOG_TAG = FirebaseToWidgetService.class.getSimpleName();
  private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
  private static final String FIREBASE_ROOT_NODE = "checkouts";
  private static final String FIREBASE_FAVOURITES_NODE = "Favourites";
  private DatabaseReference favouriteDbRef;
  private List<Result> mResultList;

  public FirebaseToWidgetService() {
    super("FirebaseToWidgetService");
  }


  @Override
  protected void onHandleIntent(@Nullable Intent intent) {

    buildUpdate();
    new ListRemoteViewFactory(getApplicationContext(), mResultList);

  }

  public List<Result> buildUpdate() {
    // Connect to Data Source
    favouriteDbRef = FirebaseDatabase.getInstance().getReference().child(FIREBASE_ROOT_NODE);
    favouriteDbRef.child(FIREBASE_FAVOURITES_NODE).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        mResultList = new ArrayList<>();
        Log.i(LOG_TAG, "DataSnapshot = " + dataSnapshot.getValue(Result.class));
        for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
          String key = locationSnapshot.getKey();
          Result result = locationSnapshot.getValue(Result.class);
          result.setFavourite_node_key(key);
          Log.d(LOG_TAG, "FireBase Location key: " + key);
          mResultList.add(result);
          Log.d(LOG_TAG, "Result List: " + mResultList);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });

    return mResultList;
  }


}
