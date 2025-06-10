package com.example.mobileteamapp.repository;

import android.content.Context;

import com.example.mobileteamapp.dao.DreamDao;
import com.example.mobileteamapp.db.AppDatabaseInstance;
import com.example.mobileteamapp.entity.Dream;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DreamRepository {
    private DreamDao dreamDao;
    private ExecutorService executorService;    // db작업을 별도의 쓰레드에서 처리

    // 생성자
    public DreamRepository(Context context) {
        dreamDao = AppDatabaseInstance.getInstance(context).dreamDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // 꿈 추가 (비동기)
    public void insert(Dream dream) {
        executorService.execute(() -> dreamDao.insert(dream));
    }

    // id 반환하는 동기 insert (별도 Thread에서 직접 사용 필요)
    public long insertAndReturnId(Dream dream) {
        return dreamDao.insert(dream);
    }

    // 꿈 수정
    public void update(Dream dream) {
        executorService.execute(() -> dreamDao.update(dream));
    }

    // 꿈 삭제
    public void delete(Dream dream) {
        executorService.execute(() -> dreamDao.delete(dream));
    }

    // 꿈 목록 전체 조회
    public List<Dream> getAllDreams() {
        return dreamDao.getAllDreams();
    }

    // 특정 꿈 상세 조회
    public Dream getDreamById(String id) {
        return dreamDao.getDreamById(id);
    }

    // 오늘 날짜의 꿈이 있는지 조회
    public int getDreamCountByDate(String date) {
        return dreamDao.getDreamCountByDate(date);
    }

    // 특정 날짜의 꿈을 조회
    public Dream getDreamByDate(String date) {
        return dreamDao.getDreamByDate(date);
    }
}
