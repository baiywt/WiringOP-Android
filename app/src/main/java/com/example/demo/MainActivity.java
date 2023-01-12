package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.wiringop.wpiControl;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wpiControl.wiringPiSetup();
	wpiControl.pinMode(28, 1);
    }
}
