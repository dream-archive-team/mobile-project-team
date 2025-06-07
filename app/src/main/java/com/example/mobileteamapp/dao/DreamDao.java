package com.example.mobileteamapp.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mobileteamapp.entity.Dream;

import java.util.List;

@Dao
public interface DreamDao {

    @Insert
    void insert(Dream dream);

    @Update
    void update(Dream dream);

    @Delete
    void delete(Dream dream);

    @Query("SELECT * FROM dream")
    LiveData<List<Dream>> getAllDreams();

    @Query("SELECT * FROM dream WHERE dream_id = :id")
    Dream getDreamById(String id);

    @Query("SELECT * FROM dream WHERE member_id = :memberId")
    List<Dream> getDreamsByMemberId(String memberId);

    // 특정날짜, 사용자 여부 확인 쿼리(꿈 작성 하루에 한 개 제한)
    @Query("SELECT COUNT(*) FROM dream WHERE member_id = :memberId AND dream_date = :date")
    int countDreamsByMemberAndDate(String memberId, String date);

}

