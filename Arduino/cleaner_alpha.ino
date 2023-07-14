#include <Servo.h> // 서보모터를 사용하기 위한 헤더파일 호출
//자이로 센서 통신
#include<Wire.h>
#include <SoftwareSerial.h>


#define EA 3  // 모터드라이버 EA 핀, 아두이노 디지털 3번 핀에 연결
#define EB 11  // 모터드라이버 EB 핀, 아두이노 디지털 11번 핀에 연결
#define M_IN1 4  // 모터드라이버 IN1 핀, 아두이노 디지털 4번 핀에 연결
#define M_IN2 5  // 모터드라이버 IN2 핀, 아두이노 디지털 5번 핀에 연결
#define M_IN3 13  // 모터드라이버 IN3 핀, 아두이노 디지털 13번 핀에 연결
#define M_IN4 12  // 모터드라이버 IN4 핀, 아두이노 디지털 12번 핀에 연결

#define echo 6  // 초음파 센서 에코(Echo) 핀, 아두이노 우노 보드의 디지털 6번 핀 연결
#define trig 7  // 초음파 센서 트리거(Trigger) 핀, 아두이노 우노 보드의 디지털 7번 핀 연결
#define echo2 8  // 초음파 센서 에코(Echo) 핀, 아두이노 우노 보드의 디지털 8번 핀 연결
#define trig2 9  // 초음파 센서 트리거(Trigger) 핀, 아두이노 우노 보드의 디지털 9번 핀 연결

// 통신 핀 설정
#define RX 10 
#define TX 2

#define speed 80 //속도
#define LEFT_LIMIT 14
#define FRONT_LIMIT 12

#define CLEANING_START "1001"
#define CLEANING_STOP "1002"
#define CLEANING_END "1003"

//#define servo_motor 2  // 서보모터 Signal 핀, 아두이노 우노 보드 디지털 3번 핀에 연결

SoftwareSerial wemos(RX, TX);

int motorA_vector = 1;  // 모터의 회전방향이 반대일 시 0을 1로, 1을 0으로 바꿔주면 모터의 회전방향이 바뀜.
int motorB_vector = 1;  // 모터의 회전방향이 반대일 시 0을 1로, 1을 0으로 바꿔주면 모터의 회전방향이 바뀜.
int motor_speed = 1;  // 모터 스피드 0 ~ 255
int angle = 90;
int state = 10;
int INT_MAX = 8;

//////////////////////////////////////////////////////////////////////////////자이로 센서 변수//////////////////////////////////////////////////////////////////////////////////
// I2C통신을 위한 주소
const int MPU_ADDR = 0x68; // I2C통신을 위한 MPU6050의 주소
int16_t GyZ; // 가속도, 온도, 자이로
double angleGyZ;
double avgGyZ;
const double RADIAN_TO_DEGREE = 180 / 3.14159; // 라디안-호도 변환
const double DEG_PER_SEC = 32767 / 250; // 1초에 회전하는 각도
// GyX, GyY, GyZ 값의 범위 : -32768 ~ +32767 (16비트 정수범위)
unsigned long now = 0; // 현재 시간 저장용 변수
unsigned long past = 0; // 이전 시간 저장용 변수
double dt = 0; // 한 사이클 동안 걸린 시간 변수 
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


String recv_data;
String send_data;

// 플레이스(지역)를 나타내는 구조체
struct Place
{
  int x; // 플레이스의 x 좌표
  int y; // 플레이스의 y 좌표
  bool visited; // 방문 여부
};

// 배열의 크기를 10으로 정의
const int N = 4;

int8_t MAP[30][30];

//현재 바라보고 있는 방향이 x좌표인지 y좌표인지를 확인하는 변수 
//1 = x, 2 = y
int direction = 2;
int x=10;
int y=10;
int turn;
//지도를 전부 그렸는지 안그렸는지 알려주는 변수
//xxx
int end =2;

