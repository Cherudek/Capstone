package com.example.gregorio.capstone;

import adapters.SightsAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import pojosplaceid.Result;

public class FoodFragment extends Fragment {

  private static final String LOG_TAG = FoodFragment.class.getSimpleName();
  private static final String FIREBASE_ROOT_NODE = "checkouts";
  private static final String FIREBASE_ROOT_NODE_FOOD = "food";
  @BindView(R.id.food_rv)
  RecyclerView rvFood;
  @BindView(R.id.food_constraint_layout)
  ConstraintLayout constraintLayout;
  private String apiKey;
  private LinearLayoutManager layoutManager;
  private SightsAdapter adapter;
  private DatabaseReference dbRef;
  private List<Result> mFoodList;

  public FoodFragment() {

  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_food, container, false);
    ButterKnife.bind(this, rootView);
    apiKey = getContext().getResources().getString(R.string.google_api_key);
    dbRef = FirebaseDatabase.getInstance().getReference().child(FIREBASE_ROOT_NODE);
    adapter = new SightsAdapter(apiKey);
    return rootView;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    layoutManager = new LinearLayoutManager(getContext());
    rvFood.setLayoutManager(layoutManager);
    rvFood.setHasFixedSize(true);
    rvFood.setItemAnimator(new DefaultItemAnimator());
    rvFood
        .addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    // Firebase Database query to fetch data for the Favorite Adapter
    dbRef.child(FIREBASE_ROOT_NODE_FOOD).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        mFoodList = new ArrayList<>();
        Log.i(LOG_TAG, "DataSnapshot = " + dataSnapshot.getValue(Result.class));
        for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
          String key = locationSnapshot.getKey();
          Result result = locationSnapshot.getValue(Result.class);
          result.setFavourite_node_key(key);
          Log.d(LOG_TAG, "Firebase Location key: " + key);
          mFoodList.add(result);
        }
        adapter.addAll(mFoodList);
        rvFood.setAdapter(adapter);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }
}

