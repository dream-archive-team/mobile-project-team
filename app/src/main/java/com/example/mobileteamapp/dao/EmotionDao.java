package com.example.mobileteamapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

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
}
