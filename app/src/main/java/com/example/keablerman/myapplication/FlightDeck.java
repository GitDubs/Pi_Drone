package com.example.keablerman.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import java.nio.ByteBuffer;

public class FlightDeck extends MainActivity{
    private MyOnTouchListener pitchRollListener, throttleYawListener;
    private static final String TAG = "FlightView";
    private static final float DISTANCE = 200;
    private static final float THROTTLEMAX = 2106, THROTTLEMIN = 1001;
    private static final float YAWMAX = 2017, YAWMIN = 993;
    private static final float PITCHMAX = 1943, PITCHMIN = 992;
    private static final float ROLLMAX = 2030, ROLLMIN = 1031;
    private static float throttle, yaw, pitch, roll;
    private LinearSolver throttleSolver, yawSolver, pitchSolver, rollSolver;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_flight_deck);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ImageView pitch_roll = (ImageView)findViewById(R.id.pitch_roll);
        ImageView throttle_yaw = (ImageView)findViewById(R.id.throttle_yaw);
        Button kill = (Button)findViewById(R.id.kill);
        pitchRollListener = new MyOnTouchListener(false);
        throttleYawListener = new MyOnTouchListener(true);
        pitch_roll.setOnTouchListener(pitchRollListener);
        throttle_yaw.setOnTouchListener(throttleYawListener);

        //Solvers for each control value
        throttleSolver = new LinearSolver(DISTANCE, THROTTLEMIN, THROTTLEMAX);
        yawSolver = new LinearSolver(DISTANCE, YAWMIN, YAWMAX);
        pitchSolver = new LinearSolver(DISTANCE, PITCHMIN, PITCHMAX);
        rollSolver = new LinearSolver(DISTANCE, ROLLMIN, ROLLMAX);

        throttle = THROTTLEMIN;                                                                     //Set the initial control values, all except throttle
        yaw = yawSolver.getPWMValue(0);                                                          //are set to the middle of their range.
        pitch = pitchSolver.getPWMValue(0);
        roll = rollSolver.getPWMValue(0);
    }

    private class MyOnTouchListener implements View.OnTouchListener {
        public float x, y;
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
                    setControls(DISTANCE, -DISTANCE);
                    sendControls();
                    return true;
                } else if (outOfBoundsRight && outOfBoundsDown) {                                     //Bottom Right
                    view.setX(originalX + DISTANCE);
                    view.setY(originalY + DISTANCE);
                    setControls(DISTANCE, DISTANCE);
                    sendControls();
                    return true;
                } else if (outOfBoundsLeft && outOfBoundsDown) {                                      //Bottom Left
                    view.setX(originalX - DISTANCE);
                    view.setY(originalY + DISTANCE);
                    setControls(-DISTANCE, DISTANCE);
                    sendControls();
                    return true;
                } else if (outOfBoundsLeft && outOfBoundsUp) {                                        //Top Left
                    view.setX(originalX - DISTANCE);
                    view.setY(originalY - DISTANCE);
                    setControls(-DISTANCE, -DISTANCE);
                    sendControls();
                    return true;
                }

                if (outOfBoundsRight) {
                    view.setX(originalX + DISTANCE);
                    view.setY(eventRawY + dY);
                    setControls(DISTANCE, eventRawY + dY - originalY);
                    sendControls();
                    return true;
                }

                if (outOfBoundsLeft) {
                    view.setX(originalX - DISTANCE);
                    view.setY(eventRawY + dY);
                    setControls(-DISTANCE, eventRawY + dY - originalY);
                    sendControls();
                    return true;
                }

                if (outOfBoundsUp) {
                    view.setX(eventRawX + dX);
                    view.setY(originalY - DISTANCE);
                    setControls(eventRawX + dX - originalX, -DISTANCE);
                    sendControls();
                    return true;
                }

                if (outOfBoundsDown) {
                    view.setX(eventRawX + dX);
                    view.setY(originalY + DISTANCE);
                    setControls(eventRawX + dX - originalX, DISTANCE);
                    sendControls();
                    return true;
                }

                view.setX(eventRawX + dX);
                view.setY(eventRawY + dY);
                setControls(eventRawX + dX - originalX, eventRawY + dY - originalY);
                sendControls();


            } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {                          //When the circle is released it is reset to it's original position
                if (!throttle_yaw) {
                    view.setY(originalY);
                    view.setX(originalX);
                    setControls(0, 0);
                }else {//Else let it return to normal
                    view.setX(originalX);
                    boolean outOfBoundsUp = eventRawY + dY < originalY - DISTANCE;
                    boolean outOfBoundsDown = eventRawY + dY > originalY + DISTANCE;

                    if(outOfBoundsUp){
                        setControls(0, -DISTANCE);
                    }else if(outOfBoundsDown){
                        setControls(0, DISTANCE);
                    }else{
                        setControls(0, eventRawY + dY - originalY);
                    }
                }
                sendControls();
            }
            return true;
        }

        void setControls(float x, float y){
            //Java's Y mapping is inverse to the stick mappings
            y = -y;
            //We are controlling throttle and yaw with this stick
            if(throttle_yaw){
                throttle = throttleSolver.getPWMValue(y);
                yaw = yawSolver.getPWMValue(x);
            //We are controlling pitch and roll with this stick
            }else{
                pitch = pitchSolver.getPWMValue(y);
                roll = rollSolver.getPWMValue(x);
            }
        }
    }

    private void sendControls(){
        super.connectedThread.write(flightControlsToByteArray());
    }

    private static byte [] flightControlsToByteArray()
    {
        byte temp;
        byte[] toReturn = ByteBuffer.allocate(16).putFloat(throttle).putFloat(yaw).putFloat(pitch).putFloat(roll).array();
        //Swapping the bytes because the structs in python read in the opposite direction

        for(int i = 0; i <= 12; i+= 4){
            temp = toReturn[i];
            toReturn[i] = toReturn [i + 3];
            toReturn[i + 3] = temp;

            temp = toReturn[i + 1];
            toReturn[i + 1] = toReturn[i + 2];
            toReturn[i + 2] = temp;
        }
        Log.i(TAG, "throttle: " + throttle);
        Log.i(TAG, "yaw: " + yaw);
        Log.i(TAG, "pitch: " + pitch);
        Log.i(TAG, "roll: " + roll);

        return toReturn;
    }

    private void returnToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("reset", true);
        startActivity(intent);
    }
}
