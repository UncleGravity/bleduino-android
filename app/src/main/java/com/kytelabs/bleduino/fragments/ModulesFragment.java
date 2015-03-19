package com.kytelabs.bleduino.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kytelabs.bleduino.R;
import com.kytelabs.bleduino.modules.Module1Activity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModulesFragment extends Fragment {

    @OnClick(R.id.moduleButton)
    public void submit(View view) {
        startActivity(new Intent(getActivity(), Module1Activity.class));
    }


    public ModulesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modules, container, false);
        ButterKnife.inject(this, view);


        return view;
    }


}
