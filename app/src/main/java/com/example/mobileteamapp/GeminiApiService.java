package com.example.mobileteamapp;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

// Gemini API 호출 서비스 클래스
public class GeminiApiService {
    // Gemini API 엔드포인트 (키는 BuildConfig에서 분리관리)
    private static final String BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    // 시스템 프롬프트(영문, 응답은 반드시 한국어)
    private static final String SYSTEM_PROMPT =
            "You are a creative novelist and an expert in psychological analysis. "
                    + "Listen to the user’s dream and interpret its symbols and emotions in a richly narrative style. "
                    + "Respond only in Korean, use at least five sentences across multiple paragraphs, and do not ask any follow-up questions. "
                    + "Keep it concise—around 1,000 characters—and finish with a warm, encouraging sentence.";

    private final OkHttpClient client = new OkHttpClient();

    // 콜백 인터페이스
    public interface Callback {
        void onSuccess(String result);
        void onFailure(String errorMsg);
    }

    /**
     * 꿈 텍스트를 전송해 해몽결과(텍스트)를 콜백으로 전달
     * @param userInput   꿈 텍스트
     * @param callback    결과 콜백
     */
    public void requestGemini(String userInput, Callback callback) {
        String apiUrl = BASE_URL + BuildConfig.GEMINI_API_KEY;

        JSONObject json = new JSONObject();
        try {
            // 프롬프트+꿈내용 합쳐서 하나의 text로
            String combined = SYSTEM_PROMPT
                    + "\n\nUser Dream:\n"
                    + userInput;

            JSONObject part = new JSONObject();
            part.put("text", combined);

            JSONArray parts = new JSONArray();
            parts.put(part);

            JSONObject content = new JSONObject();
            content.put("parts", parts);

            JSONArray contents = new JSONArray();
            contents.put(content);

            json.put("contents", contents);

        } catch (Exception e) {
            Log.e("GeminiAPI", "JSON 생성 오류", e);
            callback.onFailure("JSON 생성 오류");
            return;
        }

        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure("네트워크 오류: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String err = response.body() != null ? response.body().string() : "no body";
                    callback.onFailure("Error " + response.code() + ": " + err);
                    return;
                }
                ResponseBody responseBody = response.body();
                if (responseBody == null) {
                    callback.onFailure("응답 본문이 없습니다.");
                    return;
                }
                String result = responseBody.string();
                callback.onSuccess(result);
            }
        });
    }
}
