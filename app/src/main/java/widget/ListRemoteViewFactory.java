package widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.example.gregorio.capstone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import pojosplaceid.Result;

public class ListRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

  private static final String LOG_TAG = ListRemoteViewFactory.class.getSimpleName();
  private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
  private static final String FIREBASE_ROOT_NODE = "checkouts";
  private static final String FIREBASE_FAVOURITES_NODE = "Favourites";
  private String mApiKey;
  private Context context;
  private DatabaseReference favouriteDbRef;
  private List<Result> mResultList;


  public ListRemoteViewFactory(Context context, String apiKey) {
    this.context = context;
    this.mApiKey = apiKey;
  }

  public ListRemoteViewFactory(Context context) {
    this.context = context;
  }

  @Override
  public void onCreate() {

  }

  @Override
  public void onDataSetChanged() {

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
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  @Override
  public void onDestroy() {

  }

  @Override
  public int getCount() {
    return mResultList.size();
  }

  @Override
  public RemoteViews getViewAt(int position) {

    if (favouriteDbRef == null || mResultList.size() == 0) {
      return null;
    }
    Result result = mResultList.get(position);
    String photoReference = result.getPhotos().get(0).getPhotoReference();
    String address = result.getVicinity();
    String name = result.getName();
    String placeId = result.getPlaceId();
    String photoUrl =
        PHOTO_PLACE_URL + "maxwidth=100&photoreference=" + photoReference + "&key=" + mApiKey;
    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favourite_widget);
    views.setTextViewText(R.id.appwidget_text, name);

    return views;
  }

  @Override
  public RemoteViews getLoadingView() {
    return null;
  }

  @Override
  public int getViewTypeCount() {
    return 0;
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public boolean hasStableIds() {
    return false;
  }
}
