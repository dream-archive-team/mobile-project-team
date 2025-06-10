package com.example.mobileteamapp;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

// 카카오 로그인 클래스 - 앱 실행 시 가장 먼저 실행되는 기능
public class NativeKakaoSdk extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // 카카오 로그인 사용
        KakaoSdk.init(this, "89a395ea9e3d5adeb7874ac8789884a4");
    }
}
