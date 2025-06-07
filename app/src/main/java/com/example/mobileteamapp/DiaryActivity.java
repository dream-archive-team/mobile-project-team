package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import java.util.concurrent.Executors;

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
        Button btnGenerateStory = findViewById(R.id.btnGenerateStory);
        tvDreamAnalysis = findViewById(R.id.tvDreamAnalysis);

        dreamViewModel = new ViewModelProvider(this).get(DreamViewModel.class);
        service = new GeminiApiService();

        // DB 전체 확인 로그
        dreamViewModel.getAllDreams().observe(this, dreams -> {
            for (Dream dream : dreams) {
                Log.d("DreamCheck", "ID: " + dream.getDream_id()
                        + ", 날짜: " + dream.getDream_date()
                        + ", 내용: " + dream.getDream_content()
                        + ", 해몽: " + dream.getInterpretation()
                        + ", 사용자: " + dream.getMember_id());
            }
        });

        // 분석 버튼 클릭 (꿈 저장)
        btnAnalyze.setOnClickListener(v -> {
            String dreamContent = etDreamInput.getText().toString().trim();
            String memberId = getCurrentUserId();
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            if (dreamContent.isEmpty()) {
                Toast.makeText(DiaryActivity.this, "꿈 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- [여기부터 비동기 중복 검사] ---
            Executors.newSingleThreadExecutor().execute(() -> {
                int alreadyExist = dreamViewModel.countDreamsByMemberAndDate(memberId, today);

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (alreadyExist > 0) {
                        Toast.makeText(DiaryActivity.this, "이미 오늘의 꿈 일기를 작성하셨습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 저장 로직
                    saveDreamAndRequestAnalysis(dreamContent, memberId, today);
                });
            });
        });

        // 소설 생성 버튼
        btnGenerateStory.setOnClickListener(v -> {
            String analysisResult = tvDreamAnalysis.getText().toString().trim();

            if (analysisResult.isEmpty()
                    || analysisResult.equals("꿈 해석 결과가 없습니다.")
                    || analysisResult.equals("로딩 중...")
                    || analysisResult.startsWith("파싱 오류")) {
                Toast.makeText(DiaryActivity.this, "해몽 결과가 있을 때만 소설을 생성할 수 있습니다.", Toast.LENGTH_SHORT).show();
            } else {
                String dreamContent = etDreamInput.getText().toString().trim();
                Log.d("DiaryActivity", "Sending dreamContent: " + dreamContent);

                Intent intent = new Intent(DiaryActivity.this, SelectNovGenreActivity.class);
                intent.putExtra("dream_content", dreamContent);
                startActivity(intent);
            }
        });
    }

    // 꿈 저장 및 Gemini 해몽 요청 로직 분리
    private void saveDreamAndRequestAnalysis(String dreamContent, String memberId, String today) {
        String dreamId = UUID.randomUUID().toString();

        Dream dream = new Dream();
        dream.setDream_id(dreamId);
        dream.setMember_id(memberId);
        dream.setDream_date(today);
        dream.setDream_content(dreamContent);
        dream.setInterpretation(""); // 초기 해몽 빈 값

        dreamViewModel.insert(dream);

        Toast.makeText(DiaryActivity.this, "꿈이 저장되었습니다.", Toast.LENGTH_SHORT).show();
        tvDreamAnalysis.setText("로딩 중...");

        service.requestGemini(dreamContent, new GeminiApiService.Callback() {
            @Override
            public void onSuccess(String result) {
                String fullText;
                try {
                    JSONObject root = new JSONObject(result);
                    JSONArray candidates = root.optJSONArray("candidates");
                    if (candidates != null && candidates.length() > 0) {
                        JSONObject contentObj = candidates.getJSONObject(0).optJSONObject("content");
                        JSONArray parts = (contentObj != null) ? contentObj.optJSONArray("parts") : null;
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

                String formatted = fullText
                        .replaceAll("([.!?])\\s+", "$1\n\n")
                        .replaceAll("\\n{3,}", "\n\n");

                runOnUiThread(() -> {
                    tvDreamAnalysis.setText(formatted);

                    // Gemini 결과 DB에 저장
                    dream.setInterpretation(formatted);
                    dreamViewModel.update(dream);

                    // 저장 확인 로그
                    Log.d("DreamUpdate", "Updated Dream 해몽 저장됨 - ID: " + dream.getDream_id()
                            + ", 해몽: " + formatted);
                });
            }

            @Override
            public void onFailure(String errorMsg) {
                runOnUiThread(() -> tvDreamAnalysis.setText("오류: " + errorMsg));
            }
        });
    }

    private String getCurrentUserId() {
        return getIntent().getStringExtra("member_id");
    }
}
