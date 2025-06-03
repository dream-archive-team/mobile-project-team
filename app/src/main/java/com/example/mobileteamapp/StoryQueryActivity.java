package com.example.mobileteamapp;

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

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StoryQueryActivity extends AppCompatActivity {

    Button btnSelectDate;
    Button btnHome;  // 홈으로 가는 버튼
    Spinner spinnerGenre;
    LinearLayout layoutDateCheckboxes;
    TextView tvStoryTitle, tvStoryContent;

    String selectedGenre = "";
    List<String> selectedDates = new ArrayList<>();  // 선택한 날짜들

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_query);

        // UI 요소 초기화
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnHome = findViewById(R.id.btnHome);
        spinnerGenre = findViewById(R.id.spinnerGenre);
        layoutDateCheckboxes = findViewById(R.id.layoutDateCheckboxes);
        tvStoryTitle = findViewById(R.id.tvStoryTitle);
        tvStoryContent = findViewById(R.id.tvStoryContent);

        // 장르 스피너 설정
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.genre_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenre.setAdapter(adapter);

        // 장르 선택 이벤트 처리
        spinnerGenre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGenre = parent.getItemAtPosition(position).toString();
                updateCheckboxes();  // 장르가 바뀌면 체크박스 갱신
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 날짜 선택 버튼 이벤트 처리
        btnSelectDate.setOnClickListener(v -> {
            MaterialDatePicker.Builder<androidx.core.util.Pair<Long, Long>> builder =
                    MaterialDatePicker.Builder.dateRangePicker();
            builder.setTitleText("기간을 선택하세요");

            final MaterialDatePicker<androidx.core.util.Pair<Long, Long>> picker = builder.build();
            picker.show(getSupportFragmentManager(), picker.toString());

            picker.addOnPositiveButtonClickListener(selection -> {
                if (selection.first != null && selection.second != null) {
                    Date start = new Date(selection.first);
                    Date end = new Date(selection.second);
                    selectedDates = getDatesBetween(start, end);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA);
                    String text = sdf.format(start) + " ~ " + sdf.format(end);
                    btnSelectDate.setText(text);

                    updateCheckboxes();
                }
            });
        });

        // 홈으로 이동 버튼
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoryQueryActivity.this, HomeActivity.class);
                intent.putExtra("member_id", getIntent().getStringExtra("member_id")); // 현재 받은 member_id 전달
                startActivity(intent);
                finish(); // 현재 액티비티 종료하여 뒤로가기 시 다시 돌아오지 않게
            }
        });
    }

    // 시작일 ~ 종료일 사이의 날짜 리스트 반환
    private List<String> getDatesBetween(Date start, Date end) {
        List<String> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

        while (!calendar.getTime().after(end)) {
            dates.add(sdf.format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        return dates;
    }

    // 체크박스 생성 및 갱신
    private void updateCheckboxes() {
        layoutDateCheckboxes.removeAllViews();

        if (selectedDates.isEmpty() || selectedGenre.equals("선택") || selectedGenre.isEmpty()) {
            return;
        }

        for (String date : selectedDates) {
            CheckBox checkBox = new CheckBox(this);
            checkBox.setText(date);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    // 다른 체크박스 해제
                    for (int i = 0; i < layoutDateCheckboxes.getChildCount(); i++) {
                        View child = layoutDateCheckboxes.getChildAt(i);
                        if (child instanceof CheckBox && child != checkBox) {
                            ((CheckBox) child).setChecked(false);
                        }
                    }
                    // 해당 날짜 소설 로딩
                    loadStory(date, selectedGenre);
                } else {
                    // 체크 해제 시 초기화
                    tvStoryTitle.setText("꿈 제목");
                    tvStoryContent.setText("");
                }
            });

            layoutDateCheckboxes.addView(checkBox);
        }
    }

    // 소설 정보 로딩 (샘플 구현)
    private void loadStory(String date, String genre) {
        String title = date + "의 " + genre + " 소설";
        String content = "이 날 꾼 꿈을 바탕으로 작성된 " + genre + " 장르의 소설 내용입니다.";
        tvStoryTitle.setText(title);
        tvStoryContent.setText(content);
    }
}
