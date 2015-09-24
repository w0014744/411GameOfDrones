package com.overpaoered.robotremote;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.bluetooth.BluetoothAdapter;

public class LocationListActivity extends AppCompatActivity implements OnClickListener {
    private Button loc1, loc2, loc3, loc4;
    private BtInterface bt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        loc1 = (Button)findViewById(R.id.location1Btn);
        loc1.setOnClickListener(this);

        loc2 = (Button)findViewById(R.id.location2Btn);
        loc2.setOnClickListener(this);

        loc3 = (Button)findViewById(R.id.location3Btn);
        loc3.setOnClickListener(this);

        loc4 = (Button)findViewById(R.id.location4Btn);
        loc4.setOnClickListener(this);
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
            bt.sendData("Fayard GPS location");
        }

        if (v == loc2) {
            bt.sendData("Library GPS location");
        }

        if (v == loc1) {
            bt.sendData("Union GPS location");
        }

        if (v == loc1) {
            bt.sendData("Stadium GPS location");
        }
    }
}
