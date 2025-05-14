package com.example.mobileteamapp.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// 답변 테이블
@Entity(
        tableName = "answer",
        foreignKeys = {
                @ForeignKey(entity = Dream.class,
                        parentColumns = "dream_id",
                        childColumns = "dream_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Question.class,
                        parentColumns = "question_id",
                        childColumns = "question_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Emotion.class,
                        parentColumns = "emotion_id",
                        childColumns = "emotion_id",
                        onDelete = ForeignKey.CASCADE)
        }
)
public class Answer {
    @PrimaryKey
    @NonNull
    private String answer_id;

    @NonNull
    private String dream_id;
    private int question_id;
    private int emotion_id;
    private String answer_text;


    // Getters and Setters
    public String getAnswer_id() { return answer_id; }
    public void setAnswer_id(String answer_id) { this.answer_id = answer_id; }

    public String getDream_id() { return dream_id; }
    public void setDream_id(String dream_id) { this.dream_id = dream_id; }

    public int getQuestion_id() { return question_id; }
    public void setQuestion_id(int question_id) { this.question_id = question_id; }

    public int getEmotion_id() { return emotion_id; }
    public void setEmotion_id(int emotion_id) { this.emotion_id = emotion_id; }

    public String getAnswer_text() { return answer_text; }
    public void setAnswer_text(String answer_text) { this.answer_text = answer_text; }
}
