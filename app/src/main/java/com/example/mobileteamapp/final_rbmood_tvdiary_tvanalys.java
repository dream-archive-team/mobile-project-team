package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class final_rbmood_tvdiary_tvanalys extends AppCompatActivity {

    private Button btnDelete;
    private Button btnGoToNovel;

    private Button btnSharing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_rbmood_tvdiary_tvanalys); // 해당 레이아웃 XML 이름으로 수정

        // 삭제 버튼 클릭 시
        btnDelete.setOnClickListener(view -> {

        });

        // 소설 이동 버튼 클릭 시
        btnGoToNovel = findViewById(R.id.btnGoToNovel);

        btnGoToNovel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(final_rbmood_tvdiary_tvanalys.this, Final_novelActivity.class);
                startActivity(intent);
            }
        });
    }
}

