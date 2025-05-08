package com.example.mobileteamapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class NextMemberPro extends AppCompatActivity {

    Spinner spinnerYear, spinnerMonth, spinnerDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.next_new_member_pro);

        boolean loginSuccess = getIntent().getBooleanExtra("login_success", false);
        if (loginSuccess) {
            Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show();
        }

        spinnerYear = findViewById(R.id.spinner_year);
        spinnerMonth = findViewById(R.id.spinner_month);
        spinnerDay = findViewById(R.id.spinner_day);

        // 안내문구 추가
        List<String> yearList = new ArrayList<>();
        yearList.add("년도");
        for (int i = 1950; i <= 2025; i++) {
            yearList.add(String.valueOf(i));
        }

        List<String> monthList = new ArrayList<>();
        monthList.add("월");
        for (int i = 1; i <= 12; i++) {
            monthList.add(String.format("%02d", i));
        }

        List<String> dayList = new ArrayList<>();
        dayList.add("일");
        for (int i = 1; i <= 31; i++) {
            dayList.add(String.format("%02d", i));
        }

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearList);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, monthList);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dayList);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);
    }

    // 생년월일 문자열 가져오기 함수 (예: "1995-08-15")
    public String getSelectedBirthday() {
        String year = spinnerYear.getSelectedItem().toString();
        String month = spinnerMonth.getSelectedItem().toString();
        String day = spinnerDay.getSelectedItem().toString();

        if (year.equals("년도") || month.equals("월") || day.equals("일")) {
            return ""; // 안내문구 선택시 빈 문자열 반환
        }
        return year + "-" + month + "-" + day;
    }
}
