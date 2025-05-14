package com.example.mobileteamapp;

import android.os.Bundle;
import android.util.Log;
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
    }
}
