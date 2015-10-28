package com.overpaoered.robotremote;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.location.Criteria;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);
        setRequestedOrientation(ActivityInfo
                .SCREEN_ORIENTATION_PORTRAIT);
        setLocations();

        loc1 = (Button)findViewById(R.id.location1Btn);
        loc1.setOnClickListener(this);

        loc2 = (Button)findViewById(R.id.location2Btn);
        loc2.setOnClickListener(this);

        loc3 = (Button)findViewById(R.id.location3Btn);
        loc3.setOnClickListener(this);

        loc4 = (Button)findViewById(R.id.location4Btn);
        loc4.setOnClickListener(this);

        readOut = (TextView)findViewById(R.id.readOut);

        LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        String provider = locManager.GPS_PROVIDER;
        current = new Location(provider);
        double currentLongitude = current.getLongitude();
        double currentLatitude = current.getLatitude();
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
            bt.sendData("Union GPS location");
        }

        if (v == loc4) {
            bt.sendData("Stadium GPS location");
        }
    }

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
