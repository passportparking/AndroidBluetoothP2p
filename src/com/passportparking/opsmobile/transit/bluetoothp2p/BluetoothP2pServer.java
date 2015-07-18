package com.passportparking.opsmobile.transit.bluetoothp2p;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class BluetoothP2pServer {

	private static final String TAG = "BluetoothP2pServer";
	private final static String SERVICE = "com.passportparking.opsmobile.transit.bluetoothp2p";
	
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothServerSocket mServerSocket;
	private BluetoothP2pServerCallback bluetoothP2pServerCallback;

	private HandlerThread backgroundThread;
	private Handler backgroundHandler;
	
	public BluetoothP2pServer(BluetoothP2pServerCallback callback, UUID uuid) {
		this.bluetoothP2pServerCallback = callback;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter != null) {
			try {
				mServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(SERVICE, uuid);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		createBackgroundThread();
	}
	
	private void init() {
		backgroundHandler.post(new Runnable() {
			@Override
			public void run() {
				startServerSocket();
			}
		});
	}
	
	private void startServerSocket() {
		BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true) {
            try {
    			Log.d(TAG, "Starting Mirror Server Socket");
    			// This is a blocking call and will only return on a
                // successful connection or an exception
                socket = mServerSocket.accept();
                // If a connection was accepted
                if (socket != null) {
        			Log.d(TAG, "Server Socket connection accepted");
                    InputStream inputStream = socket.getInputStream();
                    processConnection(inputStream);
                    
        			//Log.d(TAG, "Stopping Mirror Server Socket");
                    //mServerSocket.close();
//                    init(); //start over
//                    break;
                } else {
        			Log.e(TAG, "Bluetooth Socket null!");
                }
            } catch (IOException e) {
    			Log.e(TAG, "Server Socket coonection not accepted!");
                break;
            }
        }
	}
	
	private void processConnection(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int bytes;
        // Keep listening to the InputStream while connected
        while (true) {
	        try {
	            // Read from the InputStream
	            bytes = inputStream.read(buffer);
	
	            // construct a string from the valid bytes in the buffer
	            String readMessage = new String(buffer, 0, bytes);
	    		Log.d(TAG, "Received string: " + readMessage);
	    		bluetoothP2pServerCallback.onConnectionReceived(readMessage);
	        } catch (IOException e) {
	            Log.e(TAG, "disconnected", e);
	            break;
	        }
        }
	}
//	private void processConnection(InputStream inputStream) throws IOException {
//		BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
//		StringBuilder total = new StringBuilder();
//		String line;
//		while ((line = r.readLine()) != null) {
//		    total.append(line);
//		}
//		Log.d(TAG, "Received string: " + total.toString());
//		bluetoothP2pServerCallback.onConnectionReceived(total.toString());
//	}

	// background thread functions
	private void createBackgroundThread() {
		Log.d(TAG, "Creating background thread");
		backgroundThread = new HandlerThread("Sessions Fetch Handler Thread");
		backgroundThread.start();
		backgroundHandler = new Handler(backgroundThread.getLooper());
		
		init();
	}
	public void startBackgroundThread() {
		Log.d(TAG, "Starting background thread");
		if (backgroundThread != null && !backgroundThread.isAlive()) {
			Log.d(TAG, "Background thread state: " + backgroundThread.getState().name());
			createBackgroundThread();
		}
	}
	public void stopBackgroundThread() {
		Log.d(TAG, "Stopping background thread");
		if (backgroundThread != null) {
			backgroundThread.quit();
		}
	}
	
	// callback interface
	public interface BluetoothP2pServerCallback {
		public void onConnectionReceived(String message);
	}
	
}
