package com.example.keablerman.myapplication;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.view.ViewGroup;
import java.lang.Math;

public class FlightView extends View implements View.OnTouchListener{
    private static final String TAG = "FlightView";
    private static final float DISTANCE = 200;
    private float originalX;
    private float originalY;
    private float dX, dY;
    private boolean firstCall = true;

    public FlightView(Context context){
        super(context);
        setOnTouchListener(this);
        this.setWillNotDraw(false);
    }


    public boolean onTouch(View view, MotionEvent event){
        float eventRawX = event.getRawX();
        float eventRawY = event.getRawY();

        if(event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            if (firstCall){
                originalX = view.getX();
                originalY = view.getY();
                firstCall = false;
            }
            dX = view.getX() - eventRawX;
            dY = view.getY() - eventRawY;
        }else if(event.getActionMasked() == MotionEvent.ACTION_MOVE){
            if(checkDistance(eventRawX + dX, eventRawY + dY)) {
                view.setX(eventRawX + dX);
                view.setY(eventRawY + dY);
            }else if(originalX - dX == eventRawX){
                double angle = Math.asin((originalY - (eventRawY + dY)) / getDistance(eventRawX + dX, eventRawY + dY));
                view.setY((float)(originalY - DISTANCE));
                Log.println(Log.INFO, TAG, "angle: " + angle);
                Log.println(Log.INFO, TAG, "cos: " + Math.cos(angle));
                Log.println(Log.INFO, TAG, "sin: " + Math.sin(angle));
            }else if(originalX - dX < eventRawX){
                double angle = Math.asin((originalY - (eventRawY + dY)) / getDistance(eventRawX + dX, eventRawY + dY));
                view.setX((float)(originalX + (DISTANCE * Math.cos(angle))));
                view.setY((float)(originalY - (DISTANCE * Math.sin(angle))));
                Log.println(Log.INFO, TAG, "angle: " + angle);
                Log.println(Log.INFO, TAG, "cos: " + Math.cos(angle));
                Log.println(Log.INFO, TAG, "sin: " + Math.sin(angle));
            }else{
                double angle = Math.asin((originalY - (eventRawY + dY)) / getDistance(eventRawX + dX, eventRawY + dY));
                view.setX((float)(originalX - (DISTANCE * Math.cos(angle))));
                view.setY((float)(originalY - (DISTANCE * Math.sin(angle))));
                Log.println(Log.INFO, TAG, "angle: " + angle);
                Log.println(Log.INFO, TAG, "cos: " + Math.cos(angle));
                Log.println(Log.INFO, TAG, "sin: " + Math.sin(angle));
            }

        }else if(event.getActionMasked() == MotionEvent.ACTION_UP){
            view.setX(originalX);
            view.setY(originalY);
        }else
            return false;
        return true;
    }

    private boolean checkDistance(float x, float y){
        double xSquared = Math.pow((originalX - x), 2);
        double ySquared = Math.pow((originalY - y), 2);

        if(Math.sqrt(xSquared + ySquared) > DISTANCE)
            return false;
        return true;
    }

    private double getDistance(float x, float y){
        double xSquared = Math.pow((originalX - x), 2);
        double ySquared = Math.pow((originalY - y), 2);

        return Math.sqrt(xSquared + ySquared);
    }

}
