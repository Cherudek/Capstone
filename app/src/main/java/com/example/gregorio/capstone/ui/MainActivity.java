package com.example.gregorio.capstone.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.SimpleDrawerListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gregorio.capstone.R;
import com.example.gregorio.capstone.model.User;
import com.example.gregorio.capstone.model.placeId.Photo;
import com.example.gregorio.capstone.model.placeId.Result;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import static com.example.gregorio.capstone.ui.FavouritesFragment.WIDGET_INTENT_TAG;
import static com.example.gregorio.capstone.widget.FavouriteWidgetProvider.INTENT_TO_FAVOURITE_LIST_KEY;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        MapFragment.OnFragmentInteractionListener,
        DetailFragment.OnFragmentInteractionListener,
        FavouritesFragment.OnFavouritesFragmentInteractionListener,
        OnFragmentInteractionListener {

    public final static int RC_SIGN_IN = 1;
    public static final String ANONYMOUS = "anonymous guest";
    public static final String UNKNOWN = "Sign in to read your favourite places!";
    public final static String PLACE_PICKER_PLACE_ID_TAG = "PLACE PICKER PLACE ID";
    public final static String PHOTO_REFERENCE_TAG = "Photo Reference Tag";
    private final static String LOG_TAG = MainActivity.class.getSimpleName();
    private final static String MAP_FRAGMENT_TAG = "Map Fragment Tag";
    private final static String DETAIL_FRAGMENT_TAG = "Detail Fragment Tag";
    private final static String FAVOURITE_DETAIL_FRAGMENT_TAG = "Favourite Detail Fragment Tag";
    private final static String FAVOURITE_FRAGMENT_TAG = "Favourite Fragment Tag";
    private final static String SIGHTS_FRAGMENT_TAG = "Sights Fragment Tag";
    private final static String MUSEUMS_FRAGMENT_TAG = "Museums Fragment Tag";
    private final static String FOOD_FRAGMENT_TAG = "Food Fragment Tag";
    private final static String BARS_FRAGMENT_TAG = "Bars Fragment Tag";
    private final static String CLUBS_FRAGMENT_TAG = "Clubs Fragment Tag";
    private final static String PHOTO_FRAGMENT_TAG = "Photo Fragment Tag";
    private Fragment fragment;
    private Runnable runnable;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String name;
    private String email;
    private String id;
    private TextView userName;
    private TextView userEmail;
    private ImageView userImage;
    private FirebaseUser user;
    private FragmentManager fragmentManager;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = ANONYMOUS;
        email = UNKNOWN;
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.ic_logo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        userName = headerView.findViewById(R.id.user_name);
        userEmail = headerView.findViewById(R.id.user_email);
        userImage = headerView.findViewById(R.id.user_image);
        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (savedInstanceState != null) {
            // If the fragment is not null retain the fragment state
            if (fragment instanceof MapFragment) {
                fragment = getSupportFragmentManager().getFragment(savedInstanceState, MAP_FRAGMENT_TAG);
            } else if (fragment instanceof DetailFragment) {
                fragment = getSupportFragmentManager()
                        .getFragment(savedInstanceState, DETAIL_FRAGMENT_TAG);
            } else if (fragment instanceof FavouritesFragment) {
                fragment = getSupportFragmentManager()
                        .getFragment(savedInstanceState, FAVOURITE_FRAGMENT_TAG);
            } else if (fragment instanceof FavouriteDetailFragment) {
                fragment = getSupportFragmentManager()
                        .getFragment(savedInstanceState, FAVOURITE_DETAIL_FRAGMENT_TAG);
            } else if (fragment instanceof SightsFragment) {
                fragment = getSupportFragmentManager()
                        .getFragment(savedInstanceState, SIGHTS_FRAGMENT_TAG);
            } else if (fragment instanceof MuseumsFragment) {
                fragment = getSupportFragmentManager()
                        .getFragment(savedInstanceState, MUSEUMS_FRAGMENT_TAG);
            } else if (fragment instanceof FoodFragment) {
                fragment = getSupportFragmentManager().getFragment(savedInstanceState, FOOD_FRAGMENT_TAG);
            } else if (fragment instanceof BarsFragment) {
                fragment = getSupportFragmentManager().getFragment(savedInstanceState, BARS_FRAGMENT_TAG);
            } else if (fragment instanceof ClubsFragment) {
                fragment = getSupportFragmentManager().getFragment(savedInstanceState, CLUBS_FRAGMENT_TAG);
            }
        } else {
            MapFragment mapFragment = new MapFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().add(R.id.fragment_container, mapFragment)
                    .addToBackStack(MAP_FRAGMENT_TAG)
                    .commit();
            mapFragment.setRetainInstance(true);
        }
        if (extras != null) {
            String widgetIntent = (String) extras.get(INTENT_TO_FAVOURITE_LIST_KEY);
            if (widgetIntent != null) {
                FavouritesFragment favouritesFragment = new FavouritesFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, favouritesFragment)
                        .addToBackStack(FAVOURITE_FRAGMENT_TAG)
                        .commit();
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new
                ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Listener to check when the Drawer is in STATE_IDLE so we can perform UI operation such as
        // fragment replacement.
        SimpleDrawerListener drawerListener = new
                SimpleDrawerListener() {
                    @Override
                    public void onDrawerStateChanged(int newState) {
                        super.onDrawerStateChanged(newState);
                        if (runnable != null && newState == DrawerLayout.STATE_IDLE) {
                            runnable.run();
                            runnable = null;
                        }
                    }
                };
        drawer.addDrawerListener(drawerListener);

        // Firebase Authentication
        authStateListener = firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            if (user != null) {
                id = user.getProviderId();
                Log.d(LOG_TAG, "USer getEncodedQuery: " + user.getPhotoUrl());
                Log.d(LOG_TAG, "User Provider ID: " + id);
                String userUrl = "http://graph.facebook.com/" + id + "/picture?type=large";
                MainActivity.this
                        .onSignedInInitialize(user.getDisplayName(), user.getEmail(), user.getUid(), userUrl);
            } else {
                MainActivity.this.onSignedOutCleanup();
            }
        };

        // Check whether or not a use is already present in the db before adding it.
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
        if (id != null) {
            mDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.drawer_layout), "User Already Signed In: " + user.getDisplayName(), Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else {
                        // if the user doesn't exist add it to the db
                        User user = new User(name, email, id);
                        mDatabase.child(id).setValue(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(LOG_TAG, "Database Error: " + databaseError.getMessage());
                }
            });
        }
    }

    public void SignIn() {
        if (user == null || user.isAnonymous()) {
            // Choose authentication providers
            List<IdpConfig> providers = Arrays.asList(
                    new IdpConfig.EmailBuilder().build(),
                    new IdpConfig.FacebookBuilder().build());
            // User is signed out
            MainActivity.this.startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setLogo(R.drawable.ic_logo)
                            .setTheme(R.style.AppTheme)
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.drawer_layout), "Signed In!", Snackbar.LENGTH_LONG);
                snackbar.show();
                FavouritesFragment favouritesFragment = new FavouritesFragment();
                FragmentTransaction ft1 = fragmentManager.beginTransaction();
                ft1.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                        R.anim.enter_from_right, R.anim.exit_to_right);
                ft1.replace(R.id.fragment_container, favouritesFragment, FAVOURITE_FRAGMENT_TAG)
                        .addToBackStack(FAVOURITE_FRAGMENT_TAG)
                        .commit();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.drawer_layout), "Sign in canceled.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);

    }

    private void onSignedInInitialize(String username, String userEmail, String userID,
                                      String imageUrl) {
        this.name = username;
        this.email = userEmail;
        this.id = userID;
        Log.d(LOG_TAG, "User Id: " + userID);
        this.userName.setText(this.name);
        this.userEmail.setText(this.email);
        Log.d(LOG_TAG, "User Image url: " + imageUrl);
        Glide.with(this).load(imageUrl).into(userImage);
    }

    private void onSignedOutCleanup() {
        name = ANONYMOUS;
        email = UNKNOWN;
    }


    public void runWhenIdle(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String value = (String) extras.get(WIDGET_INTENT_TAG);
            if (value.matches("Favourite")) {
                FavouritesFragment favouritesFragment = new FavouritesFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, favouritesFragment)
                        .addToBackStack(FAVOURITE_FRAGMENT_TAG)
                        .commit();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment instanceof MapFragment) {
            getSupportFragmentManager().putFragment(outState, MAP_FRAGMENT_TAG, this.fragment);
        } else if (fragment instanceof DetailFragment) {
            getSupportFragmentManager().putFragment(outState, DETAIL_FRAGMENT_TAG, this.fragment);
        } else if (fragment instanceof FavouritesFragment) {
            getSupportFragmentManager().putFragment(outState, FAVOURITE_FRAGMENT_TAG, this.fragment);
        } else if (fragment instanceof FavouriteDetailFragment) {
            getSupportFragmentManager().putFragment(outState, FAVOURITE_DETAIL_FRAGMENT_TAG, this.fragment);
        } else if (fragment instanceof SightsFragment) {
            getSupportFragmentManager().putFragment(outState, SIGHTS_FRAGMENT_TAG, this.fragment);
        } else if (fragment instanceof MuseumsFragment) {
            getSupportFragmentManager().putFragment(outState, MUSEUMS_FRAGMENT_TAG, this.fragment);
        } else if (fragment instanceof FoodFragment) {
            getSupportFragmentManager().putFragment(outState, FOOD_FRAGMENT_TAG, this.fragment);
        } else if (fragment instanceof BarsFragment) {
            getSupportFragmentManager().putFragment(outState, BARS_FRAGMENT_TAG, this.fragment);
        } else if (fragment instanceof ClubsFragment) {
            getSupportFragmentManager().putFragment(outState, CLUBS_FRAGMENT_TAG, this.fragment);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        fragmentManager = getSupportFragmentManager();

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_map:
                runWhenIdle(() -> {
                    MapFragment mapFragment = new MapFragment();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                            R.anim.enter_from_right, R.anim.exit_to_right);
                    ft.replace(R.id.fragment_container, mapFragment, MAP_FRAGMENT_TAG)
                            .addToBackStack(MAP_FRAGMENT_TAG)
                            .commit();
                    mapFragment.setRetainInstance(true);
                });
                break;

            case R.id.nav_favourites:
                runWhenIdle(() -> {

                    FavouritesFragment favouritesFragment = new FavouritesFragment();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                            R.anim.enter_from_right, R.anim.exit_to_right);
                    ft.replace(R.id.fragment_container, favouritesFragment, FAVOURITE_FRAGMENT_TAG)
                            .addToBackStack(FAVOURITE_FRAGMENT_TAG)
                            .commit();
                });
                break;

            case R.id.nav_sights:
                runWhenIdle(() -> {

                    SightsFragment sightsFragment = new SightsFragment();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                            R.anim.enter_from_right, R.anim.exit_to_right);
                    ft.replace(R.id.fragment_container, sightsFragment, SIGHTS_FRAGMENT_TAG)
                            .addToBackStack(SIGHTS_FRAGMENT_TAG)
                            .commit();

                });
                break;

            case R.id.nav_museums:
                runWhenIdle(() -> {

                    MuseumsFragment museumsFragment = new MuseumsFragment();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                            R.anim.enter_from_right, R.anim.exit_to_right);
                    ft.replace(R.id.fragment_container, museumsFragment, MUSEUMS_FRAGMENT_TAG)
                            .addToBackStack(MUSEUMS_FRAGMENT_TAG)
                            .commit();

                });
                break;

            case R.id.nav_food:
                runWhenIdle(() -> {

                    FoodFragment foodFragment = new FoodFragment();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                            R.anim.enter_from_right, R.anim.exit_to_right);
                    ft.replace(R.id.fragment_container, foodFragment, FOOD_FRAGMENT_TAG)
                            .addToBackStack(FOOD_FRAGMENT_TAG)
                            .commit();
                });
                break;

            case R.id.nav_bars:
                runWhenIdle(() -> {

                    BarsFragment barsFragment = new BarsFragment();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                            R.anim.enter_from_right, R.anim.exit_to_right);
                    ft.replace(R.id.fragment_container, barsFragment, BARS_FRAGMENT_TAG)
                            .addToBackStack(BARS_FRAGMENT_TAG)
                            .commit();
                });
                break;

            case R.id.nav_clubs:
                runWhenIdle(() -> {

                    ClubsFragment clubsFragment = new ClubsFragment();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                            R.anim.enter_from_right, R.anim.exit_to_right);
                    ft.replace(R.id.fragment_container, clubsFragment, CLUBS_FRAGMENT_TAG)
                            .addToBackStack(CLUBS_FRAGMENT_TAG)
                            .commit();
                });
                break;

            case R.id.sign_out:
                runWhenIdle(() -> AuthUI.getInstance().signOut(this));
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.drawer_layout), "Logged Out", Snackbar.LENGTH_LONG);
                snackbar.show();

                MapFragment mapFragment = new MapFragment();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                        R.anim.enter_from_right, R.anim.exit_to_right);
                ft.replace(R.id.fragment_container, mapFragment, MAP_FRAGMENT_TAG)
                        .addToBackStack(MAP_FRAGMENT_TAG)
                        .commit();
                mapFragment.setRetainInstance(true);
                userEmail.setText(UNKNOWN);
                userName.setText(ANONYMOUS);
                Glide.with(this).load(R.drawable.mole_small_25).into(userImage);
                break;

            case R.id.sign_in:
                runWhenIdle(() -> SignIn());
                break;

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Marker marker) {
        // set DetailFragment Arguments
        DetailFragment detailFragment = new DetailFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Log.i(LOG_TAG, "Marker Tag is: " + marker.getTag());
        transaction
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right,
                        R.anim.exit_to_right);
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.fragment_container, detailFragment);
        transaction.addToBackStack(DETAIL_FRAGMENT_TAG);
        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void OnPlacePickerInteraction(Place place) {
        Bundle bundle = new Bundle();
        String placeId = place.getId();
        bundle.putString(PLACE_PICKER_PLACE_ID_TAG, placeId);
        // set DetailFragment Arguments
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right,
                R.anim.exit_to_right);
        ft.replace(R.id.fragment_container, detailFragment);
        ft.addToBackStack(DETAIL_FRAGMENT_TAG);
        // Commit the transaction
        ft.commit();
    }

    // Detail View Photo Gallery to Large Photo Fragment
    @Override
    public void onFragmentInteraction(Photo photo) {
        Bundle bundle = new Bundle();
        String photoReference = photo.getPhotoReference();
        bundle.putString(PHOTO_REFERENCE_TAG, photoReference);
        PhotoFragment photoFragment = new PhotoFragment();
        photoFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right,
                        R.anim.exit_to_right);
        transaction.replace(R.id.fragment_container, photoFragment);
        transaction.addToBackStack(PHOTO_FRAGMENT_TAG);
        transaction.commit();
    }

    @Override
    public void onFavouritesFragmentInteraction(Result result, View view) {
        // set DetailFragment Arguments
        FavouriteDetailFragment favouritedetailFragment = new FavouriteDetailFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        transaction
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right,
                        R.anim.exit_to_right);
        transaction.replace(R.id.fragment_container, favouritedetailFragment);
        transaction.addToBackStack(FAVOURITE_DETAIL_FRAGMENT_TAG);
        // Commit the transaction
        transaction.commit();
    }

    // TODO: Check Write Settings permissions for Android KitKat Huawei
    private void checkWritePermissions() {


    }

    @Override
    public void onFragmentInteraction(Result result) {
        Bundle bundle = new Bundle();
        String placeId = result.getPlaceId();
        bundle.putString(PLACE_PICKER_PLACE_ID_TAG, placeId);
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right,
                        R.anim.exit_to_right);
        transaction.replace(R.id.fragment_container, detailFragment);
        transaction.addToBackStack(DETAIL_FRAGMENT_TAG);
        transaction.commit();
    }
}
