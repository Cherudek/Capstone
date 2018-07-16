package com.example.gregorio.capstone;

import adapters.SightsAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import pojosplaceid.Result;

public class SightsFragment extends Fragment {

  private static final String LOG_TAG = SightsFragment.class.getSimpleName();
  private static final String FIREBASE_ROOT_NODE_SIGHTS = "sights";

  @BindView(R.id.sights_rv)
  RecyclerView rvSights;
  @BindView(R.id.sights_constraint_layout)
  ConstraintLayout constraintLayout;

  private LinearLayoutManager sightsLayoutManager;
  private SightsAdapter favouritesAdapter;
  private DatabaseReference sightsDbRef;
  private List<Result> mSightsList;


  public SightsFragment() {

  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_sights, container, false);
    ButterKnife.bind(this, rootView);
    sightsDbRef = FirebaseDatabase.getInstance().getReference().child(FIREBASE_ROOT_NODE_SIGHTS);

    return rootView;
  }
}
