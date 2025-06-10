package com.example.mobileteamapp.repository;

import android.content.Context;

import com.example.mobileteamapp.dao.EmotionDao;
import com.example.mobileteamapp.db.AppDatabaseInstance;
import com.example.mobileteamapp.entity.Emotion;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmotionRepository {
    private EmotionDao emotionDao;
    private ExecutorService executorService;

    public EmotionRepository(Context context) {
        emotionDao = AppDatabaseInstance.getInstance(context).emotionDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Emotion emotion) {
        executorService.execute(() -> emotionDao.insert(emotion));
    }

    public void update(Emotion emotion) {
        executorService.execute(() -> emotionDao.update(emotion));
    }

    public void delete(Emotion emotion) {
        executorService.execute(() -> emotionDao.delete(emotion));
    }

    // id로 감정 1개 조회
    public Emotion getEmotionById(int id) {
        return emotionDao.getEmotionById(id);
    }

    // 이름으로 감정 1개 조회
    public Emotion getEmotionByName(String name) {
        return emotionDao.getEmotionByName(name);
    }

    // 감정 전체 리스트 조회
    public List<Emotion> getAllEmotions() {
        return emotionDao.getAllEmotions();
    }

    // 감정을 동기적으로 삽입하고 생성된 ID 반환
    public long insertAndReturnId(Emotion emotion) {
        return emotionDao.insertAndReturnId(emotion);
    }

}
