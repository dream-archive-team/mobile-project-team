package com.example.mobileteamapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobileteamapp.entity.Emotion;
import com.example.mobileteamapp.entity.EmotionRecord;

import java.util.List;

@Dao
public interface EmotionDao {


    @Insert
    long insertAndReturnId(Emotion emotion);

    @Insert
    void insert(Emotion emotion);

    @Update
    void update(Emotion emotion);

    @Delete
    void delete(Emotion emotion);

    // id로 감정 1개 조회
    @Query("SELECT * FROM emotion WHERE emotion_id = :id")
    Emotion getEmotionById(int id);

    // 이름으로 감정 1개 조회
    @Query("SELECT * FROM emotion WHERE emotion_name = :name")
    Emotion getEmotionByName(String name);

    // 감정 전체 리스트 조회
    @Query("SELECT * FROM emotion")
    List<Emotion> getAllEmotions();


    // 주간 감정기록(조인)
    @Query("SELECT d.dream_date, e.emotion_name FROM dream d LEFT JOIN emotion e ON d.emotion_id = e.emotion_id " +
            "WHERE d.dream_date BETWEEN :startDate AND :endDate ORDER BY d.dream_date")
    List<EmotionRecord> getEmotionRecordsByPeriod(String startDate, String endDate);

    // 월간 감정기록(조인)
    @Query("SELECT d.dream_date, e.emotion_name FROM dream d LEFT JOIN emotion e ON d.emotion_id = e.emotion_id " +
            "WHERE d.dream_date LIKE :yearMonth || '%' ORDER BY d.dream_date")
    List<EmotionRecord> getEmotionRecordsByMonth(String yearMonth);

}
