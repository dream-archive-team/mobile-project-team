package com.example.mobileteamapp.dao;

import com.example.mobileteamapp.entity.*;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MemberDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Member member);


    @Update
    void update(Member member);

    @Delete
    void delete(Member member);

    @Query("SELECT * FROM member")
    LiveData<List<Member>> getAllMembers();

    @Query("SELECT * FROM member WHERE member_id = :id")
    LiveData<Member> getMemberById(String id);

    @Query("SELECT COUNT(*) FROM member")
    int countMembers();
}
