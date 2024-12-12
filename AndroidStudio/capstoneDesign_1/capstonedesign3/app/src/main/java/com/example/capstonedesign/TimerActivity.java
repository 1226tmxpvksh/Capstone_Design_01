package com.example.capstonedesign;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;



public class TimerActivity extends AppCompatActivity {
    public TimerActivity getContext(){
        return this;
    }

    //출력함수
    static void sop(String a)
    {
        System.out.println("----------------------------------------------------------------------------------------------");
        System.out.println(a);
        System.out.println("----------------------------------------------------------------------------------------------");
    }
    static void sop(int a)
    {
        System.out.println("----------------------------------------------------------------------------------------------");
        System.out.println(a);
        System.out.println("----------------------------------------------------------------------------------------------");
    }

    private static final String message = "알람이 울립니다."; // 알람 메시지
    //시간과 분을 입력받는 텍스트
    EditText aclock_text;
    EditText minute_text;
    //버튼을 추가할 레이아웃
    LinearLayout reservation_layout;
    //확인 버튼
    Button check;
    //취소 버튼
    Button Back;
    //버튼을 저장할 배열
    Button[] time_button = new Button[30];
    //시간을 저장할 배열
    int aclock[] = new int[30];
    //분을 저장할 배열
    int minit[] = new int[30];
    //버튼 배열 인덱스
    int count =0;

    int month=0;
    int day=0;

    // 알람 때문에 추가하는 변수
    // 마지막 알람 id
    int lastId;


