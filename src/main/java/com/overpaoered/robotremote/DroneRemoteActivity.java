package com.overpaoered.robotremote;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DroneRemoteActivity extends Activity implements OnClickListener {
	private TextView logview, readout;
	private Button connect, deconnect, gpsMenu, incAlt, decAlt;
	private ImageView forwardArrow, backArrow, rightArrow, leftArrow, stop, leftBack, rightBack, leftFor, rightFor;
	private BluetoothAdapter mBluetoothAdapter = null;
	
	private String[] logArray = null;

	private BtInterface bt = null;
	
	static final String TAG = "Device";
	static final int REQUEST_ENABLE_BT = 3;

	final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String data = msg.getData().getString("receivedData");
            addToLog(data);            
        }
    };

    final Handler handlerStatus = new Handler() {
        public void handleMessage(Message msg) {
            int status = msg.arg1;
            if(status == BtInterface.CONNECTED) {
            	addToLog("Connected");
            } else if(status == BtInterface.DISCONNECTED) {
            	addToLog("Disconnected");
            }
        }
    };

    private void addToLog(String message){
    	for (int i = 1; i < logArray.length; i++){
        	logArray[i-1] = logArray[i];
        }
        logArray[logArray.length - 1] = message;
        
        logview.setText("");
        for (int i = 0; i < logArray.length; i++){
        	if (logArray[i] != null){
        		logview.append(logArray[i] + "\n");
        	}
        }    	
    }
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);

        
        logview = (TextView)findViewById(R.id.logview);

		readout = (TextView)findViewById(R.id.readOut);
        readout.setText(R.string.manual);

        logArray = new String[3];
        
        connect = (Button)findViewById(R.id.connect);
        connect.setOnClickListener(this);
        
        deconnect = (Button)findViewById(R.id.deconnect);
        deconnect.setOnClickListener(this);

        gpsMenu = (Button)findViewById(R.id.gpsMenu);
        gpsMenu.setOnClickListener(this);

        incAlt = (Button)findViewById(R.id.increaseAlt);
        incAlt.setOnClickListener(this);

        decAlt = (Button)findViewById(R.id.decreaseAlt);
        decAlt.setOnClickListener(this);
        
        forwardArrow = (ImageView)findViewById(R.id.forward);
        forwardArrow.setOnClickListener(this);

		leftFor = (ImageView)findViewById(R.id.leftFor);
		leftFor.setOnClickListener(this);

		rightFor = (ImageView)findViewById(R.id.rightFor);
		rightFor.setOnClickListener(this);

        backArrow = (ImageView)findViewById(R.id.back);
        backArrow.setOnClickListener(this);

		leftBack = (ImageView)findViewById(R.id.leftBack);
		leftBack.setOnClickListener(this);

		rightBack = (ImageView)findViewById(R.id.rightBack);
		rightBack.setOnClickListener(this);

        rightArrow = (ImageView)findViewById(R.id.right);
        rightArrow.setOnClickListener(this);

        leftArrow = (ImageView)findViewById(R.id.left);
        leftArrow.setOnClickListener(this);

        stop = (ImageView)findViewById(R.id.stop);
        stop.setOnClickListener(this);



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

	protected void onActivityResult(int requestCode, int resultCode, Intent moreData){
    	if (requestCode == REQUEST_ENABLE_BT){
    		if (resultCode == Activity.RESULT_OK){
    			bt = new BtInterface(handlerStatus, handler);
    		}
    		else if (resultCode == Activity.RESULT_CANCELED)
    			Log.v(TAG, "BT not activated");
    		else
    			Log.v(TAG, "result code not known");
    	}
    	else{
    		Log.v(TAG, "request code not known");    	
    	}
     }

	@Override
	public void onClick(View v) {
		if(v == connect) {
			addToLog("Trying to connect");
			bt.connect();
		} 
		else if(v == deconnect) {
			addToLog("closing connection");
			bt.close();
		}
		else if(v == forwardArrow) {
			addToLog("move forward");
			bt.sendData("F");
		}
		else if(v == backArrow) {
			addToLog("move back");
			bt.sendData("B");
		}
		else if(v == rightArrow) {
			addToLog("move right");
			bt.sendData("R");
		}
		else if(v == leftArrow) {
			addToLog("move left");
			bt.sendData("L");
		}
		else if(v == stop) {
			addToLog("stop");
			bt.sendData("S");
		}
		else if(v == gpsMenu) {
			addToLog("call GPS menu");
            gpsMenu(v);
		}
        else if(v == incAlt) {
            addToLog("increase altitude");
            bt.sendData("U");
        }
        else if(v == decAlt) {
            addToLog("decrease altitude");
            bt.sendData("D");
        }
        else if (v == rightFor) {
            addToLog("right forward");
        }
        else if(v == leftFor) {
            addToLog("left forward");
        }
        else if(v == rightBack) {
            addToLog("right back");
        }
        else if(v == leftBack) {
            addToLog("left back");
        }
	}

	public void gpsMenu (View v) {
		Intent intent = new Intent(this, LocationListActivity.class);
		startActivity(intent);
	}
}
