package com.example.gregorio.capstone;

import static com.example.gregorio.capstone.FavouritesFragment.WIDGET_INTENT_TAG;
import static widget.FavouriteWidgetProvider.INTENT_TO_FAVOURITE_LIST_KEY;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.Marker;
import pojosplaceid.Result;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
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


  public final static String PLACE_PICKER_WEBSITE_TAG = "PLACE PICKER WEB URL";
  public final static String PLACE_PICKER_NAME_TAG = "PLACE PICKER NAME";
  public final static String PLACE_PICKER_ADDRESS_TAG = "PLACE PICKER ADDRESS";
  public final static String PLACE_PICKER_TELEPHONE_TAG = "PLACE PICKER TELEPHONE";
  public final static String PLACE_PICKER_OPEN_NOW_TAG = "PLACE PICKER OPEN NOW";
  public final static String PLACE_PICKER_OPENING_HOURS_TAG = "PLACE PICKER OPENING HOURS";
  public final static String PLACE_PICKER_PHOTO_REFERENCE_TAG = "PLACE PICKER PHOTO REFERENCE";
  public final static String PLACE_PICKER_REVIEWS_TAG = "PLACE PICKER REVIEWS";
  public final static String PLACE_PICKER_PHOTO_GALLERY_TAG = "PLACE PICKER PHOTO GALLERY";
  public final static String PLACE_PICKER_PLACE_ID_TAG = "PLACE PICKER PLACE ID";
  public final static String FIREBASE_CHILD_NODE_TAG = "Firebase Child Node Tag";


  private Fragment mFragment;
  private DetailFragment detailFragment;
  private FavouriteDetailFragment favouriteDetailFragment;
  private FavouritesFragment favouritesFragment;
  private Boolean widget;

  public MainActivity() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setLogo(R.drawable.ic_logo);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);


    Intent intent = getIntent();
    Bundle extras = intent.getExtras();


    if(savedInstanceState!=null){
      // If the fragment is not null retain the fragment state
      if(mFragment instanceof MapFragment){
        mFragment = getSupportFragmentManager().getFragment(savedInstanceState, MAP_FRAGMENT_TAG);
      } else if (mFragment instanceof DetailFragment){
        mFragment = getSupportFragmentManager().getFragment(savedInstanceState, DETAIL_FRAGMENT_TAG);
      } else if (mFragment instanceof FavouritesFragment){
        mFragment = getSupportFragmentManager().getFragment(savedInstanceState, FAVOURITE_FRAGMENT_TAG);
      } else if (mFragment instanceof FavouriteDetailFragment){
        mFragment = getSupportFragmentManager().getFragment(savedInstanceState, FAVOURITE_DETAIL_FRAGMENT_TAG);
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

    } else {
      if (extras == null) {
        // If the fragment is not null retain the fragment state
        MapFragment mapFragment = new MapFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.fragment_container, mapFragment)
            .addToBackStack(MAP_FRAGMENT_TAG)
            .commit();
        mapFragment.setRetainInstance(true);
      } else {
        String widgetIntent = (String) extras.get(INTENT_TO_FAVOURITE_LIST_KEY);
        if (widgetIntent.matches("Favourite")) {
          FavouritesFragment favouritesFragment = new FavouritesFragment();
          FragmentManager fragmentManager = getSupportFragmentManager();
          fragmentManager.beginTransaction().replace(R.id.fragment_container, favouritesFragment)
              .addToBackStack(FAVOURITE_FRAGMENT_TAG)
              .commit();
        }
      }
    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

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
    if(fragment instanceof MapFragment){
      getSupportFragmentManager().putFragment(outState, MAP_FRAGMENT_TAG, mFragment);
    } else if(fragment instanceof DetailFragment) {
      getSupportFragmentManager().putFragment(outState, DETAIL_FRAGMENT_TAG, mFragment);
    } else if (fragment instanceof FavouritesFragment){
      getSupportFragmentManager().putFragment(outState, FAVOURITE_FRAGMENT_TAG, mFragment);
    } else if (fragment instanceof FavouriteDetailFragment){
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
    int id = item.getItemId();
    if (id == R.id.nav_map) {
      MapFragment mapFragment = new MapFragment();
      fragmentManager.beginTransaction().replace(R.id.fragment_container, mapFragment)
          .addToBackStack(MAP_FRAGMENT_TAG)
          .commit();
      mapFragment.setRetainInstance(true);
    } else if (id == R.id.nav_food) {
      FoodFragment foodFragment = new FoodFragment();
      fragmentManager.beginTransaction().replace(R.id.fragment_container, foodFragment)
          .addToBackStack(FOOD_FRAGMENT_TAG)
          .commit();
    } else if (id == R.id.nav_bars) {
      BarsFragment barsFragment = new BarsFragment();
      fragmentManager.beginTransaction().replace(R.id.fragment_container, barsFragment)
          .addToBackStack(BARS_FRAGMENT_TAG)
          .commit();
    } else if (id == R.id.nav_clubs) {
      ClubsFragment clubsFragment = new ClubsFragment();
      fragmentManager.beginTransaction().replace(R.id.fragment_container, clubsFragment)
          .addToBackStack(CLUBS_FRAGMENT_TAG)
          .commit();
    } else if (id == R.id.nav_favourites) {
      FavouritesFragment favouritesFragment = new FavouritesFragment();
      fragmentManager.beginTransaction().replace(R.id.fragment_container, favouritesFragment)
          .addToBackStack(FAVOURITE_FRAGMENT_TAG)
          .commit();
    } else if (id == R.id.nav_sights) {
      SightsFragment sightsFragment = new SightsFragment();
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.fragment_container, sightsFragment)
          .addToBackStack(SIGHTS_FRAGMENT_TAG)
          .commit();
    } else if (id == R.id.nav_museums) {
      MuseumsFragment museumsFragment = new MuseumsFragment();
      getSupportFragmentManager().beginTransaction()
          .replace(R.id.fragment_container, museumsFragment)
          .addToBackStack(MUSEUMS_FRAGMENT_TAG)
          .commit();
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
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    // Replace whatever is in the fragment_container view with this fragment,
    // and add the transaction to the back stack so the user can navigate back
    transaction.replace(R.id.fragment_container, detailFragment);
    transaction.addToBackStack(DETAIL_FRAGMENT_TAG);
    // Commit the transaction
    transaction.commit();
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
    transaction.replace(R.id.fragment_container, detailFragment);
    transaction.addToBackStack(DETAIL_FRAGMENT_TAG);
    transaction.commit();
  }
}
