package com.example.gregorio.capstone;

import static com.example.gregorio.capstone.FavouritesFragment.WIDGET_INTENT_TAG;
import static widget.FavouriteWidgetProvider.INTENT_TO_FAVOURITE_LIST_KEY;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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
import android.widget.TextView;
import android.widget.Toast;
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
import pojos.User;
import pojosplaceid.Result;

public class MainActivity extends AppCompatActivity implements
    NavigationView.OnNavigationItemSelectedListener,
    MapFragment.OnFragmentInteractionListener,
    DetailFragment.OnFragmentInteractionListener,
    FavouritesFragment.OnFavouritesFragmentInteractionListener,
    OnFragmentInteractionListener {

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
  private final static int RC_SIGN_IN = 1;
  public static final String ANONYMOUS = "anonymous";
  public static final String UNKNOWN = "unknown";
  public final static String PLACE_PICKER_PLACE_ID_TAG = "PLACE PICKER PLACE ID";
  public final static String FIREBASE_CHILD_NODE_TAG = "Firebase Child Node Tag";

  private Fragment mFragment;
  private DrawerLayout.SimpleDrawerListener drawerListener;
  private Runnable runnable;
  private ActionBarDrawerToggle toggle;

  private FirebaseAuth mFirebaseAuth;
  private FirebaseAuth.AuthStateListener mAuthStateListener;
  private String mUsername;
  private String mUserEmail;
  private String mUserId;
  private TextView tvUserName;
  private TextView tvUserEmail;


  public MainActivity() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mUsername = ANONYMOUS;
    mUserEmail = UNKNOWN;
    NavigationView navigationView = findViewById(R.id.nav_view);
    View headerView = navigationView.getHeaderView(0);
    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setLogo(R.drawable.ic_logo);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    tvUserName = headerView.findViewById(R.id.user_name);
    tvUserEmail = headerView.findViewById(R.id.user_email);
    mFirebaseAuth = FirebaseAuth.getInstance();

    Intent intent = getIntent();
    Bundle extras = intent.getExtras();

    if (savedInstanceState != null) {
      // If the fragment is not null retain the fragment state
      if (mFragment instanceof MapFragment) {
        mFragment = getSupportFragmentManager().getFragment(savedInstanceState, MAP_FRAGMENT_TAG);
      } else if (mFragment instanceof DetailFragment) {
        mFragment = getSupportFragmentManager()
            .getFragment(savedInstanceState, DETAIL_FRAGMENT_TAG);
      } else if (mFragment instanceof FavouritesFragment) {
        mFragment = getSupportFragmentManager()
            .getFragment(savedInstanceState, FAVOURITE_FRAGMENT_TAG);
      } else if (mFragment instanceof FavouriteDetailFragment) {
        mFragment = getSupportFragmentManager()
            .getFragment(savedInstanceState, FAVOURITE_DETAIL_FRAGMENT_TAG);
      } else if (mFragment instanceof SightsFragment) {
        mFragment = getSupportFragmentManager()
            .getFragment(savedInstanceState, SIGHTS_FRAGMENT_TAG);
      } else if (mFragment instanceof MuseumsFragment) {
        mFragment = getSupportFragmentManager()
            .getFragment(savedInstanceState, MUSEUMS_FRAGMENT_TAG);
      } else if (mFragment instanceof FoodFragment) {
        mFragment = getSupportFragmentManager().getFragment(savedInstanceState, FOOD_FRAGMENT_TAG);
      } else if (mFragment instanceof BarsFragment) {
        mFragment = getSupportFragmentManager().getFragment(savedInstanceState, BARS_FRAGMENT_TAG);
      } else if (mFragment instanceof ClubsFragment) {
        mFragment = getSupportFragmentManager().getFragment(savedInstanceState, CLUBS_FRAGMENT_TAG);
      }

    } else if (extras != null) {
      String widgetIntent = (String) extras.get(INTENT_TO_FAVOURITE_LIST_KEY);
      if (widgetIntent != null) {
        FavouritesFragment favouritesFragment = new FavouritesFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, favouritesFragment)
            .addToBackStack(FAVOURITE_FRAGMENT_TAG)
            .commit();
      }
    } else {
      // If the fragment is not null retain the fragment state
      MapFragment mapFragment = new MapFragment();
      FragmentManager fragmentManager = getSupportFragmentManager();
      fragmentManager.beginTransaction().add(R.id.fragment_container, mapFragment)
          .addToBackStack(MAP_FRAGMENT_TAG)
          .commit();
      mapFragment.setRetainInstance(true);

    }



  DrawerLayout drawer = findViewById(R.id.drawer_layout);
  toggle =new

  ActionBarDrawerToggle(
        this,drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    navigationView.setNavigationItemSelectedListener(this);

  // Listener to check when the Drawer is in STATE_IDLE so we can perform UI operation such as
  // fragment replacement.
  drawerListener =new

  SimpleDrawerListener() {
    @Override
    public void onDrawerStateChanged ( int newState){
      super.onDrawerStateChanged(newState);
      if (runnable != null && newState == DrawerLayout.STATE_IDLE) {
        runnable.run();
        runnable = null;
      }
    }
  }

  ;
    drawer.addDrawerListener(drawerListener);

  // Firebase Authentication
  mAuthStateListener =firebaseAuth ->

  {
    FirebaseUser user = firebaseAuth.getCurrentUser();
    if (user != null) {
      // User is signed in
      onSignedInInitialize(user.getDisplayName(), user.getEmail(), user.getUid());
    } else {
      onSignedOutCleanup();
      // Choose authentication providers
      List<IdpConfig> providers = Arrays.asList(
          new IdpConfig.EmailBuilder().build(),
          new IdpConfig.GoogleBuilder().build());

      // User is signed out
      startActivityForResult(
          AuthUI.getInstance()
              .createSignInIntentBuilder()
              .setIsSmartLockEnabled(false)
              .setAvailableProviders(providers)
              .build(),
          RC_SIGN_IN);
    }
  }

  ;

  // Check wheter or not a use is already present in the db before adding it.
  DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");
    if(mUserId !=null)

  {
    mDatabase.child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()) {
          // if the user exist do nothing

        } else {
          // if the user doesn't exist add it to the db
          User user = new User(mUsername, mUserEmail, mUserId);
          mDatabase.child(mUserId).setValue(user);
        }

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }


}

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_SIGN_IN) {
      if (resultCode == RESULT_OK) {
        // Sign-in succeeded, set up the UI
        Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
      } else if (resultCode == RESULT_CANCELED) {
        // Sign in was canceled by the user, finish the activity
        Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
        finish();
      }
    }
  }


  @Override
  protected void onPause() {
    super.onPause();
    if (mAuthStateListener != null) {
      mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    mFirebaseAuth.addAuthStateListener(mAuthStateListener);

  }

  private void onSignedInInitialize(String username, String userEmail, String userID) {
    mUsername = username;
    mUserEmail = userEmail;
    mUserId = userID;
    tvUserName.setText(mUsername);
    tvUserEmail.setText(mUserEmail);
  }

  private void onSignedOutCleanup() {
    mUsername = ANONYMOUS;
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
      getSupportFragmentManager().putFragment(outState, MAP_FRAGMENT_TAG, mFragment);
    } else if (fragment instanceof DetailFragment) {
      getSupportFragmentManager().putFragment(outState, DETAIL_FRAGMENT_TAG, mFragment);
    } else if (fragment instanceof FavouritesFragment) {
      getSupportFragmentManager().putFragment(outState, FAVOURITE_FRAGMENT_TAG, mFragment);
    } else if (fragment instanceof FavouriteDetailFragment) {
      getSupportFragmentManager().putFragment(outState, FAVOURITE_DETAIL_FRAGMENT_TAG, mFragment);
    } else if (fragment instanceof SightsFragment) {
      getSupportFragmentManager().putFragment(outState, SIGHTS_FRAGMENT_TAG, mFragment);
    } else if (fragment instanceof MuseumsFragment) {
      getSupportFragmentManager().putFragment(outState, MUSEUMS_FRAGMENT_TAG, mFragment);
    } else if (fragment instanceof FoodFragment) {
      getSupportFragmentManager().putFragment(outState, FOOD_FRAGMENT_TAG, mFragment);
    } else if (fragment instanceof BarsFragment) {
      getSupportFragmentManager().putFragment(outState, BARS_FRAGMENT_TAG, mFragment);
    } else if (fragment instanceof ClubsFragment) {
      getSupportFragmentManager().putFragment(outState, CLUBS_FRAGMENT_TAG, mFragment);
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
    FragmentManager fragmentManager = getSupportFragmentManager();
//    FragmentTransaction ft = fragmentManager.beginTransaction();
//    ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
//        R.anim.enter_from_right, R.anim.exit_to_right);

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
        runWhenIdle(() -> {
          AuthUI.getInstance()
              .signOut(this);
        });
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


  @Override
  public void onFragmentInteraction(Uri uri) {

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
