package com.overpaoered.robotremote;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.location.LocationListener;
import android.location.LocationManager;
import android.bluetooth.BluetoothAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class LocationListActivity extends AppCompatActivity implements OnMapReadyCallback,AdapterView.OnItemSelectedListener {
    private Spinner spinner;
    private BtInterface bt = null;

    //Locations
    private Location library = new Location("");
    private Location fayard = new Location("");
    private Location unionEast = new Location("");
    private Location unionWest = new Location("");
    private Location fountain = new Location("");
    private Location location6 = new Location("");
    private Location location7 = new Location("");
    private Location anzalone= new Location("");
    private Location dvic = new Location("");

    private Location current = null;
    private Marker currentMarker = null;
    private Circle des;

    private TextView readOut;
    private BluetoothAdapter mBluetoothAdapter = null;
    static final String TAG = "Device";
    static final int REQUEST_ENABLE_BT = 3;
    private String[] logArray = null;

    LocationManager locManager;

    //Google Map
    private GoogleMap mMap;

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
        bt = DroneRemoteActivity.bt;
        setLocations();

        spinner = (Spinner) findViewById(R.id.locationSelecter);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this , R.array.Locations, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        readOut = (TextView) findViewById(R.id.readOut);

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(getApplicationContext() ,Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        current = locManager.getLastKnownLocation(locManager.GPS_PROVIDER);
        getLocation();
        Location nearest = getNearestLocation(current);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);//Map ID
        mapFragment.getMapAsync(this);
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
        bt.connect();
        
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
/*
    @Override
    public void onClick (View v) {
        if (v == spinner) {


            float bearing = current.bearingTo(fayard);
            float distance = current.distanceTo(fayard);
            readOut.setText(bearing + " , " + distance);

            bt.sendData(((String.valueOf(bearing))));
            bt.sendData(",");
            bt.sendData(((String.valueOf(distance))));
        }


    }
    */


    /*
    ** getLocation - this method gets the users current location based on the phone GPS
    ** and places that location in the current Location object
    **
     */

    public Location getNearestLocation (Location here) {

        Location nearest = new Location("");
        List<Location> locations = new ArrayList<Location>();
        locations.add(library);
        locations.add(fayard);
        locations.add(fountain);
        locations.add(dvic);
        locations.add(unionEast);
        locations.add(unionWest);
        locations.add(anzalone);

        float dist = current.distanceTo(locations.get(0));

        for (Location loc : locations) {
            if (current.distanceTo(loc) <= dist) {
                dist = current.distanceTo(loc);
                nearest = loc;
            }
        }

        return nearest;
    }


    public void getLocation() {

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //This is where we get the location

                current = location;

                if(currentMarker != null)currentMarker.remove();
                currentMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(current.getLatitude(), current.getLongitude())).title("Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.kyang)));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(current.getLatitude(), current.getLongitude())));
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        try {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    //GPS Locations of Important Buildings.
    public void setLocations ()
    {
        library.setLatitude(30.514699);
        library.setLongitude(-90.468361);

        fayard.setLatitude(30.514883);
        fayard.setLongitude(-90.46628);

        fountain.setLatitude(30.514711);
        fountain.setLongitude(-90.467089);

        unionEast.setLatitude(30.514431);
        unionEast.setLongitude(-90.467011);

        unionWest.setLatitude(30.514240);
        unionWest.setLongitude(-90.468123);

        dvic.setLatitude(30.514736);
        dvic.setLongitude(-90.468174);

        location6.setLatitude(30.515035);
        location6.setLongitude(-90.467182);

        location7.setLatitude(30.515140);
        location7.setLongitude(-90.467215);

        anzalone.setLatitude(30.515217);
        anzalone.setLongitude(-90.466925);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.addMarker(new MarkerOptions().position(new LatLng(fayard.getLatitude(), fayard.getLongitude())).title("Fayard Hall"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(library.getLatitude(), library.getLongitude())).title("Library"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(fountain.getLatitude(), fountain.getLongitude())).title("Katrina Fountain"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(unionEast.getLatitude(), unionEast.getLongitude())).title("Union East"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(unionWest.getLatitude(), unionWest.getLongitude())).title("Union West"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(dvic.getLatitude(), dvic.getLongitude())).title("Dvic"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(anzalone.getLatitude(), anzalone.getLongitude())).title("Anzalone"));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(fountain.getLatitude(),fountain.getLongitude()),20.0f));


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(des != null) des.remove();
        LatLng latLng = new LatLng(fayard.getLatitude(),fayard.getLongitude());
        String locName = parent.getItemAtPosition(position).toString();
        readOut.setText(locName);

        if(locName.equals("Fayard")) latLng = new LatLng(fayard.getLatitude(),fayard.getLongitude());
        else if(locName.equals("DVic")) latLng = new LatLng(dvic.getLatitude(),dvic.getLongitude());
        else if(locName.equals("Katrina Fountain")) latLng = new LatLng(fountain.getLatitude(),fountain.getLongitude());
        else if(locName.equals("Anzalone")) latLng = new LatLng(anzalone.getLatitude(),anzalone.getLongitude());
        else if(locName.equals("Union East")) latLng = new LatLng(unionEast.getLatitude(),unionEast.getLongitude());
        else if(locName.equals("Union West")) latLng = new LatLng(unionWest.getLatitude(),unionWest.getLongitude());
        else if(locName.equals("Library")) latLng = new LatLng(library.getLatitude(),library.getLongitude());


        des = mMap.addCircle(new CircleOptions().center(latLng).radius(10));

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
