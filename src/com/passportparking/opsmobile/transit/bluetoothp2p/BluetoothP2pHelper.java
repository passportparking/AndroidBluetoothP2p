package com.passportparking.opsmobile.transit.bluetoothp2p;

import java.util.Set;

import com.passportparking.opsmobile.transit.bluetoothp2p.BluetoothP2pBroadcastReceiver.BluetoothP2pBroadcastReceiverCallback;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class BluetoothP2pHelper {

	private final static int DISCOVERABLE_TIME = 300;
	private final static String TAG = "BluetoothP2pHelper"; 
	
	private final static int REQUEST_ENABLE_BT = 1;
	private final static int REQUEST_DISCOVERABLE_BT = 2;
	
	private Activity mActivity;
	private BluetoothAdapter mBluetoothAdapter;
	private BroadcastReceiver mReceiver;
	private IntentFilter mIntentFilter;
	private BluetoothP2pCallback mCallback;

	private String targetDeviceAddress; // desired device WiFi MAC address to connect to; null for passive behaviour
	
	public BluetoothP2pHelper(Activity activity, String targetDeviceAddress, 
			BluetoothP2pCallback callback) 
	{
		this.mActivity = activity;
		this.targetDeviceAddress = targetDeviceAddress;
		this.mCallback = callback;
		
		init();
	}
	public void init() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
			// TODO
		}
		
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
		mReceiver = new BluetoothP2pBroadcastReceiver(bluetoothP2pBroadcastReceiverCallback);
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(BluetoothDevice.ACTION_FOUND);
		mIntentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
	}
	private BluetoothP2pBroadcastReceiverCallback bluetoothP2pBroadcastReceiverCallback = 
			new BluetoothP2pBroadcastReceiverCallback() {
				@Override
				public void onDeviceFound(BluetoothDevice device) {
			    	if (targetDeviceAddress != null && targetDeviceAddress.equalsIgnoreCase(device.getAddress())) {
						Log.d(TAG, "Discovering Device Found! Address: " + device.getAddress());
						mCallback.onDeviceFound(device);
			    		mBluetoothAdapter.cancelDiscovery();
			    	}
					Log.d(TAG, "Discovering Device Not matching. Name: " + device.getName() + ". Address: " + device.getAddress() + " vs " + targetDeviceAddress);
				}
				@Override
				public void onScanModeChanged(int scanMode, int previousScanMode) {
					// TODO
					// possible values for each one: BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, 
					// BluetoothAdapter.SCAN_MODE_CONNECTABLE, BluetoothAdapter.SCAN_MODE_NONE
				}
	};
	
	public void registerBroadcastReceiver(){
		mActivity.registerReceiver(mReceiver, mIntentFilter);
	}
	public void unregisterBroadcastReceiver(){
		mActivity.unregisterReceiver(mReceiver);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			Log.d(TAG, "Request Enable Bluetooth Result: " + resultCode);
			//TODO
			if (resultCode != Activity.RESULT_OK) {
			}
		} else if (requestCode == REQUEST_DISCOVERABLE_BT) {
			Log.d(TAG, "Request Discoverable Bluetooth Result: " + resultCode);
			//TODO
			if (resultCode != Activity.RESULT_OK) {
			}
		}
	}
	
	public void startDiscovery() {
		Log.d(TAG, "Starting Bluetooth Discovery");
		mBluetoothAdapter.startDiscovery();
	}
	public void getPairedDevices() {
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		    	if (targetDeviceAddress != null && targetDeviceAddress.equalsIgnoreCase(device.getAddress())) {
		    		// found, let's connect
					Log.d(TAG, "Paired Device Found! Address: " + device.getAddress());
					mCallback.onDeviceFound(device);
					return;
		    	}
				Log.d(TAG, "Paired Device Not matching. Name: " + device.getName() + ". Address: " + device.getAddress() + " vs " + targetDeviceAddress);
		    }
		}
		Log.e(TAG, "Device not found!");
		mCallback.onDeviceNotFound();
	}
	
	public void enableDiscoverability() {
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_TIME);
	    mActivity.startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE_BT);
	}

	// callback interface
	public interface BluetoothP2pCallback {
		public void onDeviceFound(BluetoothDevice device);
		public void onDeviceNotFound(); 
	}
	
}
