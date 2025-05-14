package com.example.mobileteamapp.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// 소설 대화 로그 테이블
@Entity(
        tableName = "novel_log",
        foreignKeys = @ForeignKey(
                entity = Novel.class,
                parentColumns = "novel_id",
                childColumns = "novel_id",
                onDelete = ForeignKey.CASCADE
        )
)
public class NovelLog {
    @PrimaryKey
    @NonNull
    private String log_id;

    @NonNull
    private String novel_id;
    private String log_text;

    // Getters and Setters
    public String getLog_id() { return log_id; }
    public void setLog_id(String log_id) { this.log_id = log_id; }

    public String getNovel_id() { return novel_id; }
    public void setNovel_id(String novel_id) { this.novel_id = novel_id; }

    public String getLog_text() { return log_text; }
    public void setLog_text(String log_text) { this.log_text = log_text; }
}
