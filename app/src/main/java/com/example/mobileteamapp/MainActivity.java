package com.example.mobileteamapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;


import com.example.mobileteamapp.entity.Emotion;
import com.example.mobileteamapp.entity.Member;
import com.example.mobileteamapp.viewModel.EmotionViewModel;
import com.example.mobileteamapp.viewModel.MemberViewModel;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.auth.model.OAuthToken;

// 카카오 로그인 기능
public class MainActivity extends AppCompatActivity {
    private EditText etDiary;
    private Button btnAnalyze;
    private TextView tvAnalysis;
    private GeminiApiService service;

    private Button kakaoLoginButton;
    private MemberViewModel memberViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 감정 초기화 (앱 시작 시 한 번 실행)
        initializeEmotions();

        kakaoLoginButton = findViewById(R.id.B_member_kakao);
        memberViewModel = new ViewModelProvider(
                this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication())
        ).get(MemberViewModel.class);

        // 자동 로그인 여부 확인
        checkAutoLogin();

        // 카카오 로그인 버튼 클릭 시 동작
        kakaoLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithKakaoAccount();
            }
        });
    }

    private void initializeEmotions() {
        EmotionViewModel emotionViewModel = new ViewModelProvider(this).get(EmotionViewModel.class);

        // 미리 저장할 감정 목록 (id와 이름 일치)
        int[] ids = {1, 2, 3, 4, 5};
        String[] names = {"기쁨", "슬픔", "분노", "놀람", "불안"};

        for (int i = 0; i < ids.length; i++) {
            Emotion emotion = new Emotion();
            emotion.setEmotion_id(ids[i]);
            emotion.setEmotion_name(names[i]);
            emotionViewModel.insert(emotion);
        }
    }


    /**
     * 카카오 로그인 토큰이 유효한지 확인하여
     * 자동 로그인 상태라면 바로 HomeActivity로 이동하는 함수.
     * 앱이 실행될 때 자동 로그인 처리를 위해 사용.
     */
    private void checkAutoLogin() {
        UserApiClient.getInstance().accessTokenInfo((tokenInfo, error) -> {
            if (error != null) {
                Log.d("카카오", "자동 로그인 불가능");
            } else {
                Log.d("카카오", "자동 로그인 성공: 사용자 ID = " + tokenInfo.getId());

                // HomeActivity로 바로 이동
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                intent.putExtra("member_id", String.valueOf(tokenInfo.getId()));
                startActivity(intent);
                finish(); // MainActivity 종료
            }
            return null;
        });
    }

    // 카카오 로그인 처리
    private void loginWithKakaoAccount() {
        // 카카오 계정으로 로그인 시도
        UserApiClient.getInstance().loginWithKakaoAccount(this, (token, error) -> {
            if (error != null) {
                // 로그인 실패 시 토스트 메시지 출력
                Toast.makeText(MainActivity.this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show();
            } else {
                // 로그인 성공 시 사용자 정보를 카카오 서버에 요청
                UserApiClient.getInstance().me((user, meError) -> {
                    if (meError != null) {
                        // 사용자 정보 요청 실패 시
                        Toast.makeText(MainActivity.this, "사용자 정보 요청 실패", Toast.LENGTH_SHORT).show();
                    } else {
                        // 사용자 정보에서 카카오 ID와 닉네임 추출
                        String kakaoId = String.valueOf(user.getId());
                        String nickname = "";

                        // 닉네임이 존재하는 경우
                        if (user.getKakaoAccount() != null && user.getKakaoAccount().getProfile() != null) {
                            nickname = user.getKakaoAccount().getProfile().getNickname();       // 카카오 프로필에서 닉네임 추출
                        } else {
                            // 닉네임 정보를 가져올 수 없을 경우 기본 닉네임 사용
                            nickname = "사용자";
                        }

                        // 사용자 정보를 Member 객체로 구성
                        Member member = new Member();
                        member.setMember_id(kakaoId);   // 회원 ID로 카카오 ID 사용
                        member.setKakao_id(kakaoId);    // 카카오 ID 저장
                        member.setNickname(nickname);   // 닉네임 저장

                        // ViewModel을 통해 DB에 회원 정보 삽입
                        memberViewModel.insert(member);

                        // HomeActivity로 이동하며 member_id 전달
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExtra("member_id", kakaoId);
                        startActivity(intent);
                    }
                    return null;
                });
            }
            return null;
        });
    }

}