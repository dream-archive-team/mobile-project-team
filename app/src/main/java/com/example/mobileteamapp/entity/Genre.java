package com.example.mobileteamapp.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// 장르 엔터티
@Entity(tableName = "genre")
public class Genre {
    @PrimaryKey(autoGenerate = true)
    public int genre_id;

    public String genre_name;

    // 기본 생성자
    public Genre() { }

    public Genre(String genre_name) {
        this.genre_name = genre_name;
    }
}
