package com.example.capstonedesign;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ThemedSpinnerAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;


public class TcpService extends Service {
    ////////////////////////////////////////////////////////////////////////////////////////////////소켓 통신용 변수////////////////////////////////////////////////////////////////////////////////////////////
    Socket socket;

    private static final String SERVER_IP = "59.26.153.27";         // 서버 IP번호
    private static final int SERVER_PORT = 9999;                    // 서버 포트번호

    private static byte[] buffer = new byte[1024];

    static final String Cleaning_Start = "1001";   // 청소 시작시 서버로 보낼 청소 시작 신호 1001.
    static final String Cleaning_Stop = "1002";   // 청소 중지시 서버로 보낼 청소 중지 신호 1002.
    static final String Cleaning_End = "1003";   // 청소 완료시 서버에서 어플이 받을 청소 완료 신호 1003.
    static final boolean CONNECT = true;
    static final boolean DISCONNECT = false;
    static boolean condition = true;
    static boolean serverConneted = false;
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private final IBinder binder = new LocalBinder();


    public class LocalBinder extends Binder {
        TcpService getService() {
            return TcpService.this;
        }
    }

    public TcpService() {
    }


    // 서비스가 생성될 때 실행되는 초기화 코드
    @Override
    public void onCreate() {
        android.util.Log.d("통신 서비스 확인용", "onCreate()진입");
        super.onCreate();
    }

    // 서비스가 시작되었을 때 실행되는 코드
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        android.util.Log.d("통신 서비스 확인용", "onStartCommand()진입");

        if(intent.getStringExtra("msg") != null){
            String tmp = intent.getStringExtra("msg");
            android.util.Log.d("통신 서비스 확인용", "tmp 길이 : " + tmp.length());
            android.util.Log.d("통신 서비스 확인용", "tmp 값 : " + tmp);

            if(tmp.equals(TcpService.Cleaning_Start)){
                android.util.Log.d("통신 서비스 확인용", "if문 진입");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sendData(TcpService.Cleaning_Start);
                    }
                });
                thread.start();
            }
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true){
                        socket = new Socket(SERVER_IP, SERVER_PORT);

                        if(condition == DISCONNECT){
                            continue;
                        }
                        android.util.Log.d("통신 서비스 확인용", "통신 연결 수립");
                        serverConneted = true;

                        while(true) {
                            // 데이터를 수신한 경우
                            InputStream inputStream = socket.getInputStream();
                            int bytesRead = inputStream.read(buffer);
                            android.util.Log.d("통신 서비스 확인용", "데이터 수신");

                            if (bytesRead != -1) {
                                // 서버로부터 수신한 데이터 처리
                                String receivedData = new String(buffer, 0, bytesRead);
                                // 수신한 데이터 전달.
                                android.util.Log.d("통신 서비스 확인용", receivedData);
                                android.util.Log.d("통신 서비스 확인용", receivedData.length()+"");
                                receivedData = receivedData.substring(0,4);
                                if(receivedData.equals(Cleaning_End)){
                                    android.util.Log.d("통신 서비스 확인용", "크로노 미터 멈추기");
                                    LayoutInflater cleaningInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View view = cleaningInflater.inflate(R.layout.cleaning, null);

                                    Chronometer cleaning_chronotime = view.findViewById(R.id.Cleaning_ChronoTime);
                                    cleaning_chronotime.stop();
                                }
                            }

                            // 네트워크 연결이 끊기면 브레이크
                            if(condition == DISCONNECT){
                                android.util.Log.d("통신 서비스 확인용", "condition");
                                break;
                            }

                            // 서버와의 연결이 끊기면 브레이크
                            if(socket.isConnected() == DISCONNECT){
                                android.util.Log.d("통신 서비스 확인용", "socket");
                                break;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
        // 서비스가 강제로 취소되도 재시작하게 함.
        return super.onStartCommand(intent, flags, startId);
    }

    ////////////////////////////////////////////////////////////////////////////////다른 액티비티 등에서 서비스에 값을 주기 위해 사용하는 메소드//////////////////////////////////////////////////////////////////////////////


    public boolean sendData(String msg){
        android.util.Log.d("통신 서비스 확인용", "송신 시작(new)");

        if(!serverConneted){
            android.util.Log.d("통신 서비스 확인용", "senddata if문");
            return false;
        }
        try {
            if (msg.equals(Cleaning_Start)) {
                android.util.Log.d("통신 서비스 확인용", "Cleaning_Start 송신");
                OutputStream output = null;
                output = socket.getOutputStream();
                output.write(Cleaning_Start.getBytes());         // 서버로 연결 완료 메세지 전송
                output.flush();                                  // output 버퍼를 비우는 작업.
            }

            else if (msg.equals(Cleaning_Stop)) {
                android.util.Log.d("통신 서비스 확인용", "Cleaning_Stop 송신");
                OutputStream output = socket.getOutputStream();
                output.write(Cleaning_Stop.getBytes());         // 서버로 연결 완료 메세지 전송
                output.flush();
            }
            android.util.Log.d("통신 서비스 확인용", "송신 완료(new)");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return binder;
    }
}