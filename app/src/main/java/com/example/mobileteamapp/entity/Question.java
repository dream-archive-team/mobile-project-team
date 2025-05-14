package com.example.mobileteamapp.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// 질문 테이블
@Entity(tableName = "question")
public class Question {
    @PrimaryKey
    private int question_id;

    private String question_text;

    // Getters and Setters
    public int getQuestion_id() { return question_id; }
    public void setQuestion_id(int question_id) { this.question_id = question_id; }

    public String getQuestion_text() { return question_text; }
    public void setQuestion_text(String question_text) { this.question_text = question_text; }
}
