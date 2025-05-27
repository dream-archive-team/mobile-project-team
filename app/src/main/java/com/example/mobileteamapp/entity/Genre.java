package com.example.mobileteamapp.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// 장르 테이블
@Entity(tableName = "genre")
public class Genre {
    @PrimaryKey
    private int genre_id;

    private String genre_name;

    // Getters and Setters
    public int getGenre_id() { return genre_id; }
    public void setGenre_id(int genre_id) { this.genre_id = genre_id; }

    public String getGenre_name() { return genre_name; }
    public void setGenre_name(String genre_name) { this.genre_name = genre_name; }
}
