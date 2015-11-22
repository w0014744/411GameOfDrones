package com.overpaoered.robotremote;

import java.io.BufferedReader;
import java.lang.Exception;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/*
This class is used for sending and receiving data between the Arduino device and Android device.
 */
public class BtInterface {

	private BluetoothDevice device = null;
	private BluetoothSocket socket = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	private InputStream receiveStream = null;
	private BufferedReader receiveReader = null;
	private OutputStream sendStream = null;

	private ReceiverThread receiverThread;

	Handler handlerStatus, handlerMessage;
	
	public static int CONNECTED = 1;
	public static int DISCONNECTED = 2;
	static final String TAG = "Device";	

	/*
		The Contructor takes in two handlers to handle the status and messages of the BlueTooth adapter.
		@param hstatus The Handler which handles the status of the Bluetooth adapter.
		@param h The Handler to handle the message sent from the Bluetooth adapter
		@see BluetoothAdapter
	 */
	public BtInterface(Handler hstatus, Handler h) {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		handlerStatus = hstatus;
		handlerMessage = h;		
	}


	/*
	This method sets up the connection between the Bluetooth device on the Drone and the Android device.
	The specific name of the Bluetooth device is JY-MCU
	 */
	public void connect() {
		
		Set<BluetoothDevice> setpairedDevices = mBluetoothAdapter.getBondedDevices();
    	BluetoothDevice[] pairedDevices = setpairedDevices.toArray(new BluetoothDevice[setpairedDevices.size()]);
	
		boolean foundDevice = false;
		for(int i=0;i<pairedDevices.length;i++) {
			if(pairedDevices[i].getName().contains("HC-06")) {
				device = pairedDevices[i];
				try {
					socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
					receiveStream = socket.getInputStream();
					receiveReader = new BufferedReader(new InputStreamReader(receiveStream));
					sendStream = socket.getOutputStream();
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				foundDevice = true;
				break;
			}
		}
		if(foundDevice == false){
			Log.v(TAG, "You have not turned on your device");
		}
		
		receiverThread = new ReceiverThread(handlerMessage);
		new Thread() {
			@Override public void run() {
				try {
					socket.connect();
					
					Message msg = handlerStatus.obtainMessage();
					msg.arg1 = CONNECTED;
	                handlerStatus.sendMessage(msg);
	                
					receiverThread.start();
					
				} 
				catch (Exception e) {
					Log.v("N", "Connection Failed : "+e.getMessage());
					e.printStackTrace();
				}
			}
		}.start();
	}

	/*
	Closes the Bluetooth connection and sends a DISCONNECTED signal to the Status Handler
	 */
	public void close() {
		try {
			socket.close();
			receiverThread.interrupt();
			
			Message msg = handlerStatus.obtainMessage();
			msg.arg1 = DISCONNECTED;
			handlerStatus.sendMessage(msg);
            
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	This method sends data from the Android device to the Adruino through a serial connection.
	@param data The data to be sent to the Arduino
	 */
		public void sendData(String data) {
			try {
				sendStream.write(data.getBytes());
		        sendStream.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
/*
This class establishes connection between the Android and Arduino
 */
	private class ReceiverThread extends Thread {
		Handler handler;
		
		ReceiverThread(Handler h) {
			handler = h;
		}
		
		@Override public void run() {
			while(socket != null) {
				if (isInterrupted()){
						try {
							join();
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
				try {
					if(receiveStream.available() > 0) {
						String dataToSend = "";
						
						dataToSend = receiveReader.readLine();
						if (dataToSend != null){
							Log.v(TAG, dataToSend);
							Message msg = handler.obtainMessage();
							Bundle b = new Bundle();
							b.putString("receivedData", dataToSend);
			                msg.setData(b);
			                handler.sendMessage(msg);
			                dataToSend = "";
						}
						
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
