package com.example.mobileteamapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 회원가입 버튼 클릭 시 화면 전환
        Button signupBtn = (Button)findViewById(R.id.new_member_log);
        signupBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NewMemberActivity.class);
            startActivity(intent);
        });
    }
}









