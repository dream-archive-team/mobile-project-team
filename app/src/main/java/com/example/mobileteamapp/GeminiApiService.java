package com.example.mobileteamapp;

import android.util.Log;
import com.example.mobileteamapp.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class GeminiApiService {

    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    private final OkHttpClient client = new OkHttpClient();

    public void requestGemini(String userInput) {
        String apiUrl = BASE_URL + BuildConfig.GEMINI_API_KEY;

        Log.d("KEY_CHECK", BuildConfig.GEMINI_API_KEY);

        JSONObject json = new JSONObject();
        try {
            JSONObject part = new JSONObject();
            part.put("text", userInput);

            JSONArray parts = new JSONArray();
            parts.put(part);

            JSONObject content = new JSONObject();
            content.put("parts", parts);


            JSONArray contents = new JSONArray();
            contents.put(content);

            json.put("contents", contents);

        } catch (Exception e) {
            Log.e("GeminiAPI", "JSON 생성 오류", e);
            return;
        }

        // JSON 생성 직후
        Log.d("GeminiAPI", "Request JSON: " + json.toString());

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("GeminiAPI", "API 요청 실패: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String respBody = response.body() != null ? response.body().string() : "null";
                if (!response.isSuccessful()) {
                    Log.e("GeminiAPI", "응답 오류: " + response.code() + " / body: " + respBody);
                    return;
                }
                Log.d("GeminiAPI", "응답 성공: " + respBody);
            }
        });
    }
}
