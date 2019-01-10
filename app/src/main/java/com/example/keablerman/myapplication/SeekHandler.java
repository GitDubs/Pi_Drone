package com.example.keablerman.myapplication;

import android.view.View;
import android.widget.SeekBar;

import java.io.IOException;

public class SeekHandler implements SeekBar.OnSeekBarChangeListener {
    private SeekBar sk;
    private ConnectedThread cThread;

    public SeekHandler(View view, ConnectedThread cThread){
        this.cThread = cThread;
        sk = (SeekBar)view;
        sk.setOnSeekBarChangeListener(this);
    }

    public SeekHandler(View view){
        this.cThread = null;
        sk = (SeekBar)view;
        sk.setOnSeekBarChangeListener(this);
    }

    public void attatchThread(ConnectedThread thread){
        cThread = thread;
    }

    public void onStopTrackingTouch(SeekBar sk) {
        byte[] dutyCycle = {(byte) sk.getProgress()};
        if(cThread != null)
            if (cThread.ready())
                cThread.write(dutyCycle);


    }
    //Implemented but not needed
    public void onProgressChanged(SeekBar sk, int progress, boolean fromUser){
        byte[] dutyCycle = {(byte) progress};
        if(cThread != null)
            if (cThread.ready())
                cThread.write(dutyCycle);
    }
    public void onStartTrackingTouch(SeekBar sk){}
}
