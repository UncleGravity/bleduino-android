package com.kytelabs.bleduino;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.kytelabs.bleduino.adapters.DrawerListAdapter;
import com.kytelabs.bleduino.ble.BLEGattAttributes;
import com.kytelabs.bleduino.ble.BLEService;
import com.kytelabs.bleduino.fragments.ConnectionManagerFragment;
import com.kytelabs.bleduino.fragments.ModulesFragment;
import com.kytelabs.bleduino.fragments.SettingsFragment;
import com.kytelabs.bleduino.pojos.NavigationItem;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.fabric.sdk.android.Fabric;


public class MainActivity
        extends ActionBarActivity
        implements
        AdapterView.OnItemClickListener,
        ConnectionManagerFragment.ConnectionManagerListener,
        ModulesFragment.ModulesFragmentListener,
        SettingsFragment.SettingsFragmentListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    //Constants
    public static final int CONSOLE = 1;
    public static final int CONNECTION_MANAGER = 2;
    public static final int SETTINGS = 3;

    //Member Variables
    //--------------------------------------------------------------------------------
    @InjectView(R.id.app_bar) Toolbar mToolbar;
    @InjectView(R.id.drawerLayout) DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    @InjectView(R.id.listViewDrawer) ListView mListView;
    private NavigationItem[] mNavigationItems;

    //BLE Member Variables
    //--------------------------------------------------------------------------------
    BLEService mBluetoothLeService;
    BluetoothGattCharacteristic mFirmataCharacteristic;

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
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
        }
    };

    //================================================================================
    // Activity Life Cycle Methods
    //================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this); //neverforget.jpg

        // Toolbar and Drawer Setup
        //----------------------------------------------------------------------------
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerSetUp();

        // List View Setup
        //----------------------------------------------------------------------------
        populateNavigation(CONSOLE);
        listViewSetUp();

        setFragment(0, ModulesFragment.class);
        //mDrawerRecyclerView.getAdapter();

        // BLEService Setup (bind service to activity)
        //----------------------------------------------------------------------------
        Intent gattServiceIntent = new Intent(this, BLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        // Register MainActivity to receive broadcasts from the BLEService
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

    }

    //

    @Override
    protected void onResume() {
        super.onResume();

        /*
         * Check for Bluetooth LE Support.  In production, our manifest entry will keep this
         * from installing on these devices, but this will allow test devices or other
         * sideloads to report whether or not the feature exists.
         */
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private void listViewSetUp() {

        List<NavigationItem> navigationList = Arrays.asList(mNavigationItems);
        // Set adapter
        DrawerListAdapter adapter = new DrawerListAdapter(getApplicationContext(), navigationList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);
    }

    //================================================================================
    // Navigation
    //================================================================================

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (!mNavigationItems[position].isHeader() && !mNavigationItems[position].isDivider() && !mNavigationItems[position].isSelected()) {

            populateNavigation(position);
            listViewSetUp();

            Class<? extends Fragment> fragmentClass = mNavigationItems[position].getFragmentClass();
            setFragment(position, fragmentClass);
        }

        if (mNavigationItems[position].isSelected()) {
            mDrawerLayout.closeDrawers();
        }

    }


    private void populateNavigation(int selected) {
        mNavigationItems = new NavigationItem[6];

        mNavigationItems[0] = new NavigationItem();
        mNavigationItems[0].setText("General");
        mNavigationItems[0].setHeader(true);

        mNavigationItems[1] = new NavigationItem();
        mNavigationItems[1].setFragmentClass(ModulesFragment.class);
        mNavigationItems[1].setText("Modules");
        mNavigationItems[1].setIconId(R.drawable.ic_console_black_24dp);

        mNavigationItems[2] = new NavigationItem();
        mNavigationItems[2].setFragmentClass(ConnectionManagerFragment.class);
        mNavigationItems[2].setText("Connection Manager");
        mNavigationItems[2].setIconId(R.drawable.ic_bluetooth_searching_black_24dp);

        mNavigationItems[3] = new NavigationItem();
        mNavigationItems[3].setFragmentClass(SettingsFragment.class);
        mNavigationItems[3].setText("Settings");
        mNavigationItems[3].setIconId(R.drawable.ic_settings_black_24dp);

        mNavigationItems[4] = new NavigationItem();
        mNavigationItems[4].setDivider(true);

        mNavigationItems[5] = new NavigationItem();
        mNavigationItems[5].setText("Other Stuff");
        mNavigationItems[5].setHeader(true);

        // Set selected from parameter
        mNavigationItems[selected].setSelected(true);

    }

    public void setFragment(int position, Class<? extends Fragment> fragmentClass) {
        try {
            Fragment fragment = fragmentClass.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameContainer, fragment, fragmentClass.getSimpleName())
                    .commit();

            //mListView.setItemChecked(position, true);
            mDrawerLayout.closeDrawers();
            mListView.invalidateViews();

        } catch (Exception ex) {
            Log.e("setFragment", ex.getMessage());
        }
    }


    //================================================================================
    // Drawer Set Up
    //================================================================================

    private void drawerSetUp() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                if (newState == DrawerLayout.STATE_DRAGGING && mNavigationItems[CONSOLE].isSelected()) {
                    //ConsoleFragment consoleFragment = (ConsoleFragment) getSupportFragmentManager().findFragmentById(R.id.frameContainer);
                    //consoleFragment.onKeyboardShouldClose();
                }


            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened
                if (mNavigationItems[CONSOLE].isSelected()) {
                    //ConsoleFragment consoleFragment = (ConsoleFragment) getSupportFragmentManager().findFragmentById(R.id.frameContainer);
                    //consoleFragment.onKeyboardShouldClose();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
                if (mNavigationItems[CONSOLE].isSelected()) {
                    //ConsoleFragment consoleFragment = (ConsoleFragment) getSupportFragmentManager().findFragmentById(R.id.frameContainer);
                    //consoleFragment.onKeyboardShouldOpen();
                }
            }

        }; // Drawer Toggle Object Made
        mDrawerLayout.setDrawerListener(mDrawerToggle); // Drawer Listener set to the Drawer toggle
    }


    //================================================================================
    // Drawer Override Methods
    //================================================================================


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mListView)) {
            mDrawerLayout.closeDrawer(mListView);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    //================================================================================
    // Actionbar/Toolbar Methods
    //================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    // Bluetooth Low Energy Code
    //================================================================================

    private void showNotification(String message) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.kytelabs_logo)
                        .setContentTitle("BLEduino Notification")
                        .setContentText(message);
