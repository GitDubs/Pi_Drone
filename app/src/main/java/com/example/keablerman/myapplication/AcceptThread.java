package com.example.keablerman.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class AcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;

    public AcceptThread() {
        int REQUEST_ENABLE_BT = 1;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }else
            Log.e("No bluetooth capability", "system does not support BT");

        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        UUID MY_UUID =  UUID.fromString("0a76efc0-6f89-46f3-be94-92055b259ab6");
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            if(mBluetoothAdapter == null)
            tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Ben", MY_UUID);
        } catch (IOException e) {
            Log.e("", "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;

    }

    public void run(){
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
                //updateText("Passed accept()");
            } catch (IOException e) {
                Log.e("", "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.
                //manageMyConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                }catch(IOException e){
                    Log.println(Log.ERROR,"serverSocket.close()", "IO exception while closing server socket");
                }
                break;
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e("", "Could not close the connect socket", e);
        }
    }
}
