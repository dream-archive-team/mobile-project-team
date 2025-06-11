package com.example.mobileteamapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileteamapp.db.AppDatabase;
import com.example.mobileteamapp.db.AppDatabaseInstance;
import com.example.mobileteamapp.entity.Emotion;
import com.example.mobileteamapp.entity.EmotionRecord;
import com.example.mobileteamapp.repository.EmotionRepository;
import com.example.mobileteamapp.viewmodel.DreamViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// 홈 화면 (캘린더 화면)
public class HomeActivity extends AppCompatActivity {

    private DreamViewModel dreamViewModel;
    private Button btnDiary;
    private String lastSelectedDate = null;
    private int chartMode = 0; // 0=주간, 1=월간
    private int weekOffset = 0, monthOffset = 0;
    private String memberId = "1"; // 임시 memberId
    private LineChart lineChart;
    private BarChart barChart;
    private TextView tvPeriod;
    private Map<String, Float> emotionMap;
    private AppDatabase db;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // UI 요소 연결
        Button btnNov = findViewById(R.id.btn_nov);
        btnDiary = findViewById(R.id.buttonGoToDiary);
        Button btnPrev = findViewById(R.id.btnPrev);
        Button btnNext = findViewById(R.id.btnNext);
        tvPeriod = findViewById(R.id.tvPeriod);
        lineChart = findViewById(R.id.lineChart);
        barChart = new BarChart(this);
        RadioGroup radioGroup = findViewById(R.id.radioGroupPeriod);
        CalendarView calendarView = findViewById(R.id.calendarView);
        btnLogout = findViewById(R.id.btnLogout);

        // ViewModel 연결
        dreamViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(DreamViewModel.class);

        // 감정명 → 숫자 매핑
        emotionMap = new HashMap<>();
        emotionMap.put("불안", 1f);
        emotionMap.put("놀람", 2f);
        emotionMap.put("분노", 3f);
        emotionMap.put("슬픔", 4f);
        emotionMap.put("기쁨", 5f);

        db = AppDatabaseInstance.getInstance(this);

        // 감정 데이터 최초 1회만 생성
        EmotionRepository emotionRepository = new EmotionRepository(this);
        new Thread(() -> {
            if (emotionRepository.getAllEmotions().isEmpty()) {
                emotionRepository.insert(new Emotion("기쁨"));
                emotionRepository.insert(new Emotion("슬픔"));
                emotionRepository.insert(new Emotion("분노"));
                emotionRepository.insert(new Emotion("놀람"));
                emotionRepository.insert(new Emotion("불안"));
            }
        }).start();

        // SharedPreferences에서 사용자 정보
        SharedPreferences prefs = getSharedPreferences("user_info", MODE_PRIVATE);
        String nickname = getIntent().getStringExtra("nickname");
        String kakaoId = getIntent().getStringExtra("kakaoId");

        if (nickname != null && !nickname.isEmpty()) {
            prefs.edit().putString("nickname", nickname).apply();
        } else {
            nickname = prefs.getString("nickname", "게스트");
        }
        // Toast.makeText(this, nickname + "님 환영합니다!", Toast.LENGTH_SHORT).show();