void setup()
{
  // 초기화 코드 생략
  pinMode(EA, OUTPUT);
  pinMode(EB, OUTPUT);
  pinMode(M_IN1, OUTPUT);
  pinMode(M_IN2, OUTPUT);
  pinMode(M_IN3, OUTPUT);
  pinMode(M_IN4, OUTPUT);
  pinMode(trig, OUTPUT);
  pinMode(echo, INPUT);
  pinMode(trig2, OUTPUT);
  pinMode(echo2, INPUT);



  initIMU(); // MPU-6050 센서 초기화
  caliSensor(); // 초기 센서 캘리브레이션 함수 호출
  past = millis(); // past에 현재 시간 저장 

  Serial.begin(115200);
  wemos.begin(115200);
  delay(3000);

  // start_move();
  // MAP[10][10] = 2;



}

void Straight(int a)
{
  digitalWrite(EA, HIGH);
  digitalWrite(EB, HIGH);


  digitalWrite(M_IN1, !motorA_vector);
  digitalWrite(M_IN2, motorA_vector);
  digitalWrite(M_IN3, !motorB_vector);
  digitalWrite(M_IN4, motorB_vector);
  delay(a);
  stop();
}
void turn_left_all()
{
  digitalWrite(EA, HIGH);
  digitalWrite(EB, HIGH);


  digitalWrite(M_IN1, !motorA_vector);
  digitalWrite(M_IN2, motorA_vector);
  digitalWrite(M_IN3, motorB_vector);
  digitalWrite(M_IN4, !motorB_vector);
/*
  angleGyZ = 0;
  while(true){
    getDT();
    getAngleGyZ();
    if(angleGyZ >= 90.0)
      break;
  }
  stop();
  */
}

void turn_left()
{
  digitalWrite(EA, HIGH);



  digitalWrite(M_IN1, !motorA_vector);
  digitalWrite(M_IN2, motorA_vector);
}

void turn_left_gyro()
{
  digitalWrite(EA, HIGH);
  digitalWrite(EB, HIGH);


  digitalWrite(M_IN1, !motorA_vector);
  digitalWrite(M_IN2, motorA_vector);
  digitalWrite(M_IN3, motorB_vector);
  digitalWrite(M_IN4, !motorB_vector);

  angleGyZ = 0;
 
  while(true){
     getDT();
    getAngleGyZ();
    if(angleGyZ >= 90.0)
      break;
  }
  stop();
  
}

void turn_right()
{
  digitalWrite(EA, HIGH);
  digitalWrite(EB, HIGH);
  digitalWrite(M_IN1, motorA_vector);
  digitalWrite(M_IN2, !motorA_vector);
  digitalWrite(M_IN3, !motorB_vector);
  digitalWrite(M_IN4, motorB_vector);
  /*
  // past를 현재시점으로 저장
  getDT();
  while(true){
    getAngleGyZ();
    if(angleGyZ <= -90.0){
      break;
    }
  }
  stop();
  */
}

void turn_right_gyro()
{
  digitalWrite(EA, HIGH);
  digitalWrite(EB, HIGH);
  digitalWrite(M_IN1, motorA_vector);
  digitalWrite(M_IN2, !motorA_vector);
  digitalWrite(M_IN3, !motorB_vector);
  digitalWrite(M_IN4, motorB_vector);
  
  // past를 현재시점으로 저장
  while(true){
    getDT();
    getAngleGyZ();
    if(angleGyZ <= -90.0){
      break;
    }
  }
  stop();
  
}

void stop()
{
  digitalWrite(EA, LOW);
  digitalWrite(EB, LOW);
  digitalWrite(M_IN1, LOW);
  digitalWrite(M_IN2, LOW);
  digitalWrite(M_IN3, LOW);
  digitalWrite(M_IN4, LOW);
}


//보는 방향을 바꿔줌
void change_xy()
{
  if(direction == 1)
  {
    direction=2;
  }
  else
  {
    direction=1;
  }
}

void map_paint()
{
  int count;
  int first_wall =-1;
  int second_wall = -1;
  for(int i=0;i<30;i++)
  {
    count=0;
    first_wall=-1;
    for(int a=0;a<30;a++)
    {
      if(MAP[i][a] == 4 && MAP[i][a+1] != 4)
      {
        count ++;
        if(first_wall==-1)
        {
          first_wall=a;

        }
        else
        {
          second_wall=a;
        }
      }
    }
    if(count==2)
    {
      Serial.print("first_wll ");
      Serial.println(first_wall);
      Serial.print("second_wall ");
      Serial.println(second_wall);
      for(int b = first_wall+1;b<second_wall;b++)
      {
        if(MAP[i][b] ==0)
        {
          MAP[i][b] = 3;
        }
      }
    }
    else
    {
      first_wall=-1;
      count=0;
    }
  }
}

