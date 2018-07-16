package com.example.gregorio.capstone;

import adapters.FavouritesAdapter;
import adapters.FavouritesAdapter.FavouriteViewHolder;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
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
import utils.RecyclerItemTouchHelper;
import viewmodel.FavouriteDetailSharedViewModel;
import viewmodel.FavouritesViewModel;

public class FavouritesFragment extends Fragment implements
    FavouritesAdapter.FavouriteAdapterOnClickHandler,
    RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

  private static final String LOG_TAG = FavouritesFragment.class.getSimpleName();
  private static final String FIREBASE_ROOT_NODE = "checkouts";
  private static final String FIREBASE_FAVOURITES_NODE = "Favourites";
  @BindView(R.id.favourites_constraint_layout)
  ConstraintLayout constraintLayout;
  private FavouriteDetailSharedViewModel sharedModel;

  @BindView(R.id.favourites_rv)RecyclerView rvFavourites;
  private FavouritesViewModel favouritesViewModel;
  private LinearLayoutManager favouritesLayoutManager;
  private FavouritesAdapter favouritesAdapter;
  private String apiKey;
  private DatabaseReference favouriteDbRef;
  private List<Result> mResultList;
  private OnFavouritesFragmentInteractionListener mListener;

  public FavouritesFragment() {
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    sharedModel = ViewModelProviders.of(getActivity()).get(FavouriteDetailSharedViewModel.class);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
    apiKey = getContext().getResources().getString(R.string.google_api_key);
    ButterKnife.bind(this, rootView);
    favouriteDbRef = FirebaseDatabase.getInstance().getReference().child(FIREBASE_ROOT_NODE);
    int dbSize = favouriteDbRef.getRoot().child(FIREBASE_ROOT_NODE).child(FIREBASE_FAVOURITES_NODE)
        .getKey().length();
    favouritesAdapter = new FavouritesAdapter(this, dbSize, apiKey);
    return rootView;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    favouritesLayoutManager = new LinearLayoutManager(getContext());
    rvFavourites.setLayoutManager(favouritesLayoutManager);
    rvFavourites.setHasFixedSize(true);
    rvFavourites.setItemAnimator(new DefaultItemAnimator());
    rvFavourites
        .addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    // Firebase Database query to fetch data for the Favorite Adapter
    favouriteDbRef.child(FIREBASE_FAVOURITES_NODE).addValueEventListener(new ValueEventListener() {
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

    // Swipe to Delete Favourite from recycler View and Firebase db.
    ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new SimpleCallback(0,
        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
      @Override
      public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
        return false;
      }

      /**
       * callback when recycler view is swiped
       * item will be removed on swiped
       * undo option will be provided in snackbar to restore the item
       */
      @Override
      public void onSwiped(ViewHolder viewHolder, int direction) {
        // Row is swiped from recycler view
        // remove it from adapter
        if (viewHolder instanceof FavouriteViewHolder) {
          // get the removed item name to display it in snack bar
          String name = mResultList.get(viewHolder.getAdapterPosition()).getName();
          // backup of removed item for undo purpose
          final Result deletedItem = mResultList.get(viewHolder.getAdapterPosition());
          final String firebaseChildKey = deletedItem.getFavourite_node_key();
          final int deletedIndex = viewHolder.getAdapterPosition();
          // remove the item from recycler view
          favouritesAdapter.removeItem(viewHolder.getAdapterPosition());
          // remove item from the firebase db
          favouriteDbRef.child(FIREBASE_FAVOURITES_NODE).child(firebaseChildKey).removeValue();
          // showing snack bar with Undo option
          Snackbar snackbar = Snackbar
              .make(getView(), name + " removed from favourites!", Snackbar.LENGTH_LONG);
          snackbar.setAction("UNDO", view -> {

            // undo is selected, restore the deleted item on the adapter and back on the firebase
            favouritesAdapter.restoreItem(deletedItem, deletedIndex);
            DatabaseReference pushedPostRef = favouriteDbRef.child(FIREBASE_FAVOURITES_NODE).push();
            pushedPostRef.setValue(deletedItem);
          });
          snackbar.setActionTextColor(Color.YELLOW);
          snackbar.show();
        }
      }

      @Override
      public void onChildDraw(Canvas c, RecyclerView recyclerView, ViewHolder viewHolder, float dX,
          float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
      }
    };
    // attaching the touch helper to recycler view
    new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(rvFavourites);
  }


  @Override
  public void onSwiped(ViewHolder viewHolder, int direction, int position) {
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

  // Intent to launch the favorite detail fragment
  @Override
  public void onClick(Result result) {
    sharedModel.select(result);
    FavouritesFragment.this.onFavouritePressedIntent(result);
  }

  public interface OnFavouritesFragmentInteractionListener {
    void onFavouritesFragmentInteraction(Result result);
  }


  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }
}
