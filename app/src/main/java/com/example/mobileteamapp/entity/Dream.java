package com.example.mobileteamapp.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

// 꿈 테이블
@Entity(
        tableName = "dream",
        foreignKeys = @ForeignKey(
                entity = Member.class,
                parentColumns = "member_id",
                childColumns = "member_id",
                onDelete = ForeignKey.CASCADE
        )
)
public class Dream {
    @PrimaryKey
    @NonNull
    private String dream_id;

    @NonNull
    private String member_id;
    private String dream_date;
    private String dream_content;
    private String interpretation;

    // Getters and Setters
    public String getDream_id() { return dream_id; }
    public void setDream_id(String dream_id) { this.dream_id = dream_id; }

    public String getMember_id() { return member_id; }
    public void setMember_id(String member_id) { this.member_id = member_id; }

    public String getDream_date() { return dream_date; }
    public void setDream_date(String dream_date) { this.dream_date = dream_date; }

    public String getDream_content() { return dream_content; }
    public void setDream_content(String dream_content) { this.dream_content = dream_content; }

    public String getInterpretation() { return interpretation; }
    public void setInterpretation(String interpretation) { this.interpretation = interpretation; }
}
