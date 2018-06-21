package com.example.gregorio.capstone;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import java.util.List;
import pojos.Photo;
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
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_TITLE = "TITLE";
  private static final String ARG_ID = "ID";
  private static final String ARG_WEB_URL = "PLACE WEB URL";

  // TODO: Rename and change types of parameters
  private String mPlaceId;
  private String mName;
  private String mWebUrl;
  private String mAddress;
  private String openNow = "Open Now";
  private String closed = "Closed";
  private String reviews;
  private Double mRating;
  private String mPhoneNumber;
  private int mPriceLevel;
  private List<Photo> photoList;
  private int height;
  private int width;
  private String photoReference;
  private String apiKey;
  private String picassoPhotoUrl;


  @BindView(R.id.imageView)ImageView ivPhotoView;
  @BindView(R.id.place_address)TextView tvAddress;
  @BindView(R.id.place_mame)TextView tvName;
  @BindView(R.id.place_url)TextView tvWebAddress;


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
    if (getArguments() != null) {
//      mTitle = getArguments().getString(ARG_TITLE);
//      mId = getArguments().getString(ARG_ID);
//      mWebUrl = getArguments().getString(ARG_WEB_URL);
    }

    apiKey = getContext().getResources().getString(R.string.google_api_key);

    MapDetailSharedViewHolder model = ViewModelProviders.of(getActivity()).get(MapDetailSharedViewHolder.class);
    model.getSelected().observe(this, item -> {
      // Update the UI.
      mName = item.getName();
      mAddress = item.getVicinity();
      mRating = item.getRating();
      mPlaceId = item.getPlaceId();
      mPriceLevel = item.getPriceLevel();
      photoList = item.getPhotos();
      height = photoList.get(0).getHeight();
      width = photoList.get(0).getWidth();
      photoReference = photoList.get(0).getPhotoReference();

      Log.i(LOG_TAG, "The Name Retrived from the MapDetailSharedViewHolder is " + mName);
    });



  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null) {
      // Get the Data from the map object clicked in the map fragment
//      mTitle = getArguments().getString(ARG_TITLE);
//      mId = getArguments().getString(ARG_ID);
//      mWebUrl = getArguments().getString(ARG_WEB_URL);

    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
    ButterKnife.bind(this, rootView);

    tvName.setText(mName);
    tvWebAddress.setText(mWebUrl);
    tvAddress.setText(mAddress);
    picassoPhotoUrl = PHOTO_PLACE_URL + "maxwidth=400&photoreference=" + photoReference + "&key=" + apiKey;
    Picasso.get().load(picassoPhotoUrl).into(ivPhotoView);


    // Inflate the layout for this fragment
    return rootView;

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
