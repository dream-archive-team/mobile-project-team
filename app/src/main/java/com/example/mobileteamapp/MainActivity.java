package com.example.mobileteamapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.auth.model.OAuthToken;

public class MainActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button kakaoLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        kakaoLoginButton = findViewById(R.id.B_member_kakao);


        // 카카오 로그인 버튼 클릭 시
        kakaoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithKakaoAccount();
            }
        });
    }

    // 카카오 로그인 처리 (성공/실패 메시지만)
    private void loginWithKakaoAccount() {
        UserApiClient.getInstance().loginWithKakaoAccount(this, (OAuthToken token, Throwable error) -> {
            if (error != null) {
                Toast.makeText(MainActivity.this, " 카카오톡 로그인 실패", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "카카오톡 로그인 성공", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
            return null;
        });
    }
}

