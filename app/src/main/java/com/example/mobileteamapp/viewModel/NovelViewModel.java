package com.example.mobileteamapp.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mobileteamapp.entity.Novel;
import com.example.mobileteamapp.repository.NovelRepository;

import java.util.List;

public class NovelViewModel extends AndroidViewModel {

    private final NovelRepository repository;
    private final LiveData<List<Novel>> allNovels;

    // Repository 연결 및 LiveData 초기화
    public NovelViewModel(@NonNull Application application) {
        super(application);
        repository = new NovelRepository(application);
        allNovels = repository.getAllNovels();
    }

    // repository->viewmodel
    // UI에서 사용할 전체 소설 목록 반환
    public LiveData<List<Novel>> getAllNovels() {
        return allNovels;
    }

    // 소설 삽입 * 수정 * 삭제 요청을 Repository에 위임
    public void insert(Novel novel) {
        repository.insert(novel);
    }

    public void update(Novel novel) {
        repository.update(novel);
    }

    public void delete(Novel novel) {
        repository.delete(novel);
    }
}
