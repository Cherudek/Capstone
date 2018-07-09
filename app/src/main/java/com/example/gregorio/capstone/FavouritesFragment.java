package com.example.gregorio.capstone;

import adapters.FavouritesAdapter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class FavouritesFragment extends Fragment {

  private static final String LOG_TAG = FavouritesFragment.class.getSimpleName();

  @BindView(R.id.favourites_rv)RecyclerView rvFavourites;
  private LinearLayoutManager favouritesLayoutManager;
  private FavouritesAdapter favouritesAdapter;
  private int favouriteSize;
  private String apiKey;
  private DatabaseReference scoresRef;
  private List<Result> mResultList;

  private OnFragmentInteractionListener mListener;

  public FavouritesFragment() {

  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
    apiKey = getContext().getResources().getString(R.string.google_api_key);
    ButterKnife.bind(this, rootView);
    String favourite = getString(R.string.nv_favourites);
    scoresRef = FirebaseDatabase.getInstance().getReference(favourite);
    return rootView;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    favouritesLayoutManager = new LinearLayoutManager(getContext());
    rvFavourites.setLayoutManager(favouritesLayoutManager);
    rvFavourites.setHasFixedSize(true);

    scoresRef.getRoot().child("checkouts").child("Favourites").addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        int dbSize = scoresRef.getRoot().child("checkouts").child("Favourites").getKey().length();
        Log.i(LOG_TAG, "dbSize size = " + dbSize);
        favouritesAdapter = new FavouritesAdapter(dbSize, apiKey);
        mResultList = new ArrayList<>();
        Log.i(LOG_TAG, "DataSnapshot = " + dataSnapshot.getValue(Result.class));
        for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
          Result result = locationSnapshot.getValue(Result.class);
          Log.d(LOG_TAG, "location: " + result);
          mResultList.add(result);
        }
        favouritesAdapter.addAll(mResultList);
        rvFavourites.setAdapter(favouritesAdapter);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });



    Log.i(LOG_TAG, "OffLine Db = " + scoresRef.getRoot().child("checkouts").child("Favourites").getKey().length());
    Log.i(LOG_TAG, "OffLine Db = " + scoresRef.getRoot().child("checkouts").child("Favourites").getKey());



  }

  public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    void onFragmentInteraction(Uri uri);
  }
}
