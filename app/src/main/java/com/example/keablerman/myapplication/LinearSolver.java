package com.example.keablerman.myapplication;

public class LinearSolver {
    private float m, b;

    public LinearSolver(float distance, float min, float max){

        //Point slope calculation, (distance - ( -distance) = distance * 2
        m = (max - min) / (distance * 2);

        //Solve for y intercept using distance and max: b = y - mx
        b = max - m * distance;
    }

    public float getPWMValue(float x){
        return m * x + b;
    }
}
