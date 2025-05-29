package com.example.mobileteamapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileteamapp.entity.Dream;
import com.example.mobileteamapp.viewModel.DreamViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

// 꿈 일기 작성 기능
public class DiaryActivity extends AppCompatActivity {

    private EditText etDreamInput;
    private DreamViewModel dreamViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary);

        etDreamInput = findViewById(R.id.etDreamInput);
        Button btnAnalyze = findViewById(R.id.btnAnalyze);

        dreamViewModel = new ViewModelProvider(this).get(DreamViewModel.class);

        // 저장된 모든 꿈 데이터를 로그로 출력(DB 확인용)
        dreamViewModel.getAllDreams().observe(this, dreams -> {
            for (Dream dream : dreams) {
                Log.d("DreamCheck", "ID: " + dream.getDream_id()
                        + ", 날짜: " + dream.getDream_date()
                        + ", 내용: " + dream.getDream_content()
                        + ", 해몽: " + dream.getInterpretation()
                        + ", 사용자: " + dream.getMember_id());
            }
        });

        // '분석' 버튼 클릭 시 DB에 저장
        btnAnalyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dreamContent = etDreamInput.getText().toString().trim();

                if (!dreamContent.isEmpty()) {
                    // 1. UUID로 꿈 ID 생성
                    String dreamId = UUID.randomUUID().toString();

                    // 2. 로그인된 사용자 ID 받아오기 (예: sharedPreferences, intent 등)
                    String memberId = getCurrentUserId(); // 이 부분은 상황에 맞게 구현 필요

                    // 3. 오늘 날짜 저장
                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    // 4. Dream 객체 생성 및 저장
                    Dream dream = new Dream();
                    dream.setDream_id(dreamId);
                    dream.setMember_id(memberId);
                    dream.setDream_date(today);
                    dream.setDream_content(dreamContent);
                    dream.setInterpretation(""); // 해몽 결과는 아직 없음

                    dreamViewModel.insert(dream);

                    Toast.makeText(DiaryActivity.this, "꿈이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DiaryActivity.this, "꿈 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    // 현재 로그인한 사용자 ID를 가져오는 메서드
    private String getCurrentUserId() {
        return getIntent().getStringExtra("member_id");
    }



}