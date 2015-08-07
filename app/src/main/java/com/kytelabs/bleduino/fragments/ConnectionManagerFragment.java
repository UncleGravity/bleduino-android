package com.kytelabs.bleduino.fragments;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kytelabs.bleduino.MainActivity;
import com.kytelabs.bleduino.R;
import com.kytelabs.bleduino.adapters.ConnectionManagerListAdapter;
import com.kytelabs.bleduino.ble.BLEService;
import com.kytelabs.bleduino.pojos.DividerItemDecoration;
import com.kytelabs.bleduino.pojos.LeParsedDevice;
import com.kytelabs.bleduino.pojos.SettingsListItem;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectionManagerFragment extends Fragment implements BluetoothAdapter.LeScanCallback, ConnectionManagerListAdapter.OnDeviceSelectedListener {

    private final static String TAG = ConnectionManagerFragment.class.getSimpleName();

    //Member Variables
    //--------------------------------------------------------------------------------
    @InjectView(R.id.connectionManagerRecyclerView) RecyclerView mRecyclerView;
    @InjectView(R.id.connectionManagerRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    DividerItemDecoration mDividerItemDecoration;

    private BLEService mBluetoothLeService;
    private BluetoothAdapter mBluetoothAdapter;
    public static ArrayList<BluetoothDevice> mDevices = new ArrayList<>();

    //Get bleduino settings
    boolean isFilterActive;

    // This is how we talk to MainActivity
    ConnectionManagerListener mListener;

    // MainActivity must implement this interface.
    public interface ConnectionManagerListener {
        void connectionManagerEvent();

        //Add more as necessary.
        // ...
    }

    // Ignore this
    //--------------------------------------------------------------------------------
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

        setupBLE();
        setupAdapter();

        //Get bleduino settings
        SharedPreferences prefs = getActivity().getSharedPreferences(SettingsListItem.SETTINGS_FILE, 0);
        isFilterActive = prefs.getBoolean(SettingsListItem.SETTING_FILTER, false);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.accentColor);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCallback();
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mSwipeRefreshLayout.isRefreshing()){
            mSwipeRefreshLayout.setRefreshing(false);
            Log.d(TAG, "refreshing stop");
        }

        mBluetoothAdapter.stopLeScan(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (ConnectionManagerListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ConnectionManagerListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //Use one of the interface functions whenever you need to talk to MainActivity
        mListener.connectionManagerEvent();
        manualRefresh();
    }

    //================================================================================
    // Le Scanning
    //================================================================================

    private void refreshCallback() {
        Log.e("Conn State ", "" + mBluetoothLeService.getConnectionState());
        mDevices.clear();

        // Start scanning
        startScan();

        // Populate device array (This happens in startLeScan callback, implemented here)
        // filtered by BLEduino or not, depending on settings
        // Make sure connected devices are between "found" and "connected"
        // The rest will be after "found"

        // Run the handler below
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSwipeRefreshLayout.isRefreshing()) {
                    stopScan();
                    setupAdapter();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }, 2500); // <-- Time spent scanning.
    }

    private void manualRefresh() {
        //Start scanning automagically.
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                refreshCallback();
            }
        });
    }

    private void startScan() {
        mBluetoothAdapter.startLeScan(this);
        //setProgressBarIndeterminateVisibility(true);

        //mHandler.postDelayed(mStopRunnable, 2500);
    }

    private void stopScan() {
        mBluetoothAdapter.stopLeScan(this);
        //invalidateOptionsMenu();
        //setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

//        if(!isFilterActive && device.getName().equals("BLEduino")){
//            mDevices.add(device);
//            Log.e("Found BLEduino! ", device.getName());
//        }
//
//        else {
//            mDevices.add(device);
//            Log.e("Found not BLEduino! ", device.getName());
//        }

        //mBluetoothLeService.connect(device.getAddress());

        mDevices.add(device);
        //Log.e("Found BLEduino! ", device.getName());

    }

    //================================================================================
    // Setup BLE - Get BLE Service from MainActivity
    //================================================================================
    private void setupBLE() {
        // Get Bluetooth Adapter from Service
        mBluetoothLeService = ((MainActivity) getActivity()).getBluetoothLeService();
        mBluetoothAdapter = mBluetoothLeService.getBluetoothAdapter();

        //TODO add already connected devices to list.
        mDevices = new ArrayList<>();
    }

    //================================================================================
    // Setup Adapter - Build list from BLE devices found in scan.
    //================================================================================
    public void setupAdapter() {

        String connectedDeviceAddress = null;

        //Add Connected and Found labels.
        //Create LeParsedDevice list to simplify display/handling of the ble devices.
        ArrayList<LeParsedDevice> parsedDevices = new ArrayList<>();
        parsedDevices.add(new LeParsedDevice(LeParsedDevice.CONNECTED_LABEL));
        parsedDevices.add(new LeParsedDevice(LeParsedDevice.FOUND_LABEL)); //shut up, it works.

        // Parse BLE devices into array.
        // TODO Allow connection of multiple devices at once
        //--------------------------------------------------------------------------------
        if(mBluetoothLeService.getConnectionState() == BLEService.STATE_CONNECTED){

            //add connected device to list
            LeParsedDevice connectedDevice = new LeParsedDevice();
            connectedDevice.setAddress(mBluetoothLeService.getBluetoothGatt().getDevice().getAddress());
            connectedDevice.setConnected(true);
            connectedDevice.setName(mBluetoothLeService.getBluetoothGatt().getDevice().getName());

            parsedDevices.add(1, connectedDevice);
        }

        for(BluetoothDevice device : mDevices){

            //add found devices (ie. not connected) to list
            LeParsedDevice nonConnectedDevice = new LeParsedDevice(device.getName(),device.getAddress(),false);

            if(parsedDevices.get(1).isConnected()) {
                if (!nonConnectedDevice.getAddress().equals(mBluetoothLeService.getBluetoothGatt().getDevice().getAddress())) {
                    parsedDevices.add(parsedDevices.size(), nonConnectedDevice);
                }
            }

            else{
                parsedDevices.add(parsedDevices.size(), nonConnectedDevice);
            }

        }

        // Do divider decoration stuff
        //--------------------------------------------------------------------------------
        if (mDividerItemDecoration != null) {
            mRecyclerView.removeItemDecoration(mDividerItemDecoration);
        }

        mDividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST, 1);
        mRecyclerView.addItemDecoration(mDividerItemDecoration);


        // Up up and away.
        ConnectionManagerListAdapter adapter = new ConnectionManagerListAdapter(getActivity(),this,parsedDevices);
        mRecyclerView.setAdapter(adapter);

    }

    //On click of a device.
    @Override
    public void onDeviceSelected(final LeParsedDevice clickedDevice) {

        if(mBluetoothLeService.getConnectionState() == BLEService.STATE_CONNECTED){
            Toast.makeText(getActivity(), "Disconnecting from: " + mBluetoothLeService.getBluetoothGatt().getDevice().getName(), Toast.LENGTH_SHORT).show();
            mBluetoothLeService.disconnect();
            manualRefresh();
            //Toast.makeText(getActivity(),"Disconnect from previous device first", Toast.LENGTH_SHORT).show();
        }

        if(!clickedDevice.isConnected()){

            mBluetoothLeService.connect(clickedDevice.getAddress());
            Toast.makeText(getActivity(), "Connecting", Toast.LENGTH_SHORT).show();

            //TODO handle unsuccessful connections
            // start a loading dialog.
            // when connection state comes back, reset list and end loading dialog.

            manualRefresh();

//            //for now
//            // Run the handler below
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    BluetoothDevice connectedDevice = mBluetoothLeService.getBluetoothGatt().getDevice();
//                    mDevices.remove(connectedDevice);
//                    setupAdapter();
//                }
//            }, 2000); // <-- Time spent scanning.
        }

    }
}
