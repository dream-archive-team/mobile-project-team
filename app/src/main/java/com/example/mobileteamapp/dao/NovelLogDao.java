package com.example.mobileteamapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobileteamapp.entity.NovelLog;

import java.util.List;

@Dao
public interface NovelLogDao {

    @Insert
    void insert(NovelLog novelLog);

    @Update
    void update(NovelLog novelLog);

    @Delete
    void delete(NovelLog novelLog);

    @Query("SELECT * FROM novel_log")
    List<NovelLog> getAllNovelLogs();

    @Query("SELECT * FROM novel_log WHERE log_id = :id")
    NovelLog getNovelLogById(String id);

    @Query("SELECT * FROM novel_log WHERE novel_id = :novelId")
    List<NovelLog> getLogsByNovelId(String novelId);
}
