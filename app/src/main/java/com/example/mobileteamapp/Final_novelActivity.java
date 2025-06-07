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
        setContentView(R.layout.final_novel); // 레이아웃 설정

        setButtonListener(R.id.btnSharing, Sharing.class);
        setButtonListener(R.id.btnBack, final_rbmood_tvdiary_tvanalys.class);
        setButtonListener(R.id.btnHome, HomeActivity.class);
    }

    // 버튼 클릭 리스너를 설정하는 헬퍼 메서드
    private void setButtonListener(int buttonId, Class<?> destination) {
        findViewById(buttonId).setOnClickListener(view -> {
            Intent intent = new Intent(Final_novelActivity.this, destination);
            startActivity(intent);
        });
    }


}