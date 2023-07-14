package com.example.capstonedesign;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static android.content.Context.MODE_PRIVATE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

public class DeviceBootReceiver extends BroadcastReceiver {
    SharedPreferences alarmPref;

    public void onReceive(Context context, Intent intent) {
        alarmPref = context.getSharedPreferences("alarm", MODE_PRIVATE);
        int lastId = alarmPref.getInt("lastId", 0);


        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {
            Toast.makeText(context.getApplicationContext(),"부팅 후 알람 설정 시작", Toast.LENGTH_LONG).show();

            android.util.Log.d("부팅 확인", lastId+"");
            for(int i = 0; i < lastId; i++){
                if(alarmPref.contains(i+"")){
                    Intent alarm = new Intent(context, CleenerStartAlarmReceiver.class);
                    AlarmManager cleenerStartAlarm = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                    alarm.putExtra("requestCode", i);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, i, alarm, FLAG_CANCEL_CURRENT);

                    String alarmTime = alarmPref.getString(i+"", "");

                    int month = Integer.parseInt(alarmTime.substring(0,2));
                    int day = Integer.parseInt(alarmTime.substring(2,4));
                    int hour = Integer.parseInt(alarmTime.substring(4,6));
                    int minute = Integer.parseInt(alarmTime.substring(6,8));

                    Calendar calendar = Calendar.getInstance();
                    // 월은 0부터 시작
                    calendar.set(Calendar.MONTH, month - 1);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);

                    cleenerStartAlarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                    android.util.Log.d("부팅 확인", "ID : " + i);
                    android.util.Log.d("부팅 확인", alarmTime);
                }
            }
        }
    }
}
