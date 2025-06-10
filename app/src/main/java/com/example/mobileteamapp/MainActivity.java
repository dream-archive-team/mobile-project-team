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
                // 이미 로그인 상태, 바로 HomeActivity로 이동
                String nickname = user.getKakaoAccount().getProfile().getNickname();
                String kakaoId = String.valueOf(user.getId());
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
                                    String nickname = newUser.getKakaoAccount().getProfile().getNickname();
                                    String kakaoId = String.valueOf(newUser.getId());
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