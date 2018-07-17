package com.example.gregorio.capstone;

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
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.Marker;
import pojosplaceid.Result;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
    MapFragment.OnFragmentInteractionListener,
    DetailFragment.OnFragmentInteractionListener,
    FavouritesFragment.OnFavouritesFragmentInteractionListener {

  private final static String LOG_TAG = MainActivity.class.getSimpleName();
  private final static String MAP_FRAGMENT_TAG = "Map Fragment Tag";
  private final static String DETAIL_FRAGMENT_TAG = "Detail Fragment Tag";
  private final static String FAVOURITE_DETAIL_FRAGMENT_TAG = "Favourite Detail Fragment Tag";
  private final static String FAVOURITE_FRAGMENT_TAG = "Favourite Fragment Tag";

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


  private MapFragment mapFragment;
  private Fragment mFragment;
  private DetailFragment detailFragment;
  private FavouriteDetailFragment favouriteDetailFragment;
  private FavouritesFragment favouritesFragment;

  public MainActivity() {
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setLogo(R.drawable.ic_logo);
    setSupportActionBar(toolbar);

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
      }
//      //Restore the fragment's instance
//        if(mFragment == null){
//          mFragment = getSupportFragmentManager().getFragment(savedInstanceState, DETAIL_FRAGMENT_TAG);
//        }
    } else {
      // If the fragment is not null retain the fragment state
      mapFragment = new MapFragment();
      FragmentManager fragmentManager = getSupportFragmentManager();
      fragmentManager.beginTransaction().add(R.id.fragment_container, mapFragment)
          .addToBackStack(MAP_FRAGMENT_TAG)
          .commit();
      mapFragment.setRetainInstance(true);
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
    int id = item.getItemId();
    Fragment fragment = null;
    Class fragmentClass = null;
    if (id == R.id.nav_map) {
      fragmentClass = MapFragment.class;
    } else if (id == R.id.nav_food) {
      fragmentClass = FoodFragment.class;
    } else if (id == R.id.nav_bars) {
      fragmentClass = BarsFragment.class;
    } else if (id == R.id.nav_clubs) {
      fragmentClass = ClubsFragment.class;
    } else if (id == R.id.nav_favourites) {
      fragmentClass = FavouritesFragment.class;
    } else if (id == R.id.nav_sights) {
      fragmentClass = SightsFragment.class;
    } else if (id == R.id.nav_share) {

    } else if (id == R.id.nav_museums) {
      fragmentClass = MuseumsFragment.class;
    }
    try {
      fragment = (Fragment) fragmentClass.newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }

    FragmentManager fragmentManager = getSupportFragmentManager();
    if (fragment != mapFragment) {
      fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
          .addToBackStack(null)
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
    String placeName = place.getName().toString();

    if (place.getWebsiteUri() == null || place.getWebsiteUri().toString().isEmpty()) {
      String placeWebUrl = "";
      bundle.putString(PLACE_PICKER_WEBSITE_TAG, placeWebUrl);
    } else {
      String placeWebUrl = place.getWebsiteUri().toString();
      bundle.putString(PLACE_PICKER_WEBSITE_TAG, placeWebUrl);
    }
    bundle.putString(PLACE_PICKER_PLACE_ID_TAG, placeId);
    bundle.putString(PLACE_PICKER_NAME_TAG, placeName);
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
  public void onFavouritesFragmentInteraction(Result result) {
    // set DetailFragment Arguments
    FavouriteDetailFragment favouritedetailFragment = new FavouriteDetailFragment();
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    Log.i(LOG_TAG, "The Favourite Place is: " + result.getName());
    // Replace whatever is in the fragment_container view with this fragment,
    transaction.replace(R.id.fragment_container, favouritedetailFragment);
    transaction.addToBackStack(FAVOURITE_DETAIL_FRAGMENT_TAG);
    // Commit the transaction
    transaction.commit();
  }
}
