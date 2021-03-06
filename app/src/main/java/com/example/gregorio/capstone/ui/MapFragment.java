package com.example.gregorio.capstone.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.gregorio.capstone.R;
import com.example.gregorio.capstone.application.LostInTurin;
import com.example.gregorio.capstone.googleplacesapi.GoogleMapsApi;
import com.example.gregorio.capstone.googleplacesapi.GoogleNearbyPlacesParser;
import com.example.gregorio.capstone.model.NearbyPlaces;
import com.example.gregorio.capstone.network.NearbyPlacesRepository;
import com.example.gregorio.capstone.permissions.Connectivity;
import com.example.gregorio.capstone.permissions.Permissions;
import com.example.gregorio.capstone.viewmodels.MapDetailSharedViewHolder;
import com.example.gregorio.capstone.viewmodels.NearbyPlacesListViewModelFactory;
import com.example.gregorio.capstone.viewmodels.QueryNearbyPlacesViewModel;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.TreeMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.gregorio.capstone.R.id;
import static com.example.gregorio.capstone.R.layout;
import static com.example.gregorio.capstone.R.raw;
import static com.example.gregorio.capstone.R.string;

public class MapFragment extends Fragment implements SearchView.OnQueryTextListener,
        MenuItem.OnActionExpandListener, OnMapReadyCallback {

    private static final String LOG_TAG = MapFragment.class.getSimpleName();
    private static final String CURRENT_LATITUDE_TAG = "CURRENT LATITUDE TAG";
    private static final String CURRENT_LONGITUDE_TAG = "CURRENT LONGITUDE TAG";
    private static final String CURRENT_QUERY_TAG = "CURRENT QUERY TAG";
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String MARKERS_TAG_KEY = "MarkerTagKey";
    private static final int REQUEST_PLACE_PICKER = 1;
    // A default location (Piazza Castello, Turin, Italy) and default zoom to use when location permission is
    private static final LatLng PIAZZA_CASTELLO = new LatLng(45.0710394, 7.6862986);
    @BindView(id.map)
    MapView mapView;
    @BindView(id.checkout_button)
    FloatingActionButton placePicker;
    @BindView(id.map_progress_bar)
    ProgressBar progressBar;
    private GoogleMap mMap;
    private OnMarkerClickListener onMarkerClickListener;
    private OnInfoWindowClickListener onInfoWindowClickListener;
    private Double latitude;
    private Double longitude;
    private LatLng mCurrentLocation;
    private String apiKey;
    private String mQuery;
    private Permissions permissions;
    private Context context;
    private OnFragmentInteractionListener listener;
    private QueryNearbyPlacesViewModel queryViewModel;
    private GoogleNearbyPlacesParser nearbyPlacesResponseParser;
    private NearbyPlaces mNearbyPlaces;
    private boolean mSavedInstanceisNull;
    private View rootView;
    private List<MarkerOptions> mMarkerOptionsRetrieved;
    private MapDetailSharedViewHolder sharedModel;
    private SearchView searchView;
    private int placeIdInt;
    private TreeMap<Marker, Integer> eventMarkerMap;
    private Activity activity;
    private GoogleMapsApi googleMapsApi;
    Bundle mapViewBundle = null;


    @Inject
    public MapFragment() {
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(id.menu_search);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private static boolean onMarkerClick(Marker marker) {
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        rootView = inflater.inflate(layout.fragment_map, container, false);
        ButterKnife.bind(this, rootView);
        ButterKnife.setDebug(true);

        apiKey = getString(string.google_maps_key);
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
            latitude = savedInstanceState.getDouble(CURRENT_LATITUDE_TAG);
            longitude = savedInstanceState.getDouble(CURRENT_LONGITUDE_TAG);
            mCurrentLocation = new LatLng(latitude, longitude);
            mQuery = savedInstanceState.getString(CURRENT_QUERY_TAG);
            Log.i(LOG_TAG, "onCreateView savedInstanceState mapViewBundle:  " + mapViewBundle);
            Log.i(LOG_TAG, "onCreateView savedInstanceState latitude:  " + latitude);
            Log.i(LOG_TAG, "onCreateView savedInstanceState longitude:  " + longitude);
            Log.i(LOG_TAG, "onCreateView savedInstanceState Current String:  " + mQuery);
            Log.i(LOG_TAG, "onCreateView savedInstanceState Current Location:  " + mCurrentLocation);
            mSavedInstanceisNull = false;
        } else {
            mSavedInstanceisNull = true;
            longitude = 7.6862986;
            latitude = 7.6862986;
            mCurrentLocation = PIAZZA_CASTELLO;
            mQuery = "";
        }
        eventMarkerMap = new TreeMap<>();
        // mapView.getMapAsync(this);
        // Instantiate the data parsing class
        nearbyPlacesResponseParser = new GoogleNearbyPlacesParser();
        // Shared View Model to send Data from this fragment to the Detail one
        sharedModel = ViewModelProviders.of(getActivity()).get(MapDetailSharedViewHolder.class);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Connectivity connectivity = new Connectivity();
        if (!connectivity.isOnline(context)) {
            Snackbar snackbar = Snackbar
                    .make(rootView, string.no_internet_connection,
                            Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        mapView.onCreate(mapViewBundle);
        placePicker.setOnClickListener(this::onClick);
        onMarkerClickListener = MapFragment::onMarkerClick;
        onInfoWindowClickListener = this::onInfoWindowClick;
    }

    private void onMarkerPressedIntent(Marker marker) {
        if (listener != null) {
            listener.onFragmentInteraction(marker);
        }
    }

    private void onPlacePickerPressedIntent(Place place) {
        if (listener != null) {
            listener.OnPlacePickerInteraction(place);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + getString(string.must_implement_on_frag_list));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = getActivity();
        setHasOptionsMenu(true);
        // Check if the Google Play Services are available or not and set Up Map
        googleMapsApi = new GoogleMapsApi();
        googleMapsApi.CheckGooglePlayServices(context, activity);
        googleMapsApi.GoogleApiClient(context);
        // Check if the user has granted permission to use Location Services
        permissions = new Permissions();
    }

    // Prompt the user to check out of their location. Called when the "Check Out!" button
    // is clicked.
    public void checkOut() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(getActivity());
            startActivityForResult(intent, REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
                    REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(context, string.install_google_play_services, Toast.LENGTH_LONG)
                    .show();
        }
    }

    // App bar Search View setUp
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(id.menu_search);
        searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(string.search_nearby_places));
        searchView.setIconified(true);
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {
        mQuery = query;
        progressBar.setVisibility(View.VISIBLE);
        Log.i(LOG_TAG, "The Search Query is: " + query);
        int DEFAULT_ZOOM = 1500;
        if (mNearbyPlaces == null) {
            NearbyPlacesListViewModelFactory factory = new NearbyPlacesListViewModelFactory(
                    NearbyPlacesRepository.getInstance(),
                    mQuery, latitude.toString(), longitude.toString(), DEFAULT_ZOOM, apiKey);
            queryViewModel = ViewModelProviders.of(this, factory).get(QueryNearbyPlacesViewModel.class);

        } else {
            queryViewModel.nearbyPlacesRepository = NearbyPlacesRepository.getInstance();
            queryViewModel.keyword = mQuery;
            queryViewModel.latitude = latitude.toString();
            queryViewModel.longitude = longitude.toString();
            queryViewModel.radius = DEFAULT_ZOOM;
            queryViewModel.apiKey = apiKey;
            queryViewModel.markerOptions = mMarkerOptionsRetrieved;
            queryViewModel.getNewPlaces();
        }

        queryViewModel.getData().observe(this, new Observer<NearbyPlaces>() {
            @Override
            public void onChanged(@Nullable NearbyPlaces nearbyPlaces) {
                if (nearbyPlaces != null) {
                    mNearbyPlaces = nearbyPlaces;
                    String status = nearbyPlaces.getStatus();
                    if (status.matches("ZERO_RESULTS")) {
                        Snackbar snackbar = Snackbar.make(rootView, string.no_results, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    mMarkerOptionsRetrieved = nearbyPlacesResponseParser.drawLocationMap(nearbyPlaces, mMap, mCurrentLocation, eventMarkerMap);
                    queryViewModel.markerOptions = mMarkerOptionsRetrieved;
                    progressBar.setVisibility(View.INVISIBLE);
                    hideKeyboard(activity);
                    Log.i(LOG_TAG, "queryViewModel markerOptions on Rotation is" + queryViewModel.markerOptions.size());
                    queryViewModel.getData().removeObserver(this);
                } else {
                    Snackbar snackbar = Snackbar.make(rootView, string.check_internet_connection, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return true;
    }

    // Once the user has chosen a place, onActivityResult will be called, so we need to implement that now.
    // This code checks that the intent was successful and uses PlacePicker.getPlace() to obtain the chosen Place.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLACE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(context, data);
                onPlacePickerPressedIntent(place);
                progressBar.setVisibility(View.INVISIBLE);
            } else if (resultCode == PlacePicker.RESULT_ERROR) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(context,
                        string.places_api_failure_msg,
                        Toast.LENGTH_LONG).show();
            } else {
                super.onActivityResult(requestCode, resultCode, data);
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void getLastLocation() {
        if (permissions.checkLocalPermission(context, getActivity())) {
            Task<Location> locationTask = LocationServices.getFusedLocationProviderClient(context).getLastLocation();
            locationTask.addOnSuccessListener(location -> {
                // GPS location can be null if GPS is switched off
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    mCurrentLocation = new LatLng(latitude, longitude);
                    Log.i(LOG_TAG,
                            "The Last location is: Latitude: " + latitude + " Longitude: " + longitude);
                } else {
                    latitude = PIAZZA_CASTELLO.latitude;
                    longitude = PIAZZA_CASTELLO.longitude;
                    mCurrentLocation = PIAZZA_CASTELLO;
                    Log.i(LOG_TAG, "Could not fetch the GPS location, we set to the default one: "
                            + PIAZZA_CASTELLO);
                    Snackbar snackbar = Snackbar.make(rootView,
                            context.getResources().getString(string.check_internet_gps_msg),
                            Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });
            locationTask.addOnFailureListener(e -> {
                Log.d(LOG_TAG, "Error trying to get last GPS location");
                e.printStackTrace();
                Snackbar snackbar = Snackbar
                        .make(rootView, string.check_internet_gps_msg,
                                Snackbar.LENGTH_LONG);
                snackbar.show();
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        mapView.getMapAsync(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();

    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(context);
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(context, raw.mapstyle_retro);
        googleMap.setMapStyle(style);
        //Once the Map is initialised we set Up an observer for changes to the Map
        // Check the Location permission is given before enabling setMyLocation to true.
        if (permissions.checkLocalPermission(context, getActivity())) {
            // If the Location Permission id Granted Enable Location on the Map
            googleMap.setMyLocationEnabled(true);
            googleMap.setBuildingsEnabled(true);
            googleMap.setOnMarkerClickListener(onMarkerClickListener);
            googleMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
            if (mSavedInstanceisNull) {
                // Construct a CameraPosition focusing on Piazza Castello, Turin Italy and animate the camera to that position.
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(mCurrentLocation)      // Sets the center of the map to Piazza Castello
                        .zoom(13)                   // Sets the zoom
                        .bearing(0)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();
                // Creates a CameraPosition from the builder
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            if (queryViewModel != null) {
                for (int i = 0; i < queryViewModel.markerOptions.size(); i++) {
                    Log.i(LOG_TAG, "On Rotation Map Marker size is " + queryViewModel.markerOptions.size());
                    MarkerOptions m = queryViewModel.markerOptions.get(i);
                    googleMap.addMarker(m);
                }
            }
        }
        getLastLocation();
        mMap = googleMap;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        googleMapsApi.DisconnectGoogleApiClient();
        googleMapsApi = null;
        LostInTurin.getRefWatcher(context).watch(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putDouble(CURRENT_LATITUDE_TAG, latitude);
        outState.putDouble(CURRENT_LONGITUDE_TAG, longitude);
        outState.putString(CURRENT_QUERY_TAG, mQuery);
        outState.putInt(MARKERS_TAG_KEY, placeIdInt);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }

    private void onClick(View v) {
        MapFragment.this.checkOut();
    }

    private void onInfoWindowClick(Marker marker) {
        searchView.isIconified();
        searchView.onActionViewCollapsed();
        int mPlaceIdTag = Integer.valueOf(marker.getSnippet());
        sharedModel.select(queryViewModel.getData().getValue().getResults().get(mPlaceIdTag));
        MapFragment.this.onMarkerPressedIntent(marker);
        marker.hideInfoWindow();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Marker marker);

        void OnPlacePickerInteraction(Place place);
    }
}


