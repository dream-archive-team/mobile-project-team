package com.example.mobileteamapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobileteamapp.entity.Dream;

import java.util.List;

@Dao
public interface DreamDao {
    @Insert
    long insert(Dream dream); // 반환형 long


    @Update
    void update(Dream dream);

    @Delete
    void delete(Dream dream);

    // 꿈 일기 전체 조회(최신 날짜순으로 반환)
    @Query("SELECT * FROM dream ORDER BY dream_date DESC")
    List<Dream> getAllDreams();

    // 특정 사용자의 단일 꿈 조회
    @Query("SELECT * FROM dream WHERE dream_id = :id")
    Dream getDreamById(String id);

    // 오늘 날짜의 꿈이 있는지 조회
    @Query("SELECT COUNT(*) FROM dream WHERE dream_date = :date")
    int getDreamCountByDate(String date);

    // 특정 날짜의 꿈을 조회
    @Query("SELECT * FROM dream WHERE dream_date = :date LIMIT 1")
    Dream getDreamByDate(String date);


}
