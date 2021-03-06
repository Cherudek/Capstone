package com.example.gregorio.capstone.ui;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.gregorio.capstone.R;
import com.example.gregorio.capstone.adapters.FavouritesAdapter;
import com.example.gregorio.capstone.adapters.FavouritesAdapter.FavouriteViewHolder;
import com.example.gregorio.capstone.application.LostInTurin;
import com.example.gregorio.capstone.model.placeId.Result;
import com.example.gregorio.capstone.utils.RecyclerItemTouchHelper;
import com.example.gregorio.capstone.viewmodels.FavouriteDetailSharedViewModel;
import com.example.gregorio.capstone.widget.FavouriteWidgetProvider;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.example.gregorio.capstone.ui.MainActivity.RC_SIGN_IN;

public class FavouritesFragment extends Fragment implements
        FavouritesAdapter.OnClickHandler,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    public static final String WIDGET_INTENT_TAG = "Favourite List";
    private static final String LOG_TAG = FavouritesFragment.class.getSimpleName();
    private static final String FIREBASE_FAVOURITES_NODE = "Favourites";
    private static final String FIREBASE_USERS_NODE = "users";
    @BindView(R.id.favourites_constraint_layout)
    ConstraintLayout constraintLayout;
    @BindView(R.id.favourites_rv)
    RecyclerView favouritesRecyclerView;
    @BindView(R.id.lottie_loading)
    LottieAnimationView progressBar;
    @BindView(R.id.empty_favourites)
    LottieAnimationView emptyFavourites;
    @BindView(R.id.add_some_favourites)
    TextView addSomeFavorites;
    private FavouriteDetailSharedViewModel sharedModel;
    private FavouritesAdapter favouritesAdapter;
    private DatabaseReference favouriteDbRef;
    private List<Result> resultList;
    private OnFavouritesFragmentInteractionListener listener;
    private Context context;
    private FirebaseAuth firebaseAuth;
    private String userID;
    private String apiKey;

    public FavouritesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedModel = ViewModelProviders.of(getActivity()).get(FavouriteDetailSharedViewModel.class);
        context = getContext();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);
        apiKey = getContext().getResources().getString(R.string.google_api_key);
        ButterKnife.bind(this, rootView);
        checkCurrentUser();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayoutManager favouritesLayoutManager = new LinearLayoutManager(getContext());
        favouritesRecyclerView.setLayoutManager(favouritesLayoutManager);
        favouritesRecyclerView.setHasFixedSize(true);
        favouritesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        if (userID != null) {
            loadFavouritesDb();
        } else {
            Snackbar.make(getView(), getString(R.string.sign_in_to_add_and_read_your_favourite_places),
                    Snackbar.LENGTH_LONG)
                    .show();
            final Handler handler = new Handler();
            handler.postDelayed(() -> {
                // Sign In  after 3s = 3000ms
                signIn();
            }, 3000);
        }
        ItemTouchHelper.SimpleCallback swipeToDeleteCallback = new SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NotNull RecyclerView recyclerView, @NotNull ViewHolder viewHolder, @NotNull ViewHolder target) {
                return false;
            }


            @Override
            public void onSwiped(@NotNull ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter
                if (viewHolder instanceof FavouriteViewHolder) {
                    // get the removed item name to display it in snack bar
                    String name = resultList.get(viewHolder.getAdapterPosition()).getName();
                    // backup of removed item for undo purpose
                    final Result deletedItem = resultList.get(viewHolder.getAdapterPosition());
                    final String firebaseChildKey = deletedItem.getFavourite_node_key();
                    final int deletedIndex = viewHolder.getAdapterPosition();
                    // remove the item from recycler view
                    favouritesAdapter.removeItem(viewHolder.getAdapterPosition());
                    // remove item from the firebase db
                    favouriteDbRef.child(userID)
                            .child(FIREBASE_FAVOURITES_NODE).child(firebaseChildKey).removeValue();
                    // showing snack bar with Undo option
                    Snackbar snackbar = Snackbar
                            .make(Objects.requireNonNull(getView()), name + " " + getString(R.string.removed_from_favourites2),
                                    Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", view -> {

                        // undo is selected, restore the deleted item on the adapter and back on the firebase
                        favouritesAdapter.restoreItem(deletedItem, deletedIndex);
                        DatabaseReference pushedPostRef = favouriteDbRef
                                .child(userID)
                                .child(FIREBASE_FAVOURITES_NODE).push();
                        pushedPostRef.setValue(deletedItem);

                        // Progress Bar And Empty Favourite Animation
                        emptyFavourites.setVisibility(View.GONE);
                        addSomeFavorites.setVisibility(View.GONE);

                    });
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                }
            }

            @Override
            public void onChildDraw(@NotNull Canvas c, @NotNull RecyclerView recyclerView, @NotNull ViewHolder viewHolder, float dX,
                                    float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        // attaching the touch helper to recycler view
        new ItemTouchHelper(swipeToDeleteCallback).attachToRecyclerView(favouritesRecyclerView);
    }

    private void checkCurrentUser() {
        Optional<FirebaseUser> user = Optional.ofNullable(firebaseAuth.getCurrentUser());
        user.map(s -> userID = user.get().getUid());
    }

    private void loadFavouritesDb() {
        favouriteDbRef = FirebaseDatabase.getInstance().getReference().child(FIREBASE_USERS_NODE);
        int dbSize = Objects.requireNonNull(favouriteDbRef.getRoot()
                .child(userID)
                .child(FIREBASE_FAVOURITES_NODE)
                .getKey()).length();
        favouritesAdapter = new FavouritesAdapter(this, dbSize, apiKey);
        favouriteDbRef.child(userID).child(FIREBASE_FAVOURITES_NODE)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        updateFavouritesRecyclerView(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.i(LOG_TAG, "Firebase Database Error: " + databaseError.getMessage());
                    }
                });
    }

    private void updateFavouritesRecyclerView(DataSnapshot dataSnapshot) {
        resultList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        Log.d(LOG_TAG, "DataSnapshot = " + dataSnapshot.getValue(Result.class));
        for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
            String key = locationSnapshot.getKey();
            Result result = locationSnapshot.getValue(Result.class);
            if (result != null) {
                result.setFavourite_node_key(key);
            }
            Log.d(LOG_TAG, "Firebase Location key: " + key);
            resultList.add(result);
        }
        favouritesAdapter.addAll(resultList);
        favouritesRecyclerView.setAdapter(favouritesAdapter);
        // Progress Bar And Empty Favourite Animation
        if (resultList.size() >= 1) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            emptyFavourites.setVisibility(View.VISIBLE);
            addSomeFavorites.setVisibility(View.VISIBLE);
        }
        if (context != null) {
            updateWidget();
        }
    }


    private void signIn() {
        // Choose authentication providers
        List<IdpConfig> providers = Arrays.asList(
                new IdpConfig.EmailBuilder().build(),
                new IdpConfig.FacebookBuilder().build());
        // User is signed out
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setLogo(R.drawable.ic_logo)
                        .setTheme(R.style.AppTheme)
                        .setIsSmartLockEnabled(false)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Snackbar snackbar = Snackbar
                        .make(getView(), R.string.signedin_to_favourite, Snackbar.LENGTH_LONG);
                snackbar.show();
                loadFavouritesDb();

            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Snackbar snackbar = Snackbar
                        .make(getView(), R.string.signin_cancelled, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    @Override
    public void onSwiped(ViewHolder viewHolder, int direction, int position) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OnFavouritesFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + context.getResources().getString(R.string.must_implement_on_frag_list));
        }
    }

    private void onFavouritePressedIntent(Result result, View view) {
        if (listener != null) {
            listener.onFavouritesFragmentInteraction(result, view);
        }
    }

    // Intent to launch the favorite detail fragment
    @Override
    public void onClick(Result result, View view) {
        sharedModel.select(result);
        FavouritesFragment.this.onFavouritePressedIntent(result, view);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void updateWidget() {
        // Method that fetches a list of favourites
        ArrayList<String> favourites = getFavouritesNames();
        Log.i(LOG_TAG, "getFavouritesNames: " + favourites);
        // Intent to pass recipe data (ingredient list) to the Widget Layout
        Intent widgetIntent = new Intent(context, FavouriteWidgetProvider.class);
        widgetIntent.putExtra(WIDGET_INTENT_TAG, favourites);
        widgetIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(new ComponentName(context, FavouriteWidgetProvider.class));
        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(widgetIntent);
    }

    private ArrayList<String> getFavouritesNames() {
        ArrayList<String> strings = new ArrayList<>();
        for (int x = 0; x < resultList.size(); x++) {
            Result result = resultList.get(x);
            String name = result.getName();
            strings.add(name);
        }
        return strings;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LostInTurin.getRefWatcher(context).watch(this);
    }

    public interface OnFavouritesFragmentInteractionListener {
        void onFavouritesFragmentInteraction(Result result, View view);
    }
}
