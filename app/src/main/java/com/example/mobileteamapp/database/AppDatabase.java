package com.example.mobileteamapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.mobileteamapp.dao.*;
import com.example.mobileteamapp.entity.*;

// Room db 생성 및 관리
@Database(
        entities = {
                Member.class,
                Dream.class,
                Question.class,
                Emotion.class,
                Answer.class,
                Genre.class,
                Novel.class,
                NovelLog.class
        },
        version = 1
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract MemberDao memberDao();
    public abstract DreamDao dreamDao();
    public abstract QuestionDao questionDao();
    public abstract EmotionDao emotionDao();
    public abstract AnswerDao answerDao();
    public abstract GenreDao genreDao();
    public abstract NovelDao novelDao();
    public abstract NovelLogDao novelLogDao();
}