void clean()
{
  int locatoin_x=10;
  int locatoin_y=10;
  int count=-1;
  int end=0;
  int cub=2;
  //방향을 알기위한 변수
  int draction;

  while(end ==0)
  {
    // int x=0;
    // int y=0;
    // //주변에서 가중치자 제일 큰 것을 찾음
    // if(MAP[locatoin_y][locatoin_x-1] >= cub && MAP[locatoin_y][locatoin_x-1] != 0)
    // {
    //   x=locatoin_x-1;
    //   y=locatoin_y;
    //   cub = MAP[locatoin_y][locatoin_x-1];
    //   draction = 1;

    // }
    // if(MAP[locatoin_y][locatoin_x+1] >= cub && MAP[locatoin_y][locatoin_x+1] != 0)
    // {
    //   cub = MAP[locatoin_y][locatoin_x+1];
    //   x=locatoin_x+1;
    //   y=locatoin_y;
    //   draction = 2;

    // }
    // if(MAP[locatoin_y-1][locatoin_x] >= cub && MAP[locatoin_y-1][locatoin_x] != 0)
    // {
    //   cub = MAP[locatoin_y-1][locatoin_x];
    //   x=locatoin_x;
    //   y=locatoin_y-1;
    //   draction = 3;

    // }
    // if(MAP[locatoin_y+1][locatoin_x] >= cub && MAP[locatoin_y+1][locatoin_x] != 0)
    // {
    //   cub = MAP[locatoin_y+1][locatoin_x];
    //   x=locatoin_x;
    //   y=locatoin_y+1;
    //   draction = 4;

    // }

    // //주변에 자신보다 가중치가 크거가 같은게 없으면 가중치가 가장 낮을 것을 찾음
    // if(MAP[locatoin_y+1][locatoin_x] >= cub || MAP[locatoin_y-1][locatoin_x] >= cub || MAP[locatoin_y][locatoin_x+1] >= cub || MAP[locatoin_y][locatoin_x-1] >= cub){}
    // else{
    //   if(MAP[locatoin_y][locatoin_x-1] <= cub && MAP[locatoin_y][locatoin_x-1] != 0)
    //   {
    //     x=locatoin_x-1;
    //     y=locatoin_y;
    //     cub = MAP[locatoin_y][locatoin_x-1];
    //     draction =1;

    //   }
    //   if(MAP[locatoin_y][locatoin_x+1] <= cub && MAP[locatoin_y][locatoin_x-1] != 0)
    //   {
    //     cub = MAP[locatoin_y][locatoin_x+1];
    //     x=locatoin_x+1;
    //     y=locatoin_y;
    //     draction=2;

    //   }
    //   if(MAP[locatoin_y-1][locatoin_x] <= cub && MAP[locatoin_y][locatoin_x-1] != 0)
    //   {
    //     cub = MAP[locatoin_y-1][locatoin_x];
    //     x=locatoin_x;
    //     y=locatoin_y-1;
    //     draction=3;

    //   }
    //   if(MAP[locatoin_y+1][locatoin_x] <= cub && MAP[locatoin_y][locatoin_x-1] != 0)
    //   {
    //     cub = MAP[locatoin_y+1][locatoin_x];
    //     x=locatoin_x;
    //     y=locatoin_y+1;
    //     draction=4;

    //   }
    // }
    // //자동차를 움직임

    // if(draction == 1)
    // {
    //   turn_left_gyro();
    //   int distance =read_ultrasonic();
    //   if(distance < 10)
    //   {
    //     MAP[locatoin_y][locatoin_x-1] = 0;
    //   }
    //   else
    //   {
    //     Straight(500);
    //   }
    //   turn_right_gyro();
    // }
    // else if(draction == 2)
    // {
    //   turn_right_gyro();
    //   int distance =read_ultrasonic();
    //   if(distance < 10)
    //   {
    //     MAP[locatoin_y][locatoin_x+1] = 0;
    //   }
    //   else{
    //     Straight(500);
    //   }
    //   turn_left_gyro();
    // }
    // else if(draction == 3)
    // {
    //   turn_right_gyro();
    //   turn_right_gyro();
    //   //xxx
    //   int distance =read_ultrasonic();
    //   if(distance < 10)
    //   {
    //     MAP[locatoin_y-1][locatoin_x] = 0;
    //   }
    //   else
    //   {
    //     Straight(500);
    //   }
    //   turn_right_gyro();
    //   turn_right_gyro();
    // }
    // else if(draction == 4)
    // { 
    //   //xxx
    //   int distance =read_ultrasonic();
    //   if(distance < 10)
    //   {
    //     MAP[locatoin_y-1][locatoin_x] = 0;
    //   }
    //   else
    //   {
    //     Straight(500);
    //   }
    // }
    
    
    


    // MAP[locatoin_y][locatoin_x] = count;
    // cub=count;
    // count--;
    // locatoin_y=y;
    // locatoin_x=x;

    // /*
    // Serial.print("locatoin_y = ");
    // Serial.println(locatoin_y);
    // Serial.print("locatoin_x = ");
    // Serial.println(locatoin_x);
    // */

    // for(int i=0;i<30;i++)
    // {
    //   for(int a=0;a<30;a++)
    //   {
    //     if(MAP[i][a] > 0)
    //     {
    //       Serial.print(" ");
    //       Serial.print(MAP[i][a]);
    //     }
    //     else
    //     {
    //       Serial.print(MAP[i][a]);
    //     }
        
    //   }
    //   Serial.println(" ");
    // }

    // end =0;
    // for(int i=0;i<30;i++)
    // {
    //   for(int a=0;a<30;a++)
    //   {
    //     if(MAP[i][a] >0)
    //     {
    //       end=1;
    //     }
    //   }
    // }

    if(wemos.available()){
      recv_data = wemos.readStringUntil('\n');
      recv_data.trim();
      if(recv_data.equals(CLEANING_STOP)){
        Serial.println("/////작동 중지/////");
        while(true){
            if(wemos.available()){
              recv_data = wemos.readStringUntil('\n');
              recv_data.trim();
              if(recv_data.equals(CLEANING_START)){
                Serial.println("/////재작동/////");
                break;
              }
            }
        }
      }
      delay(10000);
      break;
    }

  }
  // String tmp = CLEANING_END;
  // send_data = tmp.toInt();
  // Serial.println(send_data);
  send_data = CLEANING_END;
  wemos.println(send_data);
}





