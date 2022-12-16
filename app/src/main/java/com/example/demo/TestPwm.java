package com.example.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.wiringop.SpiControl;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import com.example.demo.RootCmd;


public class TestPwm extends Activity {
    private static final String TAG = "TestPwm";
    private EditText et_pwm_period;
    private SeekBar sb_pwm_duty;
    private CheckBox cb_pwm_en;
    private ToggleButton bt_pwm;
    private Spinner sp_pwm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pwm_test);
        et_pwm_period = findViewById(R.id.et_pwm_period);
        sb_pwm_duty = findViewById(R.id.sb_pwm_duty);
        cb_pwm_en = findViewById(R.id.cb_pwm_en);
        bt_pwm = findViewById(R.id.btn_pwm);
        cb_pwm_en.setEnabled(false);
        sb_pwm_duty.setEnabled(false);

        bt_pwm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sb_pwm_duty.setEnabled(false);
                    cb_pwm_en.setEnabled(false);
                    RootCmd.execRootCmdSilent("echo 0 >  /sys/class/pwm/pwmchip0/unexport");
                } else {
                    sb_pwm_duty.setEnabled(true);
                    cb_pwm_en.setEnabled(true);
                    sb_pwm_duty.setMax(Integer.parseInt(et_pwm_period.getText().toString()));
                    RootCmd.execRootCmdSilent("echo 0 >  /sys/class/pwm/pwmchip0/export");
                }
            }
        });
        cb_pwm_en.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    //BufferedWriter bw = new BufferedWriter(new FileWriter("/sys/class/pwm/pwmchip0/pwm0/enable"));
                    Log.i(TAG, "cccccccccccccccccccccccccccccccccc");
                    if (b) {
                        RootCmd.execRootCmdSilent("echo 1 >  /sys/class/pwm/pwmchip0/pwm0/enable");
                        String str = RootCmd.execRootCmd("cat /sys/class/pwm/pwmchip0/pwm0/enable");
                        if(! "1".equals(str)) {
                            cb_pwm_en.setChecked(false);
                        }
                    }
                    else {
                        RootCmd.execRootCmdSilent("echo 0 >  /sys/class/pwm/pwmchip0/pwm0/enable");
                    }
            }
        });

        sb_pwm_duty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int duty;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                System.out.println("onProgressChanged");
                duty = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                System.out.println("onStartTrackingTouch");
                String cmd = "echo " + seekBar.getMax() + " > /sys/class/pwm/pwmchip0/pwm0/period";
                RootCmd.execRootCmdSilent(cmd);
                System.out.println("onStartTrackingTouch end");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                System.out.println("onStopTrackingTouch");
                String cmd = "echo " + duty + " > /sys/class/pwm/pwmchip0/pwm0/duty_cycle";
                RootCmd.execRootCmdSilent(cmd);
            }
        });
    }
}

