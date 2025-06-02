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
        String dreamContent = getIntent().getStringExtra("dream_content");
        if (dreamContent == null) {
            dreamContent = "";
        }

        // Log로 실제로 넘어왔는지 확인
        Log.d(TAG, "onCreate called. Received dreamContent: " + dreamContent);

        RadioGroup radioGroup = findViewById(R.id.radioGroup_genre);
        Button nextButton = findViewById(R.id.button_next);




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
                // 선택된 값 가져오기
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId != -1) {
                    RadioButton selectedRadioButton = findViewById(selectedId);
                    String selectedGenre = selectedRadioButton.getText().toString();


                    // Log로 선택된 장르와 꿈 내용 확인
                    Log.d(TAG, "Next clicked. dreamContent: " + finalDreamContent);
                    Log.d(TAG, "Next clicked. selectedGenre: " + selectedGenre);


                    // 선택된 장르로 다음 작업 수행
                    Toast.makeText(SelectNovGenreActivity.this, "선택된 장르: " + selectedGenre, Toast.LENGTH_SHORT).show();

                    // 예: 다음 액티비티로 데이터 전달
                    Intent intent = new Intent(SelectNovGenreActivity.this, DreamBasedNewNov.class);
                    intent.putExtra("selected_genre", selectedGenre);
                    intent.putExtra("dream_content", finalDreamContent);
                    startActivity(intent);
                } else {
                    // 아무것도 선택되지 않은 경우
                    Toast.makeText(SelectNovGenreActivity.this, "장르를 선택해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}