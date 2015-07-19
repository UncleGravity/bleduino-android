package com.kytelabs.bleduino.modules;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kytelabs.bleduino.R;
import com.kytelabs.bleduino.adapters.ConsoleListAdapter;
import com.kytelabs.bleduino.ble.BLEGattAttributes;
import com.kytelabs.bleduino.ble.BLEService;
import com.kytelabs.bleduino.fragments.ConnectionManagerFragment;
import com.kytelabs.bleduino.pojos.ConsoleListItem;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.fabric.sdk.android.Fabric;

public class ConsoleModuleActivity extends ActionBarActivity {

    private final static String TAG = ConsoleModuleActivity.class.getSimpleName();

    //Member Variables
    //--------------------------------------------------------------------------------
    private ArrayList<ConsoleListItem> mMessages; //empty at first, filled dynamically by user
    @InjectView(R.id.consoleRecyclerView) RecyclerView mRecyclerView;
    @InjectView(R.id.app_bar) Toolbar mToolbar;
    @InjectView(R.id.consoleEditText) EditText mEditText;

    // BLE variables
    //----------------------------------------------------------------------------

    BluetoothGattCharacteristic mUartWriteCharacteristic;
    BluetoothGattCharacteristic mUartReadCharacteristic;
    BLEService mBluetoothLeService;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mBluetoothLeService = ((BLEService.LocalBinder) service).getService();

            if (!mBluetoothLeService.initialize()) {
                //Bluetooth is disabled
                Log.e(TAG, "Unable to initialize BLE");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(enableBtIntent);
            }

            Log.e(TAG, "OnServiceConnected called");

            //----------  Get characteristic to be used in this module -------------//
            if(mBluetoothLeService.getBluetoothGatt() != null){

                //Get list of services
                List<BluetoothGattService> mBluetoothGattServices = mBluetoothLeService.getSupportedGattServices(); //mBluetoothGatt.getServices();

                //Look for uart service
                for (BluetoothGattService leService : mBluetoothGattServices) {
                    //Found service, get write characteristic, put value, then write it.
                    if(leService.getUuid().equals(UUID.fromString(BLEGattAttributes.BLEDUINO_UART_SERVICE))){
                        mUartReadCharacteristic = leService.getCharacteristic(UUID.fromString(BLEGattAttributes.BLEDUINO_UART_READ_CHARACTERISTIC));
                        mUartWriteCharacteristic = leService.getCharacteristic(UUID.fromString(BLEGattAttributes.BLEDUINO_UART_WRITE_CHARACTERISTIC));

                        // Subscribe
                        mBluetoothLeService.setCharacteristicNotification(mUartReadCharacteristic, true);
                    }}}

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
        }
    };

    //================================================================================
    // Activity Life Cycle
    //================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_console_module);
        ButterKnife.inject(this);

        // Hide editText bottom bar (optional)
        //mEditText.setBackground(null);

        //Toolbar Setup
        //--------------------------------------------------------------------------------
        setSupportActionBar(mToolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recycler Setup
        //--------------------------------------------------------------------------------
        mMessages = new ArrayList<>();
        //populateModules(); //TODO only for debugging. Remove this.

        setupAdapter();

        // BLEService Setup (bind service to activity)
        //----------------------------------------------------------------------------
        Intent gattServiceIntent = new Intent(this, BLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register MainActivity to receive broadcasts from the BLEService
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        //Show keyboard
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                .toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister BLE service
        unregisterReceiver(mGattUpdateReceiver);

        // Hide keyboard
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    //================================================================================
    // Recycler View
    //================================================================================

    private void setupAdapter() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        ConsoleListAdapter adapter = new ConsoleListAdapter(this, mMessages);
        mRecyclerView.setAdapter(adapter);

        mRecyclerView.scrollToPosition(mMessages.size() - 1);
    }

    private void updateList(String origin, String message) {

        ConsoleListItem newMessage = new ConsoleListItem();
        newMessage.setMessageSourceName(origin);
        newMessage.setMessage(message);

        mMessages.add(newMessage);
        setupAdapter();
    }

//    private void populateModules() {
//        mMessages = new ArrayList<>();
//
//        mMessages.add(new ConsoleListItem());
//        mMessages[0].setMessageSourceName("iOS");
//        mMessages[0].setMessage("Message 1!lkjasdflijef iljeflijeflij lskj sdfl sdlfkj sdfl sdfl sdfl sfl sdfl sdfl sdfl sdfl sdfl sdlf sdfl sdfl sdlf sdlf sdf.");
//
//        mMessages[1] = new ConsoleListItem();
//        mMessages[1].setMessageSourceName("BLEduino");
//        mMessages[1].setMessage("Message 2!");
//    }

    //================================================================================
    // Action Bar
    //================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_console_module, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == R.id.action_console_send){

            if(sendLeString(mEditText.getText().toString())){
                updateList("Android", mEditText.getText().toString());
                mEditText.setText("");
            }

            //Log.i(TAG, "Enter pressed with text: " + mEditText.getText());
        }

        return super.onOptionsItemSelected(item);
    }

    //================================================================================
    // Bluetooth Low Energy Code
    //================================================================================

    private boolean sendLeString(String text) {

        // TODO handle when there is no device connected.  mBluetoothGatt == null
        // TODO handle sending data bigger than 20 bytes.

        if(text.equals("")){
            return false;
        }

        if(text.length() > 20){
            Toast.makeText(getApplicationContext(), "Data must be less than 20 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(mBluetoothLeService.getBluetoothGatt() == null){
            Toast.makeText(getApplicationContext(), "No BLEduino connected", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(mBluetoothLeService.getConnectionState() == BLEService.STATE_DISCONNECTED){
            Toast.makeText(getApplicationContext(), "No BLEduino connected", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Write text to uart characteristic
        mUartWriteCharacteristic.setValue(text.getBytes());
        mBluetoothLeService.writeCharacteristic(mUartWriteCharacteristic);

        return true;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BLEService.ACTION_GATT_CONNECTED.equals(action)) {

                Toast.makeText(getApplicationContext(), "Discovering Services.", Toast.LENGTH_SHORT).show();
                // Display "Connected" notification.

            }

            else if (BLEService.ACTION_GATT_DISCONNECTED.equals(action)) {

                //TODO make this into a dialog
                Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();

                try{
                    ConnectionManagerFragment currentFragment = (ConnectionManagerFragment)
                            getSupportFragmentManager().findFragmentById(R.id.frameContainer);

                    currentFragment.setupAdapter();

                } catch (Exception e){
                    Log.e(TAG, "Device disconnected. Not displaying connection manager, so do nothing.");
                }

            }

            else if (BLEService.ACTION_DATA_AVAILABLE.equals(action)){
                Log.d(TAG, "Data Received!");
                //Log.d(TAG, (intent.getByteArrayExtra("EXTRA_DATA")[0] + ""));

                try {
                    updateList("BLEduino", new String(intent.getByteArrayExtra("EXTRA_DATA"), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            else if (BLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // All services and characteristics discovered.
                // Show all the supported services and characteristics on the user interface.
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();

            }

        }
    };

    // No idea, check what an update filter is.
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BLEService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BLEService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BLEService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
