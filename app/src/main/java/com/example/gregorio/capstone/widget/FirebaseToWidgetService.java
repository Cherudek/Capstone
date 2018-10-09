package com.example.gregorio.capstone.widget;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.gregorio.capstone.model.placeId.Result;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseToWidgetService extends IntentService {

  private static final String LOG_TAG = FirebaseToWidgetService.class.getSimpleName();
  private static final String FIREBASE_ROOT_NODE = "checkouts";
  private static final String FIREBASE_FAVOURITES_NODE = "Favourites";
  private List<Result> resultList;

  public FirebaseToWidgetService() {
    super("FirebaseToWidgetService");
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {
    buildUpdate();
    new ListRemoteViewFactory(getApplicationContext(), resultList);
  }

  public List<Result> buildUpdate() {
    // Connect to Data Source
    DatabaseReference favouriteDbRef = FirebaseDatabase.getInstance().getReference()
        .child(FIREBASE_ROOT_NODE);
    favouriteDbRef.child(FIREBASE_FAVOURITES_NODE).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        resultList = new ArrayList<>();
        Log.i(LOG_TAG, "DataSnapshot = " + dataSnapshot.getValue(Result.class));
        for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
          String key = locationSnapshot.getKey();
          Result result = locationSnapshot.getValue(Result.class);
          result.setFavourite_node_key(key);
          Log.d(LOG_TAG, "FireBase Location key: " + key);
          resultList.add(result);
          Log.d(LOG_TAG, "Result List: " + resultList);
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {
      }
    });
    return resultList;
  }
}
