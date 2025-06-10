package com.example.mobileteamapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mobileteamapp.entity.Genre;
import com.example.mobileteamapp.repository.GenreRepository;

import java.util.List;
import java.util.concurrent.Executors;

public class GenreViewModel extends AndroidViewModel {
    private GenreRepository repository;
    private MutableLiveData<List<Genre>> allGenres = new MutableLiveData<>();

    public GenreViewModel(@NonNull Application application) {
        super(application);
        repository = new GenreRepository(application);
        loadGenres();
    }

    public LiveData<List<Genre>> getAllGenres() {
        return allGenres;
    }

    public void loadGenres() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Genre> genres = repository.getAllGenres();
            allGenres.postValue(genres);
        });
    }

    public void insert(Genre genre) {
        Executors.newSingleThreadExecutor().execute(() -> {
            repository.insert(genre);
            loadGenres(); // 새로고침
        });
    }

    public void update(Genre genre) {
        Executors.newSingleThreadExecutor().execute(() -> {
            repository.update(genre);
            loadGenres();
        });
    }

    public void delete(Genre genre) {
        Executors.newSingleThreadExecutor().execute(() -> {
            repository.delete(genre);
            loadGenres();
        });
    }

    public String getGenreNameById(int id) {
        return repository.getGenreNameById(id);
    }

}
