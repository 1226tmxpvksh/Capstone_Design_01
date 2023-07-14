package com.example.capstonedesign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;



public class CalenderActivity extends AppCompatActivity {
    Button check_calendar,cancellation_calendar;
    //현재 날짜를 받아옴
    Date now = new Date();

    TextView month;
    //년, 월, 일을 출력하는 날짜 포멧
    SimpleDateFormat Format;
    //년을 출력하는 날짜 포멧
    SimpleDateFormat yFormat;
    //월을 출력하는 날짜 포멧+
    SimpleDateFormat MFormat;
    //일을 출력하는 날짜 포멧
    SimpleDateFormat dFormat;
    //요일을 출력하는 날짜 포멧
    SimpleDateFormat EFormat;

    //첫날 요일을 저장
    String first_E;
    int first_E_int = 0;
    //마지막 요일을 저장
    String last_E;
    //마지막 요일을 숫자로 변경한 변수
    int last_E_int;

    //현재 몇달인지 저장
    int now_M;
    //어떤 날짜의 버튼을 눌렀는지 확인하는 변수
    int check_day;
    int check_mon;

    //지난달 다음달 id
    Button next_month;
    Button last_month;
    //초기화 버튼
    Button reset_calendar;

    //캘린더에 있는 버튼을 받아올 버튼 배열
    Button[] clean_btn = new Button[36];

    //출력하는 함수
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

    //요일을 숫자로 바꿔주는 함수
    static int week_to_int(String a){
        int b=0;
        switch (a)
        {
            case "월" :
                b = 1;
                break;
            case "화" :
                b = 2;
                break;
            case "수" :
                b = 3;
                break;
            case "목" :
                b = 4;
                break;
            case "금" :
                b = 5;
                break;
            case "토" :
                b = 6;
                break;
            case "일" :
                b = 7;
                break;
        }
        return b;
    }

    ////현재 일수 와 첫날의 일수 차이를 뺀 만큼 요일 수를 줄여줌
    static String gap_minus (Date N){
        SimpleDateFormat F = new SimpleDateFormat("EE");
        SimpleDateFormat D = new SimpleDateFormat("dd");
        String S= F.format(N).toString();
        String C = D.format(N).toString();

        int DG = Integer.parseInt(C);
        //현재 일수 와 첫날의 일수 차이를 뺀 만큼 요일 수를 줄여줌
        for (int x = 1; x < DG; x++){
            switch (S){
                case "월" :
                    S = "일";
                    break;
                case "화" :
                    S = "월";
                    break;
                case "수" :
                    S = "화";
                    break;
                case "목" :
                    S = "수";
                    break;
                case "금" :
                    S = "목";
                    break;
                case "토" :
                    S = "금";
                    break;
                case "일" :
                    S = "토";
                    break;
            }
        }
        return S;
    }

    //현재 달의 첫번째 요일을 구해줌
    static String gap_minus (String start_E, int Mon) {
        String S= start_E;
        int day = check_day(Mon-1);

        //현재 일수 와 첫날의 일수 차이를 뺀 만큼 요일 수를 줄여줌
        for (int x = 0; x < day; x++) {
            switch (S) {
                case "월":
                    S = "일";
                    break;
                case "화":
                    S = "월";
                    break;
                case "수":
                    S = "화";
                    break;
                case "목":
                    S = "수";
                    break;
                case "금":
                    S = "목";
                    break;
                case "토":
                    S = "금";
                    break;
                case "일":
                    S = "토";
                    break;
            }
        }
        return S;
    }

    static String  gap_plus (String start_E, int Mon){
        String S= start_E;

        int day = check_day(Mon);

        //현재 일수 와 첫날의 일수 차이를 뺀 만큼 요일 수를 줄여줌
        for (int x = 0; x < day; x++){
            switch (S){
                case "월" :
                    S = "화";
                    break;
                case "화" :
                    S = "수";
                    break;
                case "수" :
                    S = "목";
                    break;
                case "목" :
                    S = "금";
                    break;
                case "금" :
                    S = "토";
                    break;
                case "토" :
                    S = "일";
                    break;
                case "일" :
                    S = "월";
                    break;
            }
        }
        return S;
    }

    //달이 몇일까지 있는지 알려주는 함수
    static int check_day (int M)
    {
        int day=0;
        switch (M)
        {
            case 1 : case 3 : case 5: case 7: case 8: case 10: case 12:
            day = 31;
            break;
            case 2 :
                day = 28;
                break;
            case 4: case 6:case 9:case 11:
            day = 30;
            break;
        }
        return day;
    }



    //////////////////////////////////////////자바시작//////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calender);

