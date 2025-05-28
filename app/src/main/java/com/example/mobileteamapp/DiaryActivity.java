package com.example.mobileteamapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class DiaryActivity extends AppCompatActivity {
    private EditText etDreamInput;
    private TextView tvDreamAnalysis;
    private Button btnAnalyze;

    private DreamViewModel dreamViewModel;
    private GeminiApiService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary);

        // 뷰 바인딩
        etDreamInput     = findViewById(R.id.etDreamInput);
        tvDreamAnalysis  = findViewById(R.id.tvDreamAnalysis);
        btnAnalyze       = findViewById(R.id.btnAnalyze);

        // ViewModel & Service 초기화
        dreamViewModel = new ViewModelProvider(this)
                .get(DreamViewModel.class);
        service = new GeminiApiService();

        // 저장된 모든 꿈 로그 출력 (선택 사항)
        dreamViewModel.getAllDreams().observe(this, dreams -> {
            for (Dream dream : dreams) {
                Log.d("DreamCheck",
                        "ID: " + dream.getDream_id()
                                + ", 날짜: " + dream.getDream_date()
                                + ", 내용: " + dream.getDream_content()
                                + ", 해몽: " + dream.getInterpretation()
                                + ", 사용자: " + dream.getMember_id());
            }
        });

        // 분석 버튼 클릭 시
        btnAnalyze.setOnClickListener(v -> {
            String dreamContent = etDreamInput.getText().toString().trim();
            if (dreamContent.isEmpty()) {
                Toast.makeText(this, "꿈 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1) 꿈 저장
            String dreamId = UUID.randomUUID().toString();
            String memberId = getIntent().getStringExtra("member_id");
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(new Date());

            Dream dream = new Dream();
            dream.setDream_id(dreamId);
            dream.setMember_id(memberId);
            dream.setDream_date(today);
            dream.setDream_content(dreamContent);
            dream.setInterpretation("");  // 아직 없음
            dreamViewModel.insert(dream);
            Toast.makeText(this, "꿈이 저장되었습니다.", Toast.LENGTH_SHORT).show();

            // 2) Gemini API로 꿈 해석 요청
            tvDreamAnalysis.setText("로딩 중...");
            service.requestGemini(dreamContent, new GeminiApiService.Callback() {
                @Override
                public void onSuccess(String result) {
                    // JSON 파싱: parts[0].text
                    String fullText;
                    try {
                        JSONObject root = new JSONObject(result);
                        JSONArray candidates = root.optJSONArray("candidates");
                        if (candidates != null && candidates.length() > 0) {
                            JSONObject content = candidates.getJSONObject(0)
                                    .optJSONObject("content");
                            JSONArray parts = content != null
                                    ? content.optJSONArray("parts")
                                    : null;
                            fullText = (parts != null && parts.length() > 0)
                                    ? parts.getJSONObject(0).optString("text", "")
                                    : "꿈 해석 결과가 없습니다.";
                        } else {
                            fullText = "꿈 해석 결과가 없습니다.";
                        }
                    } catch (Exception e) {
                        fullText = "파싱 오류: " + e.getMessage();
                    }
                    /*
                    // 1000자 제한 + 줄바꿈 삽입
                    StringBuilder sb = new StringBuilder();
                    int nonWsCount = 0;
                    for (char c : fullText.toCharArray()) {
                        sb.append(c);
                        if (!Character.isWhitespace(c)) {
                            nonWsCount++;
                            if (nonWsCount >= 1000) break;
                        }
                    }
                    String trimmed = sb.toString();

                    String formatted = trimmed
                            .replaceAll("([.!?])\\s+", "$1\n\n")
                            .replaceAll("\\n{3,}", "\n\n");

                     */
                    String formatted = fullText
                            .replaceAll("([.!?])\\s+", "$1\n\n")
                            .replaceAll("\\n{3,}", "\n\n");
                    runOnUiThread(() -> tvDreamAnalysis.setText(formatted));
                }

                @Override
                public void onFailure(String errorMsg) {
                    runOnUiThread(() ->
                            tvDreamAnalysis.setText("오류: " + errorMsg));
                }
            });
        });
    }

    /** intent로 전달된 member_id 가져오기 */
    private String getCurrentUserId() {
        return getIntent().getStringExtra("member_id");
    }
}
