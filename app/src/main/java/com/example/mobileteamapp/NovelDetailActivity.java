package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private ScrollView scrollGenerated;
    private TextView   tvDreamAnalysis;

    // 엔딩용 라디오그룹과 내부 버튼들 (선택된 장르에 맞춰 미리 체크용)
    private RadioGroup  rgEnding;
    private RadioButton rbEndingOpen;
    private RadioButton rbEndingHappy;
    private RadioButton rbEndingTwist2;
    private RadioButton rbEndingTwist3;
    private RadioButton rbEndingTwist;
    private RadioButton rbEndingGrowth;
    private RadioButton rbEndingSad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ★ 반드시 dream_based_new_nov.xml을 가리켜야 레이아웃이 로드됩니다.
        setContentView(R.layout.dream_based_new_nov);

        // ─────────── 1) 뷰 바인딩 ───────────
        scrollGenerated   = findViewById(R.id.scrollGenerated);
        tvDreamAnalysis   = findViewById(R.id.tvDreamAnalysis);

        rgEnding          = findViewById(R.id.rgEnding);
        rbEndingOpen      = findViewById(R.id.rbEndingOpen);
        rbEndingHappy     = findViewById(R.id.rbEndingHappy);
        rbEndingTwist2    = findViewById(R.id.rbEndingTwist2);
        rbEndingTwist3    = findViewById(R.id.rbEndingTwist3);
        rbEndingTwist     = findViewById(R.id.rbEndingTwist);
        rbEndingGrowth    = findViewById(R.id.rbEndingGrowth);
        rbEndingSad       = findViewById(R.id.rbEndingSad);

        // ─── 초기 상태: ScrollView는 항상 보여주고, 내용만 “로딩 중…”으로 세팅 ───
        // 이전에 숨겼던 scrollGenerated.setVisibility(View.GONE); 코드를 제거했습니다.
        tvDreamAnalysis.setText("로딩 중...");

        // ─────────── 2) Intent로부터 데이터 가져오기 ───────────
        Intent intent = getIntent();
        dreamContent  = intent.getStringExtra("dream_content");
        selectedGenre = intent.getStringExtra("selected_genre");
        moodText      = intent.getStringExtra("mood");
        vividScene    = intent.getStringExtra("vivid_scene");
        dreamObjects  = intent.getStringExtra("dream_objects");
        endingText    = intent.getStringExtra("ending");
        requiredWords = intent.getStringExtra("required_words");

        if (dreamContent == null)  dreamContent  = "";
        if (selectedGenre == null) selectedGenre = "";
        if (moodText == null)      moodText      = "";
        if (vividScene == null)    vividScene    = "";
        if (dreamObjects == null)  dreamObjects  = "";
        if (endingText == null)    endingText    = "";
        if (requiredWords == null) requiredWords = "";

        Log.d(TAG, "onCreate: Received dreamContent  = " + dreamContent);
        Log.d(TAG, "onCreate: Received selectedGenre = " + selectedGenre);
        Log.d(TAG, "onCreate: Received mood         = " + moodText);
        Log.d(TAG, "onCreate: Received vividScene    = " + vividScene);
        Log.d(TAG, "onCreate: Received dreamObjects  = " + dreamObjects);
        Log.d(TAG, "onCreate: Received ending        = " + endingText);
        Log.d(TAG, "onCreate: Received requiredWords = " + requiredWords);

        // ─────────── 3) ‘selected_genre’에 맞춘 엔딩 버튼 미리 체크 ───────────
        // (XML에서 rgEnding을 터치 불가능하게 disabled 처리하지 않았다면, 여기서도 다시 disable 처리해도 좋습니다.)
        switch (selectedGenre) {
            case "판타지":
                rbEndingOpen.setChecked(true);
                break;
            case "로맨스":
                rbEndingHappy.setChecked(true);
                break;
            case "판타지/로맨스":
                rbEndingTwist2.setChecked(true);
                break;
            case "스릴러":
                rbEndingTwist3.setChecked(true);
                break;
            case "성장":
                rbEndingGrowth.setChecked(true);
                break;
            case "비극":
                rbEndingSad.setChecked(true);
                break;
            default:
                rgEnding.clearCheck();
                break;
        }
        // 라디오 버튼 자체를 비활성화하려면 아래 코드처럼 해도 됩니다.
        // rgEnding.setEnabled(false);
        // for (int i = 0; i < rgEnding.getChildCount(); i++) {
        //     rgEnding.getChildAt(i).setEnabled(false);
        // }

        // ─────────── 4) 프롬프트 빌딩 ───────────
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

        // ─────────── 5) API 호출 ───────────
        sendNovelDetailsToApi(prompt);
    }

    /**
     * 프롬프트를 생성하는 메서드
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
     * AI 서버에 프롬프트를 보내는 메서드 (OkHttp 예시)
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
                    // 오류 시에도 ScrollView는 보이도록 유지
                    tvDreamAnalysis.setText("네트워크 오류: " + e.getMessage());
                    Toast.makeText(NovelDetailActivity.this,
                            "네트워크 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String generatedNovel;
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null
                            ? response.body().string()
                            : "응답 본문이 없습니다.";
                    Log.e(TAG, "API 에러 응답: " + errorBody);
                    runOnUiThread(() -> {
                        tvDreamAnalysis.setText("서버 오류: " + errorBody);
                        Toast.makeText(NovelDetailActivity.this,
                                "서버 오류: " + errorBody, Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                // 5) 성공 시: JSON 파싱 ― candidates → content → parts → text
                String responseBody = response.body().string();
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

                // 6) UI 업데이트 (메인 스레드)
                String finalGeneratedNovel = generatedNovel;
                runOnUiThread(() -> {
                    // ScrollView는 항상 보이며, 텍스트만 업데이트
                    tvDreamAnalysis.setText(finalGeneratedNovel);
                    Toast.makeText(NovelDetailActivity.this,
                            "소설 생성 완료! 로그를 확인하세요.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
