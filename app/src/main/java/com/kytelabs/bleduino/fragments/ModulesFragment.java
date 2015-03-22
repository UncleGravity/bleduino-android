package com.kytelabs.bleduino.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kytelabs.bleduino.R;
import com.kytelabs.bleduino.adapters.ModuleGridAdapter;
import com.kytelabs.bleduino.modules.Module1Activity;
import com.kytelabs.bleduino.pojos.ModuleListItem;

import java.util.Arrays;
import java.util.LinkedList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ModulesFragment extends Fragment {

    //Member Variables
    //--------------------------------------------------------------------------------
    private ModuleListItem[] mModules;
    @InjectView(R.id.modulesRecyclerView) RecyclerView mRecyclerView;


    public ModulesFragment() {
        // Required empty public constructor
    }

    //================================================================================
    // On Create View (OnCreate equivalent for fragments)
    //================================================================================
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modules, container, false);
        ButterKnife.inject(this, view);

        populateModules();

        ModuleGridAdapter adapter = new ModuleGridAdapter(getActivity().getApplicationContext(), new LinkedList<>(Arrays.asList(mModules)));
        mRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        return view;
    }

    //================================================================================
    // Populate Modules TODO Add all the modules
    //================================================================================

    private void populateModules() {
        mModules = new ModuleListItem[4];

        mModules[0] = new ModuleListItem();
        mModules[0].setText("LCD");
        mModules[0].setIconId(0);
        mModules[0].setNextClass(Module1Activity.class);

        mModules[1] = new ModuleListItem();
        mModules[1].setText("Console");
        mModules[1].setIconId(0);
        mModules[1].setNextClass(Module1Activity.class);

        mModules[2] = new ModuleListItem();
        mModules[2].setText("LED");
        mModules[2].setIconId(0);
        mModules[2].setNextClass(Module1Activity.class);

        mModules[3] = new ModuleListItem();
        mModules[3].setText("Firmata");
        mModules[3].setIconId(0);
        mModules[3].setNextClass(Module1Activity.class);

    }


}