    SharedPreferences alarmPref;
    SharedPreferences.Editor alarmPrefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer);

        // 클릭된 날짜를 받아옴
        Intent In = getIntent();

        month = In.getIntExtra("mon", 0);
        sop(month);
        day = In.getIntExtra("day", 0);

        aclock_text = (EditText) findViewById(R.id.aclock_text);
        minute_text = (EditText) findViewById(R.id.minute_text);
        reservation_layout = (LinearLayout) findViewById(R.id.reservation_layout);
        check = (Button) findViewById(R.id.check);
        Back = (Button) findViewById(R.id.cancellation);

        //레이아웃에 마진을 주기 위해서 레이아웃 파라미터 지정
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                200);
        param.topMargin = 10;
        param.leftMargin = 30;
        param.rightMargin = 30;

        //대화상자 배열 생성
        AlertDialog[] Back_Text = new AlertDialog[30];
        //버튼 삭제 대화상자 생성


        // alarm이라는 이름의 SharedPreferences를 가져와서 alarmPref에 저장
        alarmPref = getSharedPreferences("alarm", MODE_PRIVATE);
        alarmPrefEditor = alarmPref.edit();

        // SharedPreferences에서 lastID라는 키에서 값을 받아옴 이 ID ㅣ작해서부터 알람을 만들어야함.
        // lastId에 값이 없다면 0을 반환
        lastId = alarmPref.getInt("lastId", 0);


        //시작할 때 버튼 만들기
        try {
            String A = Integer.toString(month) + "월" + Integer.toString(day) + "일.txt";

            FileInputStream inFs = openFileInput(A);
            byte[] text = new byte[1000];
            inFs.read(text);
            String file_text = new String(text);
            String B;
            B = file_text;

            //토큰을 사용하여 문자열을 나눠줌
            String[] aaa = file_text.split(",");


            //aclock과 minit 배열에 시간과 분을 넣어줌
            for (String token : aaa) {
                //마지막에 널을 출력하지 않게 하기위한 if문 )의 인덱스를 비교함
                if (token.indexOf(")") != -1) {

                    //버튼을 생성
                    count += 1;
                    sop(count);
                    Button btn = new Button(TimerActivity.this);
                    btn.setText(token);
                    btn.setLayoutParams(param);
                    //button_style 에서 물결효과와 색상을 받아옴
                    btn.setBackgroundResource(R.drawable.button_style);
                    reservation_layout.addView(btn);
                    time_button[count] = btn;

                    //시간을 측정해서 시간 분 배열에 넣어줌
                    String[] aa = token.split("분 ");

                    int setcount = 0;

                    for (String tk : aa) {


                        if (tk.indexOf("시") != -1) {
                            String[] r = tk.split("시");
                            for (String Q : r) {
                                sop("ct=" + setcount);
                                if (setcount % 2 == 0) {
                                    aclock[count] = Integer.parseInt(Q);
                                    sop("시간" + Q);
                                } else {
                                    minit[count] = Integer.parseInt(Q);
                                    sop(Q);
                                }
                                setcount += 1;
                            }
                        }
                    }
                }


            }
        } catch (IOException e) {
            //어떤 오류인지 확인하기 위한 코드
            sop("error");
            e.printStackTrace();
        }

        //삭제 기능 추가
        for (int i = 0; i < 30; i++) {
            System.out.println(i);
            if (time_button[i] != null) {
                int a = i;
                time_button[i].setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View view) {
                        //대화상자 빌터를 만들어 텍스트와 "네" 버튼을 만들어줌
                        AlertDialog.Builder Bil = new AlertDialog.Builder(TimerActivity.this);
                        Bil.setTitle(aclock[a] + "시" + minit[a] + "분 삭제가 완료되었습니다.");

                        //파일에서 삭제할 객체 찾아서 파일에서 삭제-----------------------------------------------------------
                        String cub3 = "";
                        String A = Integer.toString(month) + "월" + Integer.toString(day) + "일.txt";
                        try {
                            FileInputStream inFs = openFileInput(A);
                            byte[] text = new byte[1000];
                            inFs.read(text);
                            String file_text = new String(text);
                            sop("file   " + file_text);
                            String[] tok = file_text.split(",");
                            String cub2 = "";
                            //-------------------------------작업중-----------------------------------------
                            for (String Aa : tok) {
                                sop("Aa " + Aa.length() + " " + Aa);
                                String cub;
                                if(aclock[a] < 10) {
                                    if(minit[a] < 10){
                                        cub = "0" + aclock[a] + "시" + "0" + minit[a] + "분 (터치하면 삭제) ";
                                    }
                                    else{
                                        cub = "0" + aclock[a] + "시" + minit[a] + "분 (터치하면 삭제) ";
                                    }
                                }
                                else {
                                    if(minit[a] < 10){
                                        cub = aclock[a] + "시" + "0" + minit[a] + "분 (터치하면 삭제) ";
                                    }
                                    else{
                                        cub = aclock[a] + "시" + minit[a] + "분 (터치하면 삭제) ";
                                    }
                                }
                                sop("cub " + cub.length() + " " + cub);


                                if (!Aa.equals(cub)) {
                                    cub2 += Aa + ",";
                                    sop("cub2 " + cub2);
                                    sop("----------------------------------------------");
                                }
                                cub3 = cub2;
                            }

                            String fileName = Integer.toString(month) + "월" + Integer.toString(day) + "일.txt";
                            FileOutputStream fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE); // 파일 을 읽어옴
                            //주의사항 NULL값이 먼저오게 저장하면 저장되어 있던 문자열을 식별하지 못함
                            fileOutputStream.write(cub3.getBytes());
                            fileOutputStream.close();

                        } catch (IOException e) {
                        }


                        Bil.setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                time_button[a].setVisibility(View.GONE);
                                count -= 1;
                            }
                        });

                        Back_Text[a] = Bil.create();

                        Back_Text[a].show();
                    }
                });
            }
        }

        check.setOnClickListener(new View.OnClickListener() {

            //파일 의 문자열을 저장할 문자열
            String file;

            @Override
            public void onClick(View view) {
                //파일에서 읽어오기
                try {
                    String A = Integer.toString(month) + "월" + Integer.toString(day) + "일.txt";
                    FileInputStream inFs = openFileInput(A);
                    byte[] text = new byte[1000];
                    inFs.read(text);
                    String file_text = new String(text);
                    sop("fddfdff" + file_text);
                    file = file_text;
                    sop(file);
                } catch (IOException e) {
                }

                String text_clock = "";
                text_clock = aclock_text.getText().toString();
                String text_minit = "";
                text_minit = minute_text.getText().toString();
                count += 1;


                //버튼 생성해서 time_button 에 넣어줌
                //!= null 과 != ""이 안됨
                if (text_clock.length() != 0 && text_minit.length() != 0) {

                    if (Integer.parseInt(text_clock) >= 0 && Integer.parseInt(text_clock) < 25
                            && Integer.parseInt(text_minit) >= 0 && Integer.parseInt(text_minit) < 61
                    ) {
                        Button btn = new Button(TimerActivity.this);
                        btn.setText(text_clock + "시" + text_minit + "분 (터치하면 삭제)");
                        btn.setLayoutParams(param);
                        //button_style 에서 물결효과와 색상을 받아옴
                        btn.setBackgroundResource(R.drawable.button_style);
                        reservation_layout.addView(btn);
                        time_button[count] = btn;
                        aclock[count] = Integer.parseInt(text_clock);
                        minit[count] = Integer.parseInt(text_minit);


                        // 파일에 시간 분 저장
                        try {
                            String fileName = Integer.toString(month) + "월" + Integer.toString(day) + "일.txt";
                            FileOutputStream fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE); // 파일 을 읽어옴
                            //주의사항 NULL값이 먼저오게 저장하면 저장되어 있던 문자열을 식별하지 못함
                            String time = text_clock + "시" + text_minit + "분 (터치하면 삭제) ," + file + "";
                            fileOutputStream.write(time.getBytes());
                            fileOutputStream.close();
                        } catch (IOException e) {

                        }
                        // 여기다 알람 넣기

                    }
                }

                //버튼 삭제
                if (text_clock.length() != 0 && text_minit.length() != 0) {
                    if (Integer.parseInt(text_clock) >= 0 && Integer.parseInt(text_clock) < 25
                            && Integer.parseInt(text_minit) >= 0 && Integer.parseInt(text_minit) < 61
                    ) {
                        for (int i = 0; i < 30; i++) {
                            System.out.println(i);
                            if (time_button[i] != null) {
                                int a = i;
                                time_button[i].setOnClickListener(new View.OnClickListener() {
                                    @Override

                                    public void onClick(View view) {
                                        String alarmTime = "";
                                        //대화상자 빌터를 만들어 텍스트와 "네" 버튼을 만들어줌
                                        sop("asassa");
                                        AlertDialog.Builder Bil = new AlertDialog.Builder(TimerActivity.this);
                                        Bil.setTitle("삭제가 완료되었습니다.");

                                        //파일에서 삭제할 객체 찾아서 파일에서 삭제-----------------------------------------------------------
                                        String cub3 = "";
                                        String A = Integer.toString(month) + "월" + Integer.toString(day) + "일.txt";
                                        alarmTime += typeTrasform(month);
                                        alarmTime += typeTrasform(day);
                                        alarmTime += typeTrasform(aclock[a]);
                                        alarmTime += typeTrasform(minit[a]);
                                        try {
                                            FileInputStream inFs = openFileInput(A);
                                            byte[] text = new byte[1000];
                                            inFs.read(text);
                                            String file_text = new String(text);
                                            sop("file   " + file_text);
                                            String[] tok = file_text.split(",");
                                            String cub2 = "";
                                            //-------------------------------작업중-----------------------------------------
                                            for (String Aa : tok) {
                                                sop("Aa " + Aa.length() + " " + Aa);
                                                String cub;
                                                cub = aclock[a] + "시" + minit[a] + "분 (터치하면 삭제) ";
                                                sop("cub " + cub.length() + " " + cub);

                                                if (!Aa.equals(cub)) {
                                                    cub2 += Aa + ",";
                                                    sop("cub2 " + cub2);
                                                    sop("----------------------------------------------");
                                                }
                                                cub3 = cub2;
                                            }

                                            String fileName = Integer.toString(month) + "월" + Integer.toString(day) + "일.txt";
                                            FileOutputStream fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE); // 파일 을 읽어옴
                                            //주의사항 NULL값이 먼저오게 저장하면 저장되어 있던 문자열을 식별하지 못함
                                            fileOutputStream.write(cub3.getBytes());
                                            fileOutputStream.close();

                                        } catch (IOException e) {
                                        }


                                        Bil.setPositiveButton("네", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                time_button[a].setVisibility(View.GONE);
                                                count -= 1;
                                            }
                                        });

                                        Back_Text[a] = Bil.create();

                                        Back_Text[a].show();

                                        // 예약 알람 삭제시 alarmPref에 저장된 알람 시간 정보를 id값을 찾아서 제거
                                        for (int i = 0; i < lastId; i++) {
                                            if (alarmPref.contains(i + "")) {
                                                String str = alarmPref.getString(i+"", "");
                                                if(alarmTime.equals(str)){
                                                    alarmPrefEditor.remove(i + "");
                                                    alarmPrefEditor.commit();
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }

                    }

                }

                //BoroadCastReceiver 생성, CleenerStartAlarmReceiver가 BoroadCastReceiver를 상속받음.

                Intent alarm = new Intent(TimerActivity.this, CleenerStartAlarmReceiver.class);
                // PendingIntent 생성,PendingIntent는 알람마다 1개씩 다 있어야 함. 따라서 고유 id를 가짐. 그 고유 id가 lastId
                alarm.putExtra("requestCode", lastId);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(TimerActivity.this, lastId, alarm, FLAG_CANCEL_CURRENT );

                android.util.Log.i("타이머 액티비티",  "lastId : " + lastId);

                AlarmManager cleenerStartAlarm = (AlarmManager) getSystemService(TimerActivity.this.ALARM_SERVICE);

                // 에디트 텍스트에 입력한 시 분 을 받아와서 정수로 바꿔줌
                int hour = Integer.parseInt(aclock_text.getText().toString());
                int minute = Integer.parseInt(minute_text.getText().toString());

                Calendar calendar = Calendar.getInstance();
                // 월은 0부터 시작
                calendar.set(Calendar.MONTH, month - 1);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                String alarmTime = "";

                alarmTime += typeTrasform(month);
                alarmTime += typeTrasform(day);
                alarmTime += typeTrasform(hour);
                alarmTime += typeTrasform(minute);

                // 알람 시간을 영구히 저장 alarm이라는 곳에 lastId라는 키로 calendar의 값을 저장
                android.util.Log.i("타이머 액티비티",  alarmTime );

                alarmPrefEditor.putString(lastId + "", alarmTime);
                alarmPrefEditor.commit();

                if (cleenerStartAlarm != null) {
                    cleenerStartAlarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    android.util.Log.i("타이머 액티비티", "ok");
                }

                lastId += 1;
                alarmPrefEditor.putInt("lastId", lastId);
                alarmPrefEditor.commit();
            }

        });


        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimerActivity.this, CalenderActivity.class);
                startActivity(intent);
                //화면이동
            }
        });
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




