package com.example.mobileteamapp.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobileteamapp.entity.Answer;

import java.util.List;


@Dao
public interface AnswerDao {

    @Insert
    void insert(Answer answer);

    @Update
    void update(Answer answer);

    @Delete
    void delete(Answer answer);

    @Query("SELECT * FROM answer")
    List<Answer> getAllAnswers();

    @Query("SELECT * FROM answer WHERE answer_id = :id")
    Answer getAnswerById(String id);

    @Query("SELECT * FROM answer WHERE dream_id = :dreamId")
    List<Answer> getAnswersByDreamId(String dreamId);
}
