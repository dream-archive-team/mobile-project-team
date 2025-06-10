package com.example.mobileteamapp.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "novel")
public class Novel {
    @PrimaryKey(autoGenerate = true)
    public int novel_id;

    public String novel_title;
    public String novel_content;
    public String genre;
    public String mood;
    public String vivid_scene;
    public String dream_objects;
    public String ending;
    public String required_words;
    public String dream_date;
    public String dream_id;

    // 기본 생성자
    public Novel() {}

    // 전체 필드 생성자
    public Novel(String novel_title, String novel_content, String genre, String mood, String vivid_scene, String dream_objects, String ending, String required_words, String dream_date, String dream_id) {
        this.novel_title = novel_title;
        this.novel_content = novel_content;
        this.genre = genre;
        this.mood = mood;
        this.vivid_scene = vivid_scene;
        this.dream_objects = dream_objects;
        this.ending = ending;
        this.required_words = required_words;
        this.dream_date = dream_date;
        this.dream_id = dream_id;
    }

    // Getter
    public int getNovel_id() { return novel_id; }
    public String getNovel_title() { return novel_title; }
    public String getNovel_content() { return novel_content; }
    public String getGenre() { return genre; }
    public String getDream_date() { return dream_date; }
}
