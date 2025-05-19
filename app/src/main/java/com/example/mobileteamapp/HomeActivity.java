package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileteamapp.viewModel.MemberViewModel;
import com.example.mobileteamapp.entity.Member;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private String memberId;
    private MemberViewModel memberViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);  // home.xml을 레이아웃으로 설정

        // 로그인된 사용자 ID 받아오기
        memberId = getIntent().getStringExtra("member_id");
        CalendarView calendarView = findViewById(R.id.calendarView);

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

                Intent intent = new Intent(HomeActivity.this, dream_look_screen_Activity_1.class);
                intent.putExtra("year", year);
                intent.putExtra("month", month + 1);  // 월은 0부터 시작하므로 +1
                intent.putExtra("day", dayOfMonth);
                startActivity(intent);
            }
        });
    }
}