        //년 월 일 을 출력하는 포멧
        Format = new SimpleDateFormat("yyyy-MM-dd");
        //년을 출력하는 날자 포멧 지정
        yFormat = new SimpleDateFormat("yyyy");
        //월을 출력하는 날짜 포멧 지정
        MFormat = new SimpleDateFormat("MM");
        //달을 숫자로 변환
        String cub = MFormat.format(now).toString();
        //현재 몇일인지를 저장하는 변수
        now_M = Integer.parseInt(cub);
        //일을 출력하는 날짜 포멧 지정
        dFormat = new SimpleDateFormat("dd");
        //요일을 출력하는 날짜 포멧 지정
        EFormat = new SimpleDateFormat("EE");

        //캘린더 상단에 월 출력
        month = (TextView) findViewById(R.id.clean_month);
        month.setText(MFormat.format(now));

        //다음달 지난달 find id
        next_month = (Button) findViewById(R.id.next_month);
        last_month = (Button) findViewById(R.id.last_month);
        check_calendar = (Button) findViewById(R.id.check_calendar);
        cancellation_calendar = (Button) findViewById(R.id.cancellation_calendar);
        reset_calendar = (Button) findViewById(R.id.reset_calendar);

        //현재 일을 저장하는 변수
        cub = dFormat.format(now).toString();
        int daygap = Integer.parseInt(cub);

        //시작하는 요일을 알려줌
        first_E = gap_minus (now);
        //요일을 숫자로 바꿔줌
        first_E_int = week_to_int(first_E);
        //만약 일요일로 시작하면 주번째 주 부터 시작하므로 바꿔줌
        if(first_E_int == 7)
        {
            first_E_int = 0;
        }

        //몇일까지 있는지 알려주는 함수
        int day = check_day(now_M);
        //마지막 요일 저장

        last_E = first_E;

        for(int i=0;i<36;i++)
        {
            clean_btn[i]= findViewById(getResources().getIdentifier("clean_button_" + i  , "id", "com.example.capstonedesign"));
            if (clean_btn[i] != null) {
                clean_btn[i].setVisibility(View.INVISIBLE);
                clean_btn[i].setText("...");
            }
        }

