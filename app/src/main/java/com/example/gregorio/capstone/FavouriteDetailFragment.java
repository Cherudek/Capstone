package com.example.gregorio.capstone;

import adapters.PhotoAdapter;
import adapters.ReviewAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import java.util.List;
import pojos.Photo;
import pojosplaceid.Result;
import pojosplaceid.Review;
import viewmodel.DetailViewModel;
import viewmodel.DetailViewModelFactory;
import viewmodel.FavouriteDetailSharedViewModel;
import viewmodel.MapDetailSharedViewHolder;

public class FavouriteDetailFragment extends Fragment {

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
  private Double mPriceLevel;
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
  private Result favouriteResult;
  private Result mapResult;
  private Result resultFromFavourites;
  private Result result;
  private FavouriteDetailSharedViewModel favouriteDetailSharedViewModel;

  private static final String FIREBASE_URL = "https://turin-guide-1526861835739.firebaseio.com/";
  private static final String FIREBASE_ROOT_NODE = "checkouts";
  private FirebaseDatabase mFirebaseDatabase;
  private DatabaseReference mPlacesDatabaseReference;

  @BindView(R.id.favourite_detail_image)ImageView ivPhotoView;
  @BindView(R.id.favourite_place_address)TextView tvAddress;
  @BindView(R.id.favourite_place_mame)TextView tvName;
  @BindView(R.id.favourite_place_url)TextView tvWebAddress;
  @BindView(R.id.remove_from_favourites_button)FloatingActionButton removeFavourite;
  @BindView(R.id.favourite_weekday_opening_hours)TextView tvOpeningHours;
  @BindView(R.id.favourite_open_now)TextView tvOpenNow;
  @BindView(R.id.favourite_telephone_no)TextView tvTelephone;
  @BindView(R.id.favourite_photo_gallery)RecyclerView rvPhotoGallery;
  @BindView(R.id.favourite_reviews)RecyclerView rvReviews;

  public FavouriteDetailFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    favouriteDetailSharedViewModel = ViewModelProviders.of(getActivity()).get(FavouriteDetailSharedViewModel.class);
    favouriteDetailSharedViewModel.getSelected().observe(this, result -> {
      //Update UI
      String name = result.getName();
      resultFromFavourites = result;
      Log.i(LOG_TAG, "Name: " + name);
    });

    // Initialize FireBase components
    mFirebaseDatabase = FirebaseDatabase.getInstance();
    mPlacesDatabaseReference = mFirebaseDatabase.getReference().child("checkouts");


  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_favourite_detail, container, false);
    ButterKnife.bind(this, rootView);
    apiKey = getContext().getResources().getString(R.string.google_api_key);

    photosLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true);
    rvPhotoGallery.setLayoutManager(photosLayoutManager);
    rvPhotoGallery.setHasFixedSize(true);

    reviewsLayoutManager = new LinearLayoutManager(getContext());
    rvReviews.setLayoutManager(reviewsLayoutManager);
    rvReviews.setHasFixedSize(true);

    // Inflate the layout for this fragment
    return rootView;

  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    removeFavourite.setOnClickListener(v -> {
      String favourite = getString(R.string.nv_favourites);

      // TODO: REMOVE FROM FAVOURITE FIREBASE LOGIC

      mPlacesDatabaseReference.child(favourite).push().setValue(favouriteResult);
      Snackbar snackbar = Snackbar
          .make(getView(), "Removed form Your Favourites!", Snackbar.LENGTH_SHORT);
      snackbar.show();

    });

    photoReference = resultFromFavourites.getPhotos().get(0).getPhotoReference();
    picassoPhotoUrl = PHOTO_PLACE_URL + "maxwidth=600&photoreference=" + photoReference + "&key=" + apiKey;
    Picasso.get().load(picassoPhotoUrl).error(R.drawable.coming_soon).into(ivPhotoView);

    mRating = resultFromFavourites.getRating();

    tvName.setText(resultFromFavourites.getName());
    tvAddress.setText(resultFromFavourites.getVicinity());
    if(mPriceLevel!=null){
      mPriceLevel = resultFromFavourites.getRating();
    }
    tvTelephone.setText(resultFromFavourites.getInternationalPhoneNumber());
    if (result.getOpeningHours() != null) {
      Boolean openingHours = resultFromFavourites.getOpeningHours().getOpenNow();
      if (openingHours) {
        tvOpenNow.setText(R.string.open);
      } else {
        tvOpenNow.setText(R.string.closed);
      }
      Log.i(LOG_TAG, "Open Now " + openingHours);

    }
    if(resultFromFavourites.getOpeningHours()!=null){
      mOpeningWeekDays = resultFromFavourites.getOpeningHours().getWeekdayText();
      StringBuilder weeklyHours = new StringBuilder();
      weeklyHours.append("Opening Hours:"+"\n\n");
      Log.i(LOG_TAG, "Week Days opening " + mOpeningWeekDays);
      for(int i = 0; i < mOpeningWeekDays.size(); i++) {
        weeklyHours.append(mOpeningWeekDays.get(i) + "\n");
        tvOpeningHours.setText(weeklyHours);
      }
    }

    if(resultFromFavourites.getPhotos() != null){
      numberOfPhotos = resultFromFavourites.getPhotos().size();
      photoList = resultFromFavourites.getPhotos();
      mPhotoAdapter = new PhotoAdapter(numberOfPhotos, apiKey);
      mPhotoAdapter.addAll(photoList);
    } else {
      mPhotoAdapter = new PhotoAdapter(2, apiKey);
    }
    rvPhotoGallery.setAdapter(mPhotoAdapter);

    int reviewSize = resultFromFavourites.getReviews().size();
    reviewsList = resultFromFavourites.getReviews();
    mReviewsAdapter = new ReviewAdapter(reviewSize);
    mReviewsAdapter.addAll(reviewsList);
    rvReviews.setAdapter(mReviewsAdapter);
  }


}
