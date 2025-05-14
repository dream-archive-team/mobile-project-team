package com.example.mobileteamapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobileteamapp.entity.Genre;

import java.util.List;

@Dao
public interface GenreDao {

    @Insert
    void insert(Genre genre);

    @Update
    void update(Genre genre);

    @Delete
    void delete(Genre genre);

    @Query("SELECT * FROM genre")
    List<Genre> getAllGenres();

    @Query("SELECT * FROM genre WHERE genre_id = :id")
    Genre getGenreById(int id);
}
