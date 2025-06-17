package com.example.mobileteamapp.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.mobileteamapp.dao.NovelDao;
import com.example.mobileteamapp.db.AppDatabaseInstance;
import com.example.mobileteamapp.entity.Novel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NovelRepository {
    private NovelDao novelDao;
    private ExecutorService executorService;

    public NovelRepository(Context context) {
        novelDao = AppDatabaseInstance.getInstance(context).novelDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Novel novel) {
        executorService.execute(() -> novelDao.insert(novel));
    }

    public void update(Novel novel) {
        executorService.execute(() -> novelDao.update(novel));
    }

    public void delete(Novel novel) {
        executorService.execute(() -> novelDao.delete(novel));
    }

    public LiveData<List<Novel>> getAllNovels() {
        return novelDao.getAllNovels();
    }

    public Novel getNovelById(int id) {
        return novelDao.getNovelById(id);
    }

    public Novel getNovelByDreamId(String dreamId) {
        return novelDao.getNovelByDreamId(dreamId);
    }

    public Novel getNovelByDreamIdAndGenre(String dreamId, String genre) {
        return novelDao.getNovelByDreamIdAndGenre(dreamId, genre);
    }

    public List<Novel> getNovelsByDreamId(String dreamId) {
        return novelDao.getNovelsByDreamId(dreamId);
    }

}
