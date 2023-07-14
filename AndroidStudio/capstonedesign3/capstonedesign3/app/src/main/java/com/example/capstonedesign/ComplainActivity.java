package com.example.capstonedesign;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ComplainActivity extends AppCompatActivity {
    Button complain_apply,complain_cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.complain);

        complain_apply = (Button) findViewById(R.id.Complain_Apply);
        complain_cancel = (Button) findViewById(R.id.Complain_Cancel);

        complain_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComplainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        complain_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ComplainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

    }
}