void map_check()
{
  if(turn%4 ==0)
  {
    y++;
  }
  else if(turn%4 ==1)
  {
    x++;
  }
  else if(turn%4 ==2)
  {
    y--;
  }
  else if(turn%4 ==3)
  {
    x--;
  }
  if(MAP[x][y]==0)
  {
    MAP[x][y]=4;
  }
  else if(MAP[x][y]== 2)
  {
    end=1;
    map_paint();
  }
}

//처음 시작할 때 움직임
void start_move()
{
  //앞에있는 벽과의 거리를 구함
  int distance = 21;
  while(distance>FRONT_LIMIT)
  {
    distance = read_ultrasonic();
    Straight(50);
  }
  MAP[x][y] = 2;
  distance = read_ultrasonic_2();
  while(distance>13)
  {
    turn_right();
    distance = read_ultrasonic_2();
  }
  
}




void control_car(int front, int left)
{
  if(front<FRONT_LIMIT-6)
  {
    change_xy();
    turn+=1;
    turn_right();
    delay(750);
  }
  else
  {
    if(left< LEFT_LIMIT+25)
    {
      map_check();
      //앞 X 옆 O
      int distance=read_ultrasonic_2();
      if(distance >8)
      {
        turn_left_all();
        delay(80);
        Straight(120);
      }
      else
      {

        turn_right();
        delay(80);
        Straight(120);
      }
    }
    else
    {
      change_xy();

      //앞 X 옆 X
      turn_left();
      delay(450);
    }
  }

}

