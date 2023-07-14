package com.example.capstonedesign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button clean_start,clean_reservation;
    ImageButton clean_setting;
    Intent tcpServiceIntent;

    SharedPreferences alarmPref;
    SharedPreferences.Editor alarmPrefEditor;
    static int visitCount;
    boolean success = true;

    ///////////////////////////////////////////////////////////////서비스 바인딩//////////////////////////////////////////////////////////////////

    private TcpService tcpService;
    private boolean tcpServiceBound = false;

    private ServiceConnection tcpServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // 서비스에 연결되었을 때 호출되는 메소드
            TcpService.LocalBinder binder = (TcpService.LocalBinder) service;
            tcpService = binder.getService();
            tcpServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // 서비스 연결이 끊겼을 때 호출되는 메소드
            tcpServiceBound = false;
        }
    };
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 부팅 후 실행되는 리시버 사용가능하게 설정
        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        tcpServiceIntent = new Intent(this, TcpService.class);

        if (visitCount == 0) {
            android.util.Log.d("메인", "visitCount에 들어옴");
            startService(tcpServiceIntent);
            visitCount += 1;
        }

        Intent bindIntent = new Intent(this, TcpService.class);
        bindService(bindIntent, tcpServiceConnection, Context.BIND_AUTO_CREATE);

        //다되면 지우기
        alarmPref = getSharedPreferences("alarm", MODE_PRIVATE);
        alarmPrefEditor = alarmPref.edit();
        alarmPrefEditor.clear();
        alarmPrefEditor.commit();
        //

        clean_start = (Button) findViewById(R.id.Clean_start);
        clean_reservation = (Button) findViewById(R.id.Clean_reservation);
        clean_setting = (ImageButton) findViewById(R.id.Clean_setting);

        clean_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.util.Log.d("메인", "tcpServiceBound : " + tcpServiceBound);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (tcpServiceBound) {
                            if(!tcpService.sendData(TcpService.Cleaning_Start)){
                                success = false;
                            }
                        }
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(MainActivity.this, CleaningActivity.class);

                if(!success){
                    Toast.makeText(MainActivity.this, "청소기와 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
                    intent.putExtra("success", false);
                }

                startActivity(intent);
            }
        });

        clean_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CalenderActivity.class);
                startActivity(intent);
            }
        });

        clean_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }
}










