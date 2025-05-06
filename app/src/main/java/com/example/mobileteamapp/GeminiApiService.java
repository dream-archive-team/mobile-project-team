package com.example.mobileteamapp;

import android.util.Log;
import com.example.mobileteamapp.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Gemini API 호출을 담당하는 서비스 클래스
 */
public class GeminiApiService {
    // ➊ 요청할 Gemini API 엔드포인트 URL 상수
    private static final String BASE_URL =
            // 모델 이름과 엔드포인트 경로, 뒤에 키를 붙여 사용
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    // ➋ OkHttp 클라이언트 인스턴스 생성
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Gemini에 지정된 텍스트(userInput)를 보내고, 결과를 로그로 출력
     *
     * @param userInput 꿈 내용 등 요청할 텍스트
     */
    public void requestGemini(String userInput) {
        // ➌ API 키를 붙여 최종 URL 생성
        String apiUrl = BASE_URL + BuildConfig.GEMINI_API_KEY;

        // ➍ 요청 바디용 JSON 객체 생성
        JSONObject json = new JSONObject();
        try {
            //     ① 텍스트 부분 생성
            JSONObject part = new JSONObject();
            part.put("text", userInput);

            //     ② parts 배열에 추가
            JSONArray parts = new JSONArray();
            parts.put(part);

            //     ③ content 객체에 parts만 담음 (role 필드 불필요)
            JSONObject content = new JSONObject();
            content.put("parts", parts);

            //     ④ contents 배열에 content 객체 추가
            JSONArray contents = new JSONArray();
            contents.put(content);

            //     ⑤ 최종 JSON 구조에 담기
            json.put("contents", contents);
        } catch (Exception e) {
            // JSON 생성 중 오류 발생 시 로그 출력 후 메서드 종료
            Log.e("GeminiAPI", "JSON 생성 오류", e);
            return;
        }

        // ➎ 생성된 JSON 문자열을 요청 바디로 변환
        RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.get("application/json; charset=utf-8")
        );

        // ➏ HTTP POST 요청 빌드
        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .build();

        // ➐ 비동기 호출 실행
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 요청 실패 시 에러 로그
                Log.e("GeminiAPI", "API 요청 실패: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // ➑ 응답 상태 코드 확인
                if (!response.isSuccessful()) {
                    Log.e("GeminiAPI", "응답 오류: " + response.code());
                    return;
                }
                // ➒ 응답 본문을 문자열로 읽어 로그에 출력
                String result = response.body().string();
                Log.d("GeminiAPI", "응답 성공: " + result);
            }
        });
    }
}
