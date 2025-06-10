package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileteamapp.entity.Dream;
import com.example.mobileteamapp.entity.Emotion;
import com.example.mobileteamapp.viewmodel.DreamViewModel;
import com.example.mobileteamapp.viewmodel.EmotionViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DreamBasedNovActivity extends AppCompatActivity {

    private String selectedMood = "";
    private DreamViewModel dreamViewModel;
    private EmotionViewModel emotionViewModel;

    private RadioButton rbJoy, rbSad, rbAnger, rbSurprise, rbAnxiety;
    private RadioGroup rgMood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_based_nov);

        dreamViewModel = new ViewModelProvider(this).get(DreamViewModel.class);
        emotionViewModel = new ViewModelProvider(this).get(EmotionViewModel.class);

        rgMood = findViewById(R.id.rgMood);
        EditText etMostVividScene = findViewById(R.id.etMostVividScene);
        EditText etDreamObjects = findViewById(R.id.etDreamObjects);

        rbJoy      = findViewById(R.id.rbMoodJoy);
        rbSad      = findViewById(R.id.rbMoodSad);
        rbAnger    = findViewById(R.id.rbMoodAnger);
        rbSurprise = findViewById(R.id.rbMoodSurprise);
        rbAnxiety  = findViewById(R.id.rbMoodAnxiety);

        final String dreamContent = getIntent().getStringExtra("dream_content");
        final String dreamInterpretation = getIntent().getStringExtra("dream_interpretation");
        final String selectedGenre = getIntent().getStringExtra("selected_genre");
        final String intentDate = getIntent().getStringExtra("dream_date");
        final String prevDreamId = getIntent().getStringExtra("dream_id");

        final String dreamDate = (intentDate == null || intentDate.trim().isEmpty())
                ? new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date())
                : intentDate;



        // ✅ 감정 자동 체크/비활성화 처리
        new Thread(() -> {
            Dream dream = null;
            if (prevDreamId != null && !prevDreamId.trim().isEmpty()) {
                dream = dreamViewModel.getDreamById(prevDreamId);
            }
            if (dream == null) {
                dream = dreamViewModel.getDreamByDate(dreamDate);
            }

            if (dream != null && dream.emotion_id > 0) {
                Emotion emotion = emotionViewModel.getEmotionById(dream.emotion_id);
                if (emotion != null) {
                    String emotionName = emotion.getEmotion_name();
                    runOnUiThread(() -> {
                        rbJoy.setEnabled(false);
                        rbSad.setEnabled(false);
                        rbAnger.setEnabled(false);
                        rbSurprise.setEnabled(false);
                        rbAnxiety.setEnabled(false);

                        switch (emotionName) {
                            case "기쁨":
                                rbJoy.setChecked(true); rbJoy.setEnabled(true); selectedMood = "기쁨"; break;
                            case "슬픔":
                                rbSad.setChecked(true); rbSad.setEnabled(true); selectedMood = "슬픔"; break;
                            case "분노":
                                rbAnger.setChecked(true); rbAnger.setEnabled(true); selectedMood = "분노"; break;
                            case "놀람":
                                rbSurprise.setChecked(true); rbSurprise.setEnabled(true); selectedMood = "놀람"; break;
                            case "불안":
                                rbAnxiety.setChecked(true); rbAnxiety.setEnabled(true); selectedMood = "불안"; break;
                        }
                    });
                }
            } else {
                runOnUiThread(() -> {
                    rbJoy.setEnabled(true);
                    rbSad.setEnabled(true);
                    rbAnger.setEnabled(true);
                    rbSurprise.setEnabled(true);
                    rbAnxiety.setEnabled(true);
                });
            }
        }).start();

        // 감정 선택 리스너
        rgMood.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadio = findViewById(checkedId);
            if (checkedRadio != null && checkedRadio.isEnabled()) {
                selectedMood = checkedRadio.getText().toString();
            }
        });

        // 소설 생성 버튼
        findViewById(R.id.btnGenerateNovel).setOnClickListener(v -> {
            String vividScene = etMostVividScene.getText().toString().trim();
            String dreamObjects = etDreamObjects.getText().toString().trim();

            if (selectedMood.isEmpty()) {
                Toast.makeText(this, "감정을 선택하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (vividScene.isEmpty()) {
                Toast.makeText(this, "가장 뚜렷했던 장면을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dreamObjects.isEmpty()) {
                Toast.makeText(this, "꿈에 나온 물건을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                // 감정이 없으면 새로 db에 저장하고 ID 반환
                Emotion emotion = emotionViewModel.getEmotionByName(selectedMood);
                if (emotion == null) {
                    emotion = new Emotion(selectedMood);
                    long newEmotionId = emotionViewModel.insertAndReturnId(emotion);
                    emotion.setEmotion_id((int) newEmotionId);
                }
                int emotionId = emotion.getEmotion_id();

                String sendDreamId = prevDreamId;

                if (sendDreamId == null || sendDreamId.trim().isEmpty()) {
                    Dream already = dreamViewModel.getDreamByDate(dreamDate);
                    if (already == null) {
                        Dream newDream = new Dream(dreamDate, dreamContent, dreamInterpretation, emotionId);
                        long newId = dreamViewModel.insertAndReturnId(newDream);
                        sendDreamId = String.valueOf(newId);
                    } else {
                        already.dream_content = dreamContent;
                        already.interpretation = dreamInterpretation;
                        already.emotion_id = emotionId;
                        dreamViewModel.update(already);
                        sendDreamId = String.valueOf(already.getDreamId());
                    }
                } else {
                    Dream byId = dreamViewModel.getDreamById(sendDreamId);
                    if (byId != null) {
                        byId.dream_content = dreamContent;
                        byId.interpretation = dreamInterpretation;
                        byId.emotion_id = emotionId;
                        dreamViewModel.update(byId);
                    }
                }

                final String finalDreamId = sendDreamId;
                runOnUiThread(() -> {
                    Intent intent = new Intent(this, DreamBasedNov2Activity.class);
                    intent.putExtra("dream_content", dreamContent);
                    intent.putExtra("dream_interpretation", dreamInterpretation);
                    intent.putExtra("selected_genre", selectedGenre);
                    intent.putExtra("selected_mood", selectedMood);
                    intent.putExtra("vivid_scene", vividScene);
                    intent.putExtra("dream_objects", dreamObjects);
                    intent.putExtra("dream_date", dreamDate);
                    intent.putExtra("dream_id", finalDreamId);
                    intent.putExtra("emotion_id",emotionId);
                    startActivity(intent);
                    finish();
                });
            }).start();
        });
    }
}