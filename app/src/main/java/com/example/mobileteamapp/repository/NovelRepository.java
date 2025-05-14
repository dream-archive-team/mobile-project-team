package com.example.mobileteamapp.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.mobileteamapp.dao.NovelDao;
import com.example.mobileteamapp.database.AppDatabase;
import com.example.mobileteamapp.database.AppDatabaseInstance;
import com.example.mobileteamapp.entity.Novel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NovelRepository {

    private final NovelDao novelDao;
    private final LiveData<List<Novel>> allNovels;
    private final ExecutorService executorService;

    // db 인스턴스를 가져오고 novelDao에서 LiveData로 전체 꿈 데이터 불러오기
    public NovelRepository(Application application) {
        AppDatabase db = AppDatabaseInstance.getDatabase(application);
        novelDao = db.novelDao();
        allNovels = novelDao.getAllNovels();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Dao->repository
    // 모든 소설의 목록을 DB에서 조회하여 반환
    public LiveData<List<Novel>> getAllNovels() {
        return allNovels;
    }

    // 모든 데이터 삽입 * 수정 * 삭제는 백그라운드 스레드(executorService)에서 실행
    // 소설 데이터 삽입
    public void insert(Novel novel) {
        executorService.execute(() -> novelDao.insert(novel));
    }
    // 소설 데이터 수정
    public void update(Novel novel) {
        executorService.execute(() -> novelDao.update(novel));
    }
    // 소설 데이터 삭제
    public void delete(Novel novel) {
        executorService.execute(() -> novelDao.delete(novel));
    }
}
