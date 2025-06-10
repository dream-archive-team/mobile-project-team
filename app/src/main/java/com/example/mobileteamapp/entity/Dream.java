package com.example.mobileteamapp.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// 꿈 엔터티
@Entity(tableName = "dream")
public class Dream {
    @PrimaryKey(autoGenerate = true)
    public int dream_id;

    public String dream_date;
    public String dream_content;
    public String interpretation;

    public int emotion_id;

    // 기본 생성자
    public Dream() { }

    public Dream(String dream_date, String dream_content, String interpretation, int emotion_id) {
        this.dream_date = dream_date;
        this.dream_content = dream_content;
        this.interpretation = interpretation;
        this.emotion_id = emotion_id;
    }

    // getter & setter
    public int getEmotionId() {
        return emotion_id;
    }
    public void setEmotionId(int emotion_id) {
        this.emotion_id = emotion_id;
    }

    public String getDreamContent() {
        return dream_content;
    }
    public String getInterpretation() {
        return interpretation;
    }
    public int getDreamId() {
        return dream_id;
    }

}
