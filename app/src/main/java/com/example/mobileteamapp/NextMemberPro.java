package com.example.mobileteamapp;

//회원가입 후 프로필 설정에 관한 것
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class NextMemberPro extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.next_new_member_pro); // XML 파일 이름에 맞게 수정

        // MainActivity에서 전달한 로그인 성공 여부 확인
        boolean loginSuccess = getIntent().getBooleanExtra("login_success", false);

        if (loginSuccess) {
            Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show();
        }
    }
}

