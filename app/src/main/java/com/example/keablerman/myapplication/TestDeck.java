package com.example.keablerman.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.nio.ByteBuffer;

public class TestDeck extends MainActivity implements View.OnClickListener {
    private ImageButton iButton;
    private TextView[] texts = new TextView[4];
    private static float[] speed = {1600, 1600, 1600, 1600};
    private SeekBar dSpeed;
    private static final float MAX_SPEED = 2500;                 //MAX and MIN speeds match the ESC's min and max set safety speeds
    private static final float MIN_SPEED = 1600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_deck);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //Setting up the UP buttons
        //Note: the same object (iButton) is used to set up all buttons because I don't need to
        //keep the reference to the button once it's onClickListener() has been set up
        iButton = (ImageButton)findViewById(R.id.up1);
        iButton.setOnClickListener(this);
        iButton = (ImageButton)findViewById(R.id.up2);
        iButton.setOnClickListener(this);
        iButton = (ImageButton)findViewById(R.id.up3);
        iButton.setOnClickListener(this);
        iButton = (ImageButton)findViewById(R.id.up4);
        iButton.setOnClickListener(this);
        //Setting up the DOWN buttons
        iButton = (ImageButton)findViewById(R.id.down1);
        iButton.setOnClickListener(this);
        iButton = (ImageButton)findViewById(R.id.down2);
        iButton.setOnClickListener(this);
        iButton = (ImageButton)findViewById(R.id.down3);
        iButton.setOnClickListener(this);
        iButton = (ImageButton)findViewById(R.id.down4);
        iButton.setOnClickListener(this);
        //Setting up the text views
        texts[0] = (TextView)findViewById(R.id.escSpeed1);
        texts[1] = (TextView)findViewById(R.id.escSpeed2);
        texts[2] = (TextView)findViewById(R.id.escSpeed3);
        texts[3] = (TextView)findViewById(R.id.escSpeed4);
        //Setting up the ALL UP and ALL DOWN buttons
        iButton = (ImageButton)findViewById(R.id.allUp);
        iButton.setOnClickListener(this);
        iButton = (ImageButton)findViewById(R.id.allDown);
        iButton.setOnClickListener(this);
        //Setting up the kill all button
        Button kill = (Button)findViewById(R.id.killAll);
        kill.setOnClickListener(this);
        //Setting up the variable speed bar
        dSpeed = (SeekBar)findViewById(R.id.dSpeed);
    }

    public void onClick(View view){
        int deltaSpeed = dSpeed.getProgress() + 1;

        if(!super.connectedThread.ready())
            return;

        //Determine which button was pressed and adjust the corresponding ESC's speed accordingly
        if(view.getId() == R.id.up1){
            if (speed[0] + deltaSpeed <= MAX_SPEED)
                speed[0] += deltaSpeed;
        }else if(view.getId() == R.id.up2){
            if (speed[1] + deltaSpeed <= MAX_SPEED)
                speed[1] += deltaSpeed;
        }else if(view.getId() == R.id.up3){
            if (speed[2] + deltaSpeed <= MAX_SPEED)
                speed[2] += deltaSpeed;
        }else if(view.getId() == R.id.up4) {
            if (speed[3] + deltaSpeed <= MAX_SPEED)
                speed[3] += deltaSpeed;
        }else if(view.getId() == R.id.down1){
            if (speed[0] - deltaSpeed >= MIN_SPEED)
                speed[0] -= deltaSpeed;
        }else if(view.getId() == R.id.down2){
            if (speed[1] - deltaSpeed >= MIN_SPEED)
                speed[1] -= deltaSpeed;
        }else if(view.getId() == R.id.down3){
            if (speed[2] - deltaSpeed >= MIN_SPEED)
                speed[2] -= deltaSpeed;
        }else if(view.getId() == R.id.down4){
            if (speed[3] - deltaSpeed >= MIN_SPEED)
                speed[3] -= deltaSpeed;
        }else if(view.getId() == R.id.allUp){
            if (speed[0] + deltaSpeed <= MAX_SPEED && speed[1] + deltaSpeed <= MAX_SPEED && speed[2] + deltaSpeed <= MAX_SPEED && speed[3] + deltaSpeed <= MAX_SPEED) {
                speed[0] += deltaSpeed;
                speed[1] += deltaSpeed;
                speed[2] += deltaSpeed;
                speed[3] += deltaSpeed;
            }
        }else if(view.getId() == R.id.allDown) {
            if (speed[0] - deltaSpeed >= MIN_SPEED && speed[1] - deltaSpeed >= MIN_SPEED && speed[2] - deltaSpeed >= MIN_SPEED && speed[3] - deltaSpeed >= MIN_SPEED) {
                speed[0] -= deltaSpeed;
                speed[1] -= deltaSpeed;
                speed[2] -= deltaSpeed;
                speed[3] -= deltaSpeed;
            }
        } else if(view.getId() == R.id.killAll){
            speed[0] = MIN_SPEED;
            speed[1] = MIN_SPEED;
            speed[2] = MIN_SPEED;
            speed[3] = MIN_SPEED;
        }
        upDateTexts();

        //Send the data to the pi, if the connectionThread has a broken pipe write() returns false
        //and we return to Main Activity
        if(!super.connectedThread.write(speedToByteArray())){
            returnToMain();
        }

    }

    private static byte [] speedToByteArray()
    {
        byte temp;
        byte[] toReturn = ByteBuffer.allocate(16).putFloat(speed[0]).putFloat(speed[1]).putFloat(speed[2]).putFloat(speed[3]).array();
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

    private void upDateTexts(){
        Resources res = getResources();
        texts[0].setText(res.getString(R.string.ESC1, speed[0]));
        texts[1].setText(res.getString(R.string.ESC2, speed[1]));
        texts[2].setText(res.getString(R.string.ESC3, speed[2]));
        texts[3].setText(res.getString(R.string.ESC4, speed[3]));
    }

    private void returnToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("reset", true);
        startActivity(intent);
    }
}
