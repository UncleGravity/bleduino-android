package com.kytelabs.bleduino.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kytelabs.bleduino.R;
import com.kytelabs.bleduino.adapters.SettingsListAdapter;
import com.kytelabs.bleduino.pojos.SettingsListItem;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.settingsListView) ListView mListView;
    private SettingsListItem[] mSettingsListItems;

    // Interface Setup
    //----------------------------------------------------------------------------
    // This is how we talk to MainActivity
    SettingsFragmentListener mListener;

    // MainActivity must implement this interface.
    public interface SettingsFragmentListener {
        void settingsFragmentEvent();

        //Add more as necessary.
        // ...
    }


    // Ignore This
    //----------------------------------------------------------------------------
    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.inject(this,view);

        // List View Setup
        //----------------------------------------------------------------------------
        populateNavigation();
        listViewSetUp();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (SettingsFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SettingsFragmentListener");
        }

        //Use one of the interface functions whenever you need to talk to MainActivity
        mListener.settingsFragmentEvent();
    }

    private void populateNavigation() {
        // Fill this baby up
        mSettingsListItems = new SettingsListItem[7];

        mSettingsListItems[0] = new SettingsListItem();
        mSettingsListItems[0].setPrimaryText("Device scan");
        mSettingsListItems[0].setItemType(SettingsListItem.HEADER);

        mSettingsListItems[1] = new SettingsListItem();
        mSettingsListItems[1].setPrimaryText("Only Scan BLEduinos");
        mSettingsListItems[1].setItemType(SettingsListItem.TOGGLE);
        //TODO set default toggle state (from user preferences if available)
        mSettingsListItems[1].setToggleState(true);

        mSettingsListItems[2] = new SettingsListItem();
        mSettingsListItems[2].setPrimaryText("Device Connection");
        mSettingsListItems[2].setItemType(SettingsListItem.HEADER);

        mSettingsListItems[3] = new SettingsListItem();
        mSettingsListItems[3].setPrimaryText("Notify on disconnect");
        mSettingsListItems[3].setItemType(SettingsListItem.TOGGLE);
        //TODO set default toggle state (from user preferences if available)
        mSettingsListItems[3].setToggleState(false);

        mSettingsListItems[4] = new SettingsListItem();
        mSettingsListItems[4].setPrimaryText("Connection reminder alert");
        mSettingsListItems[4].setItemType(SettingsListItem.TOGGLE);
        //TODO set default toggle state (from user preferences if available)
        mSettingsListItems[4].setToggleState(true);

        mSettingsListItems[5] = new SettingsListItem();
        mSettingsListItems[5].setPrimaryText("Other");
        mSettingsListItems[5].setItemType(SettingsListItem.HEADER);

        mSettingsListItems[6] = new SettingsListItem();
        mSettingsListItems[6].setPrimaryText("Open source");
        mSettingsListItems[6].setItemType(SettingsListItem.TEXT_ICON);
        mSettingsListItems[6].setIconId(R.drawable.github_circle_24dp);

    }

    private void listViewSetUp() {

        List<SettingsListItem> navigationList = Arrays.asList(mSettingsListItems);
        // Set adapter
        //Context context = ((MainActivity) getActivity()).getThemedContextFromMain();
        SettingsListAdapter adapter = new SettingsListAdapter(getActivity(), navigationList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("Tag","Click");
        
        if(mSettingsListItems[position].getItemType() == SettingsListItem.TOGGLE){
            SwitchCompat toggleSwitch = (SwitchCompat) view.findViewById(R.id.settingsToggleSwitch);
            toggleSwitch.toggle();
        }

        //TODO other onclicks
    }
}
