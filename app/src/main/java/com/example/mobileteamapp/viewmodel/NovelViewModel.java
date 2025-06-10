package com.example.mobileteamapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobileteamapp.entity.Novel;
import com.example.mobileteamapp.repository.NovelRepository;

import java.util.List;
import java.util.concurrent.Executors;

public class NovelViewModel extends AndroidViewModel {
    private NovelRepository repository;
    private MutableLiveData<List<Novel>> allNovels = new MutableLiveData<>();

    public NovelViewModel(@NonNull Application application) {
        super(application);
        repository = new NovelRepository(application);
        loadNovels();
    }

    public LiveData<List<Novel>> getAllNovels() {
        return allNovels;
    }

    public void loadNovels() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Novel> novels = repository.getAllNovels();
            allNovels.postValue(novels);
        });
    }

    public void insert(Novel novel) {
        Executors.newSingleThreadExecutor().execute(() -> {
            repository.insert(novel);
            loadNovels();
        });
    }

    public void update(Novel novel) {
        Executors.newSingleThreadExecutor().execute(() -> {
            repository.update(novel);
            loadNovels();
        });
    }

    public void delete(Novel novel) {
        Executors.newSingleThreadExecutor().execute(() -> {
            repository.delete(novel);
            loadNovels();
        });
    }

    // 꿈 -> 소설 조회
    public Novel getNovelByDreamId(String dreamId) {
        return repository.getNovelByDreamId(dreamId);
    }

    // 꿈 + 장르 -> 소설 조회
    public Novel getNovelByDreamIdAndGenre(String dreamId, String genre) {
        return repository.getNovelByDreamIdAndGenre(dreamId, genre);
    }

    // 동기: 꿈 ID로 모든 소설 반환
    public List<Novel> getNovelsByDreamId(String dreamId) {
        return repository.getNovelsByDreamId(dreamId);
    }

    // (옵션) LiveData<List<Novel>> 반환하려면 아래 주석 해제하고,
    // NovelRepository/Dao에도 LiveData 메서드 구현 필요
    /*
    public LiveData<List<Novel>> getNovelsByDreamIdLive(String dreamId) {
        return repository.getNovelsByDreamIdLive(dreamId);
    }
    */
}
