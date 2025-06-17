package com.example.mobileteamapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileteamapp.entity.Novel;
import com.example.mobileteamapp.viewmodel.NovelViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

// 소설 생성 화면
public class DreamBasedNewNovActivity extends AppCompatActivity {

    private static final int REQ_SELECT_GENRE = 101;
    private TextView tvAnalysisLabel;
    private EditText tvDreamAnalysis;
    private EditText etModification;
    private Button btnSubmit, btnSave, btnNewGenre;
    private String basePrompt;
    private String lastNovel = "";
    private NovelViewModel novelViewModel;

    private String dreamContent, selectedGenre, moodText, vividScene, dreamObjects, endingText, requiredWords, dreamDate, dreamId;
    private String nickname, kakaoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_based_new_nov);

        // UI요소 연결
        tvAnalysisLabel = findViewById(R.id.tvAnalysisLabel);   // 제목 표시
        tvDreamAnalysis = findViewById(R.id.tvDreamAnalysis);   // 내용 표시
        btnSave = findViewById(R.id.btn_save); // '새 장르'
        etModification = findViewById(R.id.etDreamInput_1);
        btnSubmit = findViewById(R.id.btn_submit);
        Button btnHome = findViewById(R.id.btn_home);

        // ViewModel 연결
        novelViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(NovelViewModel.class);

        // 인텐트 값 받기
        Intent intent = getIntent();
        dreamContent = intent.getStringExtra("dream_content");
        selectedGenre = intent.getStringExtra("selected_genre");
        moodText = intent.getStringExtra("selected_mood");
        vividScene = intent.getStringExtra("vivid_scene");
        dreamObjects = intent.getStringExtra("dream_objects");
        endingText = intent.getStringExtra("ending");
        requiredWords = intent.getStringExtra("required_words");
        dreamDate = intent.getStringExtra("dream_date");
        dreamId = intent.getStringExtra("dream_id");
        nickname = intent.getStringExtra("nickname");
        kakaoId = intent.getStringExtra("kakaoId");

        // btnSave 동작 수정 (기존 SelectNovGenreActivity 호출 → 꿈 정보 포함하도록 변경)
        btnSave.setOnClickListener(v -> {
            Intent genreIntent = new Intent(this, SelectNovGenreActivity.class);
            genreIntent.putExtra("exclude_genre", selectedGenre);
            genreIntent.putExtra("dream_content", dreamContent);
            genreIntent.putExtra("dream_interpretation", intent.getStringExtra("dream_interpretation"));
            genreIntent.putExtra("selected_mood", moodText);
            genreIntent.putExtra("vivid_scene", vividScene);
            genreIntent.putExtra("dream_objects", dreamObjects);
            genreIntent.putExtra("ending", endingText);
            genreIntent.putExtra("required_words", requiredWords);
            genreIntent.putExtra("dream_date", dreamDate);
            genreIntent.putExtra("dream_id", dreamId);
            genreIntent.putExtra("nickname", nickname);
            genreIntent.putExtra("kakaoId", kakaoId);
            startActivityForResult(genreIntent, REQ_SELECT_GENRE);
        });


        RadioGroup radioGroup = findViewById(R.id.radioGroup_genre);
        RadioButton radioFantasy = findViewById(R.id.radio_fantasy);
        RadioButton radioRomantic = findViewById(R.id.radio_romantic);
        RadioButton radioSF = findViewById(R.id.radio_sf);
        RadioButton radioDocumentary = findViewById(R.id.radio_documentary);
        RadioButton radioThriller = findViewById(R.id.radio_thriller);
        RadioButton radioComedy = findViewById(R.id.radio_comedy);
        RadioButton radioAction = findViewById(R.id.radio_action);

        Log.d("Novel", "Novel 생성화면, 전달받은 dream_id = " + dreamId);

        // 1. 장르 자동 체크
        // 라디오 버튼 배열 선언 및 비활성화
        RadioButton[] radios = {
                radioFantasy, radioRomantic, radioSF, radioDocumentary,
                radioThriller, radioComedy, radioAction
        };
        for (RadioButton rb : radios) {
            rb.setEnabled(false); // 모두 비활성화
        }

