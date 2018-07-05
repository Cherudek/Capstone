package com.example.gregorio.capstone;

import adapters.PhotoAdapter;
import adapters.ReviewAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gms.flags.impl.DataUtils.StringUtils;
import com.squareup.picasso.Picasso;
import java.util.List;
import pojos.Photo;
import pojosplaceid.PlaceId;
import pojosplaceid.Review;
import repository.NearbyPlacesRepository;
import viewmodel.DetailViewModel;
import viewmodel.DetailViewModelFactory;
import viewmodel.MapDetailSharedViewHolder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {

  public static final String LOG_TAG = DetailFragment.class.getSimpleName();
  private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_TITLE = "TITLE";
  private static final String ARG_ID = "ID";
  private static final String ARG_WEB_URL = "PLACE WEB URL";
  private static final String PHOTO_URL_TAG = "PhotoUrlTag";
  private static final String NAME_TAG = "NameTag";
  private static final String ADDRESS_TAG = "AddressTag";
  private static final String API_KEY_TAG = "ApiKeyTag";
  private static final String PLACE_ID_TAG = "PlaceIdTag";

  private String mPlaceId;
  private String mName;
  private String mAddress;
  private Double mRating;
  private String mPhoneNumber;
  private String mOpeningHours;
  private List<String> mOpeningWeekDays;
  private String mTelephone;
  private Integer mPriceLevel;
  private List<Photo> photoHeader;
  private List<pojosplaceid.Photo> photoList;
  private List<Review> reviewsList;
  private int height;
  private int width;
  private String photoReference;
  private String apiKey;
  private String picassoPhotoUrl;
  private DetailViewModelFactory detailViewModelFactory;
  private DetailViewModel detailViewModel;
  private MapDetailSharedViewHolder detailModel;
  private PhotoAdapter mPhotoAdapter;
  private ReviewAdapter mReviewsAdapter;
  private LinearLayoutManager reviewsLayoutManager;
  private LinearLayoutManager photosLayoutManager;
  private int numberOfReviews;
  private int numberOfPhotos;
  private double numberOfStars;

  @BindView(R.id.detail_image)ImageView ivPhotoView;
  @BindView(R.id.place_address)TextView tvAddress;
  @BindView(R.id.place_mame)TextView tvName;
  @BindView(R.id.place_url)TextView tvWebAddress;
  @BindView(R.id.add_to_favourites_button)FloatingActionButton addFavourites;
  @BindView(R.id.weekday_opening_hours)TextView tvOpeningHours;
  @BindView(R.id.open_now)TextView tvOpenNow;
  @BindView(R.id.telephone_no)TextView tvTelephone;
  @BindView(R.id.photo_gallery)RecyclerView rvPhotoGallery;
  @BindView(R.id.reviews)RecyclerView rvReviews;


  private OnFragmentInteractionListener mListener;

  public DetailFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment DetailFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static DetailFragment newInstance(String param1, String param2) {
    DetailFragment fragment = new DetailFragment();
    Bundle args = new Bundle();
    args.putString(ARG_TITLE, param1);
    args.putString(ARG_ID, param2);
    fragment.setArguments(args);
    return fragment;
  }


  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if(savedInstanceState!=null){
      picassoPhotoUrl = savedInstanceState.getString(PHOTO_URL_TAG);
      mName = savedInstanceState.getString(NAME_TAG);
      mAddress = savedInstanceState.getString(ADDRESS_TAG);
      apiKey = savedInstanceState.getString(API_KEY_TAG);
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    menu.findItem(R.menu.main).collapseActionView();

  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
    ButterKnife.bind(this, rootView);
    apiKey = getContext().getResources().getString(R.string.google_api_key);
    detailModel = ViewModelProviders.of(getActivity()).get(MapDetailSharedViewHolder.class);
    detailModel.getSelected().observe(this, item -> {
      // Update the UI.
      mName = item.getName();
      mAddress = item.getVicinity();
      mRating = item.getRating();
      mPlaceId = item.getPlaceId();
      if(mPriceLevel!=null){
        mPriceLevel = item.getPriceLevel();
      }
      photoHeader = item.getPhotos();
      if(photoHeader.size() >= 1){
        Log.i(LOG_TAG, "Photo Array Size = " + photoHeader.size());
        height = photoHeader.get(0).getHeight();
        width = photoHeader.get(0).getWidth();
        photoReference = photoHeader.get(0).getPhotoReference();
      }
      tvName.setText(mName);
      tvAddress.setText(mAddress);
      picassoPhotoUrl = PHOTO_PLACE_URL + "maxwidth=600&photoreference=" + photoReference + "&key=" + apiKey;
      Log.i(LOG_TAG, "The Picasso Photo Url is " + picassoPhotoUrl);
      Picasso.get().load(picassoPhotoUrl).error(R.drawable.coming_soon).into(ivPhotoView);
      Log.i(LOG_TAG, "The Name Retrieved from the MapDetailSharedViewHolder is " + mName);
      Log.i(LOG_TAG, "The address is " + mAddress);
      Log.i(LOG_TAG, "The Photo reference is " + photoReference);
      Log.i(LOG_TAG, "The Photo PlaceId is " + mPlaceId);

      photosLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true);
      rvPhotoGallery.setLayoutManager(photosLayoutManager);
      rvPhotoGallery.setHasFixedSize(true);

      reviewsLayoutManager = new LinearLayoutManager(getContext());
      rvReviews.setLayoutManager(reviewsLayoutManager);
      rvReviews.setHasFixedSize(true);

    });

    // Inflate the layout for this fragment
    return rootView;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null) {
      // Get the Data from the map object clicked in the map fragment

    }

    // Add Place to favourites (Room Database)
    addFavourites.setOnClickListener(v -> {

    });
  }

  @Override
  public void onResume() {
    super.onResume();

    detailViewModelFactory = new DetailViewModelFactory(NearbyPlacesRepository.getInstance(), mPlaceId, apiKey);
    detailViewModel = ViewModelProviders.of(this, detailViewModelFactory).get(DetailViewModel.class);

    detailViewModel.getPlaceDetails().observe(this, (PlaceId placeId) -> {
      String website = placeId.getResult().getWebsite();
      tvWebAddress.setText(website);
      String phoneNo = placeId.getResult().getInternationalPhoneNumber();
      tvTelephone.setText(phoneNo);
      if(TextUtils.isEmpty(website)){
        tvWebAddress.setVisibility(View.GONE);
      } else {
        tvWebAddress.setVisibility(View.VISIBLE);
        tvWebAddress.setText(website);
      }
      if(placeId.getResult().getOpeningHours()!=null){
        Boolean openingHours = placeId.getResult().getOpeningHours().getOpenNow();
        if(openingHours){
          tvOpenNow.setText(R.string.open);
        } else {
          tvOpenNow.setText(R.string.closed);
        }
        Log.i(LOG_TAG, "Open Now " + openingHours);
      }
      if(placeId.getResult().getOpeningHours()!=null){
        mOpeningWeekDays = placeId.getResult().getOpeningHours().getWeekdayText();
        StringBuilder weeklyHours = new StringBuilder();
        weeklyHours.append("Opening Hours:"+"\n");
        Log.i(LOG_TAG, "Week Days opening " + mOpeningWeekDays);
        for(int i = 0; i < mOpeningWeekDays.size(); i++) {
          weeklyHours.append(mOpeningWeekDays.get(i) + "\n");
          tvOpeningHours.setText(weeklyHours);
        }
      }
      if(placeId.getResult().getPhotos() != null){
        numberOfPhotos = placeId.getResult().getPhotos().size();
        photoList = placeId.getResult().getPhotos();
        mPhotoAdapter = new PhotoAdapter(numberOfPhotos, apiKey);
      } else {
        mPhotoAdapter = new PhotoAdapter(2, apiKey);
      }
      mPhotoAdapter.addAll(photoList);
      rvPhotoGallery.setAdapter(mPhotoAdapter);



      int reviewSize = placeId.getResult().getReviews().size();
      reviewsList = placeId.getResult().getReviews();
      mReviewsAdapter = new ReviewAdapter(reviewSize);
      mReviewsAdapter.addAll(reviewsList);
      rvReviews.setAdapter(mReviewsAdapter);


      Log.i(LOG_TAG, "Website is " + website);
      Log.i(LOG_TAG, "Phone Number is " + phoneNo);
      Log.i(LOG_TAG, "Photo Size " + numberOfPhotos);
      Log.i(LOG_TAG, "Review Size " + reviewSize);

    });
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(PHOTO_URL_TAG, picassoPhotoUrl);
    outState.putString(NAME_TAG, mName);
    outState.putString(ADDRESS_TAG, mAddress);
    outState.putString(API_KEY_TAG, apiKey);
    outState.putString(PLACE_ID_TAG, mPlaceId);


  }



  // TODO: Rename method, update argument and hook method into UI event
  public void onButtonPressed(Uri uri) {
    if (mListener != null) {
      mListener.onFragmentInteraction(uri);
    }
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener) context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement OnFragmentInteractionListener");
    }
  }


  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  /**
   * This interface must be implemented by activities that contain this
   * fragment to allow an interaction in this fragment to be communicated
   * to the activity and potentially other fragments contained in that
   * activity.
   * <p>
   * See the Android Training lesson <a href=
   * "http://developer.android.com/training/basics/fragments/communicating.html"
   * >Communicating with Other Fragments</a> for more information.
   */
  public interface OnFragmentInteractionListener {

    // TODO: Update argument type and name
    void onFragmentInteraction(Uri uri);
  }
}
