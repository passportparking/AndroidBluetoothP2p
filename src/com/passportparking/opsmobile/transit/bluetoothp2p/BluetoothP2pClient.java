package com.passportparking.opsmobile.transit.bluetoothp2p;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class BluetoothP2pClient {

	private static final String TAG = "BluetoothP2pClient";
	
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothDevice mDevice;
	private UUID mUuid;
	private BluetoothSocket mSocket;
	
	private HandlerThread backgroundThread;
	private Handler backgroundHandler;
	
	public BluetoothP2pClient(BluetoothDevice device, UUID uuid) {
		this.mDevice = device;
		this.mUuid = uuid;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		createBackgroundThread();
		
		startSocket();
	}

	private void startSocket() {
		try {
			mSocket = mDevice.createRfcommSocketToServiceRecord(mUuid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void connectSocket() {
		backgroundHandler.post(new Runnable() {
			@Override
			public void run() {
				startClientSocket();
			}
		});
	}
	private void startClientSocket() {
		// Cancel discovery because it will slow down the connection
		if (mBluetoothAdapter != null) {
			mBluetoothAdapter.cancelDiscovery();
		}
 
        try {
        	Log.d(TAG, "Connecting to Server socket");
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mSocket.connect();
        } catch (IOException connectException) {
        	Log.e(TAG, "Unable to connect to Server Socket! " + connectException.getMessage());
        	// http://stackoverflow.com/a/25647197
        	try {
        		mSocket =(BluetoothSocket) mDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mDevice,1);
        		mSocket.connect();
        	} catch (Exception workaroundException) {
                // Unable to connect; close the socket and get out
                try {
                    mSocket.close();
                } catch (IOException closeException) {
                	closeException.printStackTrace();
                }
//                startSocket();
//                startClientSocket();
                return;
        	}
        }
 
        // Do work to manage the connection (in a separate thread)
    	Log.d(TAG, "Connected to Server socket!!");
        // TODO
	}
	
	public void send(String sessionInfo) {
		Log.d(TAG, "Sending session: " + sessionInfo);
		byte buf[] = sessionInfo.getBytes();
		
	    OutputStream outputStream;
		try {
			outputStream = mSocket.getOutputStream();
		    outputStream.write(buf, 0, buf.length);
		    //outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) { }
    }

    // Background Thread functions
	private void createBackgroundThread() {
		Log.d(TAG, "Creating background thread");
		backgroundThread = new HandlerThread("Sessions Fetch Handler Thread");
		backgroundThread.start();
		backgroundHandler = new Handler(backgroundThread.getLooper());
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
	
    
}
