package com.example.mobileteamapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.mobileteamapp.EmotionRecord;


import com.example.mobileteamapp.entity.Emotion;

import java.util.List;

@Dao
public interface EmotionDao {

    @Insert
    void insert(Emotion emotion);

    @Update
    void update(Emotion emotion);

    @Delete
    void delete(Emotion emotion);

    @Query("SELECT * FROM emotion")
    List<Emotion> getAllEmotions();

    @Query("SELECT * FROM emotion WHERE emotion_id = :id")
    Emotion getEmotionById(int id);

    // 예시: 주간(기간별) 감정 데이터 조회
    @Query("SELECT d.dream_date, e.emotion_name " +
            "FROM answer a " +
            "JOIN dream d ON a.dream_id = d.dream_id " +
            "JOIN emotion e ON a.emotion_id = e.emotion_id " +
            "WHERE d.member_id = :memberId " +
            "AND d.dream_date BETWEEN :startDate AND :endDate " +
            "ORDER BY d.dream_date ASC")
    List<EmotionRecord> getEmotionRecordsByPeriod(String memberId, String startDate, String endDate);

    // 예시: 월별 감정 데이터 조회
    @Query("SELECT d.dream_date, e.emotion_name " +
            "FROM answer a " +
            "JOIN dream d ON a.dream_id = d.dream_id " +
            "JOIN emotion e ON a.emotion_id = e.emotion_id " +
            "WHERE d.member_id = :memberId " +
            "AND substr(d.dream_date, 1, 7) = :yearMonth " +
            "ORDER BY d.dream_date ASC")
    List<EmotionRecord> getEmotionRecordsByMonth(String memberId, String yearMonth);
}


