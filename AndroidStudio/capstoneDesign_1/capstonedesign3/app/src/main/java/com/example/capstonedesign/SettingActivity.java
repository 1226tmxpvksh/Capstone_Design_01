package com.example.capstonedesign;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {
    Button setting_alram, setting_complain, setting_reset, setting_information, setting_manual, setting_apply, setting_cancel;
    LinearLayout setting_linearLayout;
    TimePicker setting_tPicker1;
    DatePicker setting_dPicker1;
    RadioGroup setting_alram1;
    TextView setting_information1, setting_manual1;

    int a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        setting_alram = (Button) findViewById(R.id.Setting_Alram);
        setting_complain = (Button)findViewById(R.id.Setting_Complain);
        setting_reset = (Button) findViewById(R.id.Setting_Reset);
        setting_information = (Button) findViewById(R.id.Setting_Information);
        setting_manual = (Button) findViewById(R.id.Setting_Manual);
        setting_linearLayout = (LinearLayout) findViewById(R.id.Setting_linearLayout1);
        setting_tPicker1 = (TimePicker) findViewById(R.id.Setting_Timepicker1);
        setting_dPicker1 = (DatePicker) findViewById(R.id.Setting_Datepicker1);
        setting_alram1 = (RadioGroup) findViewById(R.id.Setting_Alram1);
        setting_information1 = (TextView) findViewById(R.id.Setting_Information1);
        setting_manual1 = (TextView) findViewById(R.id.Setting_Manual1);
        setting_apply = (Button) findViewById(R.id.Setting_Apply);
        setting_cancel = (Button) findViewById(R.id.Setting_Cancel);


        setting_tPicker1.setVisibility(View.GONE);
        setting_dPicker1.setVisibility(View.GONE);
        setting_alram1.setVisibility(View.GONE);
        setting_information1.setVisibility(View.GONE);
        setting_manual1.setVisibility(View.GONE);


        setting_alram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (a == 0) {
                    setting_alram1.setVisibility(View.VISIBLE);
                    a = 1;
                } else {
                    setting_alram1.setVisibility(View.GONE);
                    a = 0;
                }
            }
        });

        setting_complain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, ComplainActivity.class);
                startActivity(intent);
            }
        });

        setting_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"초기화를 시작합니다.",Toast.LENGTH_SHORT).show();
            }
        });

        setting_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (a == 0) {
                    setting_information1.setVisibility(View.VISIBLE);
                    a = 1;
                } else {
                    setting_information1.setVisibility(View.GONE);
                    a = 0;
                }
            }
        });

        setting_manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (a == 0) {
                    setting_manual1.setVisibility(View.VISIBLE);
                    a = 1;
                } else {
                    setting_manual1.setVisibility(View.GONE);
                    a = 0;
                }
            }
        });

        setting_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        setting_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
