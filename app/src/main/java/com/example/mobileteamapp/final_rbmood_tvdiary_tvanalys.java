package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class final_rbmood_tvdiary_tvanalys extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_rbmood_tvdiary_tvanalys); // XML 파일 이름에 맞게

        // 소설 이동 버튼 클릭 시
        Button btnGoToNovel = findViewById(R.id.btnGoToNovel);
        btnGoToNovel.setOnClickListener(view -> {
            Intent intent = new Intent(this, Final_novelActivity.class);
            startActivity(intent);
        });

        // 삭제 버튼 클릭 시 (필요할 경우 로직 추가)
        Button btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(view -> {
            // TODO: 삭제 기능 구현
        });
    }
}
