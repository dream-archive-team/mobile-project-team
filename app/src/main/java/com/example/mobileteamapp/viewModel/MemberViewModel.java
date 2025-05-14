package com.example.mobileteamapp.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mobileteamapp.entity.Member;
import com.example.mobileteamapp.repository.MemberRepository;

import java.util.List;

public class MemberViewModel extends AndroidViewModel {

    private final MemberRepository repository;

    // Repository 연결 및 LiveData 초기화
    public MemberViewModel(@NonNull Application application) {
        super(application);
        repository = new MemberRepository(application);
    }

    // repository->viewmodel
    // 꿈 삽입 요청을 Repository에 위임
    public void insert(Member member) {
        repository.insert(member);
    }

    // 특정 ID를 가진 회원 데이터 조회를 Repository에 위임
    public LiveData<Member> getMemberById(String id) {
        return repository.getMemberById(id);
    }

    // UI에서 사용할 전체 회원 목록 반환
    public LiveData<List<Member>> getAllMembers() {
        return repository.getAllMembers();
    }
}
