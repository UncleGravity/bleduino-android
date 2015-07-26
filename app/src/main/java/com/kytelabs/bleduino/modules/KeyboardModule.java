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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kytelabs.bleduino.R;
import com.kytelabs.bleduino.ble.BLEGattAttributes;
import com.kytelabs.bleduino.ble.BLEService;
import com.kytelabs.bleduino.pojos.SettingsListItem;

import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.fabric.sdk.android.Fabric;

public class KeyboardModule extends ActionBarActivity {

    private final static String TAG = KeyboardModule.class.getSimpleName();

    @InjectView(R.id.app_bar) Toolbar mToolbar;
    @InjectView(R.id.keyboardEditText) EditText mEditText;

    // BLE variables
    //----------------------------------------------------------------------------

    BLEService mBluetoothLeService;
    BluetoothGattCharacteristic mBleduinoWriteCharacteristic;

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
                        mBleduinoWriteCharacteristic = leService.getCharacteristic(UUID.fromString(BLEGattAttributes.BLEDUINO_UART_WRITE_CHARACTERISTIC));
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
        setContentView(R.layout.activity_keyboard_module);
        ButterKnife.inject(this);

        // Hide editText bottom bar (optional)
        mEditText.setBackground(null);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
    // Action Bar
    //================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_keyboard_module, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_keyboard_send) {
            sendLeString(mEditText.getText().toString());
            //Log.i(TAG, "Enter pressed with text: " + mEditText.getText());
            mEditText.setText("");
        }

        else if (id == R.id.action_keyboard_backspace) {
            sendLeString("\b");
        }

        return super.onOptionsItemSelected(item);
    }

    //==========================================================================
    // Bluetooth Low Energy Code
    //================================================================================

    private boolean sendLeString(String text) {

        if(text.length() > 20){
            Toast.makeText(getApplicationContext(), "Data must be less than 20 characters.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(mBluetoothLeService.getBluetoothGatt() == null){
            Toast.makeText(getApplicationContext(), "No BLEduino connected", Toast.LENGTH_SHORT).show();
            return false;
        }



        // Write text to uart characteristic
        mBleduinoWriteCharacteristic.setValue(text.getBytes());
        mBluetoothLeService.writeCharacteristic(mBleduinoWriteCharacteristic);

        return true;
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BLEService.ACTION_GATT_CONNECTED.equals(action)) {
                Toast.makeText(getApplicationContext(), "Discovering Services.", Toast.LENGTH_SHORT).show();
            }

            else if (BLEService.ACTION_GATT_DISCONNECTED.equals(action)) {
                //TODO make this into a dialog

                //Get bleduino settings
                SharedPreferences prefs = getSharedPreferences(SettingsListItem.SETTINGS_FILE, 0);

                if(prefs.getBoolean(SettingsListItem.SETTING_NOTIFY, true)){
                    Toast.makeText(getApplicationContext(),"Disconnected!",Toast.LENGTH_SHORT).show();
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
