package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileteamapp.entity.Dream;
import com.example.mobileteamapp.viewmodel.DreamViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// 꿈 작성 및 분석 화면
public class DiaryActivity extends AppCompatActivity {

    private DreamViewModel dreamViewModel;
    private GeminiApiService geminiApiService = new GeminiApiService();
    private String latestInterpretation = ""; // 해몽 내용

    private String selectedDate; // 선택한 날짜

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        // UI요소 연결
        EditText etDreamInput = findViewById(R.id.etDreamInput);
        TextView tvDreamAnalysis = findViewById(R.id.tvDreamAnalysis);


        // ViewModel 연결
        dreamViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(DreamViewModel.class);

        // HomeActivity에서 날짜 받기
        selectedDate = getIntent().getStringExtra("selected_date");
        if (selectedDate == null || selectedDate.trim().isEmpty()) {
            // 만약 인텐트로 날짜가 안 오면 오늘 날짜로 대체
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Toast.makeText(this, "선택된 날짜가 없어 오늘 날짜로 대체되었습니다.", Toast.LENGTH_SHORT).show();  // ⭐ 날짜 전달 확인용
        }

        // 1. 꿈 내용 입력 -> 꿈 분석, db 저장
        findViewById(R.id.btnAnalyze).setOnClickListener(v -> {
            String dreamContent = etDreamInput.getText().toString().trim();
            if (dreamContent.isEmpty()) {
                Toast.makeText(this, "꿈 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            final String dateForDream = selectedDate; // 반드시 선택된 날짜

            // 이미 해당 날짜에 꿈이 있으면 update, 없으면 insert
            new Thread(() -> {
                Dream existing = dreamViewModel.getDreamByDate(dateForDream);
                // 꿈이 없으면 insert
                if (existing == null) {
                    Dream newDream = new Dream(dateForDream, dreamContent, "", 0); // 해몽은 추후 update

                    // ⭐ 로그 확인
                    Log.d("Dream", "[INSERT] dream_id(before) = " + newDream.getDreamId() + " date=" + dateForDream);

                    dreamViewModel.insert(newDream);
                }
                // 꿈이 있으면 작성 못함
                else {
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), "이미 해당 날짜에 등록된 꿈이 있습니다.", Toast.LENGTH_SHORT).show()
                    );
                    // ⭐ 로그 확인
                    Log.d("Dream", "[BLOCKED] 이미 해당 날짜에 꿈이 존재합니다. dream_id = " + existing.getDreamId());
                }
            }).start();

            tvDreamAnalysis.setText("꿈을 분석중입니다...");



            geminiApiService.requestGemini(dreamContent, new GeminiApiService.Callback() {
                @Override
                public void onSuccess(String result) {
                    String interpretation = parseGeminiResult(result);

                    new Thread(() -> {
                        Dream dream = dreamViewModel.getDreamByDate(dateForDream); // 날짜로 직접 조회

                        if (dream != null) {
                            dream.interpretation = interpretation;

                            Log.d("Dream", "[ANALYSIS UPDATE] dream_id = " + dream.getDreamId() + " date=" + dateForDream);
                            dreamViewModel.update(dream);

                            runOnUiThread(() -> {
                                tvDreamAnalysis.setText(interpretation);
                                latestInterpretation = interpretation;
                                Toast.makeText(DiaryActivity.this, "꿈 분석 완료!", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(DiaryActivity.this, "꿈이 존재하지 않아 해몽을 저장할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }).start();
                }
                @Override
                public void onFailure(String errorMsg) {
                    runOnUiThread(() -> {
                        tvDreamAnalysis.setText("꿈 분석 실패: " + errorMsg);
                    });
                }
            });
        });

        // 2. 소설 생성 버튼
        findViewById(R.id.btnGenerateStory).setOnClickListener(v -> {
            String dreamContent = etDreamInput.getText().toString().trim();
            if (dreamContent.isEmpty()) {
                Toast.makeText(this, "꿈 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (latestInterpretation == null || latestInterpretation.trim().isEmpty()) {
                Toast.makeText(this, "해몽 결과가 있을 때만 소설을 생성할 수 있습니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, SelectNovGenreActivity.class);
            intent.putExtra("dream_content", dreamContent);
            intent.putExtra("dream_content", dreamContent);
            intent.putExtra("dream_interpretation", latestInterpretation);
            intent.putExtra("dream_date", selectedDate);
            intent.putExtra("from", "diary");
            startActivity(intent);
        });

    }

    private String parseGeminiResult(String resultJson) {
        try {
            JSONObject jsonObject = new JSONObject(resultJson);
            JSONArray candidates = jsonObject.getJSONArray("candidates");
            JSONObject first = candidates.getJSONObject(0);
            JSONObject content = first.getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");
            JSONObject part = parts.getJSONObject(0);
            return part.getString("text");
        } catch (Exception e) {
            Log.e("GeminiAPI", "JSON 파싱 오류", e);
            return "해몽 결과 파싱 오류";
        }
    }
}
