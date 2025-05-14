package com.example.mobileteamapp.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// 소설 테이블
@Entity(
        tableName = "novel",
        foreignKeys = {
                @ForeignKey(entity = Dream.class,
                        parentColumns = "dream_id",
                        childColumns = "dream_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Genre.class,
                        parentColumns = "genre_id",
                        childColumns = "genre_id",
                        onDelete = ForeignKey.CASCADE)
        }
)
public class Novel {
    @PrimaryKey
    @NonNull
    private String novel_id;

    @NonNull
    private String dream_id;
    private int genre_id;
    private String novel_title;
    private String novel_content;
    private int emotion_strength;

    // Getters and Setters
    public String getNovel_id() { return novel_id; }
    public void setNovel_id(String novel_id) { this.novel_id = novel_id; }

    public String getDream_id() { return dream_id; }
    public void setDream_id(String dream_id) { this.dream_id = dream_id; }

    public int getGenre_id() { return genre_id; }
    public void setGenre_id(int genre_id) { this.genre_id = genre_id; }

    public String getNovel_title() { return novel_title; }
    public void setNovel_title(String novel_title) { this.novel_title = novel_title; }

    public String getNovel_content() { return novel_content; }
    public void setNovel_content(String novel_content) { this.novel_content = novel_content; }

    public int getEmotion_strength() { return emotion_strength; }
    public void setEmotion_strength(int emotion_strength) { this.emotion_strength = emotion_strength; }
}
