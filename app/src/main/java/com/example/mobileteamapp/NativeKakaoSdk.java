package com.example.mobileteamapp;

import android.app.Application;
import com.kakao.sdk.common.KakaoSdk;

public class NativeKakaoSdk extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Kakao SDK 초기화
        KakaoSdk.init(this, "e8f94e864e70780a25634db61c246de0");

    }
}