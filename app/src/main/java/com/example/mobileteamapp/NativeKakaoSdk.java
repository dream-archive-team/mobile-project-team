package com.example.mobileteamapp;

import android.app.Application;
import com.kakao.sdk.common.KakaoSdk;

public class NativeKakaoSdk extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Kakao SDK 초기화
        KakaoSdk.init(this, "a42806eea21cfe350b5719b8f3b8c288");
    }
}