package com.passportparking.opsmobile.transit.bluetoothp2p;

import java.util.UUID;

import com.passportparking.opsmobile.transit.bluetoothp2p.BluetoothP2pHelper.BluetoothP2pCallback;
import com.passportparking.opsmobile.transit.bluetoothp2p.BluetoothP2pServer.BluetoothP2pServerCallback;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ServerActivity extends Activity {
	
	private static final String UUID_STRING = "00112233-4455-6677-8899-AABBCCDDEEFF";

	private final static String TAG = "ServerActivity";

	private BluetoothP2pHelper bluetoothP2pHelper; // for Wifi Direct Connection to Main Unit
	private BluetoothP2pServer bluetoothP2pServer; // for Socket Server opening
	
	private TextView messageTextView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        initComponents();
		
		initBluetoothP2p();

    }
	
    private void initComponents() {
		// text view
    	messageTextView = (TextView) findViewById(R.id.messageTextView);
    }
    
    private BluetoothP2pCallback bluetoothP2pCallback = new BluetoothP2pCallback() {
    	@Override
    	public void onDeviceFound(BluetoothDevice device) {
    		Log.d(TAG, "Main unit connected! Address: " + device.getAddress());
    	}
    	@Override
    	public void onDeviceNotFound() {
    		Log.e(TAG, "Mirror not found!");
    	}
    };
	private BluetoothP2pServerCallback bluetoothP2pServerCallback = new BluetoothP2pServerCallback() {
		@Override
		public void onConnectionReceived(String message) {
			processMessage(message);
		}
	};
	
    private void initBluetoothP2p() {
    	bluetoothP2pHelper = new BluetoothP2pHelper(this, null, bluetoothP2pCallback);
    	bluetoothP2pHelper.init();
    	bluetoothP2pHelper.getPairedDevices();
    	bluetoothP2pServer = new BluetoothP2pServer(bluetoothP2pServerCallback, 
    			UUID.fromString(UUID_STRING));
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		
		if (bluetoothP2pServer != null) {
			bluetoothP2pServer.startBackgroundThread();
		}
	}
	
	@Override 
	protected void onPause() {
		super.onPause();
		
		if (bluetoothP2pServer != null) {
			bluetoothP2pServer.stopBackgroundThread();
		}
	}

	private void processMessage(String message) {
		Log.d(TAG, "Received message: " + message);
		updateUIonUiThread(message);
		//ttsHelper.speak(message);
	}

	private void updateUIonUiThread(final String message) {
		runOnUiThread(new Runnable(){
			@Override
			public void run() {
				updateUI(message);
			}
		});
	}
	private void updateUI(String text) {
		messageTextView.setText(text);

	}
	
}
