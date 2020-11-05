package com.lazerlikefoucs.whatsappfakenewsdetector3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Timer timer;

    //this will run on start
    @Override
    protected void onStart() {
        super.onStart();

        //final int alarmtimeinsec = 0;
        //final int timeAtButtonCLick = 0;
        //add timer for splash screen
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent2 = new Intent(MainActivity.this, FakeorRealActivity2.class);
                startActivity(intent2);
                finish();
            }
        }, 500);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}