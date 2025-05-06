package com.example.mobileteamapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.util.Log;
import android.widget.Toast;

import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.auth.model.OAuthToken;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button signupBtn = findViewById(R.id.new_member_log);
        Button kakaoLoginBtn = findViewById(R.id.B_member_kakao);

        kakaoLoginBtn.setOnClickListener(v -> {
            Log.d("TEST", "카카오 로그인 시도 시작");

            UserApiClient.getInstance().loginWithKakaoTalk(this, (OAuthToken token, Throwable error) -> {
                if (error != null) {
                    Log.e("Kakao Login", "카카오톡 로그인 실패", error);

                    // 카카오톡 미설치 시 계정 로그인으로 대체
                    UserApiClient.getInstance().loginWithKakaoAccount(this, (OAuthToken accountToken, Throwable accountError) -> {
                        if (accountError != null) {
                            Log.e("Kakao Login", "계정 로그인 실패", accountError);
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show());
                        } else {
                            Log.d("Kakao Login", "계정 로그인 성공");
                            runOnUiThread(() -> {
                                Toast.makeText(MainActivity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                                // 로그인 성공 시 NextMemberProActivity로 이동
                                Intent intent = new Intent(this, NextMemberPro.class);
                                startActivity(intent);
                            });
                        }
                        return null;
                    });

                } else if (token != null) {
                    Log.d("Kakao Login", "카카오톡 로그인 성공");
                    runOnUiThread(() -> {
                        Intent intent = new Intent(MainActivity.this, NextMemberPro.class);
                        startActivity(intent);
                    });
                }
                return null;
            });
        });

        // 회원가입 버튼 클릭 시 NextMemberProActivity로 이동
        signupBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NewMemberActivity.class);
            startActivity(intent);
        });
    }
}
