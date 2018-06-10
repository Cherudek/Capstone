package com.example.gregorio.capstone;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {

  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_TITLE = "TITLE";
  private static final String ARG_ID = "ID";
  private static final String ARG_WEB_URL = "PLACE WEB URL";

  // TODO: Rename and change types of parameters
  private String mTitle;
  private String mId;
  private String mWebUrl;
  private TextView tvTitle;
  private TextView tvId;
  private TextView tvWebUrl;


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
      mTitle = getArguments().getString(ARG_TITLE);
      mId = getArguments().getString(ARG_ID);
      mWebUrl = getArguments().getString(ARG_WEB_URL);
    }
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    Bundle bundle = getArguments();
    if (bundle != null) {
      // Get the Data from the map object clicked in the map fragment
      mTitle = getArguments().getString(ARG_TITLE);
      mId = getArguments().getString(ARG_ID);
      mWebUrl = getArguments().getString(ARG_WEB_URL);

    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

    tvId = rootView.findViewById(R.id.marker_id);
    tvId.setText(mId);
    tvTitle = rootView.findViewById(R.id.marker_title);
    tvTitle.setText(mTitle);
    tvWebUrl = rootView.findViewById(R.id.web_url);
    tvWebUrl.setText(mWebUrl);

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
