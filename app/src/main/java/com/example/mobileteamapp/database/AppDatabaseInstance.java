package com.example.mobileteamapp.database;

import android.content.Context;

import androidx.room.Room;

// Room db 싱글톤 클래스
public class AppDatabaseInstance {

    private static AppDatabase instance;

    public static AppDatabase getDatabase(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "app_database"
                    ).build();
                }
            }
        }
        return instance;
    }
}
