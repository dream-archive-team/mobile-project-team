package com.example.mobileteamapp.repository;

import android.content.Context;

import com.example.mobileteamapp.dao.GenreDao;
import com.example.mobileteamapp.db.AppDatabaseInstance;
import com.example.mobileteamapp.entity.Genre;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GenreRepository {
    private GenreDao genreDao;
    private ExecutorService executorService;

    public GenreRepository(Context context) {
        genreDao = AppDatabaseInstance.getInstance(context).genreDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Genre genre) {
        executorService.execute(() -> genreDao.insert(genre));
    }

    public void update(Genre genre) {
        executorService.execute(() -> genreDao.update(genre));
    }

    public void delete(Genre genre) {
        executorService.execute(() -> genreDao.delete(genre));
    }

    public List<Genre> getAllGenres() {
        return genreDao.getAllGenres();
    }

    public Genre getGenreById(int id) {
        return genreDao.getGenreById(id);
    }

    public String getGenreNameById(int id) {
        return genreDao.getGenreNameById(id);
    }

}