        //캘린더의 버튼을 한번에 받아옴
        //이번달이 무슨 요일부터 시작하는지 확인하여 시작하는 요일부터 숫자를 적음
        for(int i=1;i<day+1;i++)
        {

            //i를 직접 참조 불가능 해서 a라는 int 형 변수를 만들어 i 대신에 사용해줌
            int a = i ;
            clean_btn[i-1]= findViewById(getResources().getIdentifier("clean_button_" + (i + first_E_int) , "id", "com.example.capstonedesign"));
            clean_btn[i-1].setText(Integer.toString(i));
            clean_btn[i-1].setVisibility(View.VISIBLE);
            clean_btn[i-1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //사용자가 몇일을 클릭 했는지 저장
                    check_day = a;
                    check_mon = now_M;



                    String fileName = Integer.toString(check_mon) + "월" + Integer.toString(check_day) + "일.txt";  // 파일 이름을 월과 일로 지정

                    //클릭한 날짜를 timerActivity로 보내줌
                    Intent intent = new Intent(CalenderActivity.this, TimerActivity.class);
                    intent.putExtra("mon", check_mon);
                    intent.putExtra("day", check_day);
                    startActivity(intent);



                }
            });


        }

        //////////////////////////////////////////////////////////다음달 을 클릭 했을 때 동작/////////////////////////////////////////////////
        next_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //13월 부터는 없기 때문에 검사
                if(now_M<12)
                {
                    //달의 텍스트를 ...으로 초기화
                    for(int i=0;i<36;i++)
                    {
                        clean_btn[i]= findViewById(getResources().getIdentifier("clean_button_" + i  , "id", "com.example.capstonedesign"));
                        if (clean_btn[i] != null) {
                            clean_btn[i].setVisibility(View.INVISIBLE);
                            clean_btn[i].setText("...");
                        }
                    }



                    last_E = gap_plus (last_E, now_M);
                    last_E_int = week_to_int(last_E);
                    //만약 일요일로 시작하면 주번째 주 부터 시작하므로 바꿔줌
                    if(last_E_int == 7)
                    {
                        last_E_int = 0;
                    }




                    String str;
                    now_M = now_M+1;
                    //몇일까지 있는지 알려주는 함수
                    int next_mon_day = check_day(now_M);


                    str = Integer.toString(now_M);
                    month.setText(str);
                    for(int i=1;i<next_mon_day+1;i++)
                    {
                        //i를 직접 참조 불가능 해서 a라는 int 형 변수를 만들어 i 대신에 사용해줌
                        int a = i;
                        clean_btn[i-1]= findViewById(getResources().getIdentifier("clean_button_" + (i + last_E_int) , "id", "com.example.capstonedesign"));
                        if(clean_btn[i-1] != null){
                            clean_btn[i-1].setText(Integer.toString(i));
                            clean_btn[i-1].setVisibility(View.VISIBLE);
                            clean_btn[i-1].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //사용자가 몇일을 클릭 했는지 저장
                                    check_day = a;
                                    check_mon = now_M;
                                    /*
                                    String fileName = Integer.toString(check_mon) + "월" + Integer.toString(check_day) + "일.txt";  // 파일 이름을 월과 일로 지정
                                    try {
                                        FileOutputStream fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE); // 파일 생성
                                        fileOutputStream.close(); // 파일 닫기
                                        Toast.makeText(getApplicationContext(), "파일을 저장하였습니다 시간 예약으로 넘어갑니다.", Toast.LENGTH_SHORT).show(); // 저장 성공 메시지 표시

                                    } catch (IOException e) {
                                        e.printStackTrace(); // 예외 발생 시 예외 정보를 출력하는 메서드
                                        Toast.makeText(getApplicationContext(), "저장에 실패했습니다.", Toast.LENGTH_SHORT).show(); // 저장 실패 메시지 표시
                                    }
                                     */
                                    //날짜를 타이머로 전달 전달
                                    Intent intent = new Intent(CalenderActivity.this, TimerActivity.class);
                                    intent.putExtra("mon", check_mon);
                                    intent.putExtra("day", check_day);
                                    startActivity(intent);



                                }
                            });
                        }

                    }
                }





            }
        });




        last_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //0월 부터는 없기 때문애 검사
                if(1<now_M)
                {
                    //달의 텍스트를 ...으로 초기화
                    for(int i=0;i<36;i++)
                    {
                        clean_btn[i]= findViewById(getResources().getIdentifier("clean_button_" + i  , "id", "com.example.capstonedesign"));
                        if (clean_btn[i] != null) {
                            clean_btn[i].setVisibility(View.INVISIBLE);
                            clean_btn[i].setText("...");
                        }
                    }


                    first_E = gap_minus(last_E, now_M);
                    first_E_int = week_to_int(first_E);
                    now_M=now_M-1;
                    last_E = first_E;


                    month.setText(Integer.toString(now_M));
                    int prevo_month_day = check_day(now_M);
                    for(int i=1;i<prevo_month_day+1;i++)
                    {

                        //i를 직접 참조 불가능 해서 a라는 int 형 변수를 만들어 i 대신에 사용해줌
                        int a = i ;
                        clean_btn[i-1]= findViewById(getResources().getIdentifier("clean_button_" + (i + first_E_int) , "id", "com.example.capstonedesign"));
                        if(clean_btn[i-1] != null)
                        {
                            clean_btn[i-1].setText(Integer.toString(i));
                            clean_btn[i-1].setVisibility(View.VISIBLE);
                            clean_btn[i-1].setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //사용자가 몇일을 클릭 했는지 저장
                                    check_day = a;
                                    check_mon = now_M;
                                    /*
                                    String fileName = Integer.toString(check_mon) + "월" + Integer.toString(check_day) + "일.txt";  // 파일 이름을 월과 일로 지정
                                    try {
                                        FileOutputStream fileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE); // 파일 생성
                                        fileOutputStream.close(); // 파일 닫기
                                        Toast.makeText(getApplicationContext(), "파일을 저장하였습니다 시간 예약으로 넘어갑니다.", Toast.LENGTH_SHORT).show(); // 저장 성공 메시지 표시

                                    } catch (IOException e) {
                                        e.printStackTrace(); // 예외 발생 시 예외 정보를 출력하는 메서드
                                        Toast.makeText(getApplicationContext(), "저장에 실패했습니다.", Toast.LENGTH_SHORT).show(); // 저장 실패 메시지 표시
                                    }

                                     */
                                    //날짜를 타이머로 전달 전달
                                    Intent intent = new Intent(CalenderActivity.this, TimerActivity.class);
                                    intent.putExtra("mon", check_mon);
                                    intent.putExtra("day", check_day);
                                    startActivity(intent);
                                }
                            });
                        }

                    }
                }
            }
        });

        //////////////////////////////////////////////////////////확인 버튼 누르면/////////////////////////////////////////////////
        check_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CalenderActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        //////////////////////////////////////////////////////////취소 버튼 누르면/////////////////////////////////////////////////
        cancellation_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CalenderActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        reset_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<14;i++){
                    for(int a=0;a<34;a++)
                    {
                        int M = i;
                        int D = a;

                        String fileName = M+"월"+D+"일"+".txt";
                        sop(fileName);
                        File file = new File(getFilesDir(), fileName);
                        boolean deleted = file.delete();
                    }
                }

            }
        });


    }

}

