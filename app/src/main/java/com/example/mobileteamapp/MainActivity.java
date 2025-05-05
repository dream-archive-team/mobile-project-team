package com.example.mobileteamapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GeminiApiService service = new GeminiApiService();
        service.requestGemini("나는 하늘을 나는 꿈을 꿨어."); // API 테스트
    }
}