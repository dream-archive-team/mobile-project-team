package com.example.mobileteamapp.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.mobileteamapp.dao.EmotionDao;
import com.example.mobileteamapp.database.AppDatabase;
import com.example.mobileteamapp.database.AppDatabaseInstance;
import com.example.mobileteamapp.entity.Emotion;

import java.util.List;

public class EmotionRepository {
    private final EmotionDao emotionDao;
    private final LiveData<List<Emotion>> allEmotions;

    public EmotionRepository(Application application) {
        AppDatabase db = AppDatabaseInstance.getDatabase(application);
        emotionDao = db.emotionDao();
        allEmotions = emotionDao.getAllEmotions();
    }

    public LiveData<List<Emotion>> getAllEmotions() {
        return allEmotions;
    }

    public void insert(Emotion emotion) {
        new InsertAsyncTask(emotionDao).execute(emotion);
    }

    public void update(Emotion emotion) {
        new UpdateAsyncTask(emotionDao).execute(emotion);
    }

    public void delete(Emotion emotion) {
        new DeleteAsyncTask(emotionDao).execute(emotion);
    }

    public Emotion getEmotionById(int id) {
        return emotionDao.getEmotionById(id);
    }

    private static class InsertAsyncTask extends AsyncTask<Emotion, Void, Void> {
        private final EmotionDao dao;

        InsertAsyncTask(EmotionDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Emotion... emotions) {
            dao.insert(emotions[0]);
            return null;
        }
    }

    private static class UpdateAsyncTask extends AsyncTask<Emotion, Void, Void> {
        private final EmotionDao dao;

        UpdateAsyncTask(EmotionDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Emotion... emotions) {
            dao.update(emotions[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<Emotion, Void, Void> {
        private final EmotionDao dao;

        DeleteAsyncTask(EmotionDao dao) {
            this.dao = dao;
        }

        @Override
        protected Void doInBackground(Emotion... emotions) {
            dao.delete(emotions[0]);
            return null;
        }
    }
}