//
//        // Creates an explicit intent for an Activity in your app
//        Intent resultIntent = new Intent(this, MainActivity.class);
//
//        // The stack builder object will contain an artificial back stack for the started Activity.
//        // This ensures that navigating backward from the Activity leads out of
//        // your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//
//        // Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(MainActivity.class);
//
//        // Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//
//        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        long[] pattern = {500,500,500};
        mBuilder.setVibrate(pattern);
        mNotificationManager.notify(1, mBuilder.build());

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean subscribe(){

        //TODO Subscribe to notification on firmataclick.
        //----------  Get characteristic to be used in this Activity -------------//

        if(mBluetoothLeService.getBluetoothGatt() != null){

            if(mBluetoothLeService.getConnectionState() == BLEService.STATE_DISCONNECTED){
                return false;
            }

            //Get list of services
            List<BluetoothGattService> mBluetoothGattServices = mBluetoothLeService.getSupportedGattServices(); //mBluetoothGatt.getServices();

            //Look for uart service
            for (BluetoothGattService leService : mBluetoothGattServices) {
                //Found service, get write characteristic, put value, then write it.
                if(leService.getUuid().equals(UUID.fromString(BLEGattAttributes.BLEDUINO_NOTIFICATION_SERVICE))){
                    mFirmataCharacteristic = leService.getCharacteristic(UUID.fromString(BLEGattAttributes.BLEDUINO_NOTIFICATION_CHARACTERISTIC));

                    // Subscribe
                    mBluetoothLeService.setCharacteristicNotification(mFirmataCharacteristic, true);
                    return true;
                }}}

        return false;
    }

    //Allow Fragments to use the BLE service.
    public BLEService getBluetoothLeService() {
        return mBluetoothLeService;
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

            else if (BLEService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // All services and characteristics discovered.
                // Show all the supported services and characteristics on the user interface.
                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();

            }

            else if (BLEService.ACTION_DATA_AVAILABLE.equals(action)){
                Log.d(TAG, "Data Received!");
                //Log.d(TAG, (intent.getByteArrayExtra("EXTRA_DATA")[0] + ""));

//                try {
//                    updateList("BLEduino", new String(intent.getByteArrayExtra("EXTRA_DATA"), "UTF-8"));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }

            }

            else if (BLEService.ACTION_GATT_RSSI.equals(action)) {
               //
                Toast.makeText(getApplicationContext(), "ACTION_GATT_RSSI", Toast.LENGTH_SHORT).show();
            }

            else if(BLEService.ACTION_NOTIFICATION_AVAILABLE.equals(action)){
                try {
                    String message = new String(intent.getByteArrayExtra("EXTRA_DATA"), "UTF-8");
                    Log.d(TAG, message );
                    //TODO Present notification
                    showNotification(message);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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
        intentFilter.addAction(BLEService.ACTION_NOTIFICATION_AVAILABLE);
        return intentFilter;
    }

    //================================================================================
    // Talking to Fragments
    //================================================================================

    @Override
    public void connectionManagerEvent() {
        Log.d(TAG, "Connection Manager Fragment Listener is Working");
    }


    @Override
    public void modulesFragmentEvent() {

    }

    @Override
    public void moduleAdapterEvent(View caller, int index) {
        Log.d(TAG, "NOTIFICATION CLICK");

        if(subscribe()){
            Toast.makeText(this, "Subscribing to notifications", Toast.LENGTH_SHORT).show();

            ((ImageView) caller.findViewById(R.id.imageView)).setImageResource(R.drawable.ic_notifications_black_48dp);
        } else {
            Toast.makeText(this, "Cannot subscribe to notifications", Toast.LENGTH_SHORT).show();
            ((ImageView) caller.findViewById(R.id.imageView)).setImageResource(R.drawable.ic_notifications_none_black_48dp);
        }
    }

    @Override
    public void settingsFragmentEvent() {
        Log.d(TAG, "Settings Fragment Listener is Working");
    }
}
