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
import com.kakao.sdk.user.model.User;

public class MainActivity extends AppCompatActivity {

    // 이메일과 비밀번호 EditText
    private EditText emailEditText;
    private EditText passwordEditText;

    // 로그인 버튼
    private Button loginButton;
    private Button kakaoLoginButton;  // 카카오톡 로그인 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // XML에서 UI 요소들 가져오기
        emailEditText = findViewById(R.id.id_editT);  // 이메일 입력란
        passwordEditText = findViewById(R.id.passward_editT);  // 비밀번호 입력란
        loginButton = findViewById(R.id.B_login);  // 로그인 버튼
        kakaoLoginButton = findViewById(R.id.B_member_kakao);  // 카카오톡 로그인 버튼

        // 이메일/비밀번호 로그인 버튼 클릭 리스너 설정
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이메일과 비밀번호 입력값 가져오기
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // 입력값이 비어있는지 확인
                if (email.isEmpty()) {
                    Toast.makeText(MainActivity.this, "이메일을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 이메일과 비밀번호가 모두 입력된 경우 로그인 처리
                loginUser(email, password);
            }
        });

        // 카카오톡 로그인 버튼 클릭 리스너 설정
        kakaoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithKakaoAccount();  // 카카오 계정 로그인
            }
        });
    }

    // 사용자 이메일/비밀번호 로그인 처리
    private void loginUser(String email, String password) {
        // **실제 이메일과 비밀번호를 db 저장 내용에서 일치하는지 확인하는 코드 필요
        // 예시로 로그인 성공했다고 가정하고 홈 화면으로 이동

        Toast.makeText(MainActivity.this, "로그인 시도: 이메일 - " + email + ", 비밀번호 - " + password, Toast.LENGTH_LONG).show();

        // 로그인 성공 시 홈 화면으로 이동
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    // 카카오 계정으로 로그인 처리
    private void loginWithKakaoAccount() {
        UserApiClient.getInstance().loginWithKakaoAccount(this, (OAuthToken token, Throwable error) -> {
            if (error != null) {
                // 로그인 실패 시
                Toast.makeText(MainActivity.this, "카카오톡 로그인 실패", Toast.LENGTH_SHORT).show();
            } else {
                // 로그인 성공 시
                checkUserRegistration();  // 사용자 등록 상태 확인
            }
            return null;
        });
    }

    // 카카오 로그인 후 사용자 등록 상태 체크
    private void checkUserRegistration() {
        UserApiClient.getInstance().me((user, error) -> {
            if (error != null) {
                Toast.makeText(MainActivity.this, "사용자 정보 조회 실패", Toast.LENGTH_SHORT).show();
                return null;
            }

            if (user != null) {
                String userEmail = user.getKakaoAccount() != null ? user.getKakaoAccount().getEmail() : null;

                if (userEmail != null && !userEmail.isEmpty()) {
                    // 이미 가입된 사용자 -> HomeActivity로 이동
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    // 미가입 사용자 -> NewMemberActivity로 이동
                    //**이메일과 비번 정보 전달 코드 필요
                    Intent intent = new Intent(MainActivity.this, NextMemberPro.class);
                    startActivity(intent);
                }
            }
            return null;
        });
    }
}
