package com.example.mobileteamapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // home.xml 레이아웃을 연결
        setContentView(R.layout.home);  // home.xml을 레이아웃으로 설정
    }
}