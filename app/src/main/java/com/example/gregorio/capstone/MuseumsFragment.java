package com.example.gregorio.capstone;

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

import com.example.gregorio.capstone.adapters.AdapterOnClickHandler;
import com.example.gregorio.capstone.adapters.MuseumsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pojosplaceid.Result;

/**
 * A fragment representing a list of Items.
 * <p />
 * Activities containing this fragment MUST implement the {@link }
 * interface.
 */
public class MuseumsFragment extends Fragment implements AdapterOnClickHandler {

  // TODO: Customize parameter argument names
  private static final String ARG_COLUMN_COUNT = "column-count";
  // TODO: Customize parameters
  private static final String LOG_TAG = MuseumsFragment.class.getSimpleName();
  private static final String FIREBASE_ROOT_NODE = "checkouts";
  private static final String FIREBASE_MUSEUMS_NODE = "museums";
  @BindView(R.id.museums_constraint_layout)
  ConstraintLayout constraintLayout;

  @BindView(R.id.museums_rv)
  RecyclerView rvMuseums;
  private MuseumsAdapter adapter;
  private DatabaseReference museumsDbRef;
  private List<Result> mMueseumsList;
  private int mColumnCount = 1;
  private OnFragmentInteractionListener mListener;


  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public MuseumsFragment() {
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_museums, container, false);
    String apiKey = getContext().getResources().getString(R.string.google_api_key);

    ButterKnife.bind(this, view);
    museumsDbRef = FirebaseDatabase.getInstance().getReference().child(FIREBASE_ROOT_NODE);
    int dbSize = museumsDbRef.getRoot().child(FIREBASE_ROOT_NODE).child(FIREBASE_MUSEUMS_NODE)
        .getKey().length();
    adapter = new MuseumsAdapter(this::onClick, apiKey);
    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
    rvMuseums.setLayoutManager(layoutManager);
    rvMuseums.setHasFixedSize(true);
    rvMuseums.setItemAnimator(new DefaultItemAnimator());
    rvMuseums
        .addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    // Firebase Database query to fetch data for the Favorite Adapter
    museumsDbRef.child(FIREBASE_MUSEUMS_NODE).addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        mMueseumsList = new ArrayList<>();
        Log.i(LOG_TAG, "DataSnapshot = " + dataSnapshot.getValue(Result.class));
        for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
          String key = locationSnapshot.getKey();
          Result result = locationSnapshot.getValue(Result.class);
          result.setFavourite_node_key(key);
          Log.d(LOG_TAG, "Firebase Location key: " + key);
          mMueseumsList.add(result);
        }
        adapter.addAll(mMueseumsList);
        rvMuseums.setAdapter(adapter);
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
      mListener = (OnFragmentInteractionListener) context;
    } catch (ClassCastException e) {
      throw new ClassCastException(context.toString()
          + " must implement OnFragmentInteractionListener");
    }
  }

  public void onMuseumsPressedIntent(Result result) {
    if (mListener != null) {
      mListener.onFragmentInteraction(result);
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  @Override
  public void onClick(Result result) {
    MuseumsFragment.this.onMuseumsPressedIntent(result);
  }
}
