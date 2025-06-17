package com.example.mobileteamapp.viewmodel;

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

    public NovelViewModel(@NonNull Application application) {
        super(application);
        repository = new NovelRepository(application);
        allNovels = repository.getAllNovels();
    }

    public LiveData<List<Novel>> getAllNovels() {
        return allNovels;
    }

    public void insert(Novel novel) {
        repository.insert(novel);
    }

    public void update(Novel novel) {
        repository.update(novel);
    }

    public void delete(Novel novel) {
        repository.delete(novel);
    }

    public Novel getNovelByDreamId(String dreamId) {
        return repository.getNovelByDreamId(dreamId);
    }

    public Novel getNovelByDreamIdAndGenre(String dreamId, String genre) {
        return repository.getNovelByDreamIdAndGenre(dreamId, genre);
    }

    public List<Novel> getNovelsByDreamId(String dreamId) {
        return repository.getNovelsByDreamId(dreamId);
    }
}