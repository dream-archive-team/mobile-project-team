package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class Final_novelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_novel); // XML 파일 이름에 맞게

        setButtonListener(R.id.btnSharing, Sharing.class);
        setButtonListener(R.id.btnBack, final_rbmood_tvdiary_tvanalys.class);
        setButtonListener(R.id.btnHome, HomeActivity.class);
    }

    // 버튼 클릭 리스너를 설정하는 헬퍼 메서드
    private void setButtonListener(int buttonId, Class<?> destination) {
        findViewById(buttonId).setOnClickListener(view -> {
            Intent intent = new Intent(this, destination);
            startActivity(intent);
        });
    }
}
