package com.example.capstonedesign;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.widget.Toast;

// 앱이 실행 중일 때 앱의 네트워크 상태가 변화되면 실행됨.
public class NetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {       // 인터넷 연결 상태 변경 action이 action과 같으면
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            // 안드로이드 시스템 서비스인 인터넷 연결 관리자를 나타냄.
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();       // 활성화된 네트워크 정보 가져오기

            // 네트워크 상태가 null이 아니고, , 네트워크에 연결되어있을때 네트워크 상태를 CONNECT로 전달
            if (networkInfo != null && networkInfo.isConnected()) {
               TcpService.condition = TcpService.CONNECT;
               Toast.makeText(context, "인터넷에 연결되었습니다.", Toast.LENGTH_SHORT).show();
            }
            else{
                TcpService.condition = TcpService.DISCONNECT;
                Toast.makeText(context, "인터넷 연결이 끊겼습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

