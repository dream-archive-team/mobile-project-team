package com.example.mobileteamapp.db;

import android.content.Context;

import androidx.room.Room;

// Room DB 싱글톤 클래스(db를 하나만 두기위해 사용)
public class AppDatabaseInstance {
    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    AppDatabase.class,
                    "dream_storage_app_db"
            ).build();
        }
        return instance;
    }
}
