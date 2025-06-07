package com.example.mobileteamapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobileteamapp.entity.Emotion;

import java.util.List;

@Dao
public interface EmotionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE) // 중복 시 무시
    void insert(Emotion emotion);

    @Update
    void update(Emotion emotion);

    @Delete
    void delete(Emotion emotion);

    @Query("SELECT * FROM emotion")
    LiveData<List<Emotion>> getAllEmotions();

    @Query("SELECT * FROM emotion WHERE emotion_id = :id")
    Emotion getEmotionById(int id);
}
