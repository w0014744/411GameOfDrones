package com.overpaoered.robotremote;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.location.LocationListener;
import android.location.LocationManager;
import android.bluetooth.BluetoothAdapter;
import android.widget.TextView;

public class LocationListActivity extends AppCompatActivity implements OnClickListener {
    private Button loc1, loc2, loc3, loc4;
    private BtInterface bt = null;
    private Location library = new Location("");
    private Location stadium = new Location("");
    private Location fayard = new Location("");
    private Location union = new Location("");
    private Location current = null;
    private TextView readOut;
    private BluetoothAdapter mBluetoothAdapter = null;
    static final String TAG = "Device";
    static final int REQUEST_ENABLE_BT = 3;
    private String[] logArray = null;

    LocationManager locManager;

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String data = msg.getData().getString("receivedData");
            addToLog(data);
        }
    };

    final Handler handlerStatus = new Handler() {
        public void handleMessage(Message msg) {
            int status = msg.arg1;
            if (status == BtInterface.CONNECTED) {
                addToLog("Connected");
            } else if (status == BtInterface.DISCONNECTED) {
                addToLog("Disconnected");
            }
        }
    };

    /*
    ** addToLog - this method adds a string to the on-screen log that indicates which button has been pushed
    ** @param message - the message to be written to the log
    **
     */
    private void addToLog(String message) {
        for (int i = 1; i < logArray.length; i++) {
            logArray[i - 1] = logArray[i];
        }
        logArray[logArray.length - 1] = message;

        readOut.setText("");
        for (int i = 0; i < logArray.length; i++) {
            if (logArray[i] != null) {
                readOut.append(logArray[i] + "\n");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);
        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);
        bt = new BtInterface(handlerStatus, handler);
        setLocations();

        loc1 = (Button) findViewById(R.id.location1Btn);
        loc1.setOnClickListener(this);

        loc2 = (Button) findViewById(R.id.location2Btn);
        loc2.setOnClickListener(this);

        loc3 = (Button) findViewById(R.id.location3Btn);
        loc3.setOnClickListener(this);

        loc4 = (Button) findViewById(R.id.location4Btn);
        loc4.setOnClickListener(this);

        readOut = (TextView) findViewById(R.id.readOut);

        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        double currentLongitude = current.getLongitude();
        double currentLatitude = current.getLatitude();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.v(TAG, "Device does not support Bluetooth");
        }
        else{
            if (!mBluetoothAdapter.isEnabled()){
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else{
                bt = new BtInterface(handlerStatus, handler);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_list, menu);
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

    @Override
    public void onClick (View v) {
        if (v == loc1) {
            float bearing = current.bearingTo(fayard);
            float distance = current.distanceTo(fayard);
            readOut.setText(bearing + " , " + distance);
            String data = bearing + "," + distance;
            bt.sendData(data);

        }

        if (v == loc2) {
            float bearing = current.bearingTo(library);
            float distance = current.distanceTo(library);
            readOut.setText(bearing + " , " + distance);
            String data = bearing + "," + distance;
            bt.sendData(data);
        }

        if (v == loc3) {
            float bearing = current.bearingTo(union);
            float distance = current.distanceTo(union);
            readOut.setText(bearing + " , " + distance);
            String data = bearing + "," + distance;
            bt.sendData(data);
        }

        if (v == loc4) {
            float bearing = current.bearingTo(stadium);
            float distance = current.distanceTo(stadium);
            readOut.setText(bearing + ", " + distance);
            String data = bearing + "," + distance;
            bt.sendData(data);
        }
    }
    /*
    ** getLocation - this method gets the users current location based on the phone GPS
    ** and places that location in the current Location object
    **
     */
    public void getLocation() {

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //This is where we get the location
                readOut.setText(location.getLongitude() + "  ,  " + location.getLatitude());
                current = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        try {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    //GPS Locations of Important Buildings.
    public void setLocations ()
    {
        library.setLatitude(30.514708);
        library.setLongitude(-90.468334);
        stadium.setLatitude(30.511353);
        stadium.setLongitude(-90.469176);
        fayard.setLatitude(30.514883);
        fayard.setLongitude(-90.46628);
        union.setLatitude(30.514148);
        union.setLongitude(-90.467122);
    }
}
