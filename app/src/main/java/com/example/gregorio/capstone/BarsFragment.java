package com.example.gregorio.capstone;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BarsFragment extends Fragment {


  public BarsFragment(){
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_bars, container, false);

    TextView textView1 = rootView.findViewById(R.id.bar);
    textView1.setText("BAR FRAGMENT");


    return rootView;
  }

}
