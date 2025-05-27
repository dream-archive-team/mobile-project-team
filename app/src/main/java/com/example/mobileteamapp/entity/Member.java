package com.example.mobileteamapp.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// 회원 테이블
@Entity(tableName = "member")
public class Member {
    @PrimaryKey
    @NonNull
    private String member_id;

    private String kakao_id;
    private String nickname;

    // Getters and Setters
    public String getMember_id() { return member_id; }
    public void setMember_id(String member_id) { this.member_id = member_id; }

    public String getKakao_id() { return kakao_id; }
    public void setKakao_id(String kakao_id) { this.kakao_id = kakao_id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}
