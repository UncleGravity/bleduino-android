package com.kytelabs.bleduino.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kytelabs.bleduino.R;
import com.kytelabs.bleduino.adapters.ModuleGridAdapter;
import com.kytelabs.bleduino.modules.ConsoleModuleActivity;
import com.kytelabs.bleduino.modules.KeyboardModule;
import com.kytelabs.bleduino.modules.LedModuleActivity;
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

    // Interface Setup
    //----------------------------------------------------------------------------
    // This is how we talk to MainActivity
    ModulesFragmentListener mListener;

    // MainActivity must implement this interface.
    public interface ModulesFragmentListener {
        void modulesFragmentEvent();
        void moduleAdapterEvent(View caller, int index);

        //Add more as necessary.
        // ...
    }


    // Ignore This
    //----------------------------------------------------------------------------
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

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (ModulesFragmentListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement ModulesFragmentListener");
        }

        //Use one of the interface functions whenever you need to talk to MainActivity
        mListener.modulesFragmentEvent();

        populateModules();

        ModuleGridAdapter adapter = new ModuleGridAdapter(getActivity().getApplicationContext(), new LinkedList<>(Arrays.asList(mModules)), mListener);
        mRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    //================================================================================
    // Populate Modules TODO Add all the modules
    //================================================================================

    private void populateModules() {
        mModules = new ModuleListItem[4];

        mModules[0] = new ModuleListItem();
        mModules[0].setText("Keyboard");
        mModules[0].setIconId(R.drawable.ic_keyboard_black_48dp);
        mModules[0].setNextClass(KeyboardModule.class);

        mModules[1] = new ModuleListItem();
        mModules[1].setText("Console");
        mModules[1].setIconId(R.drawable.ic_chat_black_48dp);
        mModules[1].setNextClass(ConsoleModuleActivity.class);

        mModules[2] = new ModuleListItem();
        mModules[2].setText("LED");
        mModules[2].setIconId(R.drawable.ic_wb_incandescent_black_48dp);
        mModules[2].setNextClass(LedModuleActivity.class);

        mModules[3] = new ModuleListItem();
        mModules[3].setText("Notification");
        mModules[3].setIconId(R.drawable.ic_notifications_none_black_48dp);
        mModules[3].setNextClass(null);

    }


}
