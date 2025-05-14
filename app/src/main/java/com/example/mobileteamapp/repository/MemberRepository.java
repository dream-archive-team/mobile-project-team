package com.example.mobileteamapp.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.mobileteamapp.dao.MemberDao;
import com.example.mobileteamapp.database.AppDatabase;
import com.example.mobileteamapp.database.AppDatabaseInstance;
import com.example.mobileteamapp.entity.Member;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MemberRepository {

    private final MemberDao memberDao;
    private final ExecutorService executorService;

    // db 인스턴스를 가져오고 memberDao에서 LiveData로 전체 꿈 데이터 불러오기
    public MemberRepository(Application application) {
        AppDatabase db = AppDatabaseInstance.getDatabase(application);
        memberDao = db.memberDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // Dao->repository
    // 회원 데이터 삽입
    public void insert(Member member) {
        executorService.execute(() -> memberDao.insert(member));
    }

    // 특정 ID를 가진 회원 데이터를 DB에서 조회하여 반환
    public LiveData<Member> getMemberById(String id) {
        return memberDao.getMemberById(id);
    }

    // 모든 회원의 목록을 DB에서 조회하여 반환
    public LiveData<List<Member>> getAllMembers() {
        return memberDao.getAllMembers();
    }
}
