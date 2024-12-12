
#include <SoftwareSerial.h>

#define RX 10
#define TX 2

SoftwareSerial wemos(RX, TX);

void setup() {
  Serial.begin(115200);
  wemos.begin(115200);
}

void loop() {
  //Serial에 들어온 값이 있다면
  
  if (wemos.available()) {
    //'\n'를 만나면 뒤에있는건 다 지워버림
    String data = wemos.readStringUntil('\n'); // 시리얼 입력 데이터 읽기
    Serial.println(data);
    String tmp = "1001" ;
    data.trim();
    if(data == "1001"){
      Serial.println("true");
    }
    wemos.print(data); // 데이터를 ESP8266으로 전송
  }

}
