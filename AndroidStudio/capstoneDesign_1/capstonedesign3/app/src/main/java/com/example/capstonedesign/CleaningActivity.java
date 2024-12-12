package com.example.capstonedesign;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CleaningActivity extends AppCompatActivity {
    Button cleaning_stopCleaning;
    Chronometer cleaning_chronotime;

    private TcpService tcpService;
    private boolean tcpServiceBound = false;
    boolean success;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cleaning);

        cleaning_stopCleaning = (Button) findViewById(R.id.Cleaning_StopCleaning);
        cleaning_chronotime = (Chronometer)findViewById(R.id.Cleaning_ChronoTime);

        Intent bindIntent = new Intent(this, TcpService.class);
        bindService(bindIntent, tcpServiceConnection, Context.BIND_AUTO_CREATE);
        Intent getIntent =  getIntent();
        success = getIntent.getBooleanExtra("success", true);

        cleaning_chronotime.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                int hours = (int) (elapsedMillis / 3600000);
                int minutes = (int) (elapsedMillis - hours * 3600000) / 60000;
                int seconds = (int) (elapsedMillis - hours * 3600000 - minutes * 60000) / 1000;
                String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                chronometer.setText(time);
            }
        });

        cleaning_chronotime.setBase(SystemClock.elapsedRealtime());
        if(success)
            cleaning_chronotime.start();
        if(!success){
            cleaning_stopCleaning.setEnabled(false);
            Toast.makeText(CleaningActivity.this, "청소기와 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
        }

        cleaning_stopCleaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                Toast.makeText(getApplicationContext(),"청소를 중지합니다",Toast.LENGTH_SHORT).show();

                cleaning_chronotime.stop();
                Intent intent = new Intent(CleaningActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