void loop()
{
  // //앞에 있는 초음파 센서의 거리 구함
  // int distance =read_ultrasonic();
  // //왼쪽에 있는 초음파 센서의 거리 구함
  // int distance_2 =read_ultrasonic_2();
  // //xxx
  // if(end == 0)
  // {
  //   control_car(distance, distance_2);
  // }
  // //xxx
  // if(end == 0)
  // {
  //   for(int i=0; i<30;i++)
  //   {
  //     for(int a=0;a<30;a++)
  //     {
  //       Serial.print(MAP[i][a]);
        
  //     }
  //     Serial.println("");
  //   }

  //   Serial.println("----------------------------");

  //   delay(1000);
  //   Serial.println("가리세요");

  //   delay(1000);
  // }
  // if(end == 1)
  // {
  //   map_paint();
  //   for(int i=0; i<30;i++)
  //   {
  //     for(int a=0;a<30;a++)
  //     {
  //       Serial.print(MAP[i][a]);
        
  //     }
  //     Serial.println("");
  //   }
  //   delay(1000);
    
  //   for(int i=0; i<30;i++)
  //   {
  //     for(int a=0;a<30;a++)
  //     {
  //       Serial.print(MAP[i][a]);
        
  //     }
  //     Serial.println("");
  //   }
  //   delay(1000);
  //   end++;
  // }

  if(end == 2){
    if(wemos.available()){
      Serial.println("avail문 진입");
      recv_data = wemos.readStringUntil('\n');
      Serial.println("//////////////////////////////////////////////////////////////////////");
      Serial.println(recv_data);
      Serial.println("//////////////////////////////////////////////////////////////////////");
      recv_data.trim();
      if(recv_data.equals(CLEANING_START)){
        Serial.println("///작동시작///");
        clean();
      }

    }
  }

/*
  // 경로 계산
  calculateShortestPath();
  for(int i=0;i<1000;i++)
  {
    for(int a=0;a<1000;a++)
    {
      MAP[i][a] = false;
    }
  }


  // 경로 따라 이동 
  //N=4
  for (int i = 1; i < N; i++)
  {
    int targetX = path[i].x;
    int targetY = path[i].y;

   if(path[i].visited == false)	//false 면 방문을 안한거
    {
    moveTo(targetX, targetY); 	
    // 방문한 플레이스 표시
    path[i].visited = true;
    }
  }

  // 출발점으로 돌아옴
  moveTo(path[0].x, path[0].y);

  // 움직임을 멈춤
  digitalWrite(EA, LOW);
  digitalWrite(EB, LOW);
  digitalWrite(M_IN1, LOW);
  digitalWrite(M_IN2, LOW);
  digitalWrite(M_IN3, LOW);
  digitalWrite(M_IN4, LOW);

  while (true)
  {
    // 아무 작업도 수행하지 않고 대기
  }
  */
}
/*
void moveTo(int targetX, int targetY)
{
  int currentX = path[0].x;
  int currentY = path[0].y;

  while (currentX != targetX || currentY != targetY)
  {
    // 목표 위치와 현재 위치의 차이를 계산하여 이동 방향을 결정
    int deltaX = targetX - currentX;
    int deltaY = targetY - currentY;




    // X 축 이동
    if (deltaX != 0)
    {
      if (deltaX > 0)
      {
        // 목표 위치가 현재 위치보다 오른쪽에 있음
        // 오른쪽으로 이동
        digitalWrite(EB, HIGH);
        digitalWrite(M_IN3, !motorB_vector);
        digitalWrite(M_IN4, motorB_vector);
        delay(1000);
        digitalWrite(EA, LOW);
        digitalWrite(EB, LOW);
        digitalWrite(M_IN1, LOW);
        digitalWrite(M_IN2, LOW);
        digitalWrite(M_IN3, LOW);
        digitalWrite(M_IN4, LOW);
        currentX++;
      }
      else
      {
        // 목표 위치가 현재 위치보다 왼쪽에 있음
        // 왼쪽으로 이동
        digitalWrite(EA, HIGH);
        digitalWrite(M_IN1, !motorA_vector);
        digitalWrite(M_IN2, motorA_vector);
        delay(1000);
        digitalWrite(EA, LOW);
        digitalWrite(EB, LOW);
        digitalWrite(M_IN1, LOW);
        digitalWrite(M_IN2, LOW);
        digitalWrite(M_IN3, LOW);
        digitalWrite(M_IN4, LOW);
        currentX--;
      }
    }

    // Y 축 이동
    if (deltaY != 0)
    {
      if (deltaY > 0)
      {
        // 목표 위치가 현재 위치보다 아래에 있음
        // 아래로 이동
        digitalWrite(EA, HIGH);
        digitalWrite(EB, HIGH);
        digitalWrite(M_IN1, !motorA_vector);
        digitalWrite(M_IN2, motorA_vector);
        digitalWrite(M_IN3, !motorB_vector);
        digitalWrite(M_IN4, motorB_vector);
        delay(1000);
        digitalWrite(EA, LOW);
        digitalWrite(EB, LOW);
        digitalWrite(M_IN1, LOW);
        digitalWrite(M_IN2, LOW);
        digitalWrite(M_IN3, LOW);
        digitalWrite(M_IN4, LOW);
        currentY++;
      }
      else
      {
        // 목표 위치가 현재 위치보다 위에 있음
        // 위로 이동
        digitalWrite(EA, HIGH);
        digitalWrite(EB, HIGH);
        digitalWrite(M_IN1, motorA_vector);
        digitalWrite(M_IN2, !motorA_vector);
        digitalWrite(M_IN3, motorB_vector);
        digitalWrite(M_IN4, !motorB_vector);
        delay(1000);
        digitalWrite(EA, LOW);
        digitalWrite(EB, LOW);
        digitalWrite(M_IN1, LOW);
        digitalWrite(M_IN2, LOW);
        digitalWrite(M_IN3, LOW);
        digitalWrite(M_IN4, LOW);
        currentY--;
      }
    }

    int distance = getDistance();
    
    // 초음파 센서로 장애물을 감지하여 회피
    // if (obstacleDetected())
    // {
      
    // }
  }
}
*/

