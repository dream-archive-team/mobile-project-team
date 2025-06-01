package com.example.mobileteamapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

/**
 * Intent로 넘어온 데이터(dreamContent, selectedGenre, mood, vividScene, dreamObjects, ending, requiredWords)를
 * 받아 곧바로 API를 호출하고, 응답 받은 소설 텍스트를 화면에 보여줍니다.
 *
 * Layout: res/layout/dream_based_new_nov.xml
 */
public class NovelDetailActivity extends AppCompatActivity {
    private static final String TAG = "NovelDetailActivity";

    // Intent로 넘어온 값들
    private String dreamContent, selectedGenre;
    private String moodText, vividScene, dreamObjects, endingText, requiredWords;

    // 결과 표시용 뷰
    private ProgressBar progressLoading;
    private ScrollView scrollGenerated;
    private TextView tvGeneratedNovel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ★ 변경된 부분: 두 번째 화면에서는 “질문 레이아웃”이 아닌 “결과 전용 레이아웃”을 로드
        setContentView(R.layout.dream_based_new_nov);

        // (A) 레이아웃 바인딩
        progressLoading  = findViewById(R.id.progressLoading);
        scrollGenerated  = findViewById(R.id.scrollGenerated);
        tvGeneratedNovel = findViewById(R.id.tvGeneratedNovel);

        // (B) Intent에서 모든 값 꺼내기
        dreamContent  = getIntent().getStringExtra("dream_content");
        selectedGenre = getIntent().getStringExtra("selected_genre");
        moodText      = getIntent().getStringExtra("mood");
        vividScene    = getIntent().getStringExtra("vivid_scene");
        dreamObjects  = getIntent().getStringExtra("dream_objects");
        endingText    = getIntent().getStringExtra("ending");
        requiredWords = getIntent().getStringExtra("required_words");

        Log.d(TAG, "onCreate: Received dreamContent  = " + dreamContent);
        Log.d(TAG, "onCreate: Received selectedGenre = " + selectedGenre);
        Log.d(TAG, "onCreate: Received mood         = " + moodText);
        Log.d(TAG, "onCreate: Received vividScene   = " + vividScene);
        Log.d(TAG, "onCreate: Received dreamObjects = " + dreamObjects);
        Log.d(TAG, "onCreate: Received ending       = " + endingText);
        Log.d(TAG, "onCreate: Received requiredWords= " + requiredWords);

        // (C) 빌드된 프롬프트 생성
        String prompt = buildPrompt(
                dreamContent,
                selectedGenre,
                moodText,
                vividScene,
                dreamObjects,
                endingText,
                requiredWords
        );
        Log.d(TAG, "Prompt built: " + prompt);

        // (D) API 요청 전 “로딩 중” 표시
        progressLoading.setVisibility(View.VISIBLE);
        scrollGenerated.setVisibility(View.GONE);

