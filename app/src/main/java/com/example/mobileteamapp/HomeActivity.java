package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.mobileteamapp.database.AppDatabase;
import com.example.mobileteamapp.viewModel.MemberViewModel;
import com.example.mobileteamapp.entity.Member;

import java.util.List;

import java.util.HashMap;
import java.util.Map;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarData;

import android.graphics.Color;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.*;



public class HomeActivity extends AppCompatActivity {

    private String memberId;
    private MemberViewModel memberViewModel;
    private Button buttonGoToDiary, buttonnov;
    private String moodText;

    private AppDatabase db;
    private Map<String, Float> emotionMap;

    private int weekOffset = 0;
    private int monthOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);  // home.xml을 레이아웃으로 설정

        // 로그인된 사용자 ID 받아오기
        moodText = getIntent().getStringExtra("mood");
        memberId = getIntent().getStringExtra("member_id");

        String[] yLabels = {"", "불안", "놀람", "분노", "슬픔", "기쁨"};

        CalendarView calendarView = findViewById(R.id.calendarView);
        RadioGroup radioGroup = findViewById(R.id.radioGroupPeriod);
        RadioButton radioWeekly = findViewById(R.id.radioWeekly);
        RadioButton radioMonthly = findViewById(R.id.radioMonthly);
        TextView tvPeriod = findViewById(R.id.tvPeriod);
        BarChart barChart = findViewById(R.id.barChart);
        Button btnPrev = findViewById(R.id.btnPrev);
        Button btnNext = findViewById(R.id.btnNext);




        // 감정 → 숫자 매핑
        emotionMap = new HashMap<>();
        emotionMap.put("기쁨", 5f);
        emotionMap.put("슬픔", 4f);
        emotionMap.put("분노", 3f);
        emotionMap.put("놀람", 2f);
        emotionMap.put("불안", 1f);

        db = Room.databaseBuilder(
                getApplicationContext(),
                AppDatabase.class,
                "app_database"
        ).allowMainThreadQueries().build();

        memberId = getIntent().getStringExtra("member_id");
        // 최초: 주간 그래프 표시
        showWeeklyChart(barChart, tvPeriod, memberId, weekOffset);

        // 주간/월별 토글
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioWeekly) {
                weekOffset = 0;
                showWeeklyChart(barChart, tvPeriod, memberId, weekOffset);
            } else if (checkedId == R.id.radioMonthly) {
                monthOffset = 0;
                showMonthlyChart(barChart, tvPeriod, memberId, monthOffset);
            }
        });

        btnPrev.setOnClickListener(v -> {
            if (radioWeekly.isChecked()) {
                weekOffset++;
                showWeeklyChart(barChart, tvPeriod, memberId, weekOffset);
            } else if (radioMonthly.isChecked()) {
                monthOffset++;
                showMonthlyChart(barChart, tvPeriod, memberId, monthOffset);
            }
        });

        btnNext.setOnClickListener(v -> {
            if (radioWeekly.isChecked() && weekOffset > 0) {
                weekOffset--;
                showWeeklyChart(barChart, tvPeriod, memberId, weekOffset);
            } else if (radioMonthly.isChecked() && monthOffset > 0) {
                monthOffset--;
                showMonthlyChart(barChart, tvPeriod, memberId, monthOffset);
            }
        });




    // ViewModel 초기화
        memberViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
        ).get(MemberViewModel.class);

        // DB에 저장된 전체 회원 로그 출력(회원DB조회)
        memberViewModel.getAllMembers().observe(this, members -> {
            for (Member m : members) {
                Log.d("MEMBER_DB", "id=" + m.getMember_id() + ", nickname=" + m.getNickname());
            }
        });


        //캘린더 부분
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // 날짜 문자열 생성 (선택사항)
                String date = year + "/" + (month + 1) + "/" + dayOfMonth;

                Intent intent = new Intent(HomeActivity.this, DiaryActivity.class);
                intent.putExtra("member_id", getIntent().getStringExtra("member_id")); // MainActivity에서 받은 member_id를 그대로 전달
                startActivity(intent);


            }
        });

        // 메인페이지 -> 꿈 일기 작성 화면 이동 버튼
        buttonGoToDiary = findViewById(R.id.buttonGoToDiary);
        buttonnov = findViewById(R.id.btn_nov);
        buttonGoToDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DiaryActivity.class);
                intent.putExtra("member_id", memberId); // ← memberId 전달
                intent.putExtra("from", "HomeActivity");
                startActivity(intent);
            }
        });

        buttonnov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, StoryQueryActivity.class);
                intent.putExtra("member_id", memberId); // ← memberId 전달
                startActivity(intent);
            }
        });




    }
    // 주간 그래프 표시 함수
    private void showWeeklyChart(BarChart barChart, TextView tvPeriod, String memberId, int weekOffset) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_YEAR, -weekOffset);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        List<String> xLabels = new ArrayList<>();
        List<String> dateKeys = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        SimpleDateFormat labelFormat = new SimpleDateFormat("M.d", Locale.KOREA);

        for (int i = 0; i < 7; i++) {
            Date date = cal.getTime();
            xLabels.add(labelFormat.format(date)); // "8.1", "8.2", ...
            dateKeys.add(sdf.format(date));        // "2024-08-01", ...
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        // 기간 표시
        tvPeriod.setText(xLabels.get(0) + "~" + xLabels.get(6));

        // DB에서 해당 주간 데이터 조회
        String startDate = dateKeys.get(0);
        String endDate = dateKeys.get(6);
        List<EmotionRecord> weekRecords = db.emotionDao().getEmotionRecordsByPeriod(memberId, startDate, endDate);

        // 날짜별 감정 매핑
        Map<String, Float> dateToValue = new HashMap<>();
        for (EmotionRecord record : weekRecords) {
            dateToValue.put(record.dream_date, emotionMap.get(record.emotion_name));
        }

        // Entry 리스트 생성
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Float yValue = dateToValue.get(dateKeys.get(i));
            if (yValue != null) entries.add(new BarEntry(i, yValue));
            else entries.add(new BarEntry(i, Float.NaN));
        }

        updateChart(barChart, entries, xLabels, emotionMap);
    }

    // 월별 그래프 표시 함수
    private void showMonthlyChart(BarChart barChart, TextView tvPeriod, String memberId, int monthOffset) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -monthOffset); // monthOffset으로 월 이동
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1; // 0=1월, 1=2월, ...
        // 원하는 포맷으로 표시
        tvPeriod.setText(month + "월"); // "8월"처럼 표시
        // 또는 tvPeriod.setText(year + "년 " + month + "월"); // "2024년 8월"

        String yearMonth = new SimpleDateFormat("yyyy-MM", Locale.KOREA).format(cal.getTime());

        List<EmotionRecord> monthRecords = db.emotionDao().getEmotionRecordsByMonth(memberId, yearMonth);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        List<String> xLabels = new ArrayList<>();
        for (int i = 1; i <= daysInMonth; i++) xLabels.add(String.valueOf(i));

        Map<Integer, Float> dayToValue = new HashMap<>();
        for (EmotionRecord record : monthRecords) {
            int day = Integer.parseInt(record.dream_date.substring(8, 10));
            dayToValue.put(day, emotionMap.get(record.emotion_name));
        }
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 1; i <= daysInMonth; i++) {
            Float yValue = dayToValue.get(i);
            if (yValue != null) entries.add(new BarEntry(i - 1, yValue));
            else entries.add(new BarEntry(i - 1, Float.NaN));
        }
        updateChart(barChart, entries, xLabels, emotionMap);
    }

    // 그래프 갱신 함수
    private void updateChart(BarChart barChart, List<BarEntry> entries, List<String> xLabels, Map<String, Float> emotionMap) {
        BarDataSet dataSet = new BarDataSet(entries, "감정 변화");
        dataSet.setDrawValues(false);

        // 최고/최저점 색상 강조
        int maxIdx = -1, minIdx = -1;
        float maxY = -Float.MAX_VALUE, minY = Float.MAX_VALUE;
        for (int i = 0; i < entries.size(); i++) {
            float y = entries.get(i).getY();
            if (!Float.isNaN(y)) {
                if (y > maxY) { maxY = y; maxIdx = i; }
                if (y < minY) { minY = y; minIdx = i; }
            }
        }
        List<Integer> barColors = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            if (i == maxIdx) barColors.add(Color.GREEN);
            else if (i == minIdx) barColors.add(Color.RED);
            else barColors.add(Color.BLUE);
        }
        dataSet.setColors(barColors);

        // BarData 설정
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.4f);
        barChart.setData(barData);
        barChart.setFitBars(true);

        // X축 설정 (강화)
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true); // 추가
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
        xAxis.setLabelCount(xLabels.size(), false); // force를 false로
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setDrawGridLines(false);
        xAxis.setEnabled(true); // 명시적으로 활성화
        xAxis.setDrawLabels(true); // 라벨 그리기 활성화
        xAxis.setTextSize(12f); // 텍스트 크기 설정
        xAxis.setAxisMinimum(-0.5f); // 양끝 여백 추가
        xAxis.setAxisMaximum(xLabels.size() - 0.5f); // 양끝 여백 추가


        // Y축 설정 (감정명)
        String[] yLabels = {"", "불안", "놀람", "분노", "슬픔", "기쁨"};
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setAxisMinimum(1f);
        leftAxis.setAxisMaximum(5f);
        leftAxis.setLabelCount(5, true);
        leftAxis.setValueFormatter(new IndexAxisValueFormatter(yLabels));
        barChart.getAxisRight().setEnabled(false);

        // 기타 스타일
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        barChart.invalidate();
    }
    // 날짜 → 요일 변환
    private String getDayOfWeek(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            Date date = sdf.parse(dateStr);
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.ENGLISH);
            return dayFormat.format(date); // "Mon", "Tue", ...
        } catch (Exception e) {
            return "";
        }
    }

    // 기간 표시 포맷 (예: "8.1~8.7")
    private String formatPeriodLabel(String start, String end) {
        String[] s = start.split("-");
        String[] e = end.split("-");
        return Integer.parseInt(s[1]) + "." + Integer.parseInt(s[2]) + "~" + Integer.parseInt(e[1]) + "." + Integer.parseInt(e[2]);
    }
}
