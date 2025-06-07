package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Final_novelActivity extends AppCompatActivity {

    private Button btnBack;
    private Button btnHome;

    private Button btnSharing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_novel); // 해당 레이아웃 XML 이름으로 수정

        btnSharing = findViewById(R.id.btnSharing);

        btnSharing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Final_novelActivity.this, Sharing.class);
                startActivity(intent);
            }
        });

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Final_novelActivity.this, final_rbmood_tvdiary_tvanalys.class);
                startActivity(intent);
            }
        });

        btnHome = findViewById(R.id.btnHome);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Final_novelActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

}