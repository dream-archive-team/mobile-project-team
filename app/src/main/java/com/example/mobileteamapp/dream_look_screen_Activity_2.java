package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileteamapp.entity.Novel;
import com.example.mobileteamapp.viewmodel.NovelViewModel;

public class dream_look_screen_Activity_2 extends AppCompatActivity {

    private static final int REQ_SELECT_GENRE = 101;
    private NovelViewModel novelViewModel;
    private RadioGroup radioGroupGenre1, radioGroupGenre2;
    private TextView tvDreamTitle, tvDreamAnalysis;
    private Button btnDeleteNovel, btnSharing, btnBack, btnHome,btnGenNovel;
    private String dreamId;
    private Novel currentNovel;
    private boolean radioChanging = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream_look_screen_2);

        // UI 연결
        radioGroupGenre1 = findViewById(R.id.radioGroup_genre1);
        radioGroupGenre2 = findViewById(R.id.radioGroup_genre2);
        tvDreamTitle = findViewById(R.id.tvDreamTitle);
        tvDreamAnalysis = findViewById(R.id.tvDreamAnalysis);
        btnDeleteNovel = findViewById(R.id.btnDeleteNovel);
        btnSharing = findViewById(R.id.btnSharing);
        btnBack = findViewById(R.id.btnBack);
        btnHome = findViewById(R.id.btnHome);
        btnGenNovel=findViewById(R.id.btnGenNovel);

        RadioButton radioFantasy = findViewById(R.id.radio_fantasy);
        RadioButton radioRomantic = findViewById(R.id.radio_romantic);
        RadioButton radioSF = findViewById(R.id.radio_sf);
        RadioButton radioDocumentary = findViewById(R.id.radio_documentary);
        RadioButton radioThriller = findViewById(R.id.radio_thriller);
        RadioButton radioComedy = findViewById(R.id.radio_comedy);
        RadioButton radioAction = findViewById(R.id.radio_action);

        novelViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(NovelViewModel.class);

        dreamId = getIntent().getStringExtra("dream_id");
        Log.d("Novel", "조회 시 dream_id = " + dreamId);

        if (dreamId == null || dreamId.isEmpty()) {
            Toast.makeText(this, "소설 조회 실패: 꿈 정보 없음", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 두 RadioGroup 모두에 리스너 등록
        radioGroupGenre1.setOnCheckedChangeListener((group, checkedId) -> {
            if (radioChanging) return;
            if (checkedId != -1) {
                radioChanging = true;
                radioGroupGenre2.clearCheck();
                handleGenreRadioChecked(checkedId);
                radioChanging = false;
            } else {
                tvDreamTitle.setText("");
                tvDreamAnalysis.setText("");
                btnDeleteNovel.setEnabled(false);
                btnSharing.setEnabled(false);
            }
        });

        radioGroupGenre2.setOnCheckedChangeListener((group, checkedId) -> {
            if (radioChanging) return;
            if (checkedId != -1) {
                radioChanging = true;
                radioGroupGenre1.clearCheck();
                handleGenreRadioChecked(checkedId);
                radioChanging = false;
            } else {
                tvDreamTitle.setText("");
                tvDreamAnalysis.setText("");
                btnDeleteNovel.setEnabled(false);
                btnSharing.setEnabled(false);
            }
        });

        // 시작 시 아무것도 선택 안 함
        radioGroupGenre1.clearCheck();
        radioGroupGenre2.clearCheck();

        // 소설 생성
        btnGenNovel.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectNovGenreActivity.class);
            intent.putExtra("dream_id", dreamId);
            intent.putExtra("from", "look"); // 이 부분 추가!
            // 필요한 정보(날짜 등)도 넘겨도 됨
            startActivity(intent);
        });


        // 소설 삭제
        btnDeleteNovel.setOnClickListener(v -> {
            if (currentNovel == null) return;
            new AlertDialog.Builder(this)
                    .setTitle("소설 삭제")
                    .setMessage("정말로 소설을 삭제하시겠습니까?")
                    .setPositiveButton("삭제", (dialog, which) -> {
                        new Thread(() -> {
                            novelViewModel.delete(currentNovel);
                            runOnUiThread(() -> {
                                Toast.makeText(this, "소설이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                // 삭제 후 다시 선택된 장르 조회
                                int checkedId1 = radioGroupGenre1.getCheckedRadioButtonId();
                                int checkedId2 = radioGroupGenre2.getCheckedRadioButtonId();
                                if (checkedId1 != -1) {
                                    radioGroupGenre1.clearCheck();
                                    radioGroupGenre1.check(checkedId1);
                                } else if (checkedId2 != -1) {
                                    radioGroupGenre2.clearCheck();
                                    radioGroupGenre2.check(checkedId2);
                                }
                            });
                        }).start();
                    })
                    .setNegativeButton("취소", null)
                    .show();
        });

        // 공유 버튼
        btnSharing.setOnClickListener(v -> {
            if (currentNovel == null) return;
            StringBuilder textToShare = new StringBuilder();
            if (currentNovel.novel_title != null && !currentNovel.novel_title.isEmpty()) {
                textToShare.append("[소설 제목]\n").append(currentNovel.novel_title).append("\n\n");
            }
            if (currentNovel.novel_content != null && !currentNovel.novel_content.isEmpty()) {
                textToShare.append("[소설 내용]\n").append(currentNovel.novel_content);
            }
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare.toString());
            startActivity(Intent.createChooser(shareIntent, "소설 공유"));
        });

        btnBack.setOnClickListener(v -> finish());

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    private void handleGenreRadioChecked(int checkedId) {
        String selectedGenre = null;
        if (checkedId == R.id.radio_fantasy) selectedGenre = "판타지";
        else if (checkedId == R.id.radio_romantic) selectedGenre = "로맨틱";
        else if (checkedId == R.id.radio_sf) selectedGenre = "SF";
        else if (checkedId == R.id.radio_documentary) selectedGenre = "다큐멘터리";
        else if (checkedId == R.id.radio_thriller) selectedGenre = "스릴러";
        else if (checkedId == R.id.radio_comedy) selectedGenre = "코미디";
        else if (checkedId == R.id.radio_action) selectedGenre = "액션";

        if (selectedGenre != null) {
            loadNovelByGenre(selectedGenre);
        } else {
            tvDreamTitle.setText("");
            tvDreamAnalysis.setText("");
            btnDeleteNovel.setEnabled(false);
            btnSharing.setEnabled(false);
        }
    }

    // 장르별 소설 불러와서 표시
    private void loadNovelByGenre(String genre) {
        new Thread(() -> {
            Novel novel = novelViewModel.getNovelByDreamIdAndGenre(dreamId, genre);
            runOnUiThread(() -> {
                currentNovel = novel;
                if (novel == null) {
                    tvDreamTitle.setText("이 장르로 생성된 소설이 없습니다.");
                    tvDreamAnalysis.setText("");
                    btnDeleteNovel.setEnabled(false);
                    btnSharing.setEnabled(false);
                } else {
                    String cleanedTitle = cleanTitle(novel.novel_title);
                    tvDreamTitle.setText(cleanedTitle.isEmpty() ? "소설" : cleanedTitle);
                    tvDreamAnalysis.setText(novel.novel_content != null ? novel.novel_content : "");
                    btnDeleteNovel.setEnabled(true);
                    btnSharing.setEnabled(true);
                }
            });
        }).start();
    }

    // 제목에서 **, *, 공백 등 제거 (앞뒤)
    private String cleanTitle(String title) {
        if (title == null) return "";
        return title.replaceAll("^[\\*\\s]+", "").replaceAll("[\\*\\s]+$", "");
    }

    
    // 생성 및 선택된 장르에서 돌아온 후 동작하려면
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SELECT_GENRE && resultCode == RESULT_OK && data != null) {
            // 새로 생성된 장르를 체크 등 필요 동작
            String newGenre = data.getStringExtra("selected_genre");
            if (newGenre != null) {
                // 해당 장르 라디오 체크 (radioGroupGenre1, 2에서 id 찾아 체크)
                // 또는 loadNovelByGenre(newGenre) 호출
            }
        }
    }
}
