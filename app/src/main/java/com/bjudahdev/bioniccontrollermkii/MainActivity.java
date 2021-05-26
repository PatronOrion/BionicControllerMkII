package com.bjudahdev.bioniccontrollermkii;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.lang.ref.WeakReference;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private AppBarConfiguration mAppBarConfiguration;

    int durShort = Toast.LENGTH_SHORT;
    int durLong  = Toast.LENGTH_LONG;

    // BT inits
    BluetoothAdapter myBTAdapter;
    BluetoothDevice myBTDevice;
    BTConnectionService myBTConnection;
    private static final UUID MY_UUID_INSECURE = UUID.fromString("c3074d45-0a1c-478d-9946-71491ad8568f");
    public ArrayList<BluetoothDevice> BTDevices = new ArrayList<>();
    public DeviceListAdapter BTDeviceListAdapter;
    ListView dashRecyclerView;

    Slider slider;
    float slider_float;
    int slider_value;
    String slider_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow).setDrawerLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // BT Stuffs
        myBTAdapter = BluetoothAdapter.getDefaultAdapter();
        dashRecyclerView = (ListView) findViewById(R.id.BTListView);
        BTDevices = new ArrayList<>();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(myBR_Bond, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBR_StateChange);
        unregisterReceiver(myBR_Bond);
        unregisterReceiver(myBR_Discovery);
    }

    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBTConnection: Init RFCOM BT Connection");

        myBTConnection.startClient(device, uuid);
    }

    ///////////////////////////////////////////////////////////////////////
    // BT Methods
    public void enableDisableBT(){
        if (myBTAdapter == null){
            Log.d(TAG, "Does not support BT.");
        }
        if (!myBTAdapter.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(myBR_StateChange, BTIntent);
        }
        if (myBTAdapter.isEnabled()){
            myBTAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(myBR_StateChange, BTIntent);
        }
    }

    // Needed for devices running API23+ (Lollipop)
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    public void BTDiscover(View view){
        Log.d(TAG, "btn_BTDiscover: Looking for devices");

        if (myBTAdapter.isDiscovering()){
            myBTAdapter.cancelDiscovery();
            Log.d(TAG, "btn_BTDiscover: Cancel Discovery");

            // Must check permissions for discovery
            checkBTPermissions();

            myBTAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(myBR_Discovery, discoverDevicesIntent);
        }
        if (!myBTAdapter.isDiscovering()){
            // Must check permissions for discovery
            checkBTPermissions();

            myBTAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(myBR_Discovery, discoverDevicesIntent);
        }
    }

    public void BTConnect(View view){
        Log.d(TAG, "btn_BTConnect: Connecting to device");
        startBTConnection(myBTDevice, MY_UUID_INSECURE);
    }

    ///////////////////////////////////////////
    // BT Send data methods
    public void btn_HeightSave(View view){
//        // Get slider value
//        slider = findViewById(R.id.height_slider);
//        slider_float = slider.getValue();
//        slider_value = (int)Math.round(slider_float);
//        //Edit to add ability to parse data on MCU
//        slider_string = Integer.toString(slider_value);
//        byte[] bytes = slider_string.getBytes(Charset.defaultCharset());
        TextInputLayout textInputLayout = findViewById(R.id.height_Number);
        String tmp = textInputLayout.getEditText().getText().toString();
        byte[] bytes = tmp.getBytes(Charset.defaultCharset());
        myBTConnection.write(bytes);
        Log.d(TAG, "btn_HeightSave: BT Message Sent: " + tmp);
    }

    // Broadcast Receivers
    private final BroadcastReceiver myBR_StateChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(myBTAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, myBTAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "BR_StateChange:: State OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "BR_StateChange:: State TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "BR_StateChange:: State ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "BR_StateChange:: State TURNING ON");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver myBR_Discovery = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BTDevices.add(device);
                BTDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter, BTDevices);
                Log.d(TAG, "onReceive:: " + device.getName() + ": " + device.getAddress());
                BTDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter, BTDevices);
                dashRecyclerView = (ListView) findViewById(R.id.BTListView);
                dashRecyclerView.setAdapter(BTDeviceListAdapter);
            }
        }
    };

    private final BroadcastReceiver myBR_Bond = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice myDevice  = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // Case 1: already bonded
                if (myDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "myBR_Bond: BOND_BONDED. ");
                    myBTDevice = myDevice;
                }
                // Case 2: creating bonded
                if (myDevice.getBondState() == BluetoothDevice.BOND_BONDING){
                    Log.d(TAG, "myBR_Bond: BOND_BONDING. ");
                    myBTDevice = myDevice;
                }
                // Case 3: bond broken
                if (myDevice.getBondState() == BluetoothDevice.BOND_NONE){
                    Log.d(TAG, "myBR_Bond: BOND_NONE. Bond Broken. ");
                }
            }
        }
    };

//    private final BroadcastReceiver myBR_Discovery = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                BTDevices.add(device);
//                BTDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter, BTDevices);
//                Log.d(TAG, "onReceive:: " + device.getName() + ": " + device.getAddress());
//                //dashRecyclerView.setAdapter(BTDeviceListAdapter);
//            }
//        }
//    };



    ////////////////////////////////////////////////////////////////
    // Navigation Methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    public void BTDevicesOnItemClick(AdapterView<?> parent, View view, int position, long id) {
        myBTAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: You Clicked a device ");
        String deviceName = BTDevices.get(position).getName();
        String deviceAddress = BTDevices.get(position).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with " + deviceName);
            BTDevices.get(position).createBond();

            myBTDevice = BTDevices.get(position);
            myBTConnection = new BTConnectionService(MainActivity.this);
        }

        toastCall(R.string.BT_ableConnect, 0);

    }

    public void toastCall(int text, int dur){
        int duration = durShort;
        if(dur == durLong){ duration = durLong; };

        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.show();
    }
}