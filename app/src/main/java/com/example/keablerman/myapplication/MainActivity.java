package com.example.keablerman.myapplication;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;

import java.io.IOException;
import java.util.UUID;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    protected static ConnectedThread connectedThread;
    //Thread must be static so that it is not lost when switching activities
    private ClientThread clientThread;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().getExtras() != null)
            if(getIntent().getExtras().getBoolean("reset"))
               connectedThread = null;

        setContentView(R.layout.activity_main);
        btn = (Button)findViewById(R.id.button);
        btn.setTransformationMethod(null);
        btn.setOnClickListener(this);
        btn.setText("Connect");
    }

    public void onClick(View view){
        //findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

        if(connectedThread == null)
            searchPairedDevices();
    }

    private void searchPairedDevices(){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                if(device.getName().equals("raspberrypi")) {
                    clientThread = new ClientThread(device);
                    clientThread.start();
                }
            }
        }
    }

    public void manageMyConnectedSocket(BluetoothSocket soc){
        Intent intent;
        connectedThread = new ConnectedThread(soc, this);
        connectedThread.start();
        Switch test = (Switch)findViewById(R.id.testMode);
        if(test.isChecked())
            intent = new Intent(this, TestDeck.class);
        else
            intent = new Intent(this, FlightDeck.class);
        startActivity(intent);
    }


    //----------------------------------------------------------------------------------------------
    //ClientThread: Establishes a connection with the given BluetoothDevice
    //ClientThread is made as a separate thread because it preforms blocking IO on mmSocket.connect()
    //----------------------------------------------------------------------------------------------
    private class ClientThread extends Thread {
        private final BluetoothSocket mmSocket;
        private static final String TAG = "ClientThread";


        public ClientThread(BluetoothDevice device) {
            Log.println(Log.INFO, TAG, "Spawned new client thread");
            BluetoothSocket tmp = null;
            UUID MY_UUID = UUID.fromString("0a76efc0-6f89-46f3-be94-92055b259ab6");
            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }


        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }

    }


}
