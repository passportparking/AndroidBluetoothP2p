package com.passportparking.opsmobile.transit.bluetoothp2p;

import java.util.UUID;

import com.passportparking.opsmobile.transit.bluetoothp2p.BluetoothP2pHelper.BluetoothP2pCallback;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;

/**
 * ClientActivity
 * 
 * Example of usage of Bluetooth P2P unidirectional connection library
 * for Client side
 */
public class ClientActivity extends Activity {

	private static final String BLUETOOTH_TARGET_ADDRESS = "00:00:00:00:00:00"; // TODO
	private static final String UUID_STRING = "00112233-4455-6677-8899-AABBCCDDEEFF";

	private static final String TAG = "ClientActivity";
	
	private BluetoothP2pHelper bluetoothP2pHelper;
	private BluetoothP2pClient bluetoothP2pClient;
	private UUID uuid;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		initBluetoothP2p();
        
    }
    
    private BluetoothP2pCallback bluetoothP2pCallback = new BluetoothP2pCallback() {
    	@Override
    	public void onDeviceFound(BluetoothDevice device) {
    		Log.d(TAG, "Mirror connected! Address: " + device.getAddress());
        	bluetoothP2pClient = new BluetoothP2pClient(device, uuid);
        	bluetoothP2pClient.connectSocket();
        	bluetoothP2pClient.send("Connected!");
    	}
    	@Override
    	public void onDeviceNotFound() {
    		Log.e(TAG, "Mirror not found!");
    	}
    };
    private void initBluetoothP2p() {
    	uuid = UUID.fromString(UUID_STRING);
    	bluetoothP2pHelper = new BluetoothP2pHelper(this, BLUETOOTH_TARGET_ADDRESS, bluetoothP2pCallback);
    	bluetoothP2pHelper.init();
    	//bluetoothP2pHelper.enableDiscoverability();
    	//bluetoothP2pHelper.startDiscovery();
    	bluetoothP2pHelper.getPairedDevices();
    }

	@Override
	protected void onResume() {
		super.onResume();
    
		if (bluetoothP2pClient != null) {
			bluetoothP2pClient.startBackgroundThread();
		}
//		if (bluetoothP2pHelper != null) {
//		bluetoothP2pHelper.registerBroadcastReceiver();
//	}
	}
	@Override 
	protected void onPause() {
		super.onPause();
		
		if (bluetoothP2pClient != null) {
			bluetoothP2pClient.stopBackgroundThread();
		}
		
//		if (bluetoothP2pHelper != null) {
//			bluetoothP2pHelper.unregisterBroadcastReceiver();
//		}
	}
	
	/**
	 * Function to send String message to server.
	 * This is the one to be called in Application logic
	 * 
	 * @param message
	 */
	private void send(String message) {
		if (bluetoothP2pClient != null) {
			bluetoothP2pClient.send(message);
		}
	}
}
