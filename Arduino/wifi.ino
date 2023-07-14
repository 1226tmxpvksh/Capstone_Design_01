#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <WiFiServer.h>
#include <SoftwareSerial.h>

// 와이파이 네트워크 이름
const char* ssid = "iptime1";
// 와이파이 비번
const char* password = "12345678";

#define SERVER_PORT 9999 /* 포트번호*/
#define RX D4
#define TX D3
#define BYTES_SIZE 5

#define CLEANING_START "1001"
#define CLEANING_STOP "1002"
#define CLEANING_END "1003"

WiFiServer server(SERVER_PORT);
SoftwareSerial uno(RX, TX);

String send_to_uno;
String send_to_client;
String recv_from_uno;

byte bytes[BYTES_SIZE];


void setup(void)
{
  Serial.begin(115200);
  uno.begin(115200);

  Serial.print("start");
  Serial.println();

  WiFi.mode(WIFI_STA);
  // ssid와 password에 해당하는 wifi 연결 시도
  WiFi.begin(ssid, password);

  // wifi가 연결될 때 까지 무한반복으로 대기
  while (WiFi.status() != WL_CONNECTED) {
    delay(100);
  }

  Serial.print("Wi-Fi connected");
  Serial.println();

  server.begin();
  Serial.print("Server started");
  Serial.println();

  Serial.print("Local IP: ");
  Serial.print(WiFi.localIP());
  Serial.println();

  

}


void loop(void)
{
  // 클라이언트로 부터 연결 요청이 있다면 객체를 생성하여 client에 할당
  WiFiClient client = server.available();


  // 유효한 연결이 있다면 값을 반환함. 그렇지 않다면 client는 false나 null 값을 가져 if문이 실행안됨.
  if (client) {
    Serial.println("Client connected");
    // 연결 여부를 반복을 걸어 주기적으로 확인
    while (client.connected()) {
      //Serial.println("===========================");

      // 데이터의 유무를 확인 데이터가 들어 왔다면 수신 버퍼에 있는 데이터의 바이트 수를 반환해줌. 없다면 0
      if (client.available()) {
        Serial.println("Recieve from Client");
        // 엔터를 기준으로 한줄을 읽어옴
        String recv_data = client.readStringUntil('\n');
        // Serial.print("Received data: ");
        // Serial.println(recv_data);
        send_to_uno = recv_data;

        // 받은 데이터를 아두이노에 전송
        uno.println(send_to_uno);


        // // 클라이언트에 응답 전송
        // String response = "Server response";
        // client.print(response);
      }

      if (uno.available()) {
        recv_from_uno = uno.readStringUntil('\n'); // Uno로부터 데이터 수신
        Serial.print("recv_from_uno 데이터 출력 : ");
        Serial.println(recv_from_uno);

        client.print(recv_from_uno); // uno한테 받은 데이터를 핸드폰에 전송
      }

    }

    client.stop();
    Serial.println("Client disconnected");
  }
  
}