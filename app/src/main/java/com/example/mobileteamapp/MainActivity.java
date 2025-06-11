package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.kakao.sdk.user.UserApiClient;

// 카카오 로그인 화면
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 카카오 로그인 되어 있으면 바로 HomeActivity로 전환
        UserApiClient.getInstance().me((user, error) -> {
            if (user != null) {
                String nickname = "사용자";  // 기본 닉네임
                String kakaoId = String.valueOf(user.getId());

                if (user.getKakaoAccount() != null && user.getKakaoAccount().getProfile() != null) {
                    nickname = user.getKakaoAccount().getProfile().getNickname();
                }

                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("nickname", nickname);
                intent.putExtra("kakaoId", kakaoId);
                startActivity(intent);
                finish();
            } else {
                // 로그인 필요: 로그인 화면 보여줌
                setContentView(R.layout.activity_main);

                Button kakaoLoginButton = findViewById(R.id.B_member_kakao);
                kakaoLoginButton.setOnClickListener(v -> {
                    UserApiClient.getInstance().loginWithKakaoAccount(this, (oAuthToken, loginError) -> {
                        if (oAuthToken != null) {
                            UserApiClient.getInstance().me((newUser, meError) -> {
                                if (newUser != null) {
                                    String nickname = "사용자";
                                    String kakaoId = String.valueOf(newUser.getId());

                                    if (newUser.getKakaoAccount() != null && newUser.getKakaoAccount().getProfile() != null) {
                                        nickname = newUser.getKakaoAccount().getProfile().getNickname();
                                    }

                                    Intent intent = new Intent(this, HomeActivity.class);
                                    intent.putExtra("nickname", nickname);
                                    intent.putExtra("kakaoId", kakaoId);
                                    startActivity(intent);
                                    finish();
                                }
                                return null;
                            });
                        }
                        return null;
                    });
                });
            }
            return null;
        });

    }
}