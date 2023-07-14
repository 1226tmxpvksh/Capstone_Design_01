package com.example.capstonedesign;


import static android.content.Context.MODE_PRIVATE;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;

import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;

import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;



import androidx.core.app.NotificationCompat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class CleenerStartAlarmReceiver extends BroadcastReceiver {
    private static final String message = "청소를 시작합니다."; // 알람 메시지
    int requestCode;
    SharedPreferences alarmPref;
    SharedPreferences.Editor alarmPrefEditor;
    Intent tcpServiceIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        alarmPref = context.getSharedPreferences("alarm", MODE_PRIVATE);
        alarmPrefEditor = alarmPref.edit();

        android.util.Log.d("알람 리시버 확인용", "Intent 내용: " + intent.toString());

        if(!intent.hasExtra("requestCode")){
            android.util.Log.d("알람 리시버 확인용", "requestCode : 없음");
        }
        requestCode = intent.getIntExtra("requestCode", 0);

        showToast(message, context);
        showNotification(message, context);

        tcpServiceIntent = new Intent(context, TcpService.class);
        tcpServiceIntent.putExtra("msg", TcpService.Cleaning_Start);
        context.startService(tcpServiceIntent);


/*
        Intent sendIntent = new Intent(context, SendClientActivity.class);
        android.util.Log.d("통신 서비스 확인용", "서비스에 값 보내기");
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(sendIntent);
        android.util.Log.d("통신 서비스 확인용", "send 이후");
*/

        //TcpService.msg = TcpService.Cleaning_Start;

        android.util.Log.d("알람 리시버 확인용", "requestCode : " + requestCode);

        String alarmtime = alarmPref.getString(requestCode+"", "");
        android.util.Log.d("알람 리시버 확인용", alarmtime);

        int month = Integer.parseInt(alarmtime.substring(0,2));
        int day = Integer.parseInt(alarmtime.substring(2,4));

        String file_name = month + "월" + day + "일.txt";
        android.util.Log.d("알람 리시버 확인용", "file_name : " + file_name);

        FileInputStream inFs = null;
        byte[] text = new byte[1000];
        try {
            inFs = context.openFileInput(file_name);
            inFs.read(text);
            android.util.Log.d("알람 리시버 확인용", "text : " + text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String file_text = new String(text);
        android.util.Log.d("알람 리시버 확인용", "file_text : " + file_text);
        String[] tok = file_text.split(",");
        String newFileText = "";
        android.util.Log.d("알람 리시버 확인용", 1+"");
        android.util.Log.d("알람 리시버 확인용", "tok.length : " + tok.length);

        for(String str : tok){
            // 시분 추출
            if(str.contains("null")){
                break;
            }
            String tmp = str.replaceAll("[^0-9]", "");

            android.util.Log.d("알람 리시버 확인용", "str : " + str);
            android.util.Log.d("알람 리시버 확인용", "tmp : " + tmp);

            int time = Integer.parseInt(tmp.substring(0,2));
            int minute = Integer.parseInt(tmp.substring(2,4));

            // 시분 추출
            if(!tmp.equals(alarmtime.substring(4,8))){
                android.util.Log.d("알람 리시버 확인용", "alarmtime.substring(4,8) : " + alarmtime.substring(4,8));
                android.util.Log.d("알람 리시버 확인용", "tmp : " + tmp);
                newFileText += typeTrasform(time) + "시"+ typeTrasform(minute) + "분 (터치하면 삭제) ,";
            }
        }
        android.util.Log.d("알람 리시버 확인용", "newFileText : " + newFileText);

        FileOutputStream fileOutputStream = null; // 파일 을 읽어옴
        try {
            fileOutputStream = context.openFileOutput(file_name, Context.MODE_PRIVATE);
            newFileText += "null";
            fileOutputStream.write(newFileText.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //주의사항 NULL값이 먼저오게 저장하면 저장되어 있던 문자열을 식별하지 못함


        alarmPrefEditor.remove(requestCode+"");
        alarmPrefEditor.commit();

        android.util.Log.d("알람 리시버 확인용", "다싱행됨");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////// 토스트 메세지, 푸쉬알림 구현부////////////////////////////////////////////////////////////////////////////////////////////
    private void showToast(String message, Context context) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private static final String CHANNEL_ID = "timer_channel";

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Timer Channel";
            String description = "Channel for Timer Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String message, Context context) {
        // Create the notification channel
        createNotificationChannel(context);

        // Set the unique notification ID
        int notificationId = 1;

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.setting)
                .setContentTitle("청소기")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Get the notification manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            // Show the notification
            notificationManager.notify(notificationId, builder.build());
        }
    }

    public String typeTrasform(int number) {
        String str = "";
        if (number < 10) {
            str += "0" + number;
        }
        else {
            str += number;
        }
        return str;
    }

}
