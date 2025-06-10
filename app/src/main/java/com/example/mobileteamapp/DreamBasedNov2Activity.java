package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// 질문 화면 (2)
public class DreamBasedNov2Activity extends AppCompatActivity {

    private String selectedEnding = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_based_nov2);

        // 이전 액티비티에서 값 받기
        Intent prevIntent = getIntent();
        String dreamContent = prevIntent.getStringExtra("dream_content");   // 꿈
        String dreamInterpretation = prevIntent.getStringExtra("dream_interpretation"); // 해몽
        String selectedGenre = prevIntent.getStringExtra("selected_genre");
        String selectedMood = prevIntent.getStringExtra("selected_mood");
        String vividScene = prevIntent.getStringExtra("vivid_scene");
        String dreamObjects = prevIntent.getStringExtra("dream_objects");
        String dreamDate = prevIntent.getStringExtra("dream_date"); // 날짜
        String dreamId = prevIntent.getStringExtra("dream_id");
        String nickname = prevIntent.getStringExtra("nickname");
        String kakaoId = prevIntent.getStringExtra("kakaoId");

        //Log.d("NovActivity2 : ", dreamContent);  // ⭐ 로그 확인
        // UI 요소 연결
        RadioGroup rgEnding = findViewById(R.id.rgEnding);
        EditText etRequiredWords = findViewById(R.id.etRequiredWords);

        // 1. 엔딩 라디오 체크 버튼
        rgEnding.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadio = findViewById(checkedId);
            if (checkedRadio != null) {
                selectedEnding = checkedRadio.getText().toString();
            }
        });

        // 2. 소설 생성 버튼
        findViewById(R.id.btnGenerateNovel).setOnClickListener(v -> {
            String requiredWords = etRequiredWords.getText().toString().trim();

            if (selectedEnding.isEmpty()) {
                Toast.makeText(this, "엔딩 유형을 선택하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 소설 생성 화면으로 이동 (정보만 전달)
            Intent intent = new Intent(this, DreamBasedNewNovActivity.class);
            intent.putExtra("dream_content", dreamContent);
            intent.putExtra("dream_interpretation", dreamInterpretation);
            intent.putExtra("selected_genre", selectedGenre);
            intent.putExtra("selected_mood", selectedMood);
            intent.putExtra("vivid_scene", vividScene);
            intent.putExtra("dream_objects", dreamObjects);
            intent.putExtra("ending", selectedEnding);
            intent.putExtra("required_words", requiredWords);
            intent.putExtra("dream_date", dreamDate);
            intent.putExtra("dream_id", dreamId);
            intent.putExtra("nickname", nickname);
            intent.putExtra("kakaoId", kakaoId);
            startActivity(intent);
            finish();
        });
    }
}
