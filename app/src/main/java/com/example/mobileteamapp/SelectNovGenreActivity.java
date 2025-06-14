package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileteamapp.entity.Genre;
import com.example.mobileteamapp.entity.Novel;
import com.example.mobileteamapp.viewmodel.GenreViewModel;
import com.example.mobileteamapp.viewmodel.NovelViewModel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SelectNovGenreActivity extends AppCompatActivity {

    private String selectedGenre = "";
    private GenreViewModel genreViewModel;
    private NovelViewModel novelViewModel;

    private RadioButton radioFantasy, radioRomantic, radioSF, radioDocumentary,
            radioThriller, radioComedy, radioAction;

    private boolean fromLookScreen = false; // 진입 경로 플래그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_nov_genre);

        // UI 연결
        RadioGroup radioGroup = findViewById(R.id.radioGroup_genre);
        Button buttonNext = findViewById(R.id.button_next);

        radioFantasy      = findViewById(R.id.radio_fantasy);
        radioRomantic     = findViewById(R.id.radio_romantic);
        radioSF           = findViewById(R.id.radio_sf);
        radioDocumentary  = findViewById(R.id.radio_documentary);
        radioThriller     = findViewById(R.id.radio_thriller);
        radioComedy       = findViewById(R.id.radio_comedy);
        radioAction       = findViewById(R.id.radio_action);

        // ViewModel 연결
        genreViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(GenreViewModel.class);
        novelViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(NovelViewModel.class);

        // 인텐트 값 받기
        Intent intent = getIntent();
        String dreamContent = intent.getStringExtra("dream_content");
        String dreamInterpretation = intent.getStringExtra("dream_interpretation");
        String dreamId = intent.getStringExtra("dream_id");
        String dreamDate = intent.getStringExtra("dream_date");
        String selectedMood = intent.getStringExtra("selected_mood");
        String vividScene = intent.getStringExtra("vivid_scene");
        String dreamObjects = intent.getStringExtra("dream_objects");
        String nickname = intent.getStringExtra("nickname");
        String kakaoId = intent.getStringExtra("kakaoId");
        String from = intent.getStringExtra("from");

        // 진입 경로 확인 (dream_look_screen_Activity_2에서 진입하면 true)
        fromLookScreen = intent.getBooleanExtra("from_look_screen", false);

        // 이미 생성된 장르 비활성화 처리 (dreamId 있을 때만)
        if (dreamId != null && !dreamId.isEmpty()) {
            new Thread(() -> {
                List<Novel> novels = novelViewModel.getNovelsByDreamId(dreamId);
                Set<String> createdGenres = new HashSet<>();
                for (Novel n : novels) {
                    if (n.genre != null) createdGenres.add(n.genre);
                }
                runOnUiThread(() -> {
                    if (createdGenres.contains("판타지"))     radioFantasy.setEnabled(false);
                    if (createdGenres.contains("로맨틱"))     radioRomantic.setEnabled(false);
                    if (createdGenres.contains("SF"))         radioSF.setEnabled(false);
                    if (createdGenres.contains("다큐멘터리")) radioDocumentary.setEnabled(false);
                    if (createdGenres.contains("스릴러"))     radioThriller.setEnabled(false);
                    if (createdGenres.contains("코미디"))     radioComedy.setEnabled(false);
                    if (createdGenres.contains("액션"))       radioAction.setEnabled(false);
                });
            }).start();
        }

        // 라디오 그룹 체크
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadio = findViewById(checkedId);
            if (checkedRadio != null && checkedRadio.isEnabled()) {
                selectedGenre = checkedRadio.getText().toString();
            } else {
                selectedGenre = "";
            }
        });

        // 다음 버튼
        buttonNext.setOnClickListener(v -> {
            if (selectedGenre.isEmpty()) {
                Toast.makeText(this, "장르를 선택하세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            Genre genre = new Genre(selectedGenre);
            genreViewModel.insert(genre);

            Intent nextIntent;
            if ("diary".equals(from)) {
                // 감정이 없음 → DreamBasedNovActivity로 이동
                nextIntent = new Intent(this, DreamBasedNovActivity.class);
                nextIntent.putExtra("dream_content", dreamContent);
                nextIntent.putExtra("dream_interpretation", dreamInterpretation);
                nextIntent.putExtra("selected_genre", selectedGenre);
                nextIntent.putExtra("dream_date", dreamDate);
                nextIntent.putExtra("dream_id", dreamId);
                // 필요하다면 nickname, kakaoId도 추가
                nextIntent.putExtra("nickname", nickname);
                nextIntent.putExtra("kakaoId", kakaoId);
            } else {
                // 감정이 있음 → DreamBasedNov2Activity로 이동
                nextIntent = new Intent(this, DreamBasedNov2Activity.class);
                nextIntent.putExtra("dream_content", dreamContent);
                nextIntent.putExtra("dream_interpretation", dreamInterpretation);
                nextIntent.putExtra("selected_genre", selectedGenre);
                nextIntent.putExtra("selected_mood", selectedMood);
                nextIntent.putExtra("vivid_scene", vividScene);
                nextIntent.putExtra("dream_objects", dreamObjects);
                nextIntent.putExtra("dream_date", dreamDate);
                nextIntent.putExtra("dream_id", dreamId);
                nextIntent.putExtra("nickname", nickname);
                nextIntent.putExtra("kakaoId", kakaoId);
                Log.d("GenreActivity: dream_date확인 : ", dreamDate);
            }
            startActivity(nextIntent);
            finish();
        });
    }
}
