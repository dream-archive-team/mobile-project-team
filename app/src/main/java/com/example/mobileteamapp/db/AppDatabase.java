package com.example.mobileteamapp.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.mobileteamapp.dao.DreamDao;
import com.example.mobileteamapp.dao.EmotionDao;
import com.example.mobileteamapp.dao.GenreDao;
import com.example.mobileteamapp.dao.NovelDao;
import com.example.mobileteamapp.entity.Dream;
import com.example.mobileteamapp.entity.Emotion;
import com.example.mobileteamapp.entity.Genre;
import com.example.mobileteamapp.entity.Novel;

// db 구조 정의
@Database(entities = {Dream.class, Genre.class, Novel.class, Emotion.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    // dao를 가져오는 추상 메서드
    public abstract DreamDao dreamDao();
    public abstract GenreDao genreDao();
    public abstract NovelDao novelDao();
    public abstract EmotionDao emotionDao();
}
