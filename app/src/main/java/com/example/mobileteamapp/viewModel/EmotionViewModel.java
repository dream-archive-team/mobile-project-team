package com.example.mobileteamapp.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mobileteamapp.entity.Emotion;
import com.example.mobileteamapp.repository.EmotionRepository;

import java.util.List;

public class EmotionViewModel extends AndroidViewModel {
    private final EmotionRepository repository;
    private final LiveData<List<Emotion>> allEmotions;

    public EmotionViewModel(@NonNull Application application) {
        super(application);
        repository = new EmotionRepository(application);
        allEmotions = repository.getAllEmotions();
    }

    public LiveData<List<Emotion>> getAllEmotions() {
        return allEmotions;
    }

    public void insert(Emotion emotion) {
        repository.insert(emotion);
    }

    public void update(Emotion emotion) {
        repository.update(emotion);
    }

    public void delete(Emotion emotion) {
        repository.delete(emotion);
    }

    public Emotion getEmotionById(int id) {
        return repository.getEmotionById(id);
    }
}
