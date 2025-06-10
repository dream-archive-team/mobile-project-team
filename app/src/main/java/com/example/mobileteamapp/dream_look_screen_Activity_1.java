package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileteamapp.entity.Dream;
import com.example.mobileteamapp.entity.Emotion;
import com.example.mobileteamapp.entity.Novel;
import com.example.mobileteamapp.viewmodel.DreamViewModel;
import com.example.mobileteamapp.viewmodel.EmotionViewModel;
import com.example.mobileteamapp.viewmodel.NovelViewModel;

public class dream_look_screen_Activity_1 extends AppCompatActivity {

    private DreamViewModel dreamViewModel;
    private EmotionViewModel emotionViewModel;
    private NovelViewModel novelViewModel;

    private RadioGroup rgMood;
    private RadioButton rbJoy, rbSad, rbAnger, rbSurprise, rbAnxiety;
    private TextView tvDreamView, tvDreamAnalysis;
    private Button btnDelete, btnViewNovel;

    private String selectedDate;
    private Dream currentDream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_look_screen_1);

        // UI 요소 연결
        rgMood = findViewById(R.id.rgMood);
        rbJoy = findViewById(R.id.rbMoodJoy);
        rbSad = findViewById(R.id.rbMoodSad);
        rbAnger = findViewById(R.id.rbMoodAnger);
        rbSurprise = findViewById(R.id.rbMoodSurprise);
        rbAnxiety = findViewById(R.id.rbMoodAnxiety);

        tvDreamView = findViewById(R.id.tvDreamView);
        tvDreamAnalysis = findViewById(R.id.tvDreamAnalysis);

        btnDelete = findViewById(R.id.btnDelete);
        btnViewNovel = findViewById(R.id.btnViewNovel);

        // viewModel 연결
        dreamViewModel = new ViewModelProvider(this).get(DreamViewModel.class);
        emotionViewModel = new ViewModelProvider(this).get(EmotionViewModel.class);
        novelViewModel = new ViewModelProvider(this).get(NovelViewModel.class);

        selectedDate = getIntent().getStringExtra("selected_date");
        if (selectedDate == null) {
            Toast.makeText(this, "날짜 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 최초 데이터 세팅
        updateDreamInfo();

        // 꿈 삭제
        btnDelete.setOnClickListener(v -> {
            if (currentDream == null) return;
            new AlertDialog.Builder(this)
                    .setTitle("삭제 확인")
                    .setMessage("이 꿈과 연결된 데이터(해몽/소설 등)도 모두 삭제됩니다. 진행할까요?")
                    .setPositiveButton("삭제", (dialog, which) -> {
                        new Thread(() -> {
                            dreamViewModel.delete(currentDream);
                            runOnUiThread(() -> {
                                Toast.makeText(this, "꿈 일기가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            });
                        }).start();
                    })
                    .setNegativeButton("취소", null)
                    .show();
        });
    }

    // 꿈/감정/소설 정보 조회 & UI갱신 (onCreate/onResume 공통)
    private void updateDreamInfo() {
        new Thread(() -> {
            currentDream = dreamViewModel.getDreamByDate(selectedDate);

            Emotion emotion = null;
            if (currentDream != null && currentDream.emotion_id > 0) {
                emotion = emotionViewModel.getEmotionById(currentDream.emotion_id);
            }

            Novel novel = null;
            boolean novelExists = false;
            if (currentDream != null) {
                novel = novelViewModel.getNovelByDreamId(String.valueOf(currentDream.getDreamId()));
                novelExists = (novel != null);
            }

            final Emotion finalEmotion = emotion;
            final boolean finalNovelExists = novelExists;
            final Dream finalDream = currentDream;

            runOnUiThread(() -> {
                // 1. 꿈이 없을 때
                if (finalDream == null) {
                    tvDreamView.setText("해당 날짜에 작성한 꿈이 없습니다.");
                    tvDreamAnalysis.setText("");
                    btnDelete.setEnabled(false);
                    btnViewNovel.setEnabled(false);
                    setRadioCheckedAndLock(-1); // 전체 비활성화
                    return;
                }

                // 2. 꿈 내용/해몽 표시
                tvDreamView.setText(finalDream.dream_content);
                tvDreamAnalysis.setText(finalDream.interpretation);

                // 3. 감정 라디오: 저장된 감정만 체크, 나머지 버튼은 비활성화
                if (finalEmotion != null) {
                    setRadioCheckedAndLock(getEmotionIndex(finalEmotion.getEmotion_name()));
                } else {
                    setRadioCheckedAndLock(-1);
                }

                btnDelete.setEnabled(true);
                btnViewNovel.setEnabled(true);

                // 4. 소설 여부에 따라 버튼 텍스트/동작 변경
                if (finalNovelExists) {
                    btnViewNovel.setText("소설 보러 가기");
                    btnViewNovel.setOnClickListener(v -> {
                        Intent intent = new Intent(this, dream_look_screen_Activity_2.class);
                        intent.putExtra("dream_content", finalDream.dream_content);
                        intent.putExtra("dream_interpretation", finalDream.interpretation);
                        intent.putExtra("dream_date", finalDream.dream_date);
                        intent.putExtra("dream_id", String.valueOf(finalDream.getDreamId()));
                        startActivity(intent);
                    });
                } else {
                    btnViewNovel.setText("소설 생성");
                    btnViewNovel.setOnClickListener(v -> {
                        Intent intent = new Intent(this, SelectNovGenreActivity.class);
                        intent.putExtra("dream_content", finalDream.dream_content);
                        intent.putExtra("dream_interpretation", finalDream.interpretation);
                        intent.putExtra("dream_date", finalDream.dream_date);
                        intent.putExtra("dream_id", String.valueOf(finalDream.getDreamId()));
                        startActivity(intent);
                    });
                }
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDreamInfo();
    }

    // idx(-1: 전체 비활성화), 나머지: 그 감정만 체크/활성화, 나머지 버튼은 비활성화
    private void setRadioCheckedAndLock(int idx) {
        rbJoy.setEnabled(false);
        rbSad.setEnabled(false);
        rbAnger.setEnabled(false);
        rbSurprise.setEnabled(false);
        rbAnxiety.setEnabled(false);
        rgMood.clearCheck();
        switch (idx) {
            case 0: rbJoy.setChecked(true); break;
            case 1: rbSad.setChecked(true); break;
            case 2: rbAnger.setChecked(true); break;
            case 3: rbSurprise.setChecked(true); break;
            case 4: rbAnxiety.setChecked(true); break;
        }
    }

    private int getEmotionIndex(String emotionName) {
        if (emotionName == null) return -1;
        switch (emotionName) {
            case "기쁨": return 0;
            case "슬픔": return 1;
            case "분노": return 2;
            case "놀람": return 3;
            case "불안": return 4;
            default: return -1;
        }
    }
}
