package com.example.mobileteamapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Gemini API 호출을 담당하는 서비스 클래스
 */
public class GeminiApiService {
    // ➊ Gemini API 엔드포인트 URL (키는 뒤에 붙여서 사용)
    private static final String BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    // ➋ 영어 시스템 프롬프트(영어가 더 좋은 해석 결과를 제공함) + “응답은 한국어로만”
    private static final String SYSTEM_PROMPT =
            "You are a creative novelist and an expert in psychological analysis. "
                    + "When you hear the user’s dream, delve into its symbols and emotions, "
                    +"interpret it in a richly narrative style, respond only in Korean without any English translation or additional languages, "
                    + "limit your response to 1000 characters, "
                    + "and focus solely on analyzing the dream content provided—do not ask any clarifying or follow-up questions."
                    + "and conclude with a warm, uplifting sentence that leaves the user feeling encouraged and hopeful.";


    // ➌ OkHttpClient 인스턴스
    private final OkHttpClient client = new OkHttpClient();

    /** 콜백 인터페이스 정의 */
    public interface Callback {
        void onSuccess(String result);
        void onFailure(String errorMsg);
    }

    /**
     * 꿈 텍스트를 보내고, 해석 결과(JSON)를 콜백으로 전달
     *
     * @param userInput 사용자 꿈 텍스트
     * @param callback  결과 콜백
     */
    public void requestGemini(String userInput, Callback callback) {
        String apiUrl = BASE_URL + BuildConfig.GEMINI_API_KEY;

        JSONObject json = new JSONObject();
        try {
            // 시스템 프롬프트 + 사용자 입력을 하나의 text로 합침
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
                callback.onFailure(e.getMessage());
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
