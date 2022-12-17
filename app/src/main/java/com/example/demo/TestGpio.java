package com.example.demo;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wiringop.GPIOControl;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TestGpio extends Activity implements CompoundButton.OnCheckedChangeListener{

    Button gpio_num_btn;
    TextView gpio_num_tv;

    Button read_gpio_btn;
    TextView read_gpio_tv;

    Button write_gpio_1_btn,write_gpio_0_btn;
    TextView write_gpio_tv;

    EditText gpio_text;

    Handler handler;
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
                    "   SDA.5", "5V      ",
                    "   SCL.5", "GND     ",
                    "   PWM15", "RXD.0   ",
                    "     GND", "TXD.0   ",
                    " CAN1_RX", "CAN2_TX ",
                    " CAN1_TX", "GND     ",
                    " CAN2_RX", "SDA.1   ",
                    "    3.3V", "SCL.1   ",
                    "SPI4_TXD", "GND     ",
                    "SPI4_RXD", "GPIO2_D4",
                    "SPI4_CLK", "SPI4_CS1",
                    "     GND", "PWM1    ",
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

    int pin;
    CheckBox cb_gpio;
    Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gpio_main);

        gpio_text = (EditText)findViewById(R.id.gpio_text);

        gpio_num_btn = (Button)findViewById(R.id.gpio_num_btn);
        gpio_num_tv = (TextView)findViewById(R.id.gpio_num_tv);

        read_gpio_btn = (Button)findViewById(R.id.read_gpio_btn);
        read_gpio_tv = (TextView)findViewById(R.id.read_gpio_tv);

        write_gpio_0_btn = (Button)findViewById(R.id.write_gpio_0_btn);
        write_gpio_1_btn = (Button)findViewById(R.id.write_gpio_1_btn);
        write_gpio_tv = (TextView)findViewById(R.id.write_gpio_tv);


        gpio_num_btn.setOnClickListener(ocl);
        read_gpio_btn.setOnClickListener(ocl);
        write_gpio_0_btn.setOnClickListener(ocl);
        write_gpio_1_btn.setOnClickListener(ocl);
        for (int i = 0; i < CHECKBOX_IDS.length; i++) {
            CheckBox cb = (CheckBox) findViewById(CHECKBOX_IDS[i]);
            cb.setText(physNames[i]);
            cb.setOnCheckedChangeListener(this);
            if (physToGpio_5[i + 1] == -1)
                cb.setEnabled(false);

            idToIndex.put(CHECKBOX_IDS[i], i);
            idToCb.put(CHECKBOX_IDS[i], cb);
        }
    }

    OnClickListener ocl = new OnClickListener() {

        int pin;
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub

            switch (arg0.getId()) {
                case R.id.gpio_num_btn:
                    gpio_num_tv.setText(gpioParse(gpio_text.getText().toString())+"");
                    break;
                case R.id.read_gpio_btn:
                    pin = gpioParse(gpio_text.getText().toString());
                    GPIOControl.doExport(pin);
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            GPIOControl.pinMode(pin, 0);
                            int value = GPIOControl.digitalRead(pin);

                            read_gpio_tv.setText(value+"");
                        }
                    }, 10);

                    break;
                case R.id.write_gpio_0_btn:
                    pin = gpioParse(gpio_text.getText().toString());
                    GPIOControl.doExport(pin);
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            GPIOControl.pinMode(pin, 1);
                            write_gpio_tv.setText(GPIOControl.digitalWrite(pin, 0) == -1 ? "写入失败" : "写入成功");
                            GPIOControl.doUnexport(pin);
                        }
                    }, 10);

                    break;
                case R.id.write_gpio_1_btn:
                    pin = gpioParse(gpio_text.getText().toString());
                    GPIOControl.doExport(pin);
                    handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            GPIOControl.pinMode(pin, 1);
                            write_gpio_tv.setText(GPIOControl.digitalWrite(pin, 1) == -1 ? "写入失败" : "写入成功");
                            GPIOControl.doUnexport(pin);
                        }
                    }, 10);

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();
        cb_gpio = idToCb.get(id);
        pin = physToGpio_5[idToIndex.get(id) + 1];
        GPIOControl.doExport(pin);
        if(cb_gpio.isChecked())
        {
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    GPIOControl.pinMode(pin, 1);
                    if(-1 == GPIOControl.digitalWrite(pin, 1))
                    {
                        cb_gpio.setChecked(false);
                        toast=Toast.makeText(getApplicationContext(), "digitalWrite fail", Toast.LENGTH_SHORT);
                    }
                    else {
                        if(1 == GPIOControl.digitalRead(pin))
                            toast = Toast.makeText(getApplicationContext(), "digitalWrite " + pin + " to high", Toast.LENGTH_SHORT);
                        else {
                            toast = Toast.makeText(getApplicationContext(), "write high fail", Toast.LENGTH_SHORT);
                            cb_gpio.setChecked(false);
                        }
                    }
                    toast.show();
                    GPIOControl.doUnexport(pin);
                }
            }, 10);
        }
        else {
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    GPIOControl.pinMode(pin, 1);
                    if(-1 == GPIOControl.digitalWrite(pin, 0)) {
                        toast=Toast.makeText(getApplicationContext(), "digitalWrite fail", Toast.LENGTH_SHORT);
                    }
                    else {
                        if(0 == GPIOControl.digitalRead(pin))
                            toast = Toast.makeText(getApplicationContext(), "digitalWrite " + pin + " to low", Toast.LENGTH_SHORT);
                        else {
                            toast = Toast.makeText(getApplicationContext(), "write low fail", Toast.LENGTH_SHORT);
                        }
                    }
                    toast.show();
                    GPIOControl.doUnexport(pin);
                }
            }, 10);
        }
    }

    public int gpioParse(String gpioStr) {
        if (gpioStr != null && gpioStr.length() == 8) {
            gpioStr = gpioStr.toUpperCase();
            if (gpioStr.charAt(4) >= '0' && gpioStr.charAt(4) <= '8') {
                if (gpioStr.charAt(6) >= 'A' && gpioStr.charAt(6) <= 'D') {
                    return gpioStr.charAt(7) >= '0' && gpioStr.charAt(7) <= '7' ? (gpioStr.charAt(4) - 48) * 32 + (gpioStr.charAt(6) - 65) * 8 + (gpioStr.charAt(7) - 48) : -1;
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        } else {
            System.out.println("input gpio error!");
            return -1;
        }
    }


}