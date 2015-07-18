package com.passportparking.opsmobile.transit.bluetoothp2p;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothP2pBroadcastReceiver extends BroadcastReceiver {

	private BluetoothP2pBroadcastReceiverCallback mCallback;
	
	public BluetoothP2pBroadcastReceiver(BluetoothP2pBroadcastReceiverCallback callback) {
		this.mCallback = callback;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
        // When discovery finds a device
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            mCallback.onDeviceFound(device);
        } else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
        	int scanMode = intent.getParcelableExtra(BluetoothAdapter.EXTRA_SCAN_MODE);
        	int previousScanMode = intent.getParcelableExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE);
        	mCallback.onScanModeChanged(scanMode, previousScanMode);
        }
		
	}

	public interface BluetoothP2pBroadcastReceiverCallback {
		public void onDeviceFound(BluetoothDevice device);
		public void onScanModeChanged(int scanMode, int previousScanMode);
	}
	
}
