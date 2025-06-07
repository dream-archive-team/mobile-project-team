package com.example.mobileteamapp.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mobileteamapp.entity.Dream;
import com.example.mobileteamapp.repository.DreamRepository;

import java.util.List;

public class DreamViewModel extends AndroidViewModel {

    private final DreamRepository repository;
    private final LiveData<List<Dream>> allDreams;

    // Repository 연결 및 LiveData 초기화
    public DreamViewModel(@NonNull Application application) {
        super(application);
        repository = new DreamRepository(application);
        allDreams = repository.getAllDreams();
    }

    // repository->viewmodel
    // UI에서 사용할 전체 꿈 목록 반환
    public LiveData<List<Dream>> getAllDreams() {
        return allDreams;
    }

    // 꿈 삽입 * 수정 * 삭제 요청을 Repository에 위임
    public void insert(Dream dream) {
        repository.insert(dream);
    }
    public void update(Dream dream) {
        repository.update(dream);
    }
    public void delete(Dream dream) {
        repository.delete(dream);
    }

    // 특정날짜, 사용자 여부 확인 (꿈 작성 하루에 한 개 제한)
    public int countDreamsByMemberAndDate(String memberId, String date) {
        return repository.countDreamsByMemberAndDate(memberId, date);
    }
}
