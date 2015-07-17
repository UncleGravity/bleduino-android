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
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kytelabs.bleduino.R;
import com.kytelabs.bleduino.adapters.LedListAdapter;
import com.kytelabs.bleduino.ble.BLEGattAttributes;
import com.kytelabs.bleduino.ble.BLEService;
import com.kytelabs.bleduino.fragments.ConnectionManagerFragment;
import com.kytelabs.bleduino.pojos.LedListItem;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.fabric.sdk.android.Fabric;

public class LedModuleActivity extends ActionBarActivity implements LedListAdapter.OnLedClickListener {

    private final static String TAG = LedModuleActivity.class.getSimpleName();

    //Member Variables
    //--------------------------------------------------------------------------------
    private LedListItem[] mLedListItems;
    @InjectView(R.id.ledRecyclerView) RecyclerView mRecyclerView;
    @InjectView(R.id.app_bar) Toolbar mToolbar;

    // BLE variables
    //----------------------------------------------------------------------------

    BluetoothGattCharacteristic mFirmataCharacteristic;
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
                    if(leService.getUuid().equals(UUID.fromString(BLEGattAttributes.BLEDUINO_FIRMATA_SERVICE))){
                        mFirmataCharacteristic = leService.getCharacteristic(UUID.fromString(BLEGattAttributes.BLEDUINO_FIRMATA_CHARACTERISTIC));

                        // Subscribe
                        //mBluetoothLeService.setCharacteristicNotification(mFirmataCharacteristic, true);
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
        setContentView(R.layout.activity_led_module);
        ButterKnife.inject(this);

        //Toolbar Setup
        //--------------------------------------------------------------------------------
        setSupportActionBar(mToolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //RecyclerView Setup
        //--------------------------------------------------------------------------------
        populateLedList();
        setupAdapter();

        // BLEService Setup (bind service to activity)
        //----------------------------------------------------------------------------
        Intent gattServiceIntent = new Intent(this, BLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister BLE service
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    //================================================================================
    // Action Bar
    //================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_led_module, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //================================================================================
    // Recycler View
    //================================================================================

    private void populateLedList() {
        mLedListItems = new LedListItem[21];

        mLedListItems[0] = new LedListItem("0");
        mLedListItems[1] = new LedListItem("1");
        mLedListItems[2] = new LedListItem("2");

        mLedListItems[3] = new LedListItem("3");
        mLedListItems[4] = new LedListItem("4");
        mLedListItems[5] = new LedListItem("5");

        mLedListItems[6] = new LedListItem("6");
        mLedListItems[7] = new LedListItem("7");
        mLedListItems[8] = new LedListItem("8");

        mLedListItems[9] = new LedListItem("9");
        mLedListItems[10] = new LedListItem("10");
        mLedListItems[11] = new LedListItem("13");

        mLedListItems[12] = new LedListItem("A0");
        mLedListItems[13] = new LedListItem("A1");
        mLedListItems[14] = new LedListItem("A2");

        mLedListItems[15] = new LedListItem("A3");
        mLedListItems[16] = new LedListItem("A4");
        mLedListItems[17] = new LedListItem("A5");

        mLedListItems[18] = new LedListItem("MOSI");
        mLedListItems[19] = new LedListItem("MISO");
        mLedListItems[20] = new LedListItem("SCK");

    }

    private void setupAdapter() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        LedListAdapter adapter = new LedListAdapter(this, Arrays.asList(mLedListItems)); //TODO LedListAdapter
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLedClick(LedListItem led) {
        // do stuff
        sendLedCommand(led.getPinNumber(), led.isPinState());
    }

    //================================================================================
    // Bluetooth Low Energy Code
    //================================================================================

    private boolean sendLedCommand(int pinNumber, boolean pinState) {

        // TODO handle when there is no device connected.  mBluetoothGatt == null

        if(mBluetoothLeService.getBluetoothGatt() == null){
            Toast.makeText(getApplicationContext(), "No BLEduino connected", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(mBluetoothLeService.getConnectionState() == BLEService.STATE_DISCONNECTED){
            Toast.makeText(getApplicationContext(), "No BLEduino connected", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Toggle LED pin
        byte[] ledCommand = new byte[3];
        ledCommand[0] = (byte) pinNumber;
        ledCommand[1] = 0;
        ledCommand[2] = (byte) (pinState ? 1 : 0);

        mFirmataCharacteristic.setValue(ledCommand);
        mBluetoothLeService.writeCharacteristic(mFirmataCharacteristic);

        Log.d(TAG, "command = [" + ledCommand[0] + ", " + ledCommand[1] + ", " + ledCommand[2] + "]");

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
            }

            else if (BLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // All services and characteristics discovered.
                // Show all the supported services and characteristics on the user interface.
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();

            }

            else if (BLEService.ACTION_GATT_RSSI.equals(action)) {
                //
                //Toast.makeText(getApplicationContext(), "ACTION_GATT_RSSI", Toast.LENGTH_SHORT).show();
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