int read_ultrasonic(void)  // 초음파 센서 값 읽어오는 함수
{
    float return_time, howlong;  // 시간 값과 거리 값을 저장하는 변수를 만든다.
    // 초음파 센서는 초음파를 발사 후 돌아오는 시간을 역산하여 거리 값으로 계산한다.
    digitalWrite(trig, HIGH);  // 초음파 발사
    delay(5);  // 0.05초간 지연
    digitalWrite(trig, LOW);  // 초음파 발사 정지
    return_time = pulseIn(echo, HIGH);  // 돌아오는 시간
    howlong = ((float)(340 * return_time) / 10000) / 2;  // 시간을 거리로 계산
    return howlong;  // 거리 값 리턴
}

int read_ultrasonic_2(void)  // 초음파 센서 값 읽어오는 함수
{
    float return_time, howlong;  // 시간 값과 거리 값을 저장하는 변수를 만든다.
    // 초음파 센서는 초음파를 발사 후 돌아오는 시간을 역산하여 거리 값으로 계산한다.
    digitalWrite(trig2, HIGH);  // 초음파 발사
    delay(5);  // 0.05초간 지연
    digitalWrite(trig2, LOW);  // 초음파 발사 정지
    return_time = pulseIn(echo2, HIGH);  // 돌아오는 시간
    howlong = ((float)(340 * return_time) / 10000) / 2;  // 시간을 거리로 계산
    return howlong;  // 거리 값 리턴
}

//////////////////////////////////////////////////////////////////////////////////////////자이로 센서 함수////////////////////////////////////////////////////////////////////////////////
void initIMU() {
  Wire.begin();
  Wire.beginTransmission(MPU_ADDR); // I2C 통신용 어드레스(주소)
  Wire.write(0x6B); // MPU6050과 통신을 시작하기 위해서는 0x6B번지에 
  Wire.write(0); // MPU6050을 동작 대기 모드로 변경
  Wire.endTransmission(true);
}

// 자이로센서에서 값 받아오기
void getRawData() {
  Wire.beginTransmission(MPU_ADDR);
  Wire.write(0x3B); // AcX 레지스터 위치(주소)를 설정
  Wire.endTransmission(false);
  Wire.requestFrom(MPU_ADDR, 14, true); // AcX 주소 이후의 14byte의 데이터를 요청
  // 2개로 나누어진 바이트를 하나로 이어 붙여서 각 변수에 저장

  //GyZ 값만 얻으려고 for문을 돌림
  for(int i = 0; i < 12; i++){
    Wire.read();
  }
  GyZ = Wire.read() << 8 | Wire.read(); // Z축 자이로: Yaw   104 96
}

// loop 한 사이클동안 걸리는 시간을 알기 위한 함수
void getDT() {
  now = millis(); 
  dt = (now - past) / 1000.0; 
  past = now;
}

