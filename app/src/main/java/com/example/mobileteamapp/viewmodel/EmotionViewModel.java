package com.example.mobileteamapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobileteamapp.entity.Emotion;
import com.example.mobileteamapp.repository.EmotionRepository;

import java.util.List;

public class EmotionViewModel extends AndroidViewModel {

    private final EmotionRepository repository;
    private final MutableLiveData<List<Emotion>> allEmotions = new MutableLiveData<>();

    public EmotionViewModel(@NonNull Application application) {
        super(application);
        repository = new EmotionRepository(application);
        reloadEmotions();
    }

    public LiveData<List<Emotion>> getAllEmotions() {
        return allEmotions;
    }

    public void insert(Emotion emotion) {
        repository.insert(emotion);
        reloadEmotions();
    }

    public void update(Emotion emotion) {
        repository.update(emotion);
        reloadEmotions();
    }

    public void delete(Emotion emotion) {
        repository.delete(emotion);
        reloadEmotions();
    }

    // id로 감정 1개 조회 (동기)
    public Emotion getEmotionById(int id) {
        return repository.getEmotionById(id);
    }

    // 이름으로 감정 1개 조회 (동기)
    public Emotion getEmotionByName(String name) {
        return repository.getEmotionByName(name);
    }

    // 감정 전체 리스트 조회 (비동기)
    private void reloadEmotions() {
        new Thread(() -> {
            List<Emotion> emotionList = repository.getAllEmotions();
            allEmotions.postValue(emotionList);
        }).start();

    }

    // 감정을 DB에 저장하고, 생성된 ID 반환
    public long insertAndReturnId(Emotion emotion) {
        return repository.insertAndReturnId(emotion);
    }

}
