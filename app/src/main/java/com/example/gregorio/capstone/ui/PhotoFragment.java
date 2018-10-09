package com.example.gregorio.capstone.ui;

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

public class PhotoFragment extends Fragment {

  private OnFragmentInteractionListener listener;
  @BindView(R.id.large_image)ImageView imageView;
  private static final String PHOTO_PLACE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
  private String apiKey;

  public PhotoFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_large_image, container, false);
    ButterKnife.bind(this,rootView);
    apiKey = getContext().getResources().getString(R.string.google_api_key);
    return rootView;
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null) {
      // Get the photo reference url from the Photo object clicked in the Photo Gallery Detail View
      String photoReference = bundle.getString(PHOTO_REFERENCE_TAG);
      String photoUrl = PHOTO_PLACE_URL + "maxwidth=600&photoreference=" + photoReference + "&key=" + apiKey;
      Glide.with(this)
          .load(photoUrl)
          .into(imageView);
      imageView.setContentDescription(this.getString(R.string.photo_gallery_detail_view));
    }
  }
}