        // 1. 장르별 소설 조회 화면 이동
        btnNov.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, StoryQueryActivity.class);
            intent.putExtra("member_id", memberId);
            startActivity(intent);
        });

        // 오늘 날짜/캘린더 기본값 처리
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        lastSelectedDate = today;
        setDiaryButtonForDate(today);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
            lastSelectedDate = selectedDate;
            setDiaryButtonForDate(selectedDate);
        });


        // 2. 그래프 표시 로직
        chartMode = 0;
        weekOffset = 0;
        showWeeklyChart();

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioWeekly) {
                chartMode = 0;
                weekOffset = 0;
                showWeeklyChart();
            } else if (checkedId == R.id.radioMonthly) {
                chartMode = 1;
                monthOffset = 0;
                showMonthlyChart();
            }
        });

        btnPrev.setOnClickListener(v -> {
            if (chartMode == 0) {
                weekOffset++;
                showWeeklyChart();
            } else {
                monthOffset++;
                showMonthlyChart();
            }
        });
        btnNext.setOnClickListener(v -> {
            if (chartMode == 0 && weekOffset > 0) {
                weekOffset--;
                showWeeklyChart();
            } else if (chartMode == 1 && monthOffset > 0) {
                monthOffset--;
                showMonthlyChart();
            }
        });

        // 3. 로그아웃
        btnLogout.setOnClickListener(v -> {
            com.kakao.sdk.user.UserApiClient.getInstance().logout(error -> {
                if (error != null) {
                    Toast.makeText(HomeActivity.this, "로그아웃 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HomeActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    // MainActivity로 이동 (액티비티 스택 클리어)
                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
                return null;
            });
        });
        // -----------------------------
    }

    /**
     * 4. 날짜별로 btnDiary 세팅 (꿈 작성하기 / 꿈 조회하기 변환)
     * 해당 날짜에 이미 작성한 꿈이 있을 경우 꿈 조회하기 버튼,
     * 해당 날짜에 작성한 꿈이 없을 경우 꿈 작성하기 버튼이 나타남
     */
    private void setDiaryButtonForDate(String date) {
        new Thread(() -> {
            int count = dreamViewModel.getDreamCountByDate(date);
            runOnUiThread(() -> {
                btnDiary.setEnabled(true);
                btnDiary.setAlpha(1f);

                if (count > 0) {
                    btnDiary.setText("꿈 보러가기");
                    btnDiary.setOnClickListener(v -> {
                        Intent intent = new Intent(this, dream_look_screen_Activity_1.class);
                        intent.putExtra("selected_date", date); // 해당 날짜
                        startActivity(intent);
                    });
                } else {
                    btnDiary.setText("꿈 작성하기");
                    btnDiary.setOnClickListener(v -> {
                        Intent intent = new Intent(this, DiaryActivity.class);
                        intent.putExtra("selected_date", date); // 해당 날짜
                        startActivity(intent);
                    });
                }
            });
        }).start();
    }

    // ----- 주간 그래프 표시 함수 -----
    private void showWeeklyChart() {
        new Thread(() -> {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.WEEK_OF_YEAR, -weekOffset);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

            List<String> xLabels = new ArrayList<>();
            List<String> dateKeys = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            SimpleDateFormat labelFormat = new SimpleDateFormat("M.d", Locale.KOREA);

            for (int i = 0; i < 7; i++) {
                Date date = cal.getTime();
                xLabels.add(labelFormat.format(date));
                dateKeys.add(sdf.format(date));
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }

            String startDate = dateKeys.get(0);
            String endDate = dateKeys.get(6);

            List<EmotionRecord> weekRecords = db.emotionDao().getEmotionRecordsByPeriod(startDate, endDate);

            Map<String, Float> dateToValue = new HashMap<>();
            for (EmotionRecord record : weekRecords) {
                dateToValue.put(record.dream_date, emotionMap.get(record.emotion_name));
            }

            // 감정이 있는 날만 Entry 추가
            List<BarEntry> entries = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                Float yValue = dateToValue.get(dateKeys.get(i));
                if (yValue != null) {
                    entries.add(new BarEntry(i, yValue));
                }
            }

            runOnUiThread(() -> {
                tvPeriod.setText(xLabels.get(0) + "~" + xLabels.get(6));

                // LineChart를 숨기고 BarChart 표시
                lineChart.setVisibility(View.GONE);
                replaceChartWithBarChart();
                updateBarChart(barChart, entries, xLabels);
            });
        }).start();
    }


    // ----- 월간 그래프 표시 함수 -----
    private void showMonthlyChart() {
        new Thread(() -> {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -monthOffset);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;

            String yearMonth = new SimpleDateFormat("yyyy-MM", Locale.KOREA).format(cal.getTime());
            List<EmotionRecord> monthRecords = db.emotionDao().getEmotionRecordsByMonth(yearMonth);

            cal.set(Calendar.DAY_OF_MONTH, 1);
            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            List<String> xLabels = new ArrayList<>();
            for (int i = 1; i <= daysInMonth; i++) xLabels.add(String.valueOf(i));

            Map<Integer, Float> dayToValue = new HashMap<>();
            for (EmotionRecord record : monthRecords) {
                int day = Integer.parseInt(record.dream_date.substring(8, 10));
                dayToValue.put(day, emotionMap.get(record.emotion_name));
            }

            // 감정이 있는 날만 Entry 추가
            List<Entry> entries = new ArrayList<>();
            for (int i = 1; i <= daysInMonth; i++) {
                Float yValue = dayToValue.get(i);
                if (yValue != null) {
                    entries.add(new Entry(i - 1, yValue));
                }
            }


            runOnUiThread(() -> {
                tvPeriod.setText(month + "월");

                if (barChart != null) {
                    barChart.setVisibility(View.GONE);
                }
                lineChart.setVisibility(View.VISIBLE);
                updateLineChart(lineChart, entries, xLabels);
            });
        }).start();
    }
    private void replaceChartWithBarChart() {
        if (barChart.getParent() == null) {
            // BarChart를 LineChart와 같은 위치에 추가
            ViewGroup parent = (ViewGroup) lineChart.getParent();
            ViewGroup.LayoutParams params = lineChart.getLayoutParams();
            parent.addView(barChart, params);
        }
        barChart.setVisibility(View.VISIBLE);
    }
    private void updateBarChart(BarChart barChart, List<BarEntry> entries, List<String> xLabels) {
        BarDataSet dataSet = new BarDataSet(entries, "감정 변화");
        dataSet.setColor(Color.BLACK);
        dataSet.setValueTextColor(Color.BLUE);
        dataSet.setValueTextSize(12f);
        dataSet.setDrawValues(false);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);
        barChart.setData(barData);

        // X축 설정
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
        xAxis.setLabelCount(xLabels.size(), false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setDrawGridLines(false);
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(true);
        xAxis.setTextSize(12f);
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(xLabels.size() - 0.5f);

        // Y축 설정 (감정명)
        String[] yLabels = {"", "불안", "놀람", "분노", "슬픔", "기쁨"};
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setAxisMinimum(1f);
        leftAxis.setAxisMaximum(5f);
        leftAxis.setLabelCount(5, true);
        leftAxis.setValueFormatter(new IndexAxisValueFormatter(yLabels));
        barChart.getAxisRight().setEnabled(false);

        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.invalidate();
    }

    // ----- LineChart 갱신 함수 -----
    private void updateLineChart(LineChart lineChart, List<Entry> entries, List<String> xLabels) {
        LineDataSet dataSet = new LineDataSet(entries, "감정 변화");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setCircleRadius(4f);
        dataSet.setLineWidth(2.5f);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircleHole(false);
        dataSet.setMode(LineDataSet.Mode.LINEAR);  // 직선 연결

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // X축
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
        xAxis.setLabelCount(xLabels.size(), false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setDrawGridLines(false);
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(true);
        xAxis.setTextSize(12f);
        xAxis.setAxisMinimum(-0.5f);
        xAxis.setAxisMaximum(xLabels.size() - 0.5f);

        // Y축 (감정명)
        String[] yLabels = {"", "불안", "놀람", "분노", "슬픔", "기쁨"};
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setAxisMinimum(1f);
        leftAxis.setAxisMaximum(5f);
        leftAxis.setLabelCount(5, true);
        leftAxis.setValueFormatter(new IndexAxisValueFormatter(yLabels));
        lineChart.getAxisRight().setEnabled(false);

        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.invalidate();

        for (Entry e : entries) {
            Log.d("LineChart", "Entry: x=" + e.getX() + ", y=" + e.getY());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        SharedPreferences prefs = getSharedPreferences("user_info", MODE_PRIVATE);
        String nickname = intent.getStringExtra("nickname");
        if (nickname != null && !nickname.isEmpty()) {
            prefs.edit().putString("nickname", nickname).apply();
        } else {
            nickname = prefs.getString("nickname", "게스트");
        }
        Toast.makeText(this, nickname + "님 환영합니다!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lastSelectedDate == null) {
            lastSelectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }
        setDiaryButtonForDate(lastSelectedDate);

        // 그래프 재갱신
        if (chartMode == 0) showWeeklyChart();
        else showMonthlyChart();
    }
}
