package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileteamapp.entity.Emotion;
import com.example.mobileteamapp.viewModel.EmotionViewModel;

public class DreamBasedNovActivity extends AppCompatActivity {
    private static final String TAG = "DreamBasedNovActivity";

    // (A) 첫 번째 화면(예: DiaryActivity → SelectNovGenreActivity)에서 넘겨받은 값들
    private String dreamContent;   // 사용자가 입력한 꿈 내용
    private String selectedGenre;  // 사용자가 선택한 소설 장르

    // UI 컴포넌트
    private RadioGroup rgMood;
    private EditText etMostVividScene, etDreamObjects;

    private Button btnGenerateNovel;

    private EmotionViewModel emotionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dream_based_nov);

        // Intent로부터 “dream_content”와 “selected_genre” 받아오기
        Intent intent = getIntent();
        dreamContent  = intent.getStringExtra("dream_content");
        selectedGenre = intent.getStringExtra("selected_genre");
        if (dreamContent == null)  dreamContent  = "";
        if (selectedGenre == null) selectedGenre = "";

        Log.d(TAG, "onCreate: Received dreamContent = " + dreamContent);
        Log.d(TAG, "onCreate: Received selectedGenre = " + selectedGenre);

        // 뷰 초기화
        rgMood           = findViewById(R.id.rgMood);
        etMostVividScene = findViewById(R.id.etMostVividScene);
        etDreamObjects   = findViewById(R.id.etDreamObjects);
        btnGenerateNovel = findViewById(R.id.btnGenerateNovel);

        // ViewModel 초기화
        emotionViewModel = new ViewModelProvider(this).get(EmotionViewModel.class);

        // 감정 DB 전체 조회 및 로그 출력(DB조회용)
        emotionViewModel.getAllEmotions().observe(this, emotions -> {
            for (Emotion e : emotions) {
                Log.d("EmotionCheck", "ID: " + e.getEmotion_id() + ", 이름: " + e.getEmotion_name());
            }
        });

        // “소설 생성하기” 버튼 클릭 리스너
        btnGenerateNovel.setOnClickListener(v -> {
            Log.d(TAG, "btnGenerateNovel clicked");
            if (!validateAllInputs()) {
                return;  // 입력 검증 실패 시 리턴
            }

            saveEmotionToDb();  // 감정 저장 메서드 호출

            sendToNovelDetail();
        });
    }

    private boolean validateAllInputs() {
        // 1) 기분 선택 확인
        int selectedMoodId = rgMood.getCheckedRadioButtonId();
        if (selectedMoodId == -1) {
            Toast.makeText(this, "1번 질문: 기분을 하나 선택해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 2) 가장 뚜렷했던 장면 입력 확인
        String vividScene = etMostVividScene.getText().toString().trim();
        if (vividScene.isEmpty()) {
            Toast.makeText(this, "2번 질문: 가장 뚜렷했던 장면을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 3) 꿈 속 물건 입력 확인
        String dreamObjects = etDreamObjects.getText().toString().trim();
        if (dreamObjects.isEmpty()) {
            Toast.makeText(this, "3번 질문: 꿈 속 물건을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void sendToNovelDetail() {
        // 사용자 입력 데이터 수집
        RadioButton rbMood = findViewById(rgMood.getCheckedRadioButtonId());
        String moodText = rbMood.getText().toString();

        String vividScene = etMostVividScene.getText().toString().trim();
        String dreamObjects = etDreamObjects.getText().toString().trim();

        Intent intent = new Intent(DreamBasedNovActivity.this, DreamBasedNov2Activity.class);
        intent.putExtra("dream_content",  dreamContent);
        intent.putExtra("selected_genre", selectedGenre);
        intent.putExtra("mood",           moodText);
        intent.putExtra("vivid_scene",    vividScene);
        intent.putExtra("dream_objects",  dreamObjects);
        startActivity(intent);
    }

    private int getEmotionIdFromName(String emotionName) {
        switch (emotionName) {
            case "기쁨": return 1;
            case "슬픔": return 2;
            case "분노": return 3;
            case "놀람": return 4;
            case "불안": return 5;
            default: return 0;
        }
    }

    private void saveEmotionToDb() {
        RadioButton rbMood = findViewById(rgMood.getCheckedRadioButtonId());
        String selectedEmotionName = rbMood.getText().toString();

        int emotionId = getEmotionIdFromName(selectedEmotionName);

        Emotion emotion = new Emotion();
        emotion.setEmotion_id(emotionId);
        emotion.setEmotion_name(selectedEmotionName);

        emotionViewModel.insert(emotion);

        Log.d(TAG, "saveEmotionToDb: 감정 저장됨 → ID: " + emotionId + ", 이름: " + selectedEmotionName);
    }
}
