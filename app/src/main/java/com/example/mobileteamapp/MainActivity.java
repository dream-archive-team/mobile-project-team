package com.example.mobileteamapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private EditText etDiary;
    private Button btnAnalyze;
    private TextView tvAnalysis;
    private GeminiApiService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etDiary    = findViewById(R.id.etDiary);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        tvAnalysis = findViewById(R.id.tvAnalysis);
        service    = new GeminiApiService();

        btnAnalyze.setOnClickListener(v -> {
            String dreamText = etDiary.getText().toString().trim();
            if (dreamText.isEmpty()) {
                Toast.makeText(this, "꿈 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            tvAnalysis.setText("로딩 중...");
            service.requestGemini(dreamText, new GeminiApiService.Callback() {
                @Override
                public void onSuccess(String result) {
                    // JSON에서 parts[0].text만 추출
                    String analysisText;
                    try {
                        JSONObject root = new JSONObject(result);
                        JSONArray candidates = root.optJSONArray("candidates");
                        if (candidates != null && candidates.length() > 0) {
                            JSONObject content = candidates.getJSONObject(0).optJSONObject("content");
                            JSONArray parts = content != null
                                    ? content.optJSONArray("parts")
                                    : null;
                            if (parts != null && parts.length() > 0) {
                                analysisText = parts.getJSONObject(0).optString("text", "");
                            } else {
                                analysisText = "꿈 해석 결과가 없습니다.";
                            }
                        } else {
                            analysisText = "꿈 해석 결과가 없습니다.";
                        }
                    } catch (Exception e) {
                        analysisText = "파싱 오류: " + e.getMessage();
                    }

                    final String display = analysisText;
                    runOnUiThread(() -> tvAnalysis.setText(display));
                }

                @Override
                public void onFailure(String errorMsg) {
                    runOnUiThread(() -> tvAnalysis.setText("오류: " + errorMsg));
                }
            });
        });
    }
}
