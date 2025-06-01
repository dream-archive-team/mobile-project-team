package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileteamapp.entity.Dream;
import com.example.mobileteamapp.viewModel.DreamViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

// 꿈 일기 작성 기능
public class DiaryActivity extends AppCompatActivity {

    private EditText etDreamInput;
    private DreamViewModel dreamViewModel;
    private TextView tvDreamAnalysis;
    private GeminiApiService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary);
        Log.d("DiaryActivity", "onCreate called");
        etDreamInput = findViewById(R.id.etDreamInput);
        Button btnAnalyze = findViewById(R.id.btnAnalyze);
        Button  btnGenerateStory= findViewById(R.id.btnGenerateStory);
        tvDreamAnalysis  = findViewById(R.id.tvDreamAnalysis);

        dreamViewModel = new ViewModelProvider(this).get(DreamViewModel.class);
        service = new GeminiApiService();

        // 저장된 모든 꿈 데이터를 로그로 출력(DB 확인용)
        dreamViewModel.getAllDreams().observe(this, dreams -> {
            for (Dream dream : dreams) {
                Log.d("DreamCheck", "ID: " + dream.getDream_id()
                        + ", 날짜: " + dream.getDream_date()
                        + ", 내용: " + dream.getDream_content()
                        + ", 해몽: " + dream.getInterpretation()
                        + ", 사용자: " + dream.getMember_id());
            }
        });

        // '분석' 버튼 클릭 시 DB에 저장
        btnAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dreamContent = etDreamInput.getText().toString().trim();

                if (!dreamContent.isEmpty()) {
                    // 1. UUID로 꿈 ID 생성
                    String dreamId = UUID.randomUUID().toString();

                    // 2. 로그인된 사용자 ID 받아오기 (예: sharedPreferences, intent 등)
                    String memberId = getCurrentUserId(); // 이 부분은 상황에 맞게 구현 필요

                    // 3. 오늘 날짜 저장
                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    // 4. Dream 객체 생성 및 저장
                    Dream dream = new Dream();
                    dream.setDream_id(dreamId);
                    dream.setMember_id(memberId);
                    dream.setDream_date(today);
                    dream.setDream_content(dreamContent);
                    dream.setInterpretation(""); // 해몽 결과는 아직 없음

                    dreamViewModel.insert(dream);

                    Toast.makeText(DiaryActivity.this, "꿈이 저장되었습니다.", Toast.LENGTH_SHORT).show();

                    tvDreamAnalysis.setText("로딩 중...");  // “로딩 중” 메시지

                    service.requestGemini(dreamContent, new GeminiApiService.Callback() {
                        @Override
                        public void onSuccess(String result) {
                            // JSON 파싱: candidates[0].content.parts[0].text
                            String fullText;
                            try {
                                JSONObject root      = new JSONObject(result);
                                JSONArray candidates = root.optJSONArray("candidates");
                                if (candidates != null && candidates.length() > 0) {
                                    JSONObject contentObj = candidates.getJSONObject(0)
                                            .optJSONObject("content");
                                    JSONArray parts       = (contentObj != null)
                                            ? contentObj.optJSONArray("parts")
                                            : null;
                                    if (parts != null && parts.length() > 0) {
                                        fullText = parts.getJSONObject(0).optString("text", "");
                                    } else {
                                        fullText = "꿈 해석 결과가 없습니다.";
                                    }
                                } else {
                                    fullText = "꿈 해석 결과가 없습니다.";
                                }
                            } catch (Exception e) {
                                fullText = "파싱 오류: " + e.getMessage();
                            }

                            // 줄바꿈 포맷팅
                            String formatted = fullText
                                    .replaceAll("([.!?])\\s+", "$1\n\n")
                                    .replaceAll("\\n{3,}", "\n\n");

                            // UI 스레드에서 결과 TextView에 반영
                            runOnUiThread(() -> tvDreamAnalysis.setText(formatted));
                        }

                        @Override
                        public void onFailure(String errorMsg) {
                            runOnUiThread(() ->
                                    tvDreamAnalysis.setText("오류: " + errorMsg)
                            );
                        }
                    });

                } else {
                    Toast.makeText(DiaryActivity.this, "꿈 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

        });

        //소설 생성 버튼 반응
        btnGenerateStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String analysisResult = tvDreamAnalysis.getText().toString().trim();



                // 해몽 결과가 없거나, "꿈 해석 결과가 없습니다." 또는 "로딩 중..."이면 이동 금지
                if (analysisResult.isEmpty() ||
                        analysisResult.equals("꿈 해석 결과가 없습니다.") ||
                        analysisResult.equals("로딩 중...") ||
                        analysisResult.startsWith("파싱 오류")) {
                    Toast.makeText(DiaryActivity.this, "해몽 결과가 있을 때만 소설을 생성할 수 있습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    String dreamContent = etDreamInput.getText().toString().trim();

                    //Logcat에 꿈 내용 찍기
                    Log.d("DiaryActivity", "Sending dreamContent: " + dreamContent);

                    Intent intent = new Intent(DiaryActivity.this, SelectNovGenreActivity.class);

                    // "dream_content" 키로 꿈 내용(dreamContent)을 담아 보냅니다.
                    intent.putExtra("dream_content", dreamContent);

                    startActivity(intent);
                }
            }
        });
    }

    // 현재 로그인한 사용자 ID를 가져오는 메서드
    private String getCurrentUserId() {
        return getIntent().getStringExtra("member_id");
    }



}