package com.example.mobileteamapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
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
public class DreamBasedNewNov extends AppCompatActivity {
    private static final String TAG = "DreamBasedNewNov";

    // Intent로 넘어온 값들
    private String dreamContent, selectedGenre;
    private String moodText, vividScene, dreamObjects, endingText, requiredWords;

    // 결과 표시용 뷰
    private ScrollView scrollGenerated;
    private EditText tvDreamAnalysis;
    private EditText etModification;
    private androidx.appcompat.widget.AppCompatButton btnSubmit;
    // 엔딩용 라디오그룹과 내부 버튼들 (선택된 장르에 맞춰 미리 체크용)
    private RadioGroup  rgEnding;
    private RadioButton rbEndingOpen;
    private RadioButton rbEndingHappy;
    private RadioButton rbEndingTwist2;
    private RadioButton rbEndingTwist3;
    private RadioButton rbEndingTwist;
    private RadioButton rbEndingGrowth;
    private RadioButton rbEndingSad;
    private Button btnHome;
    private String basePrompt;
    private String lastNovel = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ★ 반드시 dream_based_new_nov.xml을 가리켜야 레이아웃이 로드됩니다.
        setContentView(R.layout.dream_based_new_nov);


        // ─────────── 1) 뷰 바인딩 ───────────
        scrollGenerated   = findViewById(R.id.scrollGenerated);
        tvDreamAnalysis   = findViewById(R.id.tvDreamAnalysis);
        etModification  = findViewById(R.id.etDreamInput_1);
        btnSubmit       = findViewById(R.id.btn_submit);
        ColorStateList blackColor = ColorStateList.valueOf(Color.BLACK);

        btnHome = findViewById(R.id.btn_home);

