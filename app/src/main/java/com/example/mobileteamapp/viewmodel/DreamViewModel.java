package com.example.mobileteamapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobileteamapp.entity.Dream;
import com.example.mobileteamapp.repository.DreamRepository;

import java.util.List;
import java.util.concurrent.Executors;

// 꿈 데이터의 화면 상태 관리
public class DreamViewModel extends AndroidViewModel {
    private DreamRepository repository;
    private MutableLiveData<List<Dream>> allDreams = new MutableLiveData<>();

    public DreamViewModel(@NonNull Application application) {
        super(application);
        repository = new DreamRepository(application);
        loadDreams();
    }

    public LiveData<List<Dream>> getAllDreams() {
        return allDreams;
    }

    public void loadDreams() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Dream> dreams = repository.getAllDreams();
            allDreams.postValue(dreams);
        });
    }

    public void insert(Dream dream) {
        Executors.newSingleThreadExecutor().execute(() -> {
            repository.insert(dream);
            loadDreams(); // 새로고침
        });
    }

    // id 반환 insert 추가 (동기, 반드시 Thread에서 사용)
    public long insertAndReturnId(Dream dream) {
        return repository.insertAndReturnId(dream);
    }

    public void update(Dream dream) {
        Executors.newSingleThreadExecutor().execute(() -> {
            repository.update(dream);
            loadDreams();
        });
    }

    public void delete(Dream dream) {
        Executors.newSingleThreadExecutor().execute(() -> {
            repository.delete(dream);
            loadDreams();
        });
    }

    // 오늘 날짜의 꿈이 있는지 조회
    public int getDreamCountByDate(String date) {
        return repository.getDreamCountByDate(date);
    }

    // 특정 날짜의 꿈을 조회 (Dream 반환)
    public Dream getDreamByDate(String date) {
        return repository.getDreamByDate(date);
    }

    public Dream getDreamById(String id) {
        return repository.getDreamById(id);
    }

}