// 센서의 초기값을 16회 정도 평균값으로 구하여 저장하는 함수
void caliSensor() {
  double sumGyZ = 0;
  getRawData();
  for (int i=0; i<10; i++) {
    getRawData();
    sumGyZ+=GyZ;
    delay(50);
  }
  avgGyZ=sumGyZ/10;
}

void getAngleGyZ(){
  getRawData(); 
  getDT();
  angleGyZ += ((GyZ - avgGyZ) / DEG_PER_SEC) * dt;
}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
void box()
{
  void motorA_con(int M1)
  {
    if (M1 > 0)
    {
      //전진
      digitalWrite(EA, HIGH);
      digitalWrite(M_IN1, !motorA_vector);
      digitalWrite(M_IN2, motorA_vector);
    }
    else if (M1 < 0)
    {
      //후진
      digitalWrite(EA, HIGH);
      digitalWrite(M_IN1, motorA_vector);
      digitalWrite(M_IN2, !motorA_vector);
    }
    else
    {
      digitalWrite(EA, LOW);
      digitalWrite(M_IN1, LOW);
      digitalWrite(M_IN2, LOW);
    }
    analogWrite(EA, abs(M1));
  }

  void motorB_con(int M2)
  {
    //정방향
    if (M2 > 0)
    {
      digitalWrite(EB, HIGH);
      digitalWrite(M_IN3, !motorB_vector);
      digitalWrite(M_IN4, motorB_vector);
    }
    else if (M2 < 0)
    {
      //역방향
      digitalWrite(EB, HIGH);
      digitalWrite(M_IN3, motorB_vector);
      digitalWrite(M_IN4, !motorB_vector);
    }
    else
    {
      //정지
      digitalWrite(EB, LOW);
      digitalWrite(M_IN3, LOW);
      digitalWrite(M_IN4, LOW);
    }
    analogWrite(EB, abs(M2));
  }

  void movePattern()
  {
    moveBackward();
    delay(500);
    
    if (angle >= 90) {
      turnRight();
      delay(500);
    } else {
      turnLeft();
      delay(500);
    }

    angle = 90;
    LKservo.write(angle);
    delay(100);
  }

  void moveForward() {
    digitalWrite(EA, HIGH);
    digitalWrite(EB, HIGH);
    digitalWrite(M_IN1, !motorA_vector);
    digitalWrite(M_IN2, motorA_vector);
    digitalWrite(M_IN3, !motorB_vector);
    digitalWrite(M_IN4, motorB_vector);
  }

  void moveBackward() {
    digitalWrite(EA, HIGH);
    digitalWrite(EB, HIGH);
    digitalWrite(M_IN1, motorA_vector);
    digitalWrite(M_IN2, !motorA_vector);
    digitalWrite(M_IN3, motorB_vector);
    digitalWrite(M_IN4, !motorB_vector);
  }

  void turnRight() {
    digitalWrite(EB, HIGH);
    digitalWrite(M_IN3, !motorB_vector);
    digitalWrite(M_IN4, motorB_vector);
  }

  void turnLeft() {
    digitalWrite(EA, HIGH);
    digitalWrite(M_IN1, !motorA_vector);
    digitalWrite(M_IN2, motorA_vector);
  }

  int getDistance() {
    digitalWrite(trig, LOW);
    delayMicroseconds(2);
    digitalWrite(trig, HIGH);
    delayMicroseconds(10);
    digitalWrite(trig, LOW);

    long duration = pulseIn(echo, HIGH);
    int distance = duration * 0.034 / 2;

    return distance;
  }

  bool obstacleDetected()
  {
    LKservo.write(angle);
    delay(50);

    if (read_ultrasonic() < 18)
    {
      movePattern();

      digitalWrite(EA, HIGH);
      digitalWrite(EB, HIGH);
      digitalWrite(M_IN1, !motorA_vector);
      digitalWrite(M_IN2, motorA_vector);
      digitalWrite(M_IN3, !motorB_vector);
      digitalWrite(M_IN4, motorB_vector);
      return true;  // 장애물 감지 시 true 반환
    }

    //회전각도
    if (angle == 140)
      state = -10;
    else if (angle == 40)
      state = 10;
    angle += state;
  }
}
*/
