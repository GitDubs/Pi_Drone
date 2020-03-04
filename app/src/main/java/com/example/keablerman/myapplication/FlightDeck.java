package com.example.keablerman.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.SeekBar;

import java.nio.ByteBuffer;

public class FlightDeck extends MainActivity{
    private static final String TAG = "FlightView";
    private static final float DISTANCE = 200;
    private static float throttle = 1500;
    private static float yaw = 1500;
    private static float pitch = 1500;
    private static float roll = 1500;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_flight_deck);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ImageView pitch_roll = (ImageView)findViewById(R.id.pitch_roll);
        ImageView throttle_yaw = (ImageView)findViewById(R.id.throttle_yaw);
        Button kill = (Button)findViewById(R.id.kill);
        pitch_roll.setOnTouchListener(new MyOnTouchListener(false));
        throttle_yaw.setOnTouchListener(new MyOnTouchListener(true));
    }

    private class MyOnTouchListener implements View.OnTouchListener {
        private float originalX;
        private float originalY;
        private float dX, dY;
        private boolean firstCall = true;
        private boolean throttle_yaw;

        public MyOnTouchListener(boolean throttle_yaw) {
            this.throttle_yaw = throttle_yaw;
        }

        public boolean onTouch(View view, MotionEvent event) {
            String TAG = "onTouch";
            float eventRawX = event.getRawX();
            float eventRawY = event.getRawY();
            if (firstCall) {                                                                        //Need to find the original X and Y coordinates so
                originalX = view.getX();                                                            //we can reset the circle to them when it it released
                originalY = view.getY();
                dX = view.getX() - eventRawX;                                                       //dX and dY represent the difference between the view's
                dY = view.getY() - eventRawY;                                                       //coordinate grid and the system's coordinate grid
                firstCall = false;
            }

            if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                boolean outOfBoundsRight = eventRawX + dX > originalX + DISTANCE;
                boolean outOfBoundsLeft = eventRawX + dX < originalX - DISTANCE;
                boolean outOfBoundsUp = eventRawY + dY < originalY - DISTANCE;
                boolean outOfBoundsDown = eventRawY + dY > originalY + DISTANCE;
                //Corners
                if (outOfBoundsRight && outOfBoundsUp) {                                             //Top Right
                    view.setX(originalX + DISTANCE);
                    view.setY(originalY - DISTANCE);
                    return true;
                } else if (outOfBoundsRight && outOfBoundsDown) {                                     //Bottom Right
                    view.setX(originalX + DISTANCE);
                    view.setY(originalY + DISTANCE);
                    return true;
                } else if (outOfBoundsLeft && outOfBoundsDown) {                                      //Bottom Left
                    view.setX(originalX - DISTANCE);
                    view.setY(originalY + DISTANCE);
                    return true;
                } else if (outOfBoundsLeft && outOfBoundsUp) {                                        //Top Left
                    view.setX(originalX - DISTANCE);
                    view.setY(originalY - DISTANCE);
                    return true;
                }

                if (outOfBoundsRight) {
                    view.setX(originalX + DISTANCE);
                    view.setY(eventRawY + dY);
                    return true;
                }

                if (outOfBoundsLeft) {
                    view.setX(originalX - DISTANCE);
                    view.setY(eventRawY + dY);
                    return true;
                }

                if (outOfBoundsUp) {
                    view.setX(eventRawX + dX);
                    view.setY(originalY - DISTANCE);
                    return true;
                }

                if (outOfBoundsDown) {
                    view.setX(eventRawX + dX);
                    view.setY(originalY + DISTANCE);
                    return true;
                }

                view.setX(eventRawX + dX);
                view.setY(eventRawY + dY);

            } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {                          //When the circle is released it is reset to it's original position
                if (!throttle_yaw)
                    view.setY(originalY);                                                           //Else let it return to normal
                view.setX(originalX);
            }
            return true;
        }
    }

    private static byte [] flightControlsToByteArray()
    {
        byte temp;
        byte[] toReturn = ByteBuffer.allocate(16).putFloat(throttle).putFloat(throttle).putFloat(throttle).putFloat(throttle).array();
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

    private void returnToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("reset", true);
        startActivity(intent);
    }
}
