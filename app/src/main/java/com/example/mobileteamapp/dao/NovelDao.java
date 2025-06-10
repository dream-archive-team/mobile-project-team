package com.example.mobileteamapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobileteamapp.entity.Novel;

import java.util.List;

@Dao
public interface NovelDao {
    @Insert
    void insert(Novel novel);

    @Update
    void update(Novel novel);

    @Delete
    void delete(Novel novel);

    @Query("SELECT * FROM novel ORDER BY novel_id DESC")
    List<Novel> getAllNovels();

    @Query("SELECT * FROM novel WHERE novel_id = :id")
    Novel getNovelById(int id);

    @Query("SELECT * FROM novel WHERE dream_id = :dreamId LIMIT 1")
    Novel getNovelByDreamId(String dreamId);

    @Query("SELECT * FROM novel WHERE dream_id = :dreamId AND genre = :genre LIMIT 1")
    Novel getNovelByDreamIdAndGenre(String dreamId, String genre);

    @Query("SELECT * FROM novel WHERE dream_id = :dreamId")
    List<Novel> getNovelsByDreamId(String dreamId);

}
