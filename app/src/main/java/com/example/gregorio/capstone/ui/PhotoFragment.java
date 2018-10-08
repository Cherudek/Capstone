package com.example.gregorio.capstone.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.gregorio.capstone.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.gregorio.capstone.ui.MainActivity.PHOTO_REFERENCE_TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PhotoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PhotoFragment extends Fragment {

  private OnFragmentInteractionListener mListener;
  @BindView(R.id.large_image)ImageView imageView;
  private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
  private String mApiKey;

  public PhotoFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_large_image, container, false);
    ButterKnife.bind(this,rootView);
    mApiKey = getContext().getResources().getString(R.string.google_api_key);
    return rootView;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null) {
      // Get the photo reference url from the Photo object clicked in the Photo Gallery Detail View
      String photoReference = bundle.getString(PHOTO_REFERENCE_TAG);
      String photoUrl = PHOTO_PLACE_URL + "maxwidth=600&photoreference=" + photoReference + "&key=" + mApiKey;
      Glide.with(this)
          .load(photoUrl)
          .into(imageView);
      imageView.setContentDescription(this.getString(R.string.photo_gallery_detail_view));
    }
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
