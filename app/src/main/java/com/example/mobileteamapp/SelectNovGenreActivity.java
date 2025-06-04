package com.example.mobileteamapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

//소설 장르 선택 화면
public class SelectNovGenreActivity extends AppCompatActivity {
    private static final String TAG = "SelectNovGenreActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_nov_genre);  // XML 파일 연동

        // 1) DiaryActivity에서 넘어온 'dream_content' 받기
        Intent intent = getIntent();
        String from = intent.getStringExtra("from");

        String dreamContent = getIntent().getStringExtra("dream_content");
        String memberId = null;
        String selectedGenre = null;
        String mood = null;
        String vividScene = null;
        String dreamObjects = null;


        if (dreamContent == null) {
            dreamContent = "";
        }

        if ("HomeActivity".equals(from)) {
            memberId = intent.getStringExtra("member_id");
            Log.d(TAG, "from HomeActivity, memberId: " + memberId);
        } else if ("DreamBasedNewNov".equals(from)) {
            dreamContent = intent.getStringExtra("dream_content");
            selectedGenre = intent.getStringExtra("selected_genre");
            mood = intent.getStringExtra("mood");
            vividScene = intent.getStringExtra("vivid_scene");
            dreamObjects = intent.getStringExtra("dream_objects");
            Log.d(TAG, "from DreamBasedNewNov, dreamContent: " + dreamContent
                    + ", selectedGenre: " + selectedGenre
                    + ", mood: " + mood
                    + ", vividScene: " + vividScene
                    + ", dreamObjects: " + dreamObjects);
        }

        // Log로 실제로 넘어왔는지 확인
        Log.d(TAG, "onCreate called. Received dreamContent: " + dreamContent);

        RadioGroup radioGroup = findViewById(R.id.radioGroup_genre);
        Button nextButton = findViewById(R.id.button_next);
        // 만약 selectedGenre가 null이 아니면 라디오버튼 비활성화 및 체크
        // selectedGenre가 null이 아니고 빈 문자열이 아니면
        if (selectedGenre != null && !selectedGenre.isEmpty()) {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                View child = radioGroup.getChildAt(i);
                if (child instanceof RadioButton) {
                    RadioButton rb = (RadioButton) child;
                    if (rb.getText().toString().equals(selectedGenre)) {
                        rb.setEnabled(false); // 이 버튼만 비활성화(선택 불가)
                        rb.setChecked(false); // 혹시 체크되어 있다면 해제
                    } else {
                        rb.setEnabled(true);  // 나머지는 선택 가능
                    }
                }
            }
        }

        // 선택 변경 리스너 설정 (선택사항)
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // 선택된 RadioButton 찾기
                RadioButton selectedRadioButton = findViewById(checkedId);

                // 선택된 장르 확인 (예시)
                if (selectedRadioButton != null) {
                    String selectedGenre = selectedRadioButton.getText().toString();
                    Toast.makeText(SelectNovGenreActivity.this, selectedGenre + " 선택됨", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // "다음" 버튼 클릭 리스너 - 여기에 선택된 값 가져오기 코드 추가
        String finalDreamContent = dreamContent;

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String selectedGenre = selectedRadioButton.getText().toString();

                    Intent nextIntent;
                    if ("HomeActivity".equals(from)) {
                        nextIntent = new Intent(SelectNovGenreActivity.this, DreamBasedNovActivity.class);
                    } else if ("DreamBasedNewNov".equals(from)) {
                        nextIntent = new Intent(SelectNovGenreActivity.this, DreamBasedNov2Activity.class);
                    } else {
                        // 기본값 또는 예외 처리
                        nextIntent = new Intent(SelectNovGenreActivity.this, DreamBasedNovActivity.class);
                    }
                    nextIntent.putExtra("selected_genre", selectedGenre);
                    nextIntent.putExtra("dream_content", finalDreamContent);
                    startActivity(nextIntent);
                } else {
                    Toast.makeText(SelectNovGenreActivity.this, "장르를 선택해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}