        // (E) API 호출
        sendNovelDetailsToApi(prompt);
    }

    /**
     * 프롬프트를 생성하는 메서드
     *
     * @param dreamText     사용자가 입력한 꿈 내용
     * @param genre         사용자가 선택한 소설 장르
     * @param mood          사용자가 선택한 기분
     * @param vividScene    사용자가 입력한 가장 뚜렷했던 장면
     * @param dreamObjects  사용자가 입력한 꿈 속 물건
     * @param ending        사용자가 선택한 엔딩 유형
     * @param requiredWords 사용자가 지정한 필수 단어/문구 (선택사항)
     * @return 최종 전송할 프롬프트 문자열
     */
    private String buildPrompt(
            String dreamText,
            String genre,
            String mood,
            String vividScene,
            String dreamObjects,
            String ending,
            String requiredWords
    ) {
        StringBuilder sb = new StringBuilder();
        // 1) 역할 및 지침
        sb.append("당신은 창의적인 소설가이자 심리 분석 전문가입니다. ");
        sb.append("사용자의 꿈 설명을 바탕으로 상상력이 풍부한 ")
                .append(genre).append(" 소설을 작성하세요. ");
        sb.append("한국어로 응답하고, 후속 질문은 하지 마세요. 약 1000자에서 1500자 사이로 작성하세요.\n\n");

        // 2) 꿈 내용
        sb.append("사용자가 제공한 꿈 내용은 다음과 같습니다:\n\"")
                .append(dreamText)
                .append("\"\n\n");

        // 3) 장르
        sb.append("선택된 장르는 '").append(genre).append("' 입니다.\n\n");

        // 4) 기분, 뚜렷했던 장면, 꿈 속 물건, 엔딩
        sb.append("사용자의 현재 기분은 '").append(mood).append("' 입니다. ");
        sb.append("가장 뚜렷했던 장면이 무엇인지 물었을 때, 사용자는 '")
                .append(vividScene).append("'이라고 답변했습니다. ");
        sb.append("꿈 속에 등장했던 물건이 무엇인지 물었을 때, 사용자는 '")
                .append(dreamObjects).append("'이라고 답변했습니다. ");
        sb.append("선호하는 엔딩 유형이 무엇인지 물었을 때, 사용자는 '")
                .append(ending).append("'이라고 답변했습니다.\n");

        if (!requiredWords.isEmpty()) {
            sb.append("반드시 포함해야 할 단어나 문구가 무엇인지 물었을 때, 사용자는 '")
                    .append(requiredWords).append("'이라고 답변했습니다.\n");
        }

        // 5) 최종 요청: 장르와 엔딩을 반영하여 소설 작성 지시
        sb.append("\n위의 모든 내용을 반영하여, '")
                .append(genre)
                .append("' 장르에 '")
                .append(ending)
                .append("' 엔딩을 가진 약 1000자에서 1500자 사이의 소설을 작성해 주세요.");

        return sb.toString();
    }

    /**
     * AI 서버(Gemini 등)에 프롬프트를 보내는 메서드 (OkHttp 예시)
     *
     * @param promptText 생성된 프롬프트 문자열
     */
    private void sendNovelDetailsToApi(String promptText) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .build();
        // 1) JSON 생성: contents → [ { parts: [ { text: promptText } ] } ]
        JSONObject partObj = new JSONObject();
        try {
            partObj.put("text", promptText);
        } catch (JSONException e) {
            e.printStackTrace();
            runOnUiThread(() ->
                    Toast.makeText(this, "JSON 생성 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
            return;
        }

        JSONArray partsArray = new JSONArray();
        partsArray.put(partObj);

        JSONObject contentObj = new JSONObject();
        try {
            contentObj.put("parts", partsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray contentsArray = new JSONArray();
        contentsArray.put(contentObj);

        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("contents", contentsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 2) RequestBody 생성
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(requestJson.toString(), mediaType);

        // 3) 엔드포인트 URL (키는 URL 파라미터로만 붙임)
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"
                + "?key=" + BuildConfig.GEMINI_API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // 4) 비동기 호출
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "API 호출 실패: " + e.getMessage());
                runOnUiThread(() -> {
                    progressLoading.setVisibility(View.GONE);
                    Toast.makeText(NovelDetailActivity.this,
                            "네트워크 오류: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null
                            ? response.body().string()
                            : "응답 본문이 없습니다.";
                    Log.e(TAG, "API 에러 응답: " + errorBody);
                    runOnUiThread(() -> {
                        progressLoading.setVisibility(View.GONE);
                        Toast.makeText(NovelDetailActivity.this,
                                "서버 오류: " + errorBody,
                                Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                // 성공 시: JSON 파싱 ― 꿈 해몽 때와 동일하게, candidates 배열을 찾아서 content → parts → text 꺼내기
                String responseBody = response.body().string();
                String generatedNovel;
                try {
                    JSONObject respJson = new JSONObject(responseBody);
                    JSONArray candidates = respJson.optJSONArray("candidates");
                    if (candidates != null && candidates.length() > 0) {
                        JSONObject firstCandidate = candidates.getJSONObject(0);
                        JSONObject cont = firstCandidate.optJSONObject("content");
                        if (cont != null) {
                            JSONArray parts = cont.optJSONArray("parts");
                            if (parts != null && parts.length() > 0) {
                                generatedNovel = parts.getJSONObject(0).optString("text", "");
                            } else {
                                generatedNovel = "생성된 소설이 없습니다.";
                            }
                        } else {
                            generatedNovel = "생성된 소설이 없습니다.";
                        }
                    } else {
                        generatedNovel = "생성된 소설이 없습니다.";
                    }
                } catch (JSONException e) {
                    generatedNovel = "응답 파싱 실패: " + e.getMessage();
                }

                Log.d(TAG, "생성된 소설: \n" + generatedNovel);

                // UI 업데이트
                String finalGeneratedNovel = generatedNovel;
                runOnUiThread(() -> {
                    progressLoading.setVisibility(View.GONE);
                    scrollGenerated.setVisibility(View.VISIBLE);
                    tvGeneratedNovel.setText(finalGeneratedNovel);
                    Toast.makeText(NovelDetailActivity.this,
                            "소설 생성 완료! 로그를 확인하세요.",
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

}
