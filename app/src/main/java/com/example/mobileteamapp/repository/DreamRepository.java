package com.example.mobileteamapp.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.mobileteamapp.dao.DreamDao;
import com.example.mobileteamapp.database.AppDatabase;
import com.example.mobileteamapp.database.AppDatabaseInstance;
import com.example.mobileteamapp.entity.Dream;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DreamRepository {

    private final DreamDao dreamDao;
    private final LiveData<List<Dream>> allDreams;
    private final ExecutorService executorService;   // 비동기 작업을 위한 객체



    // db 인스턴스를 가져오고 dreamDao에서 LiveData로 전체 꿈 데이터 불러오기
    public DreamRepository(Application application) {
        AppDatabase db = AppDatabaseInstance.getDatabase(application);
        dreamDao = db.dreamDao();
        allDreams = dreamDao.getAllDreams();
        executorService = Executors.newSingleThreadExecutor();
    }


    // Dao->repository
    // 모든 꿈의 목록을 DB에서 조회하여 반환
    public LiveData<List<Dream>> getAllDreams() {
        return allDreams;
    }

    // 모든 데이터 삽입 * 수정 * 삭제는 백그라운드 스레드(executorService)에서 실행
    // 꿈 데이터 삽입
    public void insert(Dream dream) {
        executorService.execute(() -> dreamDao.insert(dream));
    }

    // 꿈 데이터 수정
    public void update(Dream dream) {
        executorService.execute(() -> dreamDao.update(dream));
    }

    // 꿈 데이터 삭제
    public void delete(Dream dream) {
        executorService.execute(() -> dreamDao.delete(dream));
    }
}
