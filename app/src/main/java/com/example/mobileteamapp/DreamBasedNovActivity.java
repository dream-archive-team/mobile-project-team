package com.example.mobileteamapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

public class DreamBasedNovActivity extends AppCompatActivity {

    private static final String TAG = "DreamBasedNovActivity";

    private String dreamContent;
    private String selectedGenre;

    // UI 컴포넌트 선언
    private RadioGroup rgMood;
    private EditText etMostVividScene;
    private EditText etDreamObjects;
    private EditText etRequiredWords;
    private Button btnGenerateNovel;

    // 4번 질문 엔딩 선택 RadioButton들 (LinearLayout으로 감싸져 있어서 직접 관리)
    private RadioButton rbEndingOpen, rbEndingHappy, rbEndingTwist2;
    private RadioButton rbEndingTwist, rbEndingGrowth, rbEndingSad;
    private RadioButton currentSelectedEnding = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dream_based_nov);

        Log.d(TAG, "onCreate: DreamBasedNovActivity 진입");
        Toast.makeText(this, "DreamBasedNovActivity onCreate 호출됨", Toast.LENGTH_SHORT).show();

        // ───────────────────────────────────────────────────────────
        // 1) Intent 로부터 “dream_content”와 “selected_genre” 받아오기
        Intent intent = getIntent();
        dreamContent   = intent.getStringExtra("dream_content");
        selectedGenre  = intent.getStringExtra("selected_genre");
        if (dreamContent == null)  dreamContent = "";
        if (selectedGenre == null) selectedGenre = "";

        // 받아온 값 로그로 확인
        Log.d(TAG, "onCreate: Received dreamContent = " + dreamContent);
        Log.d(TAG, "onCreate: Received selectedGenre = " + selectedGenre);



        // UI 컴포넌트 초기화
        initViews();

        // 엔딩 선택 단일 선택 로직 설정
        setupEndingRadioButtons();

        // 버튼 클릭 리스너 설정
        setupClickListeners();
    }

    private void initViews() {
        // 1번 질문: 기분 선택
        rgMood = findViewById(R.id.rgMood);

        // 2번 질문: 가장 뚜렷했던 장면
        etMostVividScene = findViewById(R.id.etMostVividScene);

        // 3번 질문: 꿈 속 물건
        etDreamObjects = findViewById(R.id.etDreamObjects);

        // 5번 질문: 필수 단어 (선택사항)
        etRequiredWords = findViewById(R.id.etRequiredWords);

        // 소설 생성 버튼
        btnGenerateNovel = findViewById(R.id.btnGenerateNovel);

        // 4번 질문: 엔딩 선택 RadioButton들
        rbEndingOpen = findViewById(R.id.rbEndingOpen);
        rbEndingHappy = findViewById(R.id.rbEndingHappy);
        rbEndingTwist2 = findViewById(R.id.rbEndingTwist2);
        rbEndingTwist = findViewById(R.id.rbEndingTwist);
        rbEndingGrowth = findViewById(R.id.rbEndingGrowth);
        rbEndingSad = findViewById(R.id.rbEndingSad);
    }


    //4번의 결말 선택 로직
    private void setupEndingRadioButtons() {
        // 클릭 리스너 방식으로 확실한 단일 선택 보장
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 모든 엔딩 버튼 해제
                clearAllEndingButtons();

                // 클릭된 버튼만 선택
                RadioButton clickedButton = (RadioButton) v;
                clickedButton.setChecked(true);
                currentSelectedEnding = clickedButton;
            }
        };

        rbEndingOpen.setOnClickListener(clickListener);
        rbEndingHappy.setOnClickListener(clickListener);
        rbEndingTwist2.setOnClickListener(clickListener);
        rbEndingTwist.setOnClickListener(clickListener);
        rbEndingGrowth.setOnClickListener(clickListener);
        rbEndingSad.setOnClickListener(clickListener);
    }

    private void clearAllEndingButtons() {
        rbEndingOpen.setChecked(false);
        rbEndingHappy.setChecked(false);
        rbEndingTwist2.setChecked(false);
        rbEndingTwist.setChecked(false);
        rbEndingGrowth.setChecked(false);
        rbEndingSad.setChecked(false);
    }


    //질문에 대한 답변이 있는지 확인
    private void setupClickListeners() {
        btnGenerateNovel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼 클릭 시 바로 로그 찍기
                Log.d(TAG, "btnGenerateNovel clicked");
                if (validateAllInputs()) {
                    generateNovel();
                }
            }
        });
    }

    //질문에 대한 답변이 존재 여부 판별
    private boolean validateAllInputs() {
        // 1. 기분 선택 확인
        if (rgMood.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "기분을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 2. 가장 뚜렷했던 장면 입력 확인
        String vividScene = etMostVividScene.getText().toString().trim();
        if (vividScene.isEmpty()) {
            Toast.makeText(this, "가장 뚜렷했던 장면을 입력해주세요.", Toast.LENGTH_SHORT).show();
            etMostVividScene.requestFocus();
            return false;
        }

        // 3. 꿈 속 물건 입력 확인
        String dreamObjects = etDreamObjects.getText().toString().trim();
        if (dreamObjects.isEmpty()) {
            Toast.makeText(this, "꿈 속에 나온 물건을 입력해주세요.", Toast.LENGTH_SHORT).show();
            etDreamObjects.requestFocus();
            return false;
        }

        // 4. 엔딩 선택 확인 (커스텀 관리)
        if (currentSelectedEnding == null) {
            Toast.makeText(this, "소설 엔딩을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 5. 필수 단어는 선택사항

        return true;
    }

    private void generateNovel() {
        // 사용자 입력 데이터 수집
        String selectedMood = getSelectedMood();
        String vividScene = etMostVividScene.getText().toString().trim();
        String dreamObjects = etDreamObjects.getText().toString().trim();
        String selectedEnding = getSelectedEnding();
        String requiredWords = etRequiredWords.getText().toString().trim();

        // 입력된 모든 값을 로그로 확인
        Log.d(TAG, "generateNovel: dreamContent   = " + dreamContent);
        Log.d(TAG, "generateNovel: selectedGenre  = " + selectedGenre);
        Log.d(TAG, "generateNovel: selectedMood   = " + selectedMood);
        Log.d(TAG, "generateNovel: vividScene     = " + vividScene);
        Log.d(TAG, "generateNovel: dreamObjects   = " + dreamObjects);
        Log.d(TAG, "generateNovel: selectedEnding = " + selectedEnding);
        Log.d(TAG, "generateNovel: requiredWords  = " + requiredWords);



        // 다음 액티비티에 데이터 전달

        Intent intent = new Intent(DreamBasedNovActivity.this, NovelDetailActivity.class);
        intent.putExtra("dream_content",  dreamContent);
        intent.putExtra("selected_genre", selectedGenre);
        intent.putExtra("mood", selectedMood);
        intent.putExtra("vivid_scene", vividScene);
        intent.putExtra("dream_objects", dreamObjects);
        intent.putExtra("ending", selectedEnding);
        intent.putExtra("required_words", requiredWords);

        startActivity(intent);
    }

    private String getSelectedMood() {
        int selectedId = rgMood.getCheckedRadioButtonId();

        if (selectedId == R.id.rbMoodJoy) {
            return "기쁨";
        } else if (selectedId == R.id.rbMoodSad) {
            return "슬픔";
        } else if (selectedId == R.id.rbMoodAnger) {
            return "분노";
        } else if (selectedId == R.id.rbMoodSurprise) {
            return "놀람";
        } else if (selectedId == R.id.rbMoodAnxiety) {
            return "불안";
        }

        return "";
    }

    private String getSelectedEnding() {
        if (currentSelectedEnding == null) {
            return "";
        }

        int selectedId = currentSelectedEnding.getId();

        if (selectedId == R.id.rbEndingOpen) {
            return "열린결말";
        } else if (selectedId == R.id.rbEndingHappy) {
            return "해피엔딩";
        } else if (selectedId == R.id.rbEndingTwist2) {
            return "반전 결말";
        } else if (selectedId == R.id.rbEndingTwist) {
            return "반전정책 결말";
        } else if (selectedId == R.id.rbEndingGrowth) {
            return "성장형 결말";
        } else if (selectedId == R.id.rbEndingSad) {
            return "새드엔딩";
        }

        return "";
    }

}
