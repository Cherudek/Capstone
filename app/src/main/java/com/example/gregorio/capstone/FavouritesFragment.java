package com.example.gregorio.capstone;

import adapters.FavouritesAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import pojosplaceid.Result;
import viewmodel.FavouriteDetailSharedViewModel;

public class FavouritesFragment extends Fragment implements FavouritesAdapter.FavouriteAdapterOnClickHandler {

  private static final String LOG_TAG = FavouritesFragment.class.getSimpleName();
  private FavouriteDetailSharedViewModel model;

  @BindView(R.id.favourites_rv)RecyclerView rvFavourites;
  private LinearLayoutManager favouritesLayoutManager;
  private FavouritesAdapter favouritesAdapter;
  private int favouriteSize;
  private String apiKey;
  private DatabaseReference scoresRef;
  private List<Result> mResultList;
  private OnFavouritesFragmentInteractionListener mListener;


  public FavouritesFragment() {

  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    model = ViewModelProviders.of(getActivity()).get(FavouriteDetailSharedViewModel.class);

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
    int dbSize = scoresRef.getRoot().child("checkouts").child("Favourites").getKey().length();
    favouritesAdapter = new FavouritesAdapter(this, dbSize, apiKey);
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
        mResultList = new ArrayList<>();
        Log.i(LOG_TAG, "DataSnapshot = " + dataSnapshot.getValue(Result.class));
        for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
          String key = locationSnapshot.getKey();
          Result result = locationSnapshot.getValue(Result.class);
          result.setFavourite_node_key(key);
          Log.d(LOG_TAG, "Firebase Location key: " + key);
          mResultList.add(result);
        }
        favouritesAdapter.addAll(mResultList);
        rvFavourites.setAdapter(favouritesAdapter);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    // This makes sure that the host activity has implemented the callback interface
    // If not, it throws an exception
    try {
      mListener = (OnFavouritesFragmentInteractionListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
          + " must implement OnFavouritesFragmentInteractionListener");
    }
  }

  public void onFavouritePressedIntent(Result result) {
    if (mListener != null) {
      mListener.onFavouritesFragmentInteraction(result);
    }
  }

  @Override
  public void onClick(Result result) {
    Toast.makeText(getContext(),"The ResultId Name Clicked is: " + result.getName(),
        Toast.LENGTH_SHORT).show();
    model.select(result);
    FavouritesFragment.this.onFavouritePressedIntent(result);
  }


  public interface OnFavouritesFragmentInteractionListener {
    // TODO: Update argument type and name
    void onFavouritesFragmentInteraction(Result result);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }
}
