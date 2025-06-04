package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private Button buttonGoToDiary, buttonnov;
    private String moodText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);  // home.xml을 레이아웃으로 설정

        // 로그인된 사용자 ID 받아오기
        moodText = getIntent().getStringExtra("mood");
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
}