// 선택한 장르만 체크 및 텍스트 색상 검정으로
        if (selectedGenre != null) {
            RadioButton selectedButton = null;
            switch (selectedGenre) {
                case "판타지":
                    selectedButton = radioFantasy; break;
                case "로맨틱":
                    selectedButton = radioRomantic; break;
                case "SF":
                    selectedButton = radioSF; break;
                case "다큐멘터리":
                    selectedButton = radioDocumentary; break;
                case "스릴러":
                    selectedButton = radioThriller; break;
                case "코미디":
                    selectedButton = radioComedy; break;
                case "액션":
                    selectedButton = radioAction; break;
            }
            if (selectedButton != null) {
                selectedButton.setChecked(true);
                selectedButton.setTextColor(Color.BLACK); // 선택된 버튼만 검정색 유지
            }
        }


        // 프롬프트 생성
        basePrompt = buildPrompt(
                dreamContent,
                selectedGenre,
                moodText,
                vividScene,
                dreamObjects,
                endingText,
                requiredWords
        );
        tvAnalysisLabel.setText("제목 생성 중...");
        tvDreamAnalysis.setText("소설 생성 중...");

        // 2. 소설 최초 생성 & 생성 완료 후 자동 저장
        sendNovelDetailsToApi(basePrompt, false);

        // 3. 홈 이동 버튼
        btnHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, HomeActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            homeIntent.putExtra("nickname", nickname);
            homeIntent.putExtra("kakaoId", kakaoId);
            startActivity(homeIntent);
            finish();
        });

        // 4. 수정 버튼 → SelectNovGenreActivity로 이동
        // DreamBasedNewNovActivity.java 의 onCreate() 내부
        btnSubmit.setOnClickListener(v -> {
            String userModification = etModification.getText().toString().trim();
            String userEditedNovel = tvDreamAnalysis.getText().toString().trim();

            if (userEditedNovel.isEmpty()) {
                Toast.makeText(this, "먼저 소설을 편집하거나 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1. 직접 수정만 한 경우 → DB 저장
            if (userModification.isEmpty()) {
                String novelTitle = tvAnalysisLabel.getText().toString().trim();
                String novelContent = userEditedNovel;

                Novel novel = new Novel(
                        novelTitle,
                        novelContent,
                        selectedGenre,
                        moodText,
                        vividScene,
                        dreamObjects,
                        endingText,
                        requiredWords,
                        dreamDate,
                        dreamId
                );

                new Thread(() -> {
                    // 1. 기존 소설 삭제
                    Novel existing = novelViewModel.getNovelByDreamIdAndGenre(dreamId, selectedGenre);
                    if (existing != null) {
                        novelViewModel.delete(existing);
                    }

                    // 2. 새로운 소설 저장
                    novelViewModel.insert(novel);

                    runOnUiThread(() ->
                            Toast.makeText(this, "수정된 소설이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                    );
                }).start();

                return;
            }


            // 2. 수정 지시사항이 있는 경우 → Gemini API 호출
            lastNovel = userEditedNovel;
            btnSubmit.setEnabled(false);
            tvDreamAnalysis.setText("수정 요청 중...");

            String modifiedPrompt =
                    basePrompt.trim() + "\n\n" +
                            "===START ORIGINAL===\n" +
                            lastNovel.trim() + "\n" +
                            "===END ORIGINAL===\n\n" +
                            "바꿀 부분을 제외한 모든 텍스트는 절대 수정하면 안 됩니다.\n\n" +
                            "수정 지시사항:\n" +
                            userModification.trim() + "\n\n" +
                            "위 지시사항만 반영하여, 기존 소설의 내용을 수정해 주세요.\n" +
                            "소설 원본을 구분하는 '===START ORIGINAL===', '===END ORIGINAL==='는 포함하지마세요.\n" +
                            "결과 값에는 '제목:'과 '내용:'만 포함하고, 그 외 설명은 쓰지 마세요.";

            sendNovelDetailsToApi(modifiedPrompt, true);
        });

    }

    // 6. 새 장르 선택 후 결과 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SELECT_GENRE && resultCode == RESULT_OK && data != null) {
            etModification.setText("");
            // 새로 선택한 장르 받아서 값 갱신
            selectedGenre = data.getStringExtra("selected_genre");
            // 나머지 값들도 필요하면 갱신
            moodText = data.getStringExtra("selected_mood");
            vividScene = data.getStringExtra("vivid_scene");
            dreamObjects = data.getStringExtra("dream_objects");
            endingText = data.getStringExtra("ending");
            requiredWords = data.getStringExtra("required_words");

            // 프롬프트 및 소설 재생성
            basePrompt = buildPrompt(
                    dreamContent,
                    selectedGenre,
                    moodText,
                    vividScene,
                    dreamObjects,
                    endingText,
                    requiredWords
            );
            tvAnalysisLabel.setText("제목 생성 중...");
            tvDreamAnalysis.setText("소설 생성 중...");
            sendNovelDetailsToApi(basePrompt, false);

            // 장르 라디오 체크 갱신
            RadioButton[] radios = {
                    findViewById(R.id.radio_fantasy),
                    findViewById(R.id.radio_romantic),
                    findViewById(R.id.radio_sf),
                    findViewById(R.id.radio_documentary),
                    findViewById(R.id.radio_thriller),
                    findViewById(R.id.radio_comedy),
                    findViewById(R.id.radio_action)
            };

            for (RadioButton radio : radios) {
                radio.setChecked(false);
                radio.setEnabled(false); // 모두 비활성화
            }
            if (selectedGenre != null) {
                switch (selectedGenre) {
                    case "판타지":
                        ((RadioButton) findViewById(R.id.radio_fantasy)).setChecked(true); break;
                    case "로맨틱":
                        ((RadioButton) findViewById(R.id.radio_romantic)).setChecked(true); break;
                    case "SF":
                        ((RadioButton) findViewById(R.id.radio_sf)).setChecked(true); break;
                    case "다큐멘터리":
                        ((RadioButton) findViewById(R.id.radio_documentary)).setChecked(true); break;
                    case "스릴러":
                        ((RadioButton) findViewById(R.id.radio_thriller)).setChecked(true); break;
                    case "코미디":
                        ((RadioButton) findViewById(R.id.radio_comedy)).setChecked(true); break;
                    case "액션":
                        ((RadioButton) findViewById(R.id.radio_action)).setChecked(true); break;
                }

            }

        }
    }

    /**
     * AI 프롬프트 생성 메서드
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
        sb.append("당신은 창의적인 소설가이자 심리 분석 전문가입니다. ");
        sb.append("사용자의 꿈 설명을 바탕으로 상상력이 풍부한 ")
                .append(genre).append(" 소설을 작성하세요. ");
        sb.append("한국어로 응답하고, 후속 질문은 하지 마세요. 약 1000자에서 1500자 사이로 작성하세요.\n\n");
        sb.append("사용자가 제공한 꿈 내용은 다음과 같습니다:\n\"")
                .append(dreamText).append("\"\n\n");
        sb.append("선택된 장르는 '").append(genre).append("' 입니다.\n\n");
        sb.append("사용자의 현재 기분은 '").append(mood).append("' 입니다. ");
        sb.append("가장 뚜렷했던 장면: '").append(vividScene).append("'. ");
        sb.append("꿈 속에 등장했던 물건: '").append(dreamObjects).append("'. ");
        sb.append("엔딩 유형: '").append(ending).append("'.\n");
        if (requiredWords != null && !requiredWords.isEmpty()) {
            sb.append("반드시 포함해야 할 단어나 문구: '").append(requiredWords).append("'.\n");
        }
        sb.append("\n위의 모든 내용을 반영하여, '")
                .append(genre).append("' 장르에 '")
                .append(ending).append("' 엔딩을 가진 약 1000자에서 1500자 사이의 소설을 작성해 주세요.")
                .append(" 결과 값에는 '제목:'과 '내용:'만 포함해주세요.");
        return sb.toString();
    }

    /**
     * Gemini API 호출 (비동기)
     */
    private void sendNovelDetailsToApi(String promptText, boolean isModify) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build();

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
        } catch (JSONException e) { e.printStackTrace(); }

        JSONArray contentsArray = new JSONArray();
        contentsArray.put(contentObj);

        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("contents", contentsArray);
        } catch (JSONException e) { e.printStackTrace(); }

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(requestJson.toString(), mediaType);

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent"
                + "?key=" + BuildConfig.GEMINI_API_KEY;

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    tvAnalysisLabel.setText("제목 생성 실패");
                    tvDreamAnalysis.setText("네트워크 오류: " + e.getMessage());
                    Toast.makeText(DreamBasedNewNovActivity.this, "네트워크 오류: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSubmit.setEnabled(true);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String generatedNovel;
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null
                            ? response.body().string()
                            : "응답 본문이 없습니다.";
                    runOnUiThread(() -> {
                        tvAnalysisLabel.setText("제목 생성 실패");
                        tvDreamAnalysis.setText("서버 오류: " + errorBody);
                        Toast.makeText(DreamBasedNewNovActivity.this, "서버 오류: " + errorBody, Toast.LENGTH_SHORT).show();
                        btnSubmit.setEnabled(true);
                    });
                    return;
                }
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

                final String finalGeneratedNovel = generatedNovel;
                runOnUiThread(() -> {
                    // --- 소설 제목/내용 파싱, 앞뒤 특수문자 제거 및 같은 텍스트 방지
                    String novelTitle = "";
                    String novelContent = finalGeneratedNovel;

                    int titleIdx = finalGeneratedNovel.indexOf("제목:");
                    int contentIdx = finalGeneratedNovel.indexOf("내용:");
                    if (titleIdx != -1 && contentIdx != -1 && titleIdx < contentIdx) {
                        novelTitle = finalGeneratedNovel.substring(titleIdx + 3, contentIdx).trim();
                        novelContent = finalGeneratedNovel.substring(contentIdx + 3).trim();
                    } else if (contentIdx != -1) {
                        novelContent = finalGeneratedNovel.substring(contentIdx + 3).trim();
                    } else if (titleIdx != -1) {
                        novelTitle = finalGeneratedNovel.substring(titleIdx + 3).trim();
                    }

                    // 특수문자, 공백 등 앞뒤 제거
                    novelTitle = novelTitle.replaceAll("^[\\s*/\\*]+", "").replaceAll("[\\s*/\\*]+$", "");
                    novelContent = novelContent.replaceAll("^[\\s*/\\*]+", "").replaceAll("[\\s*/\\*]+$", "");


                    // 제목이 내용에 중복되는 경우, 내용에서 제목 제거
                    if (!novelTitle.isEmpty() && novelContent.startsWith(novelTitle)) {
                        novelContent = novelContent.substring(novelTitle.length()).trim();
                    }
                    // 제목이 내용과 완전히 같으면, 내용은 빈 칸으로 처리
                    if (!novelTitle.isEmpty() && novelTitle.equals(novelContent)) {
                        novelContent = "";
                    }

                    tvAnalysisLabel.setText(novelTitle.isEmpty() ? "(제목 없음)" : novelTitle);
                    tvDreamAnalysis.setText(novelContent.isEmpty() ? "(내용 없음)" : novelContent);

                    lastNovel = finalGeneratedNovel;
                    btnSubmit.setEnabled(true);

                    // 소설 db 자동 저장
                    if (finalGeneratedNovel != null && !finalGeneratedNovel.isEmpty() && !finalGeneratedNovel.contains("없습니다")) {
                        Novel novel = new Novel(
                                novelTitle,
                                novelContent,
                                selectedGenre,
                                moodText,
                                vividScene,
                                dreamObjects,
                                endingText,
                                requiredWords,
                                dreamDate,
                                dreamId
                        );
                        Log.d("Novel", "Novel 생성/저장, dream_id = " + dreamId);
                        novelViewModel.insert(novel);
                    }

                    if (isModify) {
                        etModification.setText("");
                        Toast.makeText(DreamBasedNewNovActivity.this, "소설 수정 완료!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(DreamBasedNewNovActivity.this, "소설 생성 및 저장 완료!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
