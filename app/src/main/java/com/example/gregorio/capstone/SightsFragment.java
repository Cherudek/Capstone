package com.example.gregorio.capstone;

import adapters.AdapterOnClickHandler;
import adapters.SightsAdapter;
import android.content.Context;
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

public class SightsFragment extends Fragment implements AdapterOnClickHandler {

  private static final String LOG_TAG = SightsFragment.class.getSimpleName();
  private static final String FIREBASE_ROOT_NODE = "checkouts";
  private static final String FIREBASE_ROOT_NODE_SIGHTS = "sights";

  @BindView(R.id.sights_rv)
  RecyclerView rvSights;
  @BindView(R.id.sights_constraint_layout)
  ConstraintLayout constraintLayout;
  private String apiKey;
  private LinearLayoutManager sightsLayoutManager;
  private SightsAdapter sightsAdapter;
  private DatabaseReference sightsDbRef;
  private List<Result> mSightsList;
  private OnFragmentInteractionListener mListener;

  public SightsFragment() {

  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_sights, container, false);
    ButterKnife.bind(this, rootView);
    apiKey = getContext().getResources().getString(R.string.google_api_key);
    sightsDbRef = FirebaseDatabase.getInstance().getReference().child(FIREBASE_ROOT_NODE);
    sightsAdapter = new SightsAdapter(this::onClick, apiKey);

    return rootView;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    sightsLayoutManager = new LinearLayoutManager(getContext());
    rvSights.setLayoutManager(sightsLayoutManager);
    rvSights.setHasFixedSize(true);
    rvSights.setItemAnimator(new DefaultItemAnimator());
    rvSights
        .addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    // Firebase Database query to fetch data for the Favorite Adapter
    sightsDbRef.child(FIREBASE_ROOT_NODE_SIGHTS).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        mSightsList = new ArrayList<>();
        Log.i(LOG_TAG, "DataSnapshot = " + dataSnapshot.getValue(Result.class));
        for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
          String key = locationSnapshot.getKey();
          Result result = locationSnapshot.getValue(Result.class);
          result.setFavourite_node_key(key);
          Log.d(LOG_TAG, "Firebase Location key: " + key);
          mSightsList.add(result);
        }
        sightsAdapter.addAll(mSightsList);
        rvSights.setAdapter(sightsAdapter);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  public void onSightsPressedIntent(Result result) {
    if (mListener != null) {
      mListener.onFragmentInteraction(result);
    }
  }

  // Intent to launch the favorite detail fragment

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    // This makes sure that the host activity has implemented the callback interface
    // If not, it throws an exception
    try {
      mListener = (OnFragmentInteractionListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
          + " must implement OnSightsFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  @Override
  public void onClick(Result result) {
    SightsFragment.this.onSightsPressedIntent(result);
  }

}