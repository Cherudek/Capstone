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
import com.example.gregorio.capstone.adapters.AdapterOnClickHandler;
import com.example.gregorio.capstone.adapters.BarsAdapter;
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

public class BarsFragment extends Fragment implements AdapterOnClickHandler {

    private static final String LOG_TAG = BarsFragment.class.getSimpleName();
    private static final String FIREBASE_ROOT_NODE = "checkouts";
    private static final String FIREBASE_ROOT_NODE_BARS = "drinks";
    @BindView(R.id.bars_rv)
    RecyclerView rvBars;
    @BindView(R.id.bars_constraint_layout)
    ConstraintLayout constraintLayout;
    private BarsAdapter adapter;
    private DatabaseReference dbRef;
    private List<Result> barsList;
    private OnFragmentInteractionListener listener;

    public BarsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_bars, container, false);
        ButterKnife.bind(this, rootView);
        String apiKey = getContext().getResources().getString(R.string.google_api_key);
        dbRef = FirebaseDatabase.getInstance().getReference().child(FIREBASE_ROOT_NODE);
        adapter = new BarsAdapter(this::onClick, apiKey);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvBars.setLayoutManager(layoutManager);
        rvBars.setHasFixedSize(true);
        rvBars.setItemAnimator(new DefaultItemAnimator());
        dbRef.child(FIREBASE_ROOT_NODE_BARS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(LOG_TAG, "DataSnapshot = " + dataSnapshot.getValue(Result.class));
                barsList = new ArrayList<>();
                for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                    String key = locationSnapshot.getKey();
                    Result result = locationSnapshot.getValue(Result.class);
                    result.setFavourite_node_key(key);
                    Log.d(LOG_TAG, "Firebase Location key: " + key);
                    barsList.add(result);
                }
                adapter.addAll(barsList);
                rvBars.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onBarsPressedIntent(Result result) {
        if (listener != null) {
            listener.onFragmentInteraction(result);
        }
    }

    // Intent to launch the detail fragment
    @Override
    public void onClick(Result result) {
        BarsFragment.this.onBarsPressedIntent(result);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            listener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + context.getResources().getString(R.string.must_implement_on_frag_list));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}

