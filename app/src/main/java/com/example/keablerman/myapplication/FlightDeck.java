package com.example.keablerman.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.SeekBar;

import java.nio.ByteBuffer;

public class FlightDeck extends MainActivity implements View.OnTouchListener, View.OnClickListener {
    private static final String TAG = "FlightView";
    SeekBar speedDelta;
    private static final float DISTANCE = 200;
    private static final float MAX_SPEED = 2500;                 //MAX and MIN speeds match the ESC's min and max set safety speeds
    private static final float MIN_SPEED = 1600;
    private static float speed = 1600;
    private float originalX;
    private float originalY;
    private float dX, dY;
    private boolean firstCall = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_flight_deck);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ImageView im = (ImageView)findViewById(R.id.blueCircle);
        ImageButton upButton = (ImageButton)findViewById(R.id.up_button);
        ImageButton downButton = (ImageButton)findViewById(R.id.down_button);
        Button kill = (Button)findViewById(R.id.kill);
        speedDelta = (SeekBar)findViewById(R.id.speedDelta);
        kill.setOnClickListener(this);
        im.setOnTouchListener(this);
        upButton.setOnClickListener(this);
        downButton.setOnClickListener(this);
    }

    public boolean onTouch(View view, MotionEvent event){
        String TAG = "onTouch";
        float eventRawX = event.getRawX();
        float eventRawY = event.getRawY();

        if (firstCall){                                                 //Need to find the original X and Y coordinates so
            originalX = view.getX();                                    //we can reset the circle to them when it it released
            originalY = view.getY();
            firstCall = false;
        }

        if(event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            dX = view.getX() - eventRawX;                               //dX and dY represent the difference between the view's
            dY = view.getY() - eventRawY;                               //coordinate grid and the system's coordinate grid


        }else if(event.getActionMasked() == MotionEvent.ACTION_MOVE){
            if(checkDistance(eventRawX + dX, eventRawY + dY)) {         //If the touch event is coming from within the boundary circle
                view.setX(eventRawX + dX);                              //Update the circle's position
                view.setY(eventRawY + dY);

                if(super.connectedThread.ready())                       //Write data to bluetooth socket
                    if(!super.connectedThread.write(float2ByteArray((eventRawX + dX) - originalX, originalY - (eventRawY + dY))))
                        returnToMain();

            }else if(originalX - dX < eventRawX){                       //If the event is out of the circle and to the right of the circle, draws on the circle boundary
                double angle = Math.asin((originalY - (eventRawY + dY)) / getDistance(eventRawX + dX, eventRawY + dY));
                view.setX((float)(originalX + (DISTANCE * Math.cos(angle))));
                view.setY((float)(originalY - (DISTANCE * Math.sin(angle))));

                if(super.connectedThread.ready())                       //Write data to bluetooth socket
                    if(!super.connectedThread.write(float2ByteArray((float)(DISTANCE * Math.cos(angle)),(float) (DISTANCE * Math.sin(angle)))))
                        returnToMain();
            }else{                                                      //If the event is out of and the the left of the circle, draws on the circle boundary
                double angle = Math.asin((originalY - (eventRawY + dY)) / getDistance(eventRawX + dX, eventRawY + dY));
                view.setX((float)(originalX - (DISTANCE * Math.cos(angle))));
                view.setY((float)(originalY - (DISTANCE * Math.sin(angle))));

                if(super.connectedThread.ready())                       //Write data to bluetooth socket
                    if(!super.connectedThread.write(float2ByteArray((float)(0 - (DISTANCE * Math.cos(angle))),(float) (DISTANCE * Math.sin(angle)))))
                        returnToMain();
            }
        }else if(event.getActionMasked() == MotionEvent.ACTION_UP){     //When the circle is released it is reset to it's original position
            view.setX(originalX);
            view.setY(originalY);
        }else
            return false;
        return true;
    }

    //----------------------------------------------------------------------------------------------
    //checkDistance: given two coordinates this method determines if they are less than DISTANCE
    //away from the center of the circle (represented by originalX and originalY
    //----------------------------------------------------------------------------------------------
    private boolean checkDistance(float x, float y){
        double xSquared = Math.pow((originalX - x), 2);
        double ySquared = Math.pow((originalY - y), 2);

        return ((Math.sqrt(xSquared + ySquared) < DISTANCE));
    }

    //----------------------------------------------------------------------------------------------
    //getDistance: given two coordinates this method returns the distance from the given point to
    //the center of the circle
    //Algorithm used: Pythagorean theorem
    //----------------------------------------------------------------------------------------------
    private double getDistance(float x, float y){
        double xSquared = Math.pow((originalX - x), 2);
        double ySquared = Math.pow((originalY - y), 2);

        return Math.sqrt(xSquared + ySquared);
    }

    public void onClick(View view){
        if(!super.connectedThread.ready())
            return;
        int dSpeed = speedDelta.getProgress() + 1;


    }

    private static byte [] speedToByteArray()
    {
        byte temp;
        byte[] toReturn = ByteBuffer.allocate(16).putFloat(speed).putFloat(speed).putFloat(speed).putFloat(speed).array();
        //Swapping the bytes because the structs in python read in the opposite direction

        for(int i = 0; i <= 12; i+= 4){
            temp = toReturn[i];
            toReturn[i] = toReturn [i + 3];
            toReturn[i + 3] = temp;

            temp = toReturn[i + 1];
            toReturn[i + 1] = toReturn[i + 2];
            toReturn[i + 2] = temp;
        }

        return toReturn;
    }

    private static byte[] float2ByteArray(float f1, float f2){
        byte [] toReturn = new byte[2];

        return toReturn;
    }

    private void returnToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("reset", true);
        startActivity(intent);
    }
}
