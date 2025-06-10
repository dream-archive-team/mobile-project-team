package com.example.mobileteamapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileteamapp.entity.Novel;
import com.example.mobileteamapp.viewmodel.NovelViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class StoryQueryActivity extends AppCompatActivity {

    private Spinner spinnerGenre;
    private Button btnSelectDate, btnHome;
    private LinearLayout layoutDateCheckboxes;
    private TextView tvStoryTitle, tvStoryContent;

    private NovelViewModel novelViewModel;
    private List<Novel> filteredNovels = new ArrayList<>();
    private String selectedGenre = "";
    private String selectedDate = ""; // yyyy-MM-dd

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_query);

        // UI요소 연결
        spinnerGenre = findViewById(R.id.spinnerGenre);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnHome = findViewById(R.id.btnHome);
        layoutDateCheckboxes = findViewById(R.id.layoutDateCheckboxes);
        tvStoryTitle = findViewById(R.id.tvStoryTitle);
        tvStoryContent = findViewById(R.id.tvStoryContent);

        // viewModel 연결
        novelViewModel = new ViewModelProvider(this).get(NovelViewModel.class);

        // 장르 스피너 세팅
        String[] genres = {"전체", "판타지", "로맨틱", "SF", "다큐멘터리", "스릴러", "코미디", "액션"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genres);
        spinnerGenre.setAdapter(adapter);

        // 장르 선택 시 필터링
        spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGenre = genres[position];
                updateNovelList();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 날짜 선택 버튼
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // 홈으로 버튼
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        // novelViewModel이 LiveData이므로 observe 필요
        novelViewModel.getAllNovels().observe(this, new Observer<List<Novel>>() {
            @Override
            public void onChanged(List<Novel> novels) {
                updateNovelList();
            }
        });

        selectedGenre = "전체";
        selectedDate = "";
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog dlg = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate = String.format(Locale.KOREA, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    updateNovelList();
                },
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dlg.show();
    }

    // 소설 DB에서 장르+날짜별 목록 추출 후 체크박스 갱신
    private void updateNovelList() {
        List<Novel> allNovels = novelViewModel.getAllNovels().getValue();
        if (allNovels == null) allNovels = new ArrayList<>();

        filteredNovels.clear();
        for (Novel n : allNovels) {
            boolean genreMatch = selectedGenre.equals("전체") || selectedGenre.equals(n.getGenre());
            boolean dateMatch = selectedDate.isEmpty() || selectedDate.equals(n.getDream_date());
            if (genreMatch && dateMatch) {
                filteredNovels.add(n);
            }
        }

        // ★ 최신 날짜(내림차순)로 정렬
        Collections.sort(filteredNovels, new Comparator<Novel>() {
            private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            @Override
            public int compare(Novel n1, Novel n2) {
                try {
                    String d1 = n1.getDream_date();
                    String d2 = n2.getDream_date();
                    if (d1 == null && d2 == null) return 0;
                    if (d1 == null) return 1;
                    if (d2 == null) return -1;
                    return -sdf.parse(d1).compareTo(sdf.parse(d2)); // 내림차순!
                } catch (ParseException e) {
                    return 0;
                }
            }
        });

        layoutDateCheckboxes.removeAllViews();
        tvStoryTitle.setText("");
        tvStoryContent.setText("");

        if (filteredNovels.isEmpty()) {
            Toast.makeText(this, "해당 조건의 소설이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 날짜별로 체크박스 추가
        for (int i = 0; i < filteredNovels.size(); i++) {
            Novel novel = filteredNovels.get(i);
            CheckBox cb = new CheckBox(this);
            String label = (novel.getDream_date() != null ? novel.getDream_date() : "") +
                    " (" + (novel.getGenre() != null ? novel.getGenre() : "-") + ")";
            cb.setText(label);

            int idx = i;
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // 체크된 소설 표시, 나머지 체크 해제
                    for (int j = 0; j < layoutDateCheckboxes.getChildCount(); j++) {
                        if (j != idx) {
                            CheckBox other = (CheckBox) layoutDateCheckboxes.getChildAt(j);
                            other.setChecked(false);
                        }
                    }
                    // 소설 제목 앞뒤 **, *, 공백 등 제거
                    String cleanedTitle = cleanTitle(novel.getNovel_title());
                    String cleanedContent = cleanContent(novel.getNovel_content());
                    tvStoryTitle.setText(cleanedTitle);
                    tvStoryContent.setText(cleanedContent);
                } else {
                    // 해제하면 내용 숨김
                    if (tvStoryTitle.getText().toString().equals(cleanTitle(novel.getNovel_title()))) {
                        tvStoryTitle.setText("");
                        tvStoryContent.setText("");
                    }
                }
            });
            layoutDateCheckboxes.addView(cb);
        }
    }

    // 소설 제목 앞뒤 **, *, 공백 등 제거
    private String cleanTitle(String title) {
        if (title == null) return "";
        return title.replaceAll("^[\\*\\s]+", "").replaceAll("[\\*\\s]+$", "");
    }

    private String cleanContent(String content) {
        if (content == null) return "";
        return content.replaceAll("^[\\*\\s]+", "").replaceAll("[\\*\\s]+$", "");
    }
}
