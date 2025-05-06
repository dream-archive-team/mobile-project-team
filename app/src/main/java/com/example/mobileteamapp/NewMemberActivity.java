package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class NewMemberActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_member); // new_member.xml로 연결

        Button nextPageBtn = (Button) findViewById(R.id.next_page_1);
        nextPageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(NewMemberActivity.this, NextMemberPro.class);
            startActivity(intent);
        });
    }
}