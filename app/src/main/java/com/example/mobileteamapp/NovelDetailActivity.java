// NovelDetailActivity.java
package com.example.mobileteamapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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

public class NovelDetailActivity extends AppCompatActivity {
    private static final String TAG = "NovelDetailActivity";

    // (A) 첫 번째 화면(SelectNovGenreActivity)에서 넘어온 “꿈 내용”/“선택된 장르”를 담을 멤버 변수
    private String dreamContent;
    private String selectedGenre;

    // 1) 두 번째 화면의 뷰들 선언
    private RadioGroup rgMood, rgEnding;
    private EditText etMostVividScene, etDreamObjects, etRequiredWords;
    private AppCompatButton btnGenerateNovel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dream_based_nov);

        Log.d(TAG, "onCreate called");

        // (B) Intent 로부터 “dream_content”와 “selected_genre” 가져오기
        dreamContent   = getIntent().getStringExtra("dream_content");
        selectedGenre  = getIntent().getStringExtra("selected_genre");
        if (dreamContent == null)   dreamContent   = "";
        if (selectedGenre == null)  selectedGenre  = "";

        // 받아온 값 로그로 확인
        Log.d(TAG, "onCreate: Received dreamContent = " + dreamContent);
        Log.d(TAG, "onCreate: Received selectedGenre = " + selectedGenre);

        // 2) 뷰 초기화
        rgMood            = findViewById(R.id.rgMood);
        etMostVividScene  = findViewById(R.id.etMostVividScene);
        etDreamObjects    = findViewById(R.id.etDreamObjects);
        rgEnding          = findViewById(R.id.rgEnding);
        etRequiredWords   = findViewById(R.id.etRequiredWords);
        btnGenerateNovel  = findViewById(R.id.btnGenerateNovel);

        // 3) 버튼 클릭 리스너
        btnGenerateNovel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "NovelDetailActivity: btnGenerateNovel clicked");
                collectInputsAndSendToApi();
            }
        });
    }

    /**
     * 사용자가 두 번째 화면에서 입력한 모든 정보를 모아서 프롬프트를 생성하고,
     * 그 프롬프트를 AI 서버(예: Gemini)로 전송하는 메서드
     */
    private void collectInputsAndSendToApi() {
        Log.d(TAG, "collectInputsAndSendToApi: 진입 성공");
        Toast.makeText(this, "collectInputsAndSendToApi() 진입", Toast.LENGTH_SHORT).show();


        // --- 1) 기분 꺼내기 ---
        int selectedMoodId = rgMood.getCheckedRadioButtonId();
        if (selectedMoodId == -1) {
            Toast.makeText(this, "1번 질문: 기분을 하나 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton rbSelectedMood = findViewById(selectedMoodId);
        String moodText = rbSelectedMood.getText().toString();

        // --- 2) 가장 뚜렷했던 장면 꺼내기 ---
        String vividScene = etMostVividScene.getText().toString().trim();
        if (vividScene.isEmpty()) {
            Toast.makeText(this, "2번 질문: 가장 뚜렷했던 장면을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 3) 꿈 속 물건 꺼내기 ---
        String dreamObjects = etDreamObjects.getText().toString().trim();
        if (dreamObjects.isEmpty()) {
            Toast.makeText(this, "3번 질문: 꿈 속 물건을 입력해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 4) 엔딩 유형 꺼내기 ---
        int selectedEndingId = rgEnding.getCheckedRadioButtonId();
        if (selectedEndingId == -1) {
            Toast.makeText(this, "4번 질문: 엔딩 유형을 하나 선택해 주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton rbSelectedEnding = findViewById(selectedEndingId);
        String endingText = rbSelectedEnding.getText().toString();

        // --- 5) 필수 단어 꺼내기 ---
        String requiredWords = etRequiredWords.getText().toString().trim();
        // (빈 문자열이어도 무방)

        // --- 6) “dreamContent”와 “selectedGenre” 포함해서 프롬프트 생성 ---
        String prompt = buildPrompt(
                dreamContent,    // 첫 화면(SelectNovGenre)에서 넘어온 꿈 내용
                selectedGenre,   // 첫 화면(장르 선택)에서 넘어온 장르
                moodText,        // 기분
                vividScene,      // 뚜렷했던 장면
                dreamObjects,    // 꿈 속 물건
                endingText,      // 엔딩 유형
                requiredWords    // 필수 단어
        );
        Log.d(TAG, "Prompt built: " + prompt);

        // --- 7) AI 서버로 전송 ---
        sendNovelDetailsToApi(prompt);
    }

    /**
     * 사용자가 입력한 모든 정보를 하나의 자연어 프롬프트로 합치는 메서드
     * (꿈 내용 + 선택된 장르 + 기분/장면/물건/엔딩/필수 단어)
     */
    private String buildPrompt(String dreamText,
                               String genre,
                               String mood,
                               String vividScene,
                               String dreamObjects,
                               String ending,
                               String requiredWords) {
        StringBuilder sb = new StringBuilder();
        // 1) 고정 템플릿: 역할 및 지침
        sb.append("You are a creative novelist and an expert in psychological analysis. ");
        sb.append("Based on the user’s dream description, write an imaginative ").append(genre).append(" story. ");
        sb.append("Respond in Korean, do not ask follow-up questions. Write approximately 1000 to 1500 characters.\n\n");

        // 2) 첫 화면에서 넘어온 꿈 내용
        sb.append("Here is the user’s dream:\n\"")
                .append(dreamText)
                .append("\"\n\n");

        // 3) 첫 화면에서 넘어온 선택된 장르
        sb.append("The chosen genre is '").append(genre).append("'.\n\n");

        // 4) 두 번째 화면에서 입력된 내용들
        sb.append("The user’s current mood is '").append(mood).append("'. ");
        sb.append("When asked what the most vivid scene was, the user replied '")
                .append(vividScene).append("'. ");
        sb.append("When asked which objects appeared in the dream, the user replied '")
                .append(dreamObjects).append("'. ");
        sb.append("When asked about the preferred ending style, the user replied '")
                .append(ending).append("'.\n");

        if (!requiredWords.isEmpty()) {
            sb.append("When asked which words or phrases must be included, the user replied '")
                    .append(requiredWords).append("'.\n");
        }

        // 5) 최종 작성 요청: 장르, 엔딩, 분량
        sb.append("\nUsing all of the above, please write a ")
                .append(genre).append(" story with an '")
                .append(ending)
                .append("' ending, approximately 1000 to 1500 characters long.");

        return sb.toString();
    }

    /**
     * AI 서버(Gemini 등)에 프롬프트를 보내는 메서드 (OkHttp 예시)
     */
    private void sendNovelDetailsToApi(String promptText) {
        String apiKey = BuildConfig.GEMINI_API_KEY; // BuildConfig에 저장된 API 키

        Log.d(TAG, "sendNovelDetailsToApi: 호출됨");
        Toast.makeText(this, "sendNovelDetailsToApi() 진입", Toast.LENGTH_SHORT).show();

        OkHttpClient client = new OkHttpClient();

        // 1) JSON 구성: { "contents": { "novelPrompt": "..." } }
        JSONObject contentsObj = new JSONObject();
        try {
            contentsObj.put("novelPrompt", promptText);
        } catch (JSONException e) {
            e.printStackTrace();
            runOnUiThread(() ->
                    Toast.makeText(this, "JSON 생성 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
            return;
        }

        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("contents", contentsObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 2) RequestBody 생성
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(requestJson.toString(), mediaType);

        // 3) 엔드포인트 URL (예시)
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .post(body)
                .build();
        Log.d(TAG, "Calling API with URL: " + url);
        Log.d(TAG, "Request body: " + requestJson.toString());

        // 4) 비동기 호출
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "API 호출 실패: " + e.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(NovelDetailActivity.this, "네트워크 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "응답 본문이 없습니다.";
                    Log.e(TAG, "API 에러 응답: " + errorBody);
                    runOnUiThread(() ->
                            Toast.makeText(NovelDetailActivity.this, "서버 오류: " + errorBody, Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                // 5) 성공 시: JSON 파싱 후 소설 내용 꺼내기
                String responseBody = response.body().string();
                String generatedNovel;
                try {
                    JSONObject respJson = new JSONObject(responseBody);
                    // 예시: { "novelText": "AI가 생성한 소설..." }
                    generatedNovel = respJson.getString("novelText");
                } catch (JSONException e) {
                    e.printStackTrace();
                    generatedNovel = "응답 파싱 실패: " + e.getMessage();
                }

                Log.d(TAG, "생성된 소설: \n" + generatedNovel);

                // 6) UI 업데이트 (예: 로그 출력 혹은 화면에 TextView/ScrollView에 세팅)
                runOnUiThread(() -> {
                    Toast.makeText(NovelDetailActivity.this, "소설 생성 완료! 로그를 확인하세요.", Toast.LENGTH_LONG).show();
                    // 실제로는 긴 텍스트를 보여줄 수 있는 TextView/ScrollView에 세팅하면 됩니다.
                });
            }
        });
    }
}
