package com.example.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.wiringop.GPIOControl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener {

    private String TAG = "MainActivity-";
    final int gpioPin = 359;
    final int[] retValue = {-1};
    final int[] fd = new int[1];
    final int[] c = new int[1];
    final int[] ret = new int[1];
    final String data = null;
    private List<CheckBox> mLeds;
//    HashMap<Integer,CheckBox> idToCb;
//    HashMap<Integer,Integer> idToIndex;
    Map<Integer, CheckBox> idToCb = new HashMap<Integer, CheckBox>();
    Map<Integer, Integer> idToIndex = new HashMap<Integer, Integer>();
    private static final int[] CHECKBOX_IDS = {
            R.id.cb1, R.id.cb2, R.id.cb3, R.id.cb4, R.id.cb5,
            R.id.cb6, R.id.cb7, R.id.cb8, R.id.cb9, R.id.cb10,
            R.id.cb11, R.id.cb12, R.id.cb13, R.id.cb14, R.id.cb15,
            R.id.cb16, R.id.cb17, R.id.cb18, R.id.cb19, R.id.cb20,
            R.id.cb21, R.id.cb22, R.id.cb23, R.id.cb24, R.id.cb25,
            R.id.cb26,
    };
    CharSequence physNames[] =
    {
            "    3.3V", "5V      ",
            "I2C8_SDA", "5V      ",
            "I2C8_SCL", "GND     ",
            "    PWM1", "I2C3_SCL",
            "     GND", "I2C3_SDA",
            "GPIO1_A1", "GPIO1_C2",
            "GPIO1_A3", "GND     ",
            "GPIO2_D4", "GPIO1_C6",
            "    3.3V", "GPIO1_C7",
            "SPI1_TXD", "GND     ",
            "SPI1_RXD", "GPIO1_D0",
            "SPI1_CLK", "SPI1_CS ",
            "     GND", "GPIO4_C5",
    };
    int physToGpio_5[] =
    {
            -1,       // 0
            -1, -1,   // 1, 2
            47, -1,   // 3, 4
            46, -1,   // 5, 6
            54, 131,   // 7, 8
            -1, 132,   // 9, 10
            138, 29,   // 11, 12
            139, -1,   // 13, 14
            28, 59,   // 15, 16
            -1, 58,   // 17, 18
            49, -1,   // 19, 20
            48, 92,   // 21, 22
            50, 52,   // 23, 24
            -1, 35,   // 25, 26
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLeds = new ArrayList<CheckBox>();
        for (int i=0; i<CHECKBOX_IDS.length; i++) {
            CheckBox cb = (CheckBox) findViewById(CHECKBOX_IDS[i]);
            cb.setText(physNames[i]);
            cb.setOnCheckedChangeListener(this);
            if(physToGpio_5[i+1] == -1)
                cb.setEnabled(false);
            mLeds.add(cb);
            idToIndex.put(CHECKBOX_IDS[i], i);
            idToCb.put(CHECKBOX_IDS[i], cb);
        }
        System.out.println("aaaaaaaaaaaaaaaaaa!");



//        Button gpio_test_btn = (Button) findViewById(R.id.gpio_test_btn);
//        Button serial_test_btn = (Button) findViewById(R.id.serial_test_btn);
//        Button i2c_test_btn = (Button) findViewById(R.id.i2c_test_btn);
//        Button spi_test_btn = (Button) findViewById(R.id.spi_test_btn);
//
//        gpio_test_btn.setOnClickListener(ocl);
//        serial_test_btn.setOnClickListener(ocl);
//        i2c_test_btn.setOnClickListener(ocl);
//        spi_test_btn.setOnClickListener(ocl);
    }
    int pin;

    Handler handler;
    View.OnClickListener ocl =new View.OnClickListener() {
        Intent intent;
        @Override
        public void onClick(View arg0) {

//            switch (arg0.getId()) {
//                case R.id.gpio_test_btn:
//                    intent = new Intent(MainActivity.this, TestGpio.class);
//                    startActivity(intent);
//                    break;
//                case R.id.serial_test_btn:
//                    intent = new Intent(MainActivity.this, TestSerialPort.class);
//                    startActivity(intent);
//                    break;
//                case R.id.i2c_test_btn:
//                    intent = new Intent(MainActivity.this, TestI2c.class);
//                    startActivity(intent);
//                    break;
//                case R.id.spi_test_btn:
//                    intent = new Intent(MainActivity.this, TestSpi.class);
//                    startActivity(intent);
//                    break;
//            }
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        Log.i("======Test======","======check :" + compoundButton.getId());
        System.out.println("aaaaaaaaaaaaaaaaaa!");
        int id = compoundButton.getId();
        CheckBox cb = idToCb.get(id);
        pin = physToGpio_5[idToIndex.get(id) + 1];
        GPIOControl.doExport(pin);
        if(cb.isEnabled())
        {
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    GPIOControl.pinMode(pin, 1);
                    GPIOControl.digitalWrite(pin, 1);

                }
            }, 10);
        }
        else
        {
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    GPIOControl.pinMode(pin, 1);
                    GPIOControl.digitalWrite(pin, 0);
                }
            }, 10);
        }
        GPIOControl.doUnexport(pin);
    }
}
