package com.example.gregorio.capstone;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.Marker;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener,
    MapFragment.OnFragmentInteractionListener, DetailFragment.OnFragmentInteractionListener {

  private final static String MAP_FRAGMENT_TAG = "Map Fragment Tag";
  private final static String DETAIL_FRAGMENT_TAG = "Detail Fragment Tag";
  private FrameLayout mapLayout;
  private FrameLayout detailLayout;
  private MapFragment mapFragment;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    detailLayout = findViewById(R.id.fragment_container_detail);
    detailLayout.setVisibility(View.INVISIBLE);
    mapLayout = findViewById(R.id.fragment_container);

    mapFragment = new MapFragment();
      FragmentManager fragmentManager = getSupportFragmentManager();
      fragmentManager.beginTransaction().add(R.id.fragment_container, mapFragment)
          .addToBackStack(MAP_FRAGMENT_TAG)
          .commit();

    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
      mapLayout.setVisibility(View.VISIBLE);
      detailLayout.setVisibility(View.INVISIBLE);
    } else {
      super.onBackPressed();
      mapLayout.setVisibility(View.VISIBLE);
      detailLayout.setVisibility(View.INVISIBLE);
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
      mapLayout.setVisibility(View.VISIBLE);
      detailLayout.setVisibility(View.INVISIBLE);
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
    } else if (id == R.id.nav_send) {

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
    String id = marker.getId();
    String title = marker.getTitle();
    // Bundle to launch the Detail Fragment
    Bundle bundle = new Bundle();
    bundle.putString("TITLE", title);
    bundle.putString("ID", id);
    // set DetailFragment Arguments
    DetailFragment detailFragment = new DetailFragment();
    detailFragment.setArguments(bundle);
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    // Replace whatever is in the fragment_container view with this fragment,
    // and add the transaction to the back stack so the user can navigate back
    mapLayout.setVisibility(View.GONE);
    FrameLayout detailLayout = findViewById(R.id.fragment_container_detail);
    detailLayout.setVisibility(View.VISIBLE);
    transaction.add(R.id.fragment_container_detail, detailFragment);
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
      bundle.putString("PLACE WEB URL", placeWebUrl);
    } else {
      String placeWebUrl = place.getWebsiteUri().toString();
      bundle.putString("PLACE WEB URL", placeWebUrl);
    }
    bundle.putString("ID", placeId);
    bundle.putString("TITLE", placeName);
    // set DetailFragment Arguments
    DetailFragment detailFragment = new DetailFragment();
    detailFragment.setArguments(bundle);
    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
    // Replace whatever is in the fragment_container view with this fragment,
    // and add the transaction to the back stack so the user can navigate back
    mapLayout.setVisibility(View.INVISIBLE);
    detailLayout.setVisibility(View.VISIBLE);
    transaction.replace(R.id.fragment_container_detail, detailFragment);
    transaction.addToBackStack(DETAIL_FRAGMENT_TAG);
    // Commit the transaction
    transaction.commit();
  }


  @Override
  public void onFragmentInteraction(Uri uri) {

  }
}
