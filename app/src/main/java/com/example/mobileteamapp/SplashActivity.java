package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.kakao.sdk.user.UserApiClient;

/** 자동 로그인 시 바로 메인화면으로 이동하는 클래스
 * AndroidManifest.xml파일에 아직 추가 안함
 *  로그아웃 생성 여부에 따라 추가할지말지 결정예정
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 자동 로그인 여부 확인
        UserApiClient.getInstance().accessTokenInfo((tokenInfo, error) -> {
            Intent intent;

            if (error != null) {
                // 로그인 안되어 있으면 MainActivity(로그인 화면)로 이동
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // 로그인되어 있으면 바로 HomeActivity로 이동
                intent = new Intent(SplashActivity.this, HomeActivity.class);
                intent.putExtra("member_id", String.valueOf(tokenInfo.getId()));
            }

            startActivity(intent);
            finish(); // SplashActivity 종료 → 뒤로가기 시 안보이게
            return null;
        });
    }
}