        // 홈으로 가기 버튼 반응
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DreamBasedNewNov.this, HomeActivity.class);
                intent.putExtra("mood_data", moodText);  // 감정 데이터 전달
                startActivity(intent);
            }
        });


        //장르 라디오 버튼 연결
        rgEnding          = findViewById(R.id.rgEnding);
        rbEndingOpen      = findViewById(R.id.rbEndingOpen);
        rbEndingHappy     = findViewById(R.id.rbEndingHappy);
        rbEndingTwist2    = findViewById(R.id.rbEndingTwist2);
        rbEndingTwist3    = findViewById(R.id.rbEndingTwist3);
        rbEndingTwist     = findViewById(R.id.rbEndingTwist);
        rbEndingGrowth    = findViewById(R.id.rbEndingGrowth);
        rbEndingSad       = findViewById(R.id.rbEndingSad);



        // ─── 초기 상태: ScrollView는 항상 보여주고, 내용만 “생성 중…”으로 세팅 ───
        // 이전에 숨겼던 scrollGenerated.setVisibility(View.GONE); 코드를 제거했습니다.
        tvDreamAnalysis.setText("생성 중...");

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


        // RadioButton들을 비활성화
        rbEndingOpen.setEnabled(false);
        rbEndingHappy.setEnabled(false);
        rbEndingTwist2.setEnabled(false);
        rbEndingTwist3.setEnabled(false);
        rbEndingTwist.setEnabled(false);
        rbEndingGrowth.setEnabled(false);
        rbEndingSad.setEnabled(false);


        // 텍스트 색상, 체크박스를 검은색으로 설정 (비활성화 시 회색이 되는 것을 방지)
        rbEndingOpen.setTextColor(Color.BLACK);
        rbEndingHappy.setTextColor(Color.BLACK);
        rbEndingTwist2.setTextColor(Color.BLACK);
        rbEndingTwist3.setTextColor(Color.BLACK);
        rbEndingTwist.setTextColor(Color.BLACK);
        rbEndingGrowth.setTextColor(Color.BLACK);
        rbEndingSad.setTextColor(Color.BLACK);

        rbEndingOpen.setButtonTintList(blackColor);
        rbEndingHappy.setButtonTintList(blackColor);
        rbEndingTwist2.setButtonTintList(blackColor);
        rbEndingTwist3.setButtonTintList(blackColor);
        rbEndingTwist.setButtonTintList(blackColor);
        rbEndingGrowth.setButtonTintList(blackColor);
        rbEndingSad.setButtonTintList(blackColor);


        // 전달받은 장르에 따라 해당 라디오버튼만 자동 체크
        if (selectedGenre != null) {
            // 모든 라디오버튼 체크 해제
            rbEndingOpen.setChecked(false);
            rbEndingHappy.setChecked(false);
            rbEndingTwist2.setChecked(false);
            rbEndingTwist3.setChecked(false);
            rbEndingTwist.setChecked(false);
            rbEndingGrowth.setChecked(false);
            rbEndingSad.setChecked(false);

            // 전달받은 장르와 일치하는 라디오버튼만 체크
            switch (selectedGenre) {
                case "판타지":
                    rbEndingOpen.setChecked(true);
                    break;
                case "로맨틱":
                    rbEndingHappy.setChecked(true);
                    break;
                case "SF":
                    rbEndingTwist2.setChecked(true);
                    break;
                case "다큐멘터리":
                    rbEndingTwist3.setChecked(true);
                    break;
                case "스릴러":
                    rbEndingTwist.setChecked(true);
                    break;
                case "코미디":
                    rbEndingGrowth.setChecked(true);
                    break;
                case "액션":
                    rbEndingSad.setChecked(true);
                    break;
                default:
                    Toast.makeText(this, "알 수 없는 장르: " + selectedGenre, Toast.LENGTH_SHORT).show();
                    break;
            }

            Toast.makeText(this, "선택된 장르: " + selectedGenre, Toast.LENGTH_SHORT).show();
        }
        // 라디오 버튼 자체를 비활성화하려면 아래 코드처럼 해도 됩니다.
        // rgEnding.setEnabled(false);
        // for (int i = 0; i < rgEnding.getChildCount(); i++) {
        //     rgEnding.getChildAt(i).setEnabled(false);
        // }

        // ─────────── 4) 프롬프트 빌딩 ───────────
        basePrompt = buildPrompt(
                dreamContent,
                selectedGenre,
                moodText,
                vividScene,
                dreamObjects,
                endingText,
                requiredWords
        );
        Log.d(TAG, "Prompt built: " + basePrompt);

        // ─────────── 5) API 호출 ───────────
        sendNovelDetailsToApi(basePrompt);

        btnSubmit.setOnClickListener(v -> {
            // (7-1) 사용자가 입력한 수정 문구 가져오기
            String userModification = etModification.getText().toString().trim();
            String userEditedNovel = tvDreamAnalysis.getText().toString().trim();
            if (userEditedNovel.isEmpty()) {
                Toast.makeText(DreamBasedNewNov.this,
                        "먼저 소설을 편집하거나 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (userModification.isEmpty()) {
                Toast.makeText(DreamBasedNewNov.this,
                        "먼저 수정할 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            lastNovel = userEditedNovel; // 사용자가 수정했을 수도 있는 소설 최종본을 프롬프트로 보내기

            // (7-2) 버튼 중복 클릭 방지 및 상태 표시
            btnSubmit.setEnabled(false);
            tvDreamAnalysis.setText("수정 요청 중...");

            // (7-3) basePrompt 뒤에 수정 요청 문구 추가. 기존 소설 내용도 보내서 기존 소설 내용 유지.
            String modifiedPrompt =
                    // 고정 프롬프트(basePrompt)
                    basePrompt.trim() + "\n\n" +
                            //  이전 생성된 소설 전체 (lastNovel)을 반드시 태그로 둘러싸기
                            "===START ORIGINAL===\n" +
                            lastNovel.trim() + "\n" +
                            "===END ORIGINAL===\n\n" +
                            "바꿀 부분을 제외한 모든 텍스트는 절대 수정하면 안 됩니다.\n\n" +
                            //  수정 지시사항 (userModification)
                            "수정 지시사항:\n" +
                            userModification.trim() + "\n\n" +
                            //  출력 형식 지시
                            "위 지시사항만 반영하여, 기존 소설의 내용을 수정해 주세요.\n" +
                            "소설 원본을 구분하는 '===START ORIGINAL===', '===END ORIGINAL==='는 포함하지마세요.\n" +
                            "결과 값에는 '제목:'과 '내용:'만 포함하고, 그 외 설명은 쓰지 마세요.";

            Log.d(TAG, "Modified Prompt: " + modifiedPrompt);

            // (7-4) 다시 API 요청
            sendNovelDetailsToApi(modifiedPrompt);
        });


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
                .append("' 엔딩을 가진 약 1000자에서 1500자 사이의 소설을 작성해 주세요.")
                .append("' 결과 값에는 '제목:'과 '내용:'만 포함해주세요.");

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
                    Toast.makeText(DreamBasedNewNov.this,
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
                        Toast.makeText(DreamBasedNewNov.this,
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
                    lastNovel = finalGeneratedNovel;
                    btnSubmit.setEnabled(true);
                    Toast.makeText(DreamBasedNewNov.this,
                            "소설 생성 완료! 로그를 확인하세요.", Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
