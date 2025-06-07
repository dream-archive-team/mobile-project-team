package com.example.mobileteamapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileteamapp.entity.Dream;
import com.example.mobileteamapp.viewModel.DreamViewModel;

import java.util.List;

public class final_rbmood_tvdiary_tvanalys extends AppCompatActivity {

    private TextView tvDreamDiaryLabel, tvDreamAnalysis;
    private EditText dreamViewModel;
    private Button btnDelete, btnGoToNovel;

    private DreamViewModel dreamViewModelInstance;

    private String memberId, selectedDate;
    private Dream currentDream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_rbmood_tvdiary_tvanalys);

        // 값 받기
        memberId = getIntent().getStringExtra("member_id");
        selectedDate = getIntent().getStringExtra("selected_date");

        // View 바인딩
        tvDreamDiaryLabel = findViewById(R.id.tvDreamDiaryLabel);
        dreamViewModel = findViewById(R.id.dreamViewModel);
        tvDreamAnalysis = findViewById(R.id.tvDreamAnalysis);
        btnDelete = findViewById(R.id.btnDelete);
        btnGoToNovel = findViewById(R.id.btnGoToNovel);

        // ViewModel 설정
        dreamViewModelInstance = new ViewModelProvider(this).get(DreamViewModel.class);

        // 해당 날짜의 꿈 찾기
        dreamViewModelInstance.getAllDreams().observe(this, dreams -> {
            for (Dream dream : dreams) {
                if (dream.getMember_id().equals(memberId) && dream.getDream_date().equals(selectedDate)) {
                    currentDream = dream;
                    dreamViewModel.setText(dream.getDream_content());
                    tvDreamAnalysis.setText(dream.getInterpretation());
                    return;
                }
            }
            Toast.makeText(this, "해당 날짜의 꿈이 없습니다.", Toast.LENGTH_SHORT).show();
        });

        // 삭제 버튼 클릭 시
        btnDelete.setOnClickListener(view -> {
            if (currentDream != null) {
                dreamViewModelInstance.delete(currentDream);
                Toast.makeText(this, "꿈이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "삭제할 꿈이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 소설 이동 버튼 클릭 시
        btnGoToNovel = findViewById(R.id.btnGoToNovel);

        btnGoToNovel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(final_rbmood_tvdiary_tvanalys.this, Final_novelActivity.class);
                startActivity(intent);
            }
        });
    }
}

