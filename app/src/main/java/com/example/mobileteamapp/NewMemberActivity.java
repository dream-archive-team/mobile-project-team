package com.example.mobileteamapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class NewMemberActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_member); // new_member.xml로 연결
    }
}