package com.example.gregorio.capstone.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gregorio.capstone.R;
import com.example.gregorio.capstone.adapters.FoodAdapter;
import com.example.gregorio.capstone.model.placeId.Result;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FoodFragment extends Fragment implements FoodAdapter.AdapterOnClickHandler {

    private static final String LOG_TAG = FoodFragment.class.getSimpleName();
    private static final String FIREBASE_ROOT_NODE = "checkouts";
    private static final String FIREBASE_ROOT_NODE_FOOD = "food";
    @BindView(R.id.food_rv)
    RecyclerView rvFood;
    @BindView(R.id.food_constraint_layout)
    ConstraintLayout constraintLayout;
    private FoodAdapter adapter;
    private DatabaseReference dbRef;
    private List<Result> mFoodList;
    private OnFragmentInteractionListener mListener;

    public FoodFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_food, container, false);
        ButterKnife.bind(this, rootView);
        String apiKey = getContext().getResources().getString(R.string.google_api_key);
        dbRef = FirebaseDatabase.getInstance().getReference().child(FIREBASE_ROOT_NODE);
        adapter = new FoodAdapter(this::onClick, apiKey);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpView();
        checkFoodDb();
    }

    private void setUpView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvFood.setLayoutManager(layoutManager);
        rvFood.setHasFixedSize(true);
        rvFood.setItemAnimator(new DefaultItemAnimator());
    }

    private void checkFoodDb() {
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
                Log.d(LOG_TAG, "Database Error: " + databaseError.getMessage());
            }
        });
    }

    private void onFoodPressedIntent(Result result) {
        if (mListener != null) {
            mListener.onFragmentInteraction(result);
        }
    }

    @Override
    public void onClick(Result result) {
        FoodFragment.this.onFoodPressedIntent(result);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + context.getResources().getString(R.string.must_implement_on_frag_list));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}

