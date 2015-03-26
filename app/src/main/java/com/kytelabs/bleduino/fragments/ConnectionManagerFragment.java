package com.kytelabs.bleduino.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kytelabs.bleduino.R;
import com.kytelabs.bleduino.adapters.ConnectionManagerListAdapter;
import com.kytelabs.bleduino.pojos.DividerItemDecoration;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionManagerFragment extends Fragment {

    //Member Variables
    //--------------------------------------------------------------------------------
    @InjectView(R.id.connectionManagerRecyclerView) RecyclerView mRecyclerView;
    @InjectView(R.id.connectionManagerRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    DividerItemDecoration mDividerItemDecoration;

    public ConnectionManagerFragment() {
        // Required empty public constructor
    }

    //================================================================================
    // On Create View (OnCreate equivalent for fragments)
    //================================================================================
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connection_manager, container, false);
        ButterKnife.inject(this, view);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        setupAdapter();

        mSwipeRefreshLayout.setColorSchemeResources(R.color.accentColor);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO Implement BLE scanning

                // Get Bluetooth Adapter from Service

                // Start scanning
                // mBluetoothAdapter.startLeScan(callback);

                // Populate device array (This happens in startLeScan callback, implemented here)
                // filtered by BLEduino or not, depending on settings
                // Make sure connected devices are between "found" and "connected"
                // The rest will be after "found"

                // Run the handler below
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //mBluetoothAdapter.stopLeScan(callback);
                        setupAdapter();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2500); // <-- Time spent scanning.
            }
        });

        return view;
    }

    private void setupAdapter() {
        //Get actual actual device list
        List<String> devices = Arrays.asList("Connected","Plancha","Found","Cuatro","Planchas");

        if (mDividerItemDecoration != null) {
            mRecyclerView.removeItemDecoration(mDividerItemDecoration);
        }

        mDividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, 1);
        mRecyclerView.addItemDecoration(mDividerItemDecoration);


        ConnectionManagerListAdapter adapter = new ConnectionManagerListAdapter(getActivity(),devices);
        mRecyclerView.setAdapter(adapter);

    }


}
