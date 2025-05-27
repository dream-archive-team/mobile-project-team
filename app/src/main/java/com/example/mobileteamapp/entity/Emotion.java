package com.example.mobileteamapp.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// 감정 테이블
@Entity(tableName = "emotion")
public class  Emotion {
    @PrimaryKey
    private int emotion_id;

    private String emotion_name;

    // Getters and Setters
    public int getEmotion_id() { return emotion_id; }
    public void setEmotion_id(int emotion_id) { this.emotion_id = emotion_id; }

    public String getEmotion_name() { return emotion_name; }
    public void setEmotion_name(String emotion_name) { this.emotion_name = emotion_name; }
}
