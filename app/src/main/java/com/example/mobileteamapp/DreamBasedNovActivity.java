package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 사용자가 네 가지 질문(기분, 뚜렷했던 장면, 꿈 속 물건, 엔딩 선택)을 답변하면
 * 모든 데이터를 Intent에 담아 DreamBasedNewNov로 넘기는 역할만 수행합니다.
 */
public class DreamBasedNovActivity extends AppCompatActivity {
    private static final String TAG = "DreamBasedNovActivity";

    // (A) 첫 번째 화면(예: DiaryActivity → SelectNovGenreActivity)에서 넘겨받은 값들
    private String dreamContent;   // 사용자가 입력한 꿈 내용
    private String selectedGenre;  // 사용자가 선택한 소설 장르

    // UI 컴포넌트
    private RadioGroup rgMood;
    private EditText etMostVividScene, etDreamObjects, etRequiredWords;
    private RadioGroup rgEnding;
    private Button btnGenerateNovel;

    private RadioButton rbEndingOpen;
    private RadioButton rbEndingHappy;
    private RadioButton rbEndingTwist2;
    private RadioButton rbEndingTwist;
    private RadioButton rbEndingGrowth;
    private RadioButton rbEndingSad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dream_based_nov);

        // ───────────────────────────────────────────────────────────
        // 1) Intent로부터 “dream_content”와 “selected_genre” 받아오기
        Intent intent = getIntent();
        dreamContent  = intent.getStringExtra("dream_content");
        selectedGenre = intent.getStringExtra("selected_genre");
        if (dreamContent == null)  dreamContent  = "";
        if (selectedGenre == null) selectedGenre = "";

        Log.d(TAG, "onCreate: Received dreamContent = " + dreamContent);
        Log.d(TAG, "onCreate: Received selectedGenre = " + selectedGenre);

        // ───────────────────────────────────────────────────────────
        // 2) 뷰 초기화
        rgMood           = findViewById(R.id.rgMood);
        etMostVividScene = findViewById(R.id.etMostVividScene);
        etDreamObjects   = findViewById(R.id.etDreamObjects);
        rgEnding         = findViewById(R.id.rgEnding);
        etRequiredWords  = findViewById(R.id.etRequiredWords);
        btnGenerateNovel = findViewById(R.id.btnGenerateNovel);

        // ───────────────────────────────────────────────────────────

        rbEndingOpen   = findViewById(R.id.rbEndingOpen);
        rbEndingHappy  = findViewById(R.id.rbEndingHappy);
        rbEndingTwist2 = findViewById(R.id.rbEndingTwist2);
        rbEndingTwist  = findViewById(R.id.rbEndingTwist);
        rbEndingGrowth = findViewById(R.id.rbEndingGrowth);
        rbEndingSad    = findViewById(R.id.rbEndingSad);

        // 3) “소설 생성하기” 버튼 클릭 리스너
        btnGenerateNovel.setOnClickListener(v -> {
            Log.d(TAG, "btnGenerateNovel clicked");
            if (!validateAllInputs()) {
                return;  // 입력 검증 실패 시 리턴
            }
            sendToNovelDetail();
        });
    }

    /**
     * 네 가지 입력(기분, 뚜렷했던 장면, 꿈 속 물건, 엔딩)을 모두 체크합니다.
     * 하나라도 비어 있으면 Toast를 띄우고 false를 반환합니다.
     */
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

        // 4) 엔딩 선택 확인
        if (!rbEndingOpen.isChecked()
                && !rbEndingHappy.isChecked()
                && !rbEndingTwist2.isChecked()
                && !rbEndingTwist.isChecked()
                && !rbEndingGrowth.isChecked()
                && !rbEndingSad.isChecked()) {

            Toast.makeText(this, "4번 질문: 엔딩 유형을 하나 선택해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 5) 필수 단어(etRequiredWords)는 선택사항이므로 검증하지 않음

        return true;  // 네 가지 모두 채워진 상태
    }

    /**
     * 모든 입력값을 Intent에 담아서 DreamBasedNewNov로 보냅니다.
     * 이전에 얻어 놓은 dreamContent, selectedGenre와
     * 여기서 새로 수집한 mood, vividScene, dreamObjects, ending, requiredWords를 함께 실어 보냄.
     */
    private void sendToNovelDetail() {
        // (D) 사용자 입력 데이터 수집
        RadioButton rbMood = findViewById(rgMood.getCheckedRadioButtonId());
        String moodText = rbMood.getText().toString();

        String vividScene = etMostVividScene.getText().toString().trim();
        String dreamObjects = etDreamObjects.getText().toString().trim();

        String endingText;
        if (rbEndingOpen.isChecked()) {
            endingText = rbEndingOpen.getText().toString();
        } else if (rbEndingHappy.isChecked()) {
            endingText = rbEndingHappy.getText().toString();
        } else if (rbEndingTwist2.isChecked()) {
            endingText = rbEndingTwist2.getText().toString();
        } else if (rbEndingTwist.isChecked()) {
            endingText = rbEndingTwist.getText().toString();
        } else if (rbEndingGrowth.isChecked()) {
            endingText = rbEndingGrowth.getText().toString();
        } else {  // rbEndingSad.isChecked()
            endingText = rbEndingSad.getText().toString();
        }


        String requiredWords = etRequiredWords.getText().toString().trim();

        // (E) Intent 생성 → DreamBasedNewNov로 데이터 전달
        Intent intent = new Intent(DreamBasedNovActivity.this, DreamBasedNewNov.class);
        intent.putExtra("dream_content",  dreamContent);
        intent.putExtra("selected_genre", selectedGenre);
        intent.putExtra("mood",           moodText);
        intent.putExtra("vivid_scene",    vividScene);
        intent.putExtra("dream_objects",  dreamObjects);
        intent.putExtra("ending",         endingText);
        intent.putExtra("required_words", requiredWords);
        startActivity(intent);
    }
}